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
import Quotation from "@/app/model/Quotation"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { formatDate } from "@/lib/utils"

const actionColumn = rawActionColumn as ColumnDef<Quotation>
const selectColumn = rawSelectColumn as ColumnDef<Quotation>

export const columns: ColumnDef<Quotation>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    accessorKey: "quotee",
    enableHiding: true,
    enableSorting: true,
    // enableColumnFilter: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Quotee" />
    ),
  },
  {
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Date" />
    ),
    cell: ({ row }) => (
      <div className="font-medium">{formatDate(row.getValue("date"))}</div>
    )
  },
  {
    accessorKey: "text",
    enableHiding: false,
    enableSorting: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Quote" />
    ),
  },
  {
    accessorKey: "source",
    enableHiding: true,
    enableSorting: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Source" />
    ),
  },
  {
    accessorKey: "url",
    enableHiding: true,
    enableSorting: false,
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
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  quotee: true,
  date: true,
  text: true,
  source: false,
  url: false,
  notes: false
}