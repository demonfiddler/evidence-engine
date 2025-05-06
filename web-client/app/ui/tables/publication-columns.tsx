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
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    // enableColumnFilter: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
  },
  {
    accessorKey: "authors",
    enableHiding: true,
    enableSorting: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Authors" />
    )
  },
  {
    accessorKey: "journal",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Journal" />
    ),
  },
  {
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Date" />
    ),
  },
  {
    accessorKey: "year",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Year" />
    ),
  },
  {
    accessorKey: "abstract",
    enableHiding: true,
    enableSorting: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Abstract" />
    ),
  },
  {
    accessorKey: "cached",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Cached" />
    ),
    cell: ({row}) => (
      <Checkbox
        checked={row.original.cached}
        aria-label="Content cached"
      />
    )
  },
  {
    accessorKey: "peerReviewed",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Peer Reviewed" />
    ),
    cell: ({row}) => (
      <Checkbox
        checked={row.original.peerReviewed}
        aria-label="Content has been peer reviewed"
      />
    )
  },
  {
    accessorKey: "doi",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="DOI" />
    ),
  },
  {
    accessorKey: "isbn",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ISBN" />
    ),
  },
  {
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="URL" />
    ),
  },
  {
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Notes" />
    ),
  },
  {
    accessorKey: "accessed",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Accessed" />
    ),
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
  cached: false,
  peerReviewed: false,
  doi: true,
  isbn: true,
  url: false,
  notes: false,
  accessed: false
}