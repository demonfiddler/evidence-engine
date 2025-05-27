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

const actionColumn = rawActionColumn as ColumnDef<Publication>
const selectColumn = rawSelectColumn as ColumnDef<Publication>

export const columns: ColumnDef<Publication>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "name",
    accessorKey: "name",
    enableHiding: false,
    enableSorting: true,
    size: 400,
    // enableColumnFilter: false,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Name" />
    ),
  },
  {
    id: "location",
    accessorKey: "location",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Location" />
    )
  },
  {
    id: "country",
    accessorKey: "country",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Country" />
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
    id: "journalCount",
    accessorKey: "journalCount",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Jnl. Count" />
    ),
    meta: {
      className: "text-right"
    }
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  name: true,
  location: true,
  country: true,
  url: false,
  journalCount: false
}