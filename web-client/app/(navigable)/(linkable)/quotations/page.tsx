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
import { columns } from "@/app/ui/tables/quotation-columns"
import Quotation from "@/app/model/Quotation"
import { FormProvider } from "react-hook-form"
import { QuotationSchema, QuotationFieldValues } from "@/app/ui/validators/quotation"
import { CREATE_QUOTATION, DELETE_QUOTATION, READ_QUOTATIONS, UPDATE_QUOTATION } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { toDate, toIsoDateString } from '@/lib/utils'
import { LinkableEntityQueryFilter, QuotationInput } from '@/app/model/schema'
import LinkableEntityTableFilter from '@/app/ui/filter/linkable-entity-table-filter'
import useLinkableEntityQueryFilter from '@/hooks/use-linkable-entity-query-filter'

function createFieldValues(quotation?: Quotation) {
  return {
    text: quotation?.text ?? '',
    quotee: quotation?.quotee ?? '',
    date: toDate(quotation?.date),
    source: quotation?.source ?? '',
    url: quotation?.url ?? '',
    notes: quotation?.notes ?? ''
  }
}

function createInput(fieldValues: QuotationFieldValues, id?: string) : QuotationInput {
  return {
    id,
    text: fieldValues.text,
    quotee: fieldValues.quotee,
    date: toIsoDateString(fieldValues.date),
    source: fieldValues.source || null,
    url: fieldValues.url || null,
    notes: fieldValues.notes || null,
  }
}

export default function Quotations() {
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
  } = usePageLogic<Quotation, QuotationFieldValues, QuotationInput, LinkableEntityQueryFilter>({
    recordKind: "Quotation",
    schema: QuotationSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_QUOTATIONS,
    createMutation: CREATE_QUOTATION,
    updateMutation: UPDATE_QUOTATION,
    deleteMutation: DELETE_QUOTATION,
    createFieldValues,
    createInput,
    filterLogic,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

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
        <QuotationDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}
