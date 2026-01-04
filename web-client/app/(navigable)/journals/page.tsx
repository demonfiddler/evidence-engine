/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

import JournalDetails from "@/app/ui/details/journal-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/journal-columns"
import Journal from "@/app/model/Journal"
import { FormProvider } from "react-hook-form"
import { JournalSchema, JournalFieldValues } from "@/app/ui/validators/journal"
import { CREATE_JOURNAL, DELETE_JOURNAL, READ_JOURNALS, UPDATE_JOURNAL } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { JournalInput, TrackedEntityQueryFilter } from '@/app/model/schema'
import LinkableEntityTableFilter from '@/app/ui/filter/linkable-entity-table-filter'
import useTrackedEntityQueryFilter from '@/hooks/use-tracked-entity-query-filter'
import { LoggerEx, page } from '@/lib/logger'
import { NewspaperIcon } from 'lucide-react'

const logger = new LoggerEx(page, "[Journals] ")

function createFieldValues(journal?: Journal) : JournalFieldValues {
  return {
    rating: journal?.rating ?? 0,
    title: journal?.title ?? '',
    abbreviation: journal?.abbreviation ?? '',
    url: journal?.url ?? '',
    issn: journal?.issn ?? '',
    publisherId: journal?.publisher?.id ?? '',
    notes: journal?.notes ?? '',
    peerReviewed: journal?.peerReviewed ?? "indeterminate",
  }
}

function createInput(fieldValues: JournalFieldValues, id?: string) : JournalInput {
  return {
    id,
    rating: fieldValues.rating || null,
    title: fieldValues.title,
    abbreviation: fieldValues.abbreviation || null,
    url: fieldValues.url || null,
    issn: fieldValues.issn || null,
    publisherId: fieldValues.publisherId || null,
    notes: fieldValues.notes || null,
    peerReviewed: fieldValues.peerReviewed === "indeterminate" ? null : fieldValues.peerReviewed,
  }
}

export default function Journals() {
  logger.debug("render")

  const filterLogic = useTrackedEntityQueryFilter()
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
  } = usePageLogic<Journal, JournalFieldValues, JournalInput, TrackedEntityQueryFilter>({
    recordKind: "Journal",
    schema: JournalSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_JOURNALS,
    createMutation: CREATE_JOURNAL,
    updateMutation: UPDATE_JOURNAL,
    deleteMutation: DELETE_JOURNAL,
    createFieldValues,
    createInput,
    filterLogic,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

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
        <JournalDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}

Journals.whyDidYouRender = true