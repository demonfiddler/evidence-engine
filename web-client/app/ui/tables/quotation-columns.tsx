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

import { ColumnDef } from "@tanstack/react-table"
import Quotation from "@/app/model/Quotation"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { formatDate } from "@/lib/utils"

const actionColumn = rawActionColumn as ColumnDef<Quotation>
const selectColumn = rawSelectColumn as ColumnDef<Quotation>

export const columns: ColumnDef<Quotation>[] = [
  selectColumn,
  ... trackedEntityColumns as ColumnDef<Quotation>[],
  {
    id: "quotee",
    accessorKey: "quotee",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    // enableColumnFilter: false,
    header: "Quotee",
  },
  {
    id: "date",
    accessorKey: "date",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: "Date",
    cell: ({ row, cell }) => (
      <div key={cell.id} className="font-medium">{formatDate(row.original.date)}</div>
    )
  },
  {
    id: "text",
    accessorKey: "text",
    enableHiding: false,
    enableSorting: false,
    size: 500,
    header: "Quote",
  },
  {
    id: "source",
    accessorKey: "source",
    enableHiding: true,
    enableSorting: false,
    size: 300,
    header: "Source",
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: false,
    size: 300,
    header: "URL",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: "Notes",
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