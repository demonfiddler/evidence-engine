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
import { LoggerEx, page } from '@/lib/logger'
import { FlaskConicalIcon } from 'lucide-react'

const logger = new LoggerEx(page, "[Publications] ")

function createFieldValues(publication?: Publication) : PublicationFieldValues {
  return {
    rating: publication?.rating ?? 0,
    title: publication?.title ?? '',
    authors: publication?.authors ?? '',
    journalId: publication?.journal?.id ?? '',
    kind: publication?.kind as unknown as PublicationKind ?? '',
    // HACK ALERT for #36: pretend that date is always set, but it'll actually be undefined for a new Publication.
    date: toDate(publication?.date) as Date,
    year: publication?.year ?? '',
    keywords: publication?.keywords ?? '',
    abstract: publication?.abstract ?? '',
    notes: publication?.notes ?? '',
    peerReviewed: !!publication?.peerReviewed,
    doi: publication?.doi ?? '',
    isbn: publication?.isbn ?? '',
    pmcid: publication?.pmcid ?? '',
    pmid: publication?.pmid ?? '',
    hsid: publication?.hsid ?? '',
    arxivid: publication?.arxivid ?? '',
    biorxivid: publication?.biorxivid ?? '',
    medrxivid: publication?.medrxivid ?? '',
    ericid: publication?.ericid ?? '',
    ihepid: publication?.ihepid ?? '',
    oaipmhid: publication?.oaipmhid ?? '',
    halid: publication?.halid ?? '',
    zenodoid: publication?.zenodoid ?? '',
    scopuseid: publication?.scopuseid ?? '',
    wsan: publication?.wsan ?? '',
    pinfoan: publication?.pinfoan ?? '',
    url: publication?.url ?? '',
    cached: !!publication?.cached,
    accessed: toDate(publication?.accessed),
  }
}

function createInput(fieldValues: PublicationFieldValues, id?: string) : PublicationInput {
  return {
    id,
    rating: fieldValues.rating || null,
    title: fieldValues.title,
    authorNames: fieldValues.authors,
    journalId: fieldValues.journalId || null,
    kind: fieldValues.kind,
    date: toIsoDateString(fieldValues.date),
    year: toInteger(fieldValues.year),
    keywords: fieldValues.keywords || null,
    abstract: fieldValues.abstract || null,
    notes: fieldValues.notes || null,
    peerReviewed: fieldValues.peerReviewed,
    doi: fieldValues.doi || null,
    isbn: fieldValues.isbn || null,
    pmcid: fieldValues.pmcid || null,
    pmid: fieldValues.pmid || null,
    hsid: fieldValues.hsid || null,
    arxivid: fieldValues.arxivid || null,
    biorxivid: fieldValues.biorxivid || null,
    medrxivid: fieldValues.medrxivid || null,
    ericid: fieldValues.ericid || null,
    ihepid: fieldValues.ihepid || null,
    oaipmhid: fieldValues.oaipmhid || null,
    halid: fieldValues.halid || null,
    zenodoid: fieldValues.zenodoid || null,
    scopuseid: fieldValues.scopuseid || null,
    wsan: fieldValues.wsan || null,
    pinfoan: fieldValues.pinfoan || null,
    url: fieldValues.url || null,
    cached: fieldValues.cached,
    accessed: toIsoDateString(fieldValues.accessed),
  }
}

export default function Publications() {
  logger.debug("render")

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
        <FlaskConicalIcon className="w-8 h-8"/>
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
        importAccept={{"application/x-research-info-systems": [".ris"]}}
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

Publications.whyDidYouRender = true