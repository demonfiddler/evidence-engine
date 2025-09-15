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

import { BeakerIcon } from '@heroicons/react/24/outline'
import PublicationDetails from "@/app/ui/details/publication-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/publication-columns"
import Publication from "@/app/model/Publication"
import { toDate, toInteger, toIsoDateString } from "@/lib/utils"
import { FormProvider } from "react-hook-form"
import { PublicationFieldValues, PublicationKind, PublicationSchema as PublicationSchema } from "@/app/ui/validators/publication"
import { CREATE_PUBLICATION, DELETE_PUBLICATION, READ_PUBLICATIONS, UPDATE_PUBLICATION } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { LinkableEntityQueryFilter, PublicationInput } from '@/app/model/schema'
import LinkableEntityTableFilter from '@/app/ui/filter/linkable-entity-table-filter'
import useLinkableEntityQueryFilter from '@/hooks/use-linkable-entity-query-filter'

function createFieldValues(publication?: Publication) : PublicationFieldValues {
  return {
    title: publication?.title ?? '',
    authors: publication?.authors ?? '',
    journalId: publication?.journal?.id ?? '',
    kind: publication?.kind as unknown as PublicationKind ?? '',
    date: toDate(publication?.date),
    year: publication?.year ?? '',
    abstract: publication?.abstract ?? '',
    notes: publication?.notes ?? '',
    peerReviewed: !!publication?.peerReviewed,
    doi: publication?.doi ?? '',
    isbn: publication?.isbn ?? '',
    url: publication?.url ?? '',
    cached: !!publication?.cached,
    accessed: toDate(publication?.accessed),
  }
}

function createInput(fieldValues: PublicationFieldValues, id?: string) : PublicationInput {
  return {
    id,
    title: fieldValues.title,
    authorNames: fieldValues.authors,
    journalId: fieldValues.journalId || null,
    kind: fieldValues.kind,
    date: toIsoDateString(fieldValues.date),
    year: toInteger(fieldValues.year),
    abstract: fieldValues.abstract || null,
    notes: fieldValues.notes || null,
    peerReviewed: fieldValues.peerReviewed,
    doi: fieldValues.doi || null,
    isbn: fieldValues.isbn || null,
    url: fieldValues.url || null,
    cached: fieldValues.cached,
    accessed: fieldValues.accessed || null,
  }
}

export default function Publications() {
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
  } = usePageLogic<Publication, PublicationFieldValues, PublicationInput, LinkableEntityQueryFilter>({
    recordKind: "Publication",
    schema: PublicationSchema,
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_PUBLICATIONS,
    createMutation: CREATE_PUBLICATION,
    updateMutation: UPDATE_PUBLICATION,
    deleteMutation: DELETE_PUBLICATION,
    createFieldValues,
    createInput,
    filterLogic,
  })

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <BeakerIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Publications</h1>
      </div>
      <DataTable<Publication, unknown>
        recordKind="Publication"
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
        <PublicationDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  );
}

// Publications.whyDidYouRender = true;