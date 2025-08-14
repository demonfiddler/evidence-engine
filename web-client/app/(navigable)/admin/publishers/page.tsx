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

import { BuildingOfficeIcon } from '@heroicons/react/24/outline'
import PublisherDetails from "@/app/ui/details/publisher-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/publisher-columns"
import Publisher from "@/app/model/Publisher"
import { toInteger } from "@/lib/utils"
import { FormProvider } from "react-hook-form"
import { PublisherSchema, PublisherFieldValues } from "@/app/ui/validators/publisher"
import { CREATE_PUBLISHER, DELETE_PUBLISHER, READ_PUBLISHERS, UPDATE_PUBLISHER } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { PublisherInput, TrackedEntityQueryFilter } from '@/app/model/schema'
import LinkableEntityTableFilter from '@/app/ui/filter/linkable-entity-table-filter'

function createFieldValues(publisher?: Publisher) : PublisherFieldValues {
  return {
    name: publisher?.name ?? '',
    location: publisher?.location ?? '',
    country: publisher?.country ?? '',
    url: publisher?.url ?? '',
    journalCount: publisher?.journalCount ?? '',
  }
}

function createInput(fieldValues: PublisherFieldValues, id?: string) : PublisherInput {
  return {
    id,
    name: fieldValues.name,
    location: fieldValues.location || null,
    country: fieldValues.country || null,
    url: fieldValues.url || null,
    journalCount: toInteger(fieldValues.journalCount),
  }
}

export default function Publishers() {
  const {
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    state,
    setMode,
    form,
    handleFormAction,
  } = usePageLogic<Publisher, PublisherFieldValues, PublisherInput, TrackedEntityQueryFilter>({
    recordKind: "Publisher",
    schema: PublisherSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_PUBLISHERS,
    createMutation: CREATE_PUBLISHER,
    updateMutation: UPDATE_PUBLISHER,
    deleteMutation: DELETE_PUBLISHER,
    createFieldValues,
    createInput,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <BuildingOfficeIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Publishers</h1>
      </div>
      <DataTable<Publisher, unknown>
        recordKind="Publisher"
        defaultColumns={columns}
        page={page}
        state={state}
        loading={loading}
        filterComponent={LinkableEntityTableFilter}
        manualPagination={true}
        manualSorting={true}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <PublisherDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  );
}
