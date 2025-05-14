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
import Journal from "@/app/model/Journal"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"

const actionColumn = rawActionColumn as ColumnDef<Journal>
const selectColumn = rawSelectColumn as ColumnDef<Journal>

export const columns: ColumnDef<Journal>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "title",
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    size: 400,
    // enableColumnFilter: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
  },
  {
    id: "abbreviation",
    accessorKey: "abbreviation",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    // enableColumnFilter: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Abbreviation" />
    ),
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="URL" />
    ),
    cell: ({getValue}) => (
      <a href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "issn",
    accessorKey: "issn",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ISSN" />
    ),
  },
  {
    id: "publisher",
    accessorKey: "publisher",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Publisher" />
    ),
    cell: ({ row }) => (
      <div className="font-medium">{
        row.original.publisher
        ? `${row.original.publisher?.id} : ${row.original.publisher?.name}`
        : <></>
      }
      </div>
    )
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Notes" />
    ),
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  title: true,
  abbreviation: false,
  url: false,
  issn: true,
  publisher: false,
  notes: false
}