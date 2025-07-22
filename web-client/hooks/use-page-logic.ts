/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

import IPage from "@/app/model/IPage";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import RecordKind from "@/app/model/RecordKind";
import { LinkableEntityQueryFilter, TrackedEntityQueryFilter } from "@/app/model/schema";
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context";
import { FormAction, getLinkFilterIdProperty, SearchSettings } from "@/lib/utils";
import { DocumentNode, useQuery } from "@apollo/client";
import { FieldNode, Kind, OperationDefinitionNode, OperationTypeNode } from "graphql/language";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
import { Dispatch, SetStateAction, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { FieldValues, useForm, UseFormReturn } from "react-hook-form";
import { toast } from "sonner";
import z from "zod/v4";

type PaginationType = { pageIndex: number, pageSize: number }
type PageConfiguration<T extends ITrackedEntity, F extends TrackedEntityQueryFilter, V extends FieldValues> = {
  recordKind: RecordKind // TODO: do we need to constrain this to trackable/linkable types?
  schema: z.ZodObject<any>
  listQuery: DocumentNode
  copyToForm: (record?: T) => V
  copyFromForm: (record: T, fieldValues: V) => void
  prepareFilter?: (filter: F) => void
  preparePage?: (page?: IPage<T>) => IPage<T> | undefined
  findRecord?: (records?: T[], id?: string | null) => T | undefined
}
export type PageLogic<T extends ITrackedEntity, F extends FieldValues> = {
  search: SearchSettings
  setSearch: Dispatch<SetStateAction<SearchSettings>>
  pagination: PaginationType
  setPagination: Dispatch<SetStateAction<PaginationType>>
  loading: boolean
  page?: IPage<T>
  selectedRecord?: T
  handleRowSelectionChange: (recordId?: string) => void
  form: UseFormReturn<F, any, F>
  handleFormAction: (command: FormAction, formFields: F) => void
}

export default function usePageLogic<T extends ITrackedEntity, F extends TrackedEntityQueryFilter, V extends FieldValues>
  (config : PageConfiguration<T, F, V>) : PageLogic<T, V> {

  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({advancedSearch: false, showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext[config.recordKind]?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });

  const filter = useMemo(() => {
    const filter = {
      status: search.status ? [search.status] : undefined,
      text: search.text ?? undefined,
    } as F
    if (filter.text)
      filter.advancedSearch = search.advancedSearch
    if (search.showOnlyLinkedRecords) {
      const mlFilter = filter as LinkableEntityQueryFilter
      if (masterLinkContext.masterTopicId) {
        mlFilter.topicId = masterLinkContext.masterTopicId
        mlFilter.recursive = true
      }
      if (masterLinkContext.masterRecordId) {
        const linkFilterIdProperty = getLinkFilterIdProperty(config.recordKind, masterLinkContext.masterRecordKind)
        if (linkFilterIdProperty)
          mlFilter[linkFilterIdProperty] = masterLinkContext.masterRecordId
      }
    }
    config.prepareFilter?.(filter)
    console.log(`${config.recordKind}s effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search, masterLinkContext])

  const pageSort = useMemo(() => {
    const pageSort = {
      pageNumber: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    console.log(`${config.recordKind}s effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [pagination])

  const result = useQuery(
    config.listQuery,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    console.log(`${config.recordKind}s effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);

  if (result.error) {
    toast.error(`Fetch error:\n\n${JSON.stringify(result.error)}`)
    console.error(result.error)
  }

  // const pageReducer = useCallback((draft: IPage<Declaration>, action: MutationAction<FormAction, DeclarationFormFields>) => {
  //   const idx = draft.content.findIndex(c => c.id == selectedRecordId)
  //   switch (action.command) {
  //     case "create":
  //       const declaration : Declaration = {
  //         id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
  //         status: "Draft"
  //       }
  //       setSelectedRecordId(declaration.id)
  //       copyFromForm(declaration, action.value)
  //       draft.content.push(declaration)
  //       break
  //     case "update":
  //       if (idx != -1) {
  //         const declaration = draft.content[idx]
  //         copyFromForm(declaration, action.value)
  //         draft.content.splice(idx, 1, declaration)
  //       }
  //       break
  //     case "delete":
  //       // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
  //       if (idx != -1)
  //         draft.content.splice(idx, 1)
  //       break
  //   }
  // }, [selectedRecordId, setSelectedRecordId])

  const queryName = useMemo(() => {
    const opDef = config.listQuery.definitions.find(
      d => d.kind == Kind.OPERATION_DEFINITION && d.operation == OperationTypeNode.QUERY) as OperationDefinitionNode | undefined
    const fieldNode = opDef?.selectionSet.selections[0] as FieldNode | undefined
    const queryName = fieldNode?.name.value
    if (!queryName)
      throw new Error("Unable to determine query name")
    return queryName
  }, [])
  const rawPage = result.data?.[queryName] as unknown as IPage<T> | undefined
  const page = useMemo(() => config.preparePage?.(rawPage) ?? rawPage, [config.preparePage, rawPage])

  // const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Claim>)
  const getSelectedRecord = useCallback((id?: string) => {
    return config.findRecord
      ? config.findRecord(page?.content, id)
      : page?.content.find(r => r.id == id)
  }, [page])
  const selectedRecord = /*mode == "create" ? newRecord :*/ getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => config.copyToForm(selectedRecord), [config.copyToForm, selectedRecord])
  const form = useForm<V>({
    resolver: standardSchemaResolver(config.schema),
    mode: "onChange",
    values: origFormValue,
  })

  const handleFormAction = useCallback((command: FormAction, fieldValues: V) => {
    // console.log(`Claims.handleFormAction: command = ${command}, mode = ${mode}`)
    switch (command) {
      case "create":
        // TODO: invoke mutation: createXxx
        break
      case "update":
        // TODO: invoke mutation: updateXxx
        break
      case "delete":
        // TODO: invoke mutation: deleteXxx
        break
        // OLD: pageDispatch({command: command, value: fieldValues})
      case "reset":
        // if (mode == "create") {
        //   setNewRecord({
        //     id: (Math.max(...page.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
        //     status: "Draft"
        //   })
        // }
        form.reset(origFormValue)
        break
    }
  }, [form, /*pageDispatch, */selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const record = getSelectedRecord(recordId)
    form.reset(config.copyToForm(record))
  }, [setSelectedRecordId, getSelectedRecord, form])

  // console.log(`${settings.recordKind}s() page: ${JSON.stringify(page)})`)
  const pageLogic = useMemo<PageLogic<T, V>>(() => {
    return {
      search,
      setSearch,
      pagination,
      setPagination,
      loading: result.loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      form,
      handleFormAction,
    }}, [
      search,
      setSearch,
      pagination,
      setPagination,
      result.loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      form,
      handleFormAction,
  ])

  return pageLogic
}