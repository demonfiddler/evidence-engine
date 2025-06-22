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
import Claim from "@/app/model/Claim"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { formatDate } from "@/lib/utils"

const actionColumn = rawActionColumn as ColumnDef<Claim>
const selectColumn = rawSelectColumn as ColumnDef<Claim>

export const columns: ColumnDef<Claim>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "text",
    accessorKey: "text",
    enableHiding: false,
    enableSorting: true,
    size: 500,
    maxSize: 750,
    // enableColumnFilter: false,
    header: "Text",
  },
  {
    id: "date",
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: "Date",
    cell: ({ row, cell }) => (
      <div key={cell.id} className="font-medium">{formatDate(row.getValue("date"))}</div>
    ),
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 500,
    maxSize: 750,
    header: "Notes",
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  text: true,
  date: true,
  notes: false
}