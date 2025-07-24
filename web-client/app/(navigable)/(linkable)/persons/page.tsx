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

import { UserIcon } from '@heroicons/react/24/outline';
import PersonDetails from "@/app/ui/details/person-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/person-columns"
import Person from "@/app/model/Person";
import { toInteger } from "@/lib/utils";
import { FormProvider } from "react-hook-form"
import { PersonFieldValues, PersonSchema as PersonSchema } from "@/app/ui/validators/person";
import { QUERY_PERSONS } from "@/lib/graphql-queries";
import usePageLogic from "@/hooks/use-page-logic";
import { LinkableEntityQueryFilter } from '@/app/model/schema';

function copyToForm(person?: Person) {
  return {
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
    rating: person?.rating ?? '',
    checked: person?.checked ?? false,
    published: person?.published ?? false
  }
}

function copyFromForm(person: Person, fieldValues: PersonFieldValues) {
  person.title = fieldValues.title ?? null,
  person.firstName = fieldValues.firstName ?? null,
  person.nickname = fieldValues.nickname ?? null,
  person.prefix = fieldValues.prefix ?? null,
  person.lastName = fieldValues.lastName ?? null,
  person.suffix = fieldValues.suffix ?? null,
  person.alias = fieldValues.alias ?? null,
  person.notes = fieldValues.notes ?? null,
  person.qualifications = fieldValues.qualifications ?? null,
  person.country = fieldValues.country ?? null,
  person.rating = toInteger(fieldValues.rating) ?? 0,
  person.checked = fieldValues.checked ?? false,
  person.published = fieldValues.published ?? false
}

export default function Persons() {
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
  } = usePageLogic<Person, LinkableEntityQueryFilter, PersonFieldValues>({
    recordKind: "Person",
    schema: PersonSchema,
    manualPagination: true,
    manualSorting: true,
    listQuery: QUERY_PERSONS,
    copyToForm,
    copyFromForm,
  })

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
        <PersonDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}