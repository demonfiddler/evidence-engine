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
import DataTableColumnHeader from "@/app/ui/data-table/data-table-column-header"
import Publication from "@/app/model/Publication"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { Checkbox } from "@/components/ui/checkbox"

const actionColumn = rawActionColumn as ColumnDef<Publication>
const selectColumn = rawSelectColumn as ColumnDef<Publication>

export const columns: ColumnDef<Publication>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "title",
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    size: 500,
    // enableColumnFilter: false,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Title" />
    ),
  },
  {
    id: "authors",
    accessorKey: "authors",
    enableHiding: true,
    enableSorting: false,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Authors" />
    )
  },
  {
    id: "journal",
    accessorKey: "journal.title",
    enableHiding: true,
    enableSorting: true,
    size: 400,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Journal" />
    ),
  },
  {
    id: "date",
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Date" />
    ),
  },
  {
    id: "year",
    accessorKey: "year",
    enableHiding: true,
    enableSorting: true,
    size: 90,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Year" />
    ),
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
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Abstract" />
    ),
  },
  {
    id: "doi",
    accessorKey: "doi",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="DOI" />
    ),
    cell: ({getValue}) => (
      <a href={`https://doi.org/${getValue() as string}`} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "isbn",
    accessorKey: "isbn",
    enableHiding: true,
    enableSorting: true,
    size: 170,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="ISBN" />
    ),
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="URL" />
    ),
    cell: ({getValue}) => (
      <a href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "accessed",
    accessorKey: "accessed",
    enableHiding: true,
    enableSorting: true,
    size: 132,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Accessed" />
    ),
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Notes" />
    ),
  },
  {
    id: "cached",
    accessorKey: "cached",
    enableHiding: true,
    enableSorting: true,
    size: 128,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Cached" />
    ),
    cell: ({row}) => (
      <Checkbox
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
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Peer Rev'd" />
    ),
    cell: ({row}) => (
      <Checkbox
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
  doi: true,
  isbn: true,
  url: false,
  accessed: false,
  notes: false,
  cached: false,
  peerReviewed: false,
}