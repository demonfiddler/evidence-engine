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
import { BaseEntityInput, LinkableEntityQueryFilter, PageableInput, TrackedEntityQueryFilter } from "@/app/model/schema";
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context";
import { getLinkFilterIdProperty, SearchSettings } from "@/lib/utils";
import { DocumentNode, useMutation, useQuery } from "@apollo/client";
import { OperationTypeNode } from "graphql/language";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
import { Dispatch, SetStateAction, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { FieldValues, useForm, UseFormReturn } from "react-hook-form";
import { toast } from "sonner";
import z from "zod/v4";
import { PaginationState, SortingState } from "@tanstack/react-table";
import type { Draft } from "immer";
import { useImmerReducer } from "use-immer";
import IBaseEntity from "@/app/model/IBaseEntity";
import { DetailMode, DetailState } from "@/app/ui/details/detail-actions";
import Authority from "@/app/model/Authority";
import useAuth from "./use-auth";
import { introspect } from "@/lib/graphql-utils";

export type FormActionHandler<V extends FieldValues> = (command: string, fieldValues?: V) => void

export type PageConfiguration<T extends ITrackedEntity, V extends FieldValues, I extends BaseEntityInput, F extends TrackedEntityQueryFilter> = {
  recordKind: RecordKind // TODO: do we need to constrain this to trackable/linkable types?
  schema?: z.ZodObject<any>
  manualPagination: boolean
  manualSorting: boolean
  readQuery: DocumentNode
  createMutation?: DocumentNode
  updateMutation?: DocumentNode
  deleteMutation?: DocumentNode
  createFieldValues: (record?: T) => V
  createInput?: (fieldValues: V, id?: string) => I
  prepareFilter?: (filter: F) => void
  preparePage?: (page?: IPage<T>) => IPage<T> | undefined
  findRecord?: (records?: T[], id?: string | null) => T | undefined
}

export type PageLogic<T extends ITrackedEntity, V extends FieldValues> = {
  search: SearchSettings
  setSearch: Dispatch<SetStateAction<SearchSettings>>
  pagination: PaginationState
  setPagination: Dispatch<SetStateAction<PaginationState>>
  sorting: SortingState,
  setSorting: Dispatch<SetStateAction<SortingState>>,
  loading: boolean
  page?: IPage<T>
  selectedRecord?: T
  handleRowSelectionChange: (recordId?: string) => void
  state: DetailState
  setMode: Dispatch<SetStateAction<DetailMode>>
  form: UseFormReturn<V, any, V>
  handleFormAction: FormActionHandler<V>
}

type ReducerArg<T> = {
  command: string
  page: IPage<T>
  record?: T
}

function createDetailState(
  hasAuthority: (authority: Authority) => boolean,
  mode: DetailMode
): DetailState {

  const allowCreate = hasAuthority("CRE")
  const allowEdit = hasAuthority("UPD")
  const allowDelete = hasAuthority("DEL")
  const allowUpdate = allowCreate || allowEdit
  const allowLink = hasAuthority("LNK")
  const allowRead = hasAuthority("REA")
  const updating = mode == "create" || mode == "edit"

  return {
    mode,
    allowCreate,
    allowEdit,
    allowDelete,
    allowUpdate,
    allowLink,
    allowRead,
    updating,
  }
}

const defaultFindRecord = <T extends IBaseEntity>(records?: T[], id?: string | null) => records?.find(r => r?.id === id)

const dummyPage = {
  hasContent: false,
  isEmpty: true,
  number: 0,
  size: 0,
  numberOfElements: 0,
  totalPages: 0,
  totalElements: 0,
  isFirst: true,
  isLast: true,
  hasNext: false,
  hasPrevious: false,
  content: [],
}

// function recomputePageFields<T extends IBaseEntity>(page: IPage<T>) {
//   page.hasContent = page.content.length != 0
//   page.isEmpty = page.content.length == 0
//   // page.number = 0,
//   // page.size = 0,
//   page.numberOfElements = page.content.length,
//   // page.totalPages = 0,
//   page.totalElements = 0,
//   // page.isFirst = true,
//   // page.isLast = true,
//   // page.hasNext = false,
//   // page.hasPrevious = false,
// }

export default function usePageLogic<
    T extends ITrackedEntity,
    V extends FieldValues,
    I extends BaseEntityInput,
    F extends TrackedEntityQueryFilter
  >(
    {
      recordKind,
      schema,
      manualPagination,
      manualSorting,
      readQuery,
      createMutation,
      updateMutation,
      deleteMutation,
      createFieldValues,
      createInput,
      prepareFilter,
      preparePage,
      findRecord,
    }
    : PageConfiguration<T, V, I, F>
  ) : PageLogic<T, V> {

  const {hasAuthority} = useAuth()
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({advancedSearch: false, showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext[recordKind]?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 })
  const [sorting, setSorting] = useState<SortingState>([])
  const [mode, setMode] = useState<DetailMode>("view")
  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])

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
        const linkFilterIdProperty = getLinkFilterIdProperty(recordKind, masterLinkContext.masterRecordKind)
        if (linkFilterIdProperty)
          mlFilter[linkFilterIdProperty] = masterLinkContext.masterRecordId
      }
    }
    prepareFilter?.(filter)
    console.log(`${recordKind}s effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search, masterLinkContext])

  const pageSort = useMemo(() => {
    const pageSort : PageableInput = manualPagination && pagination
      ? {
        pageNumber: pagination.pageIndex,
        pageSize: pagination.pageSize,
        sort: manualSorting ? sorting ? { orders: [] } : undefined : undefined,
      }
      : {}
    if (manualSorting && sorting) {
      pageSort.sort ??= { orders: [] }
      for (let s of sorting) {
        pageSort.sort?.orders.push({
          property: s.id,
          direction: s.desc ? "DESC" : undefined
        })
      }
    }
    console.log(`${recordKind}s effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [manualPagination && pagination, manualSorting && sorting])

  const [readFieldName] = useMemo(() => introspect(readQuery, OperationTypeNode.QUERY), [readQuery])
  const [createFieldName, createVarName] = useMemo(() => introspect(createMutation, OperationTypeNode.MUTATION), [createMutation])
  const [updateFieldName, updateVarName] = useMemo(() => introspect(updateMutation, OperationTypeNode.MUTATION), [updateMutation])
  const [deleteFieldName, deleteVarName] = useMemo(() => introspect(deleteMutation, OperationTypeNode.MUTATION), [deleteMutation])

  const readResult = useQuery(
    readQuery,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )
  const [createOp, createResult] = createMutation ? useMutation(createMutation, { refetchQueries: [readQuery] }) : []
  const [updateOp, updateResult] = updateMutation ? useMutation(updateMutation, { refetchQueries: [readQuery] }) : []
  const [deleteOp, deleteResult] = deleteMutation ? useMutation(deleteMutation, { refetchQueries: [readQuery] }) : []

  const loading = (readResult.loading || createResult?.loading || updateResult?.loading || deleteResult?.loading) ?? false
  const error = readResult.error || createResult?.error || updateResult?.error || deleteResult?.error

  if (error) {
    // TODO: display user-friendly error notification
    toast.error(`Operation failed:\n\n${error.message}`)
    console.error(error)
  }

  let rawPage = (readFieldName ? readResult.data?.[readFieldName] : undefined) as unknown as IPage<T> | undefined
  rawPage = useMemo(() => preparePage?.(rawPage) ?? rawPage, [preparePage, rawPage])

  const reducer = useCallback((draft: Draft<IPage<T>>, action: ReducerArg<T>) => {
    // TODO: if provided, call getSubRows as passed to DataTable to find the array and index to update/delete.
    // TODO: refactor to allow a custom reducer to be passed in (needed for the hierarchical Topics page).
    const idx = action.page?.content.findIndex(r => r.id === action.record?.id) ?? -1
    // N.B. the following 'as Draft<T>' typecasts are needed to silence the TypeScript error
    // "Argument of type 'T' is not assignable to parameter of type 'Draft<T> ts(2345)'.
    // However, this approach is only valid because the record will not be further mutated.
    switch (action.command) {
      case "init":
        if (action.page)
          Object.assign(draft, action.page)
        break;
      case "create":
        draft.content.push(action.record as Draft<T>)
        // recomputePageFields(draft)
        break
      case "update":
        if (idx != -1 && action.record)
          draft.content.splice(idx, 1, action.record as Draft<T>)
        break
      case "delete":
        if (idx != -1 && action.record) {
          // On status mismatch, remove the record from the page content.
          if (search.status && search.status !== action.record?.status) {
            draft.content.splice(idx, 1)
          } else {
            // Otherwise, replace the record.
            draft.content.splice(idx, 1, action.record as Draft<T>)
            // Alternatively:
            // Object.assign(draft.content[idx], action.record)
          }
        }
        break
    }
  }, [search.status])
  const [page, dispatch] = useImmerReducer<IPage<T>, ReducerArg<T>>(reducer, rawPage ?? dummyPage)
  const getSelectedRecord = useCallback((id?: string) => {
    return (findRecord ?? defaultFindRecord)(page?.content, id)
  }, [findRecord, page])
  const selectedRecord = getSelectedRecord(selectedRecordId)

  const origFieldValues = useMemo(() => createFieldValues(selectedRecord), [createFieldValues, selectedRecord])
  const form = useForm<V>({
    resolver: schema && standardSchemaResolver(schema),
    mode: "onChange",
    values: origFieldValues,
  })
  const handleFormAction = useCallback((command: string, fieldValues?: V) => {
    // console.log(`.handleFormAction: command = ${command}, mode = ${mode}`)
    // TODO: update Apollo cache?
    switch (command) {
      case "new":
        form?.reset(createFieldValues())
        break
      case "create":
        if (fieldValues) {
          createOp?.({
            variables: {
              [createVarName]: createInput?.(fieldValues)
            },
            onCompleted: (data, clientOptions) => {
              const record = data[createFieldName]
              setSelectedRecordId(record.id)
              // FIXME: this doesn't set the selection in the data table.
              selectedRecordsContext.setSelectedRecord(selectedRecordsContext, recordKind, record)
              setMode("view")
            },
          })
        }
        break
      case "update":
        if (fieldValues) {
          updateOp?.({
            variables: {
              [updateVarName]: createInput?.(fieldValues, selectedRecordId)
            },
            onCompleted: () => {
              setMode("view")
            },
          })
        }
        break
      case "delete":
        deleteOp?.({
          variables: {
            [deleteVarName]: selectedRecordId
          }
        })
      case "reset":
        form?.reset(fieldValues ?? origFieldValues)
        break
      default:
        console.error(`Unknown command '${command}'`)
    }
    // Dependencies assume (reasonably) that createOp, updateOp, deleteOp, createVarName, updateVarName, deleteVarName
    // will never change during the page lifetime.
  }, [selectedRecordId, form, origFieldValues, createInput])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    if (mode !== "view") {
      toast.warning("Cannot change selection while editing a record. Use the Cancel or Save button in the details panel below.")
      setSelectedRecordId(selectedRecordId)
      return
    }
    setSelectedRecordId(recordId)
    const record = getSelectedRecord(recordId)
    form?.reset(createFieldValues(record))
  }, [form, mode, setSelectedRecordId, getSelectedRecord])

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    // console.log(`${recordKind}s effect: search = ${JSON.stringify(search)}`)
    readResult.refetch({
      filter,
      pageSort
    });
  }, [filter, manualPagination && pageSort]);

  // When a new page is available, dispatch the reducer.
  useEffect(() => {
    if (rawPage)
      dispatch({command: "init", page: rawPage});
  }, [rawPage, dispatch]);

  // console.log(`${settings.recordKind}s() page: ${JSON.stringify(page)})`)

  const pageLogic = useMemo<PageLogic<T, V>>(() => {
    return {
      search,
      setSearch,
      pagination,
      setPagination,
      sorting,
      setSorting,
      loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      state,
      setMode,
      form,
      handleFormAction,
    }}, [
      search,
      setSearch,
      pagination,
      setPagination,
      sorting,
      setSorting,
      readResult.loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      state,
      setMode,
      form,
      handleFormAction,
  ])

  return pageLogic
}