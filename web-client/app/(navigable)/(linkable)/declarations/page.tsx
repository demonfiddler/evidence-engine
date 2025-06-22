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
import { useImmerReducer } from "use-immer";
import { EnvelopeOpenIcon } from '@heroicons/react/24/outline';

import DeclarationDetails from "@/app/ui/details/declaration-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/declaration-columns"
import rawPage from "@/data/declarations.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Declaration from "@/app/model/Declaration";
import { DeclarationSchema, DeclarationFormFields, DeclarationKind } from "@/app/ui/validators/declaration";
import { SelectedRecordsContext } from "@/lib/context";
import { MutationAction, FormAction, toDate, toInteger, toIsoString } from "@/lib/utils";
import { FormProvider, useForm } from "react-hook-form";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"

// export const metadata: Metadata = {
//   title: "Declarations",
//   description: "Public declarations, open letters & petitions",
// };

function copyToForm(declaration?: Declaration) {
  return {
    kind: declaration?.kind as DeclarationKind ?? '',
    date: toDate(declaration?.date),
    title: declaration?.title ?? '',
    country: declaration?.country ?? '',
    url: declaration?.url ?? '',
    cached: declaration?.cached ?? false,
    signatories: declaration?.signatories ?? '',
    signatoryCount: declaration?.signatoryCount ?? '',
    notes: declaration?.notes ?? ''
  }
}

function copyFromForm(declaration: Declaration, formValue: DeclarationFormFields) {
  declaration.kind = formValue.kind
  declaration.title = formValue.title
  declaration.date = toIsoString(formValue.date)
  declaration.country = formValue.country ?? null
  declaration.url = formValue.url ?? null
  declaration.cached = !!formValue.cached
  declaration.signatories = formValue.signatories ?? null
  declaration.signatoryCount = toInteger(formValue.signatoryCount)
  declaration.notes = formValue.notes ?? null
}

export default function Declarations() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Declaration?.id)
  const pageReducer = useCallback((draft: IPage<Declaration>, action: MutationAction<FormAction, DeclarationFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedRecordId)
    switch (action.command) {
      case "create":
        const declaration : Declaration = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(declaration.id)
        copyFromForm(declaration, action.value)
        draft.content.push(declaration)
        break
      case "update":
        if (idx != -1) {
          const declaration = draft.content[idx]
          copyFromForm(declaration, action.value)
          draft.content.splice(idx, 1, declaration)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
    }
  }, [selectedRecordId, setSelectedRecordId])
  const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Declaration>)
  const getSelectedRecord = useCallback((id?: string) => page.content.find(r => r.id == id), [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<DeclarationFormFields>({
    resolver: standardSchemaResolver(DeclarationSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: DeclarationFormFields) => {
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
    const declaration = getSelectedRecord(recordId)
    form.reset(copyToForm(declaration))
  }, [setSelectedRecordId, getSelectedRecord, form])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <EnvelopeOpenIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Declarations</h1>
      </div>
      <DataTable<Declaration, unknown>
        recordKind="Declaration"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <DeclarationDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  );
}
