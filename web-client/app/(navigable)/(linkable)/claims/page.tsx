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
import { columns } from "@/app/ui/tables/claim-columns"
import Claim from "@/app/model/Claim"
import { toDate, toIsoDateString } from "@/lib/utils"
import { FormProvider } from "react-hook-form"
import { ClaimSchema, ClaimFieldValues } from "@/app/ui/validators/claim"
import { CREATE_CLAIM, DELETE_CLAIM, READ_CLAIMS, UPDATE_CLAIM } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { ClaimInput, LinkableEntityQueryFilter } from "@/app/model/schema"
import LinkableEntityTableFilter from "@/app/ui/filter/linkable-entity-table-filter"
import useLinkableEntityQueryFilter from "@/hooks/use-linkable-entity-query-filter"

function createFieldValues(claim?: Claim) : ClaimFieldValues {
  return {
    text: claim?.text ?? '',
    date: toDate(claim?.date),
    notes: claim?.notes ?? ''
  }
}

function createInput(fieldValues: ClaimFieldValues, id?: string) : ClaimInput {
  return {
    id,
    text: fieldValues.text,
    date: toIsoDateString(fieldValues.date) || null,
    notes: fieldValues.notes || null,
  }
}

export default function Claims() {
  const filterLogic = useLinkableEntityQueryFilter()
  const {
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    state,
    setMode,
    form,
    handleFormAction,
    refetch,
    loadingPathWithSearchParams,
  } = usePageLogic<Claim, ClaimFieldValues, ClaimInput, LinkableEntityQueryFilter>({
    recordKind: "Claim",
    schema: ClaimSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_CLAIMS,
    createMutation: CREATE_CLAIM,
    updateMutation: UPDATE_CLAIM,
    deleteMutation: DELETE_CLAIM,
    createFieldValues,
    createInput,
    filterLogic,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // // When component is unmounted, flush application state to session storage.
  // useEffect(() => {
  //   return () => {
  //     console.log("Claims effect cleanup")
  //     storeAppState()
  //   }
  // }, [])

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
        page={page}
        state={state}
        loading={loading}
        filterComponent={LinkableEntityTableFilter}
        manualPagination={true}
        manualSorting={true}
        onRowSelectionChange={handleRowSelectionChange}
        refetch={refetch}
        loadingPathWithSearchParams={loadingPathWithSearchParams}
      />
      <FormProvider {...form}>
        <ClaimDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}
