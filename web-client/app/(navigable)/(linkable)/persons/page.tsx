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

import PersonDetails from "@/app/ui/details/person-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/person-columns"
import Person from "@/app/model/Person"
import { FormProvider } from "react-hook-form"
import { PersonFieldValues, PersonSchema as PersonSchema } from "@/app/ui/validators/person"
import { CREATE_PERSON, DELETE_PERSON, READ_PERSONS, UPDATE_PERSON } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { LinkableEntityQueryFilter, PersonInput } from '@/app/model/schema'
import LinkableEntityTableFilter from '@/app/ui/filter/linkable-entity-table-filter'
import useLinkableEntityQueryFilter from '@/hooks/use-linkable-entity-query-filter'
import { LoggerEx, page } from '@/lib/logger'
import { UserIcon } from 'lucide-react'

const logger = new LoggerEx(page, "[Persons] ")

function createFieldValues(person?: Person) : PersonFieldValues {
  return {
    rating: person?.rating ?? 0,
    title: person?.title ?? '',
    firstName: person?.firstName ?? '',
    nickname: person?.nickname ?? '',
    prefix: person?.prefix ?? '',
    lastName: person?.lastName ?? '',
    suffix: person?.suffix ?? '',
    alias: person?.alias ?? '',
    notes: person?.notes ?? '',
    qualifications: person?.qualifications ?? '',
    country: person?.country ?? '',
    checked: person?.checked ?? false,
    published: person?.published ?? false
  }
}

function createInput(fieldValues: PersonFieldValues, id?: string) : PersonInput {
  return {
    id,
    rating: fieldValues.rating || null,
    title: fieldValues.title || null,
    firstName: fieldValues.firstName,
    nickname: fieldValues.nickname || null,
    prefix: fieldValues.prefix || null,
    lastName: fieldValues.lastName,
    suffix: fieldValues.suffix || null,
    alias: fieldValues.alias || null,
    notes: fieldValues.notes || null,
    qualifications: fieldValues.qualifications || null,
    country: fieldValues.country || null,
    checked: fieldValues.checked ?? false,
    published: fieldValues.published ?? false
  }
}

export default function Persons() {
  logger.debug("render")

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
  } = usePageLogic<Person, PersonFieldValues, PersonInput, LinkableEntityQueryFilter>({
    recordKind: "Person",
    schema: PersonSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_PERSONS,
    createMutation: CREATE_PERSON,
    updateMutation: UPDATE_PERSON,
    deleteMutation: DELETE_PERSON,
    createFieldValues,
    createInput,
    filterLogic,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <UserIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Persons</h1>
      </div>
      <DataTable<Person, unknown>
        recordKind="Person"
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
        <PersonDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}

Persons.whyDidYouRender = true