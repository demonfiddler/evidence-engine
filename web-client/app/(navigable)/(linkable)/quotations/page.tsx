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

import { ChatBubbleBottomCenterTextIcon } from '@heroicons/react/24/outline'
import QuotationDetails from "@/app/ui/details/quotation-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns, columnVisibility } from "@/app/ui/tables/quotation-columns"
import Quotation from "@/app/model/Quotation"
import { FormProvider } from "react-hook-form"
import { QuotationSchema, QuotationFieldValues } from "@/app/ui/validators/quotation";
import { QUERY_QUOTATIONS } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { toDate, toIsoString } from '@/lib/utils'
import { LinkableEntityQueryFilter } from '@/app/model/schema'

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

function copyFromForm(quotation: Quotation, fieldValues: QuotationFieldValues) {
  quotation.text = fieldValues.text
  quotation.quotee = fieldValues.quotee
  quotation.date = toIsoString(fieldValues.date) ?? null
  quotation.source = fieldValues.source ?? null
  quotation.url = fieldValues.url ?? null
  quotation.notes = fieldValues.notes ?? null
}

export default function Quotations() {
  const {
    search,
    setSearch,
    pagination,
    setPagination,
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    form,
    handleFormAction,
  } = usePageLogic<Quotation, LinkableEntityQueryFilter, QuotationFieldValues>({
    recordKind: "Quotation",
    schema: QuotationSchema,
    listQuery: QUERY_QUOTATIONS,
    copyToForm,
    copyFromForm,
  })

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
        loading={loading}
        pagination={pagination}
        onPaginationChange={setPagination}
        search={search}
        onSearchChange={setSearch}
        onRowSelectionChange={handleRowSelectionChange}
      />
      <FormProvider {...form}>
        <QuotationDetails record={selectedRecord} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  )
}
