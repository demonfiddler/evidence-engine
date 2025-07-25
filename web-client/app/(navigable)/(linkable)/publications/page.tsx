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

import { BeakerIcon } from '@heroicons/react/24/outline';
import PublicationDetails from "@/app/ui/details/publication-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/publication-columns"
import Publication from "@/app/model/Publication";
import { toDate, toInteger, toIsoString } from "@/lib/utils";
import { FormProvider } from "react-hook-form"
import { PublicationFieldValues, PublicationKind, PublicationSchema as PublicationSchema } from "@/app/ui/validators/publication";
import { QUERY_PUBLICATIONS } from "@/lib/graphql-queries";
import usePageLogic from "@/hooks/use-page-logic";
import { LinkableEntityQueryFilter } from '@/app/model/schema';

function copyToForm(publication?: Publication) {
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

function copyFromForm(publication: Publication, fieldValues: PublicationFieldValues) {
  publication.title = fieldValues.title ?? null
  publication.authors = fieldValues.authors ?? null
  publication.journal = { id: fieldValues.journalId }
  publication.kind = fieldValues.kind as unknown as PublicationKind ?? null
  publication.date = toIsoString(fieldValues.date) ?? null
  publication.year = toInteger(fieldValues.year) ?? null
  publication.abstract = fieldValues.abstract ?? null
  publication.notes = fieldValues.notes ?? null
  publication.peerReviewed = !!fieldValues.peerReviewed
  publication.doi = fieldValues.doi ?? null
  publication.isbn = fieldValues.isbn ?? null
  publication.url = fieldValues.url ?? null
  publication.cached = !!fieldValues.cached
  publication.accessed = fieldValues.accessed
}

export default function Publications() {
  const {
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
    form,
    handleFormAction,
  } = usePageLogic<Publication, LinkableEntityQueryFilter, PublicationFieldValues>({
    recordKind: "Publication",
    schema: PublicationSchema,
    manualPagination: true,
    manualSorting: true,
    listQuery: QUERY_PUBLICATIONS,
    copyToForm,
    copyFromForm,
  })

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
        loading={loading}
        manualPagination={true}
        pagination={pagination}
        onPaginationChange={setPagination}
        manualSorting={true}
        sorting={sorting}
        onSortingChange={setSorting}
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