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

import { NewspaperIcon } from '@heroicons/react/24/outline';
import JournalDetails from "@/app/ui/details/journal-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/journal-columns"
import Journal from "@/app/model/Journal";
import { FormProvider } from "react-hook-form"
import { JournalSchema, JournalFieldValues } from "@/app/ui/validators/journal";
import { QUERY_JOURNALS } from "@/lib/graphql-queries";
import usePageLogic from "@/hooks/use-page-logic";
import { TrackedEntityQueryFilter } from '@/app/model/schema';

function copyToForm(journal?: Journal) {
  return {
    title: journal?.title ?? '',
    abbreviation: journal?.abbreviation ?? '',
    url: journal?.url ?? '',
    issn: journal?.issn ?? '',
    publisherId: journal?.publisher?.id ?? '',
    notes: journal?.notes ?? ''
  }
}

function copyFromForm(journal: Journal, fieldValues: JournalFieldValues) {
  journal.title = fieldValues.title ?? null,
  journal.abbreviation = fieldValues.abbreviation ?? null,
  journal.url = fieldValues.url ?? null,
  journal.issn = fieldValues.issn ?? null,
  journal.publisher = fieldValues.publisherId ? {id: fieldValues.publisherId} : null,
  journal.notes = fieldValues.notes ?? null
}

export default function Journals() {
  const {
    search,
    setSearch,
    pagination,
    setPagination,
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    form,
    handleFormAction,
  } = usePageLogic<Journal, TrackedEntityQueryFilter, JournalFieldValues>({
    recordKind: "Journal",
    schema: JournalSchema,
    listQuery: QUERY_JOURNALS,
    copyToForm,
    copyFromForm,
  })

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <NewspaperIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Journals</h1>
      </div>
      <DataTable<Journal, unknown>
        recordKind="Journal"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        loading={loading}
        pagination={pagination}
        onPaginationChange={setPagination}
        search={search}
        onSearchChange={setSearch}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <JournalDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
