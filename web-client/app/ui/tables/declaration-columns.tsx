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
import Declaration from "@/app/model/Declaration"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { formatDate } from "@/lib/utils"
import { Checkbox } from "@/components/ui/checkbox"

const actionColumn = rawActionColumn as ColumnDef<Declaration>
const selectColumn = rawSelectColumn as ColumnDef<Declaration>

export const columns: ColumnDef<Declaration>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "kind",
    accessorKey: "kind",
    enableHiding: true,
    enableSorting: true,
    size: 112,
    // enableColumnFilter: false,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Kind" />
    ),
  },
  {
    id: "title",
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    size: 360,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Title" />
    ),
  },
  {
    id: "date",
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Date" />
    ),
    cell: ({ row }) => (
      <div className="font-medium">{formatDate(row.getValue("date"))}</div>
    )
  },
  {
    id: "country",
    accessorKey: "country",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Country" />
    ),
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="URL" />
    ),
    cell: ({getValue}) => (
      <a href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "cached",
    accessorKey: "cached",
    enableHiding: true,
    enableSorting: true,
    size: 116,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Cached" />
    ),
    cell: ({row}) => (
      <Checkbox
        checked={row.original.cached}
        aria-label="Cached on EE server"
      />
    ),
    meta: {
      className: "text-center"
    }
  },
  {
    id: "signatories",
    accessorKey: "signatories",
    enableHiding: true,
    enableSorting: false,
    size: 200,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Signatories" />
    ),
  },
  {
    id: "signatoryCount",
    accessorKey: "signatoryCount",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Sig. Count" />
    ),
    meta: {
      className: "text-right"
    }
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 300,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader table={table} header={header} column={column} title="Notes" />
    ),
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  kind: true,
  title: true,
  date: true,
  country: true,
  url: false,
  cached: false,
  signatories: false,
  signatoryCount: false,
  notes: false
}