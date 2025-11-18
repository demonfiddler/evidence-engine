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

import { ColumnDef } from "@tanstack/react-table"
import Publication from "@/app/model/Publication"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { Checkbox } from "@/components/ui/checkbox"
import { formatDate } from "@/lib/utils"

const actionColumn = rawActionColumn as ColumnDef<Publication>
const selectColumn = rawSelectColumn as ColumnDef<Publication>

export const columns: ColumnDef<Publication>[] = [
  selectColumn,
  ... trackedEntityColumns as ColumnDef<Publication>[],
  {
    id: "title",
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    size: 490,
    // enableColumnFilter: false,
    header: "Title",
  },
  {
    id: "kind",
    accessorKey: "kind",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: "Kind",
  },
  {
    id: "authors",
    accessorKey: "authors",
    enableHiding: true,
    enableSorting: false,
    size: 150,
    header: "Authors",
  },
  {
    id: "journal",
    accessorKey: "journal.title",
    enableHiding: true,
    enableSorting: true,
    size: 400,
    header: "Journal",
  },
  {
    id: "date",
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: "Date",
    cell: ({ row, cell }) => {
      return <div id={cell.id} className="font-medium">{formatDate(row.original.date)}</div>
    },
  },
  {
    id: "year",
    accessorKey: "year",
    enableHiding: true,
    enableSorting: true,
    size: 120,
    header: "Year",
    meta: {
      className: "text-right"
    }
  },
  {
    id: "abstract",
    accessorKey: "abstract",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: "Abstract",
  },
  {
    id: "doi",
    accessorKey: "doi",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "DOI",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`https://doi.org/${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "isbn",
    accessorKey: "isbn",
    enableHiding: true,
    enableSorting: true,
    size: 170,
    header: "ISBN",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`https://isbnsearch.org/isbn/${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "pmid",
    accessorKey: "pmid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "PubMed ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "hsid",
    accessorKey: "hsid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "HS ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "arxivid",
    accessorKey: "arxivid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "ArXiv ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "biorxivid",
    accessorKey: "biorxivid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "BioRxiv ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "medrxivid",
    accessorKey: "medrxivid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "MedRxiv ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "ericid",
    accessorKey: "ericid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "ERIC ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "ihepid",
    accessorKey: "ihepid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "IHEP ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "oaipmhid",
    accessorKey: "oaipmhid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "OAI-PMH ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "halid",
    accessorKey: "halid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "HAL ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "zenodoid",
    accessorKey: "zenodoid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "Zenodo ID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "scopuseid",
    accessorKey: "scopuseid",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "Scopus EID",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "wsan",
    accessorKey: "wsan",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "WSAN",
  },
  {
    id: "pinfoan",
    accessorKey: "pinfoan",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: "PInfoAN",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={`${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: "URL",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "accessed",
    accessorKey: "accessed",
    enableHiding: true,
    enableSorting: true,
    size: 132,
    header: "Accessed",
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: "Notes",
  },
  {
    id: "cached",
    accessorKey: "cached",
    enableHiding: true,
    enableSorting: true,
    size: 128,
    header: "Cached",
    cell: ({row, cell}) => (
      <Checkbox
        key={cell.id}
        checked={row.original.cached}
        aria-label="Content cached"
      />
    ),
    meta: {
      className: "text-center"
    }
  },
  {
    id: "peerReviewed",
    accessorKey: "peerReviewed",
    enableHiding: true,
    enableSorting: true,
    size: 146,
    header: "Peer Rev'd",
    cell: ({row, cell}) => (
      <Checkbox
        key={cell.id}
        checked={row.original.peerReviewed}
        aria-label="Content has been peer reviewed"
      />
    ),
    meta: {
      className: "text-center"
    }
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  title: true,
  authors: false,
  journal: false,
  kind: true,
  date: false,
  year: true,
  abstract: false,
  doi: false,
  isbn: false,
  pmid: false,
  hsid: false,
  arxivid: false,
  biorxivid: false,
  medrxivid: false,
  ericid: false,
  ihepid: false,
  oaipmhid: false,
  halid: false,
  zenodoid: false,
  scopuseid: false,
  wsan: false,
  pinfoan: false,
  url: false,
  accessed: false,
  notes: false,
  cached: false,
  peerReviewed: false,
}