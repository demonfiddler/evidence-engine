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

import { ExclamationCircleIcon } from "@heroicons/react/24/outline"

import ClaimDetails from "@/app/ui/details/claim-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns, columnVisibility } from "@/app/ui/tables/claim-columns"
import Claim from "@/app/model/Claim";
import { toDate, toIsoString } from "@/lib/utils"
import { FormProvider } from "react-hook-form"
import { ClaimSchema, ClaimFieldValues } from "@/app/ui/validators/claim";
import { QUERY_CLAIMS } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { LinkableEntityQueryFilter } from "@/app/model/schema"

function copyToForm(claim?: Claim) {
  return {
    text: claim?.text ?? '',
    date: toDate(claim?.date) ?? '',
    notes: claim?.notes ?? ''
  }
}

function copyFromForm(claim: Claim, fieldValues: ClaimFieldValues) {
  claim.text = fieldValues.text
  claim.date = toIsoString(fieldValues.date)
  claim.notes = fieldValues.notes ?? null
}

export default function Claims() {
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
  } = usePageLogic<Claim, LinkableEntityQueryFilter, ClaimFieldValues>({
    recordKind: "Claim",
    schema: ClaimSchema,
    manualPagination: true,
    manualSorting: true,
    listQuery: QUERY_CLAIMS,
    copyToForm,
    copyFromForm,
  })

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ExclamationCircleIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Claims</h1>
      </div>
      <DataTable<Claim, unknown>
        recordKind="Claim"
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
        <ClaimDetails
          record={selectedRecord}
          // mode={mode}
          // setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}
