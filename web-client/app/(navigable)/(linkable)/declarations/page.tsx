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

import { EnvelopeOpenIcon } from '@heroicons/react/24/outline';

import DeclarationDetails from "@/app/ui/details/declaration-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/declaration-columns"
import Declaration from "@/app/model/Declaration";
import { DeclarationSchema, DeclarationFieldValues, DeclarationKind } from "@/app/ui/validators/declaration";
import { toDate, toIsoDateString } from "@/lib/utils";
import { FormProvider } from "react-hook-form";
import { CREATE_DECLARATION, DELETE_DECLARATION, READ_DECLARATIONS, UPDATE_DECLARATION } from "@/lib/graphql-queries";
import usePageLogic from "@/hooks/use-page-logic";
import { DeclarationInput, LinkableEntityQueryFilter } from '@/app/model/schema';

function createFieldValues(declaration?: Declaration) : DeclarationFieldValues {
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

function createInput(fieldValues: DeclarationFieldValues, id?: string) : DeclarationInput {
  return {
    id,
    kind: fieldValues.kind,
    title: fieldValues.title,
    date: toIsoDateString(fieldValues.date) ?? new Date(), // NOTE: the default value shouldn't apply in practice
    country: fieldValues.country || null,
    url: fieldValues.url || null,
    // cached: !!fieldValues.cached,
    signatories: fieldValues.signatories || null,
    // signatoryCount: toInteger(fieldValues.signatoryCount),
    notes: fieldValues.notes || null,
  }
}

export default function Declarations() {
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
    state,
    setMode,
    form,
    handleFormAction,
  } = usePageLogic<Declaration, DeclarationFieldValues, DeclarationInput, LinkableEntityQueryFilter>({
    recordKind: "Declaration",
    schema: DeclarationSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_DECLARATIONS,
    createMutation: CREATE_DECLARATION,
    updateMutation: UPDATE_DECLARATION,
    deleteMutation: DELETE_DECLARATION,
    createFieldValues,
    createInput,
  })

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
        state={state}
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
        <DeclarationDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
