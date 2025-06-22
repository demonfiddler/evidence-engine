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
import { useCallback, useContext, useMemo, useState } from "react";
import { BuildingOfficeIcon } from '@heroicons/react/24/outline';

import PublisherDetails from "@/app/ui/details/publisher-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/publisher-columns"
import rawPage from "@/data/publishers.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Publisher from "@/app/model/Publisher";
import { SelectedRecordsContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";
import { MutationAction, FormAction, toInteger } from "@/lib/utils";
import { useForm, FormProvider } from "react-hook-form"
import { PublisherSchema, PublisherFormFields } from "@/app/ui/validators/publisher";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"

// export const metadata: Metadata = {
//   title: "Publishers",
//   description: "Evidenced publishers of scientific fact",
// };

function copyToForm(publisher?: Publisher) {
  return {
    name: publisher?.name ?? '',
    location: publisher?.location ?? '',
    country: publisher?.country ?? '',
    url: publisher?.url ?? '',
    journalCount: publisher?.journalCount,
  }
}

function copyFromForm(publisher: Publisher, formValue: PublisherFormFields) {
    publisher.name = formValue.name
    publisher.location = formValue.location ?? null
    publisher.country = formValue.country ?? null
    publisher.url = formValue.url ?? null
    publisher.journalCount = toInteger(formValue.journalCount) ?? 0
}

export default function Publishers() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Publisher?.id)
  const pageReducer = useCallback((draft: IPage<Publisher>, action: MutationAction<FormAction, PublisherFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const publisher : Publisher = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(publisher.id)
        copyFromForm(publisher, action.value)
        draft.content.push(publisher)
        break
      case "update":
        if (idx != -1) {
          const publisher = draft.content[idx]
          copyFromForm(publisher, action.value)
          draft.content.splice(idx, 1, publisher)
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
  }, [selectedRecordId, setSelectedRecordId])
  const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Publisher>)
  const getSelectedRecord = useCallback((id?: string) => page.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<PublisherFormFields>({
    resolver: standardSchemaResolver(PublisherSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: PublisherFormFields) => {
    switch (command) {
      case "create":
      case "update":
      case "delete":
        pageDispatch({command: command, value: formValue})
        break
      case "reset":
        form.reset(origFormValue)
        break
    }
  }, [form, pageDispatch, selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const publisher = getSelectedRecord(recordId)
    form.reset(copyToForm(publisher), {
      keepErrors: false,
      keepDirty: false,
      keepTouched: false,
      keepIsValid: false,
    })
  }, [setSelectedRecordId, getSelectedRecord, form])

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
        defaultColumnVisibility={columnVisibility}
        page={page}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <PublisherDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
