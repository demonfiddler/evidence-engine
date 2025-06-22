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
import { NewspaperIcon } from '@heroicons/react/24/outline';

import JournalDetails from "@/app/ui/details/journal-details";
import DataTable from "@/app/ui/data-table/data-table";

import { columns, columnVisibility } from "@/app/ui/tables/journal-columns"
import rawPage from "@/data/journals.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Journal from "@/app/model/Journal";
import { SelectedRecordsContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";
import { MutationAction, FormAction } from "@/lib/utils";
import { useForm, FormProvider } from "react-hook-form"
import { JournalSchema, JournalFormFields } from "@/app/ui/validators/journal";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"

// export const metadata: Metadata = {
//   title: "Journals",
//   description: "Evidenced journals of scientific fact",
// };

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

function copyFromForm(journal: Journal, formValue: JournalFormFields) {
  journal.title = formValue.title ?? null,
  journal.abbreviation = formValue.abbreviation ?? null,
  journal.url = formValue.url ?? null,
  journal.issn = formValue.issn ?? null,
  journal.publisher = formValue.publisherId ? {id: formValue.publisherId} : null,
  journal.notes = formValue.notes ?? null
}

export default function Journals() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Journal?.id)
  const pageReducer = useCallback((draft: IPage<Journal>, action: MutationAction<FormAction, JournalFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const journal : Journal = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(journal.id)
        copyFromForm(journal, action.value)
        draft.content.push(journal)
        break
      case "update":
        if (idx != -1) {
          const journal = draft.content[idx]
          copyFromForm(journal, action.value)
          draft.content.splice(idx, 1, journal)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
    }
  }, [selectedRecordId, setSelectedRecordId])
  const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Journal>)
  const getSelectedRecord = useCallback((id?: string) => page.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<JournalFormFields>({
    resolver: standardSchemaResolver(JournalSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: JournalFormFields) => {
    switch (command) {
      case "create":
      case "update":
      case "delete":
        pageDispatch({command: command, value: formValue})
        break
      case "reset":
        form.reset(copyToForm(selectedRecord))
        break
    }
  }, [form, pageDispatch, selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const journal = getSelectedRecord(recordId)
    form.reset(copyToForm(journal))
  }, [setSelectedRecordId, getSelectedRecord, form])

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
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <JournalDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
