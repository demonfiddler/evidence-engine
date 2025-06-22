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

// import type { Metadata } from "next"
import { useCallback, useContext, useMemo, useState } from "react"
import { useImmerReducer } from "use-immer"
import { ChatBubbleBottomCenterTextIcon } from '@heroicons/react/24/outline'

import QuotationDetails from "@/app/ui/details/quotation-details"
import DataTable from "@/app/ui/data-table/data-table"

import { columns, columnVisibility } from "@/app/ui/tables/quotation-columns"
import rawPage from "@/data/quotations.json" assert {type: 'json'}
import IPage from "@/app/model/IPage"
import Quotation from "@/app/model/Quotation"
import { SelectedRecordsContext } from "@/lib/context"
import { useForm, FormProvider } from "react-hook-form"
import { QuotationSchema, QuotationFormFields } from "@/app/ui/validators/quotation";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { MutationAction, FormAction, toDate, toIsoString } from "@/lib/utils"

// export const metadata: Metadata = {
//   title: "Quotations",
//   description: "Public quotations, open letters & petitions",
// };

function copyToForm(quotation?: Quotation) {
  return {
    text: quotation?.text ?? '',
    quotee: quotation?.quotee ?? '',
    date: toDate(quotation?.date) ?? '',
    source: quotation?.source ?? '',
    url: quotation?.url ?? '',
    notes: quotation?.notes ?? ''
  }
}

function copyFromForm(quotation: Quotation, formValue: QuotationFormFields) {
  quotation.text = formValue.text
  quotation.quotee = formValue.quotee
  quotation.date = toIsoString(formValue.date) ?? null
  quotation.source = formValue.source ?? null
  quotation.url = formValue.url ?? null
  quotation.notes = formValue.notes ?? null
}

export default function Quotations() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Quotation?.id)
  const pageReducer = useCallback((draft: IPage<Quotation>, action: MutationAction<FormAction, QuotationFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const quotation : Quotation = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(quotation.id)
        copyFromForm(quotation, action.value)
        draft.content.push(quotation)
        break
      case "update":
        if (idx != -1) {
          const quotation = draft.content[idx]
          copyFromForm(quotation, action.value)
          draft.content.splice(idx, 1, quotation)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
    }
  }, [selectedRecordId, setSelectedRecordId])
  const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Quotation>)
  const getSelectedRecord = useCallback((id?: string) => page.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<QuotationFormFields>({
    resolver: standardSchemaResolver(QuotationSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: QuotationFormFields) => {
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
    const quotation = getSelectedRecord(recordId)
    form.reset(copyToForm(quotation))
  }, [setSelectedRecordId, getSelectedRecord, form])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ChatBubbleBottomCenterTextIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Quotations</h1>
      </div>
      <DataTable<Quotation, unknown>
        recordKind="Quotation"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <QuotationDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
