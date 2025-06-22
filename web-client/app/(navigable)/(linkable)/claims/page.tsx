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
import { useCallback, useContext, useMemo, useState } from "react"
import { useImmerReducer } from "use-immer"
import { ExclamationCircleIcon } from "@heroicons/react/24/outline"

import ClaimDetails from "@/app/ui/details/claim-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns, columnVisibility } from "@/app/ui/tables/claim-columns"
import rawPage from "@/data/claims.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Claim from "@/app/model/Claim";
import { SelectedRecordsContext } from "@/lib/context";
import { MutationAction, FormAction, toDate, toIsoString } from "@/lib/utils"
import { useForm, FormProvider } from "react-hook-form"
import { ClaimSchema, ClaimFormFields } from "@/app/ui/validators/claim";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
// import { DetailMode } from "@/app/ui/details/detail-actions"

// export const metadata: Metadata = {
//   title: "Claims",
//   description: "Evidenced claims of scientific fact",
// };

function copyToForm(claim?: Claim) {
  return {
    text: claim?.text ?? '',
    date: toDate(claim?.date) ?? '',
    notes: claim?.notes ?? ''
  }
}

function copyFromForm(claim: Claim, formValue: ClaimFormFields) {
  claim.text = formValue.text
  claim.date = toIsoString(formValue.date)
  claim.notes = formValue.notes ?? null
}

export default function Claims() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Claim?.id)
  // const [mode, setMode] = useState<DetailMode>("view")
  // const [newRecord, setNewRecord] = useState<Claim>()
  const pageReducer = useCallback((draft: IPage<Claim>, action: MutationAction<FormAction, ClaimFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const claim : Claim = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(claim.id)
        copyFromForm(claim, action.value)
        draft.content.push(claim)
        break
      case "update":
        if (idx != -1) {
          const claim = draft.content[idx]
          copyFromForm(claim, action.value)
          draft.content.splice(idx, 1, claim)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1) {
          draft.content.splice(idx, 1)
          setSelectedRecordId(undefined)
          selectedRecordsContext.setSelectedRecord(selectedRecordsContext, "Claim", undefined)
        }
        break
    }
  }, [selectedRecordId, setSelectedRecordId])
  const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Claim>)
  const getSelectedRecord = useCallback((id?: string) => page.content.find(r => r.id == id), [page])
  const selectedRecord = /*mode == "create" ? newRecord :*/ getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<ClaimFormFields>({
    resolver: standardSchemaResolver(ClaimSchema),
    mode: "onChange",
    values: origFormValue,
  })

  // console.log(`Claims() page: ${JSON.stringify(page)})`)

  const handleFormAction = useCallback((command: FormAction, formValue: ClaimFormFields) => {
    // console.log(`Claims.handleFormAction: command = ${command}, mode = ${mode}`)
    switch (command) {
      case "create":
      case "update":
      case "delete":
        pageDispatch({command: command, value: formValue})
        break
      case "reset":
        // if (mode == "create") {
        //   setNewRecord({
        //     id: (Math.max(...page.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
        //     status: "Draft"
        //   })
        // }
        form.reset(copyToForm(selectedRecord))
        break
    }
  }, [form, pageDispatch, selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const claim = getSelectedRecord(recordId)
    form.reset(copyToForm(claim))
  }, [setSelectedRecordId, getSelectedRecord, form])

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
  );
}
