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
import { UserIcon } from '@heroicons/react/24/outline';

import PersonDetails from "@/app/ui/details/person-details";
import DataTable from "@/app/ui/data-table/data-table";

import { columns, columnVisibility } from "@/app/ui/tables/person-columns"
import IPage from "@/app/model/IPage";
import Person from "@/app/model/Person";
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";
import { MutationAction, FormAction, toInteger, SearchSettings } from "@/lib/utils";
import { useForm, FormProvider } from "react-hook-form"
import z from "zod/v4"
import { PersonFormFields, PersonSchema as PersonSchema } from "@/app/ui/validators/person";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { QUERY_PERSONS } from "@/lib/graphql-queries";
import { useQuery } from "@apollo/client";
import { LinkableEntityQueryFilter } from "@/app/model/schema";
import { toast } from "sonner";

// export const metadata: Metadata = {
//   title: "Persons",
//   description: "Scientists, academics and researchers",
// };

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

function copyFromForm(person: Person, formValue: PersonFormFields) {
  person.title = formValue.title ?? null,
  person.firstName = formValue.firstName ?? null,
  person.nickname = formValue.nickname ?? null,
  person.prefix = formValue.prefix ?? null,
  person.lastName = formValue.lastName ?? null,
  person.suffix = formValue.suffix ?? null,
  person.alias = formValue.alias ?? null,
  person.notes = formValue.notes ?? null,
  person.qualifications = formValue.qualifications ?? null,
  person.country = formValue.country ?? null,
  person.rating = toInteger(formValue.rating) ?? 0,
  person.checked = formValue.checked ?? false,
  person.published = formValue.published ?? false
}

export default function Persons() {
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({advancedSearch: false, showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Person?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });
/*  const pageReducer = useCallback((draft: IPage<Person>, action: MutationAction<FormAction, PersonFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const person : Person = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(person.id)
        copyFromForm(person, action.value)
        draft.content.push(person)
        break
      case "update":
        if (idx != -1) {
          const person = draft.content[idx]
          copyFromForm(person, action.value)
          draft.content.splice(idx, 1, person)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
    }
  }, [selectedRecordId, setSelectedRecordId])*/

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
          case "Declaration":
          case "Publication":
          case "Quotation":
            filter.fromEntityId = masterLinkContext.masterRecordId
            break
        }
      }
    }
    console.log(`Persons effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search, masterLinkContext])

  const pageSort = useMemo(() => {
    const pageSort = {
      pageNumber: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    console.log(`Persons effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [pagination])

  const result = useQuery(
    QUERY_PERSONS,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    console.log(`Persons effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);

  const page = result.data?.persons as IPage<Person>

  // const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Person>)
  const getSelectedRecord = useCallback((id?: string) => page?.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<z.infer<typeof PersonSchema>>({
    resolver: standardSchemaResolver(PersonSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: PersonFormFields) => {
    switch (command) {
      case "create":
        // TODO: invoke mutation: createPerson
        break
      case "update":
        // TODO: invoke mutation: updatePerson
        break
      case "delete":
        // TODO: invoke mutation: deletePerson
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
        <UserIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Persons</h1>
      </div>
      <DataTable<Person, unknown>
        recordKind="Person"
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
        <PersonDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}