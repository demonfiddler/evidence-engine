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

'use client'

// import type { Metadata } from "next";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useImmerReducer } from "use-immer";
import { EnvelopeOpenIcon } from '@heroicons/react/24/outline';

import DeclarationDetails from "@/app/ui/details/declaration-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/declaration-columns"
import IPage from "@/app/model/IPage";
import Declaration from "@/app/model/Declaration";
import { DeclarationSchema, DeclarationFormFields, DeclarationKind } from "@/app/ui/validators/declaration";
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context";
import { MutationAction, FormAction, toDate, toInteger, toIsoString, SearchSettings } from "@/lib/utils";
import { FormProvider, useForm } from "react-hook-form";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { LinkableEntityQueryFilter } from "@/app/model/schema";
import { QUERY_DECLARATIONS } from "@/lib/graphql-queries";
import { useQuery } from "@apollo/client";
import { toast } from "sonner";

// export const metadata: Metadata = {
//   title: "Declarations",
//   description: "Public declarations, open letters & petitions",
// };

function copyToForm(declaration?: Declaration) {
  return {
    kind: declaration?.kind as DeclarationKind ?? '',
    date: toDate(declaration?.date),
    title: declaration?.title ?? '',
    country: declaration?.country ?? '',
    url: declaration?.url ?? '',
    cached: declaration?.cached ?? false,
    signatories: declaration?.signatories ?? '',
    signatoryCount: declaration?.signatoryCount ?? '',
    notes: declaration?.notes ?? ''
  }
}

function copyFromForm(declaration: Declaration, formValue: DeclarationFormFields) {
  declaration.kind = formValue.kind
  declaration.title = formValue.title
  declaration.date = toIsoString(formValue.date)
  declaration.country = formValue.country ?? null
  declaration.url = formValue.url ?? null
  declaration.cached = !!formValue.cached
  declaration.signatories = formValue.signatories ?? null
  declaration.signatoryCount = toInteger(formValue.signatoryCount)
  declaration.notes = formValue.notes ?? null
}

export default function Declarations() {
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({advancedSearch: false, showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Declaration?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });
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
  const filter = useMemo(() => {
    const filter: LinkableEntityQueryFilter = {
      status: search.status ? [search.status] : undefined,
      text: search.text,
    }
    if (search.text)
      filter.advancedSearch = search.advancedSearch
    if (search.showOnlyLinkedRecords) {
      if (masterLinkContext.masterTopicId) {
        filter.topicId = masterLinkContext.masterTopicId
        filter.recursive = true
      }
      if (masterLinkContext.masterRecordId) {
        switch (masterLinkContext.masterRecordKind) {
          case "Claim":
            filter.fromEntityId = masterLinkContext.masterRecordId
            break
          case "Person":
          case "Quotation":
            filter.toEntityId = masterLinkContext.masterRecordId
            break
        }
      }
    }
    console.log(`Declarations effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search, masterLinkContext])

  const pageSort = useMemo(() => {
    const pageSort = {
      pageNumber: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    console.log(`Declarations effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [pagination])

  const result = useQuery(
    QUERY_DECLARATIONS,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    console.log(`Declarations effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);

  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}, result.data = ${JSON.stringify(result.data)}`)
  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}`)
  const page = result.data?.declarations as IPage<Declaration>

  // const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Declaration>)
  const getSelectedRecord = useCallback((id?: string) => page?.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<DeclarationFormFields>({
    resolver: standardSchemaResolver(DeclarationSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: DeclarationFormFields) => {
    switch (command) {
      case "create":
        // TODO: invoke mutation: createDeclaration
        break
      case "update":
        // TODO: invoke mutation: updateDeclaration
        break
      case "delete":
        // TODO: invoke mutation: deleteDeclaration
        break
        // OLD: pageDispatch({command: command, value: formValue})
      case "reset":
        form.reset(origFormValue)
        break
    }
  }, [form, /*pageDispatch, */selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const declaration = getSelectedRecord(recordId)
    form.reset(copyToForm(declaration))
  }, [setSelectedRecordId, getSelectedRecord, form])

  if (result.error) {
    toast.error(`Fetch error:\n\n${JSON.stringify(result.error)}`)
    console.error(result.error)
  }

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <EnvelopeOpenIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Declarations</h1>
      </div>
      <DataTable<Declaration, unknown>
        recordKind="Declaration"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        loading={result.loading}
        pagination={pagination}
        onPaginationChange={setPagination}
        search={search}
        onSearchChange={setSearch}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <DeclarationDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
