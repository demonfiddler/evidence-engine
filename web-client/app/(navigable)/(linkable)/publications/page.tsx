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
import { BeakerIcon } from '@heroicons/react/24/outline';

import PublicationDetails from "@/app/ui/details/publication-details";
import DataTable from "@/app/ui/data-table/data-table";

import { columns, columnVisibility } from "@/app/ui/tables/publication-columns"
import IPage from "@/app/model/IPage";
import Publication from "@/app/model/Publication";
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";
import { MutationAction, FormAction, toDate, toInteger, toIsoString, SearchSettings } from "@/lib/utils";
import { useForm, FormProvider } from "react-hook-form"
import { PublicationFormFields, PublicationKind, PublicationSchema as PublicationSchema } from "@/app/ui/validators/publication";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { LinkableEntityQueryFilter } from "@/app/model/schema";
import { useQuery } from "@apollo/client";
import { QUERY_PUBLICATIONS } from "@/lib/graphql-queries";
import { toast } from "sonner";

// export const metadata: Metadata = {
//   title: "Publications",
//   description: "Evidenced publications of scientific fact",
// };

function copyToForm(publication?: Publication) {
  // console.log(`Publications.copyToForm`)
  return {
    title: publication?.title ?? '',
    authors: publication?.authors ?? '',
    journalId: publication?.journal?.id ?? '',
    kind: publication?.kind as unknown as PublicationKind ?? '',
    date: toDate(publication?.date),
    year: publication?.year ?? '',
    abstract: publication?.abstract ?? '',
    notes: publication?.notes ?? '',
    peerReviewed: !!publication?.peerReviewed,
    doi: publication?.doi ?? '',
    isbn: publication?.isbn ?? '',
    url: publication?.url ?? '',
    cached: !!publication?.cached,
    accessed: toDate(publication?.accessed),
  }
}

function copyFromForm(publication: Publication, formValue: PublicationFormFields) {
  // console.log(`Publications.copyFromForm: formValue = ${JSON.stringify(formValue)}`)
  publication.title = formValue.title ?? null
  publication.authors = formValue.authors ?? null
  publication.journal = { id: formValue.journalId }
  publication.kind = formValue.kind as unknown as PublicationKind ?? null
  publication.date = toIsoString(formValue.date) ?? null
  publication.year = toInteger(formValue.year) ?? null
  publication.abstract = formValue.abstract ?? null
  publication.notes = formValue.notes ?? null
  publication.peerReviewed = !!formValue.peerReviewed
  publication.doi = formValue.doi ?? null
  publication.isbn = formValue.isbn ?? null
  publication.url = formValue.url ?? null
  publication.cached = !!formValue.cached
  publication.accessed = formValue.accessed
}

export default function Publications() {
  // All functions declared here are wrapped in calls to useCallback() in order to avoid re-rendering loop errors.
  // Similarly, objects passed to hooks are memoized in order to avoid passing different identical objects to hooks.
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({advancedSearch: false, showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Publication?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });
/*  const pageReducer = useCallback((draft: IPage<Publication>, action: MutationAction<FormAction, PublicationFormFields>) => {
    // console.log(`Publications.pageReducer: action = ${JSON.stringify(action)}`)
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const publication : Publication = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(publication.id)
        copyFromForm(publication, action.value)
        draft.content.push(publication)
        break
      case "update":
        if (idx != -1) {
          const publication = draft.content[idx]
          copyFromForm(publication, action.value)
          draft.content.splice(idx, 1, publication)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1) {
          draft.content.splice(idx, 1)
          setSelectedRecordId(undefined)
        }
        break
    }
  }, [selectedRecordId, setSelectedRecordId])*/
  // const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Publication>)
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
          case "Person":
            filter.toEntityId = masterLinkContext.masterRecordId
            break
          case "Claim":
          case "Quotation":
            filter.fromEntityId = masterLinkContext.masterRecordId
            break
        }
      }
    }
    console.log(`Publications effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search, masterLinkContext])

  const pageSort = useMemo(() => {
    const pageSort = {
      pageNumber: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    console.log(`Publications effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [pagination])

  const result = useQuery(
    QUERY_PUBLICATIONS,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    console.log(`Publications effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);

  console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}, result.data = ${JSON.stringify(result.data)}`)
  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}`)
  const page = result.data?.publications as IPage<Publication>

  const getSelectedRecord = useCallback((id?: string) => page?.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<PublicationFormFields>({
    resolver: standardSchemaResolver(PublicationSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: PublicationFormFields) => {
    // console.log(`Publications.handleFormAction: command="${command}" action = ${JSON.stringify(formValue)}`)
    switch (command) {
      case "create":
        // TODO: invoke mutation: createPublication
        break
      case "update":
        // TODO: invoke mutation: updatePublication
        break
      case "delete":
        // TODO: invoke mutation: deletePublication
        break
        // OLD: pageDispatch({command: command, value: formValue})
      case "reset":
        form.reset(origFormValue)
        break
    }
  }, [form, /*pageDispatch, */selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    // console.log(`Publications.handleRowSelectionChange: recordId = ${recordId}`)
    setSelectedRecordId(recordId)
    const publication = getSelectedRecord(recordId)
    form.reset(copyToForm(publication))
  }, [setSelectedRecordId, getSelectedRecord, form])

  if (result.error) {
    toast.error(`Fetch error:\n\n${JSON.stringify(result.error)}`)
    console.error(result.error)
  }

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <BeakerIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Publications</h1>
      </div>
      <DataTable<Publication, unknown>
        recordKind="Publication"
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
        <PublicationDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}

// Publications.whyDidYouRender = true;