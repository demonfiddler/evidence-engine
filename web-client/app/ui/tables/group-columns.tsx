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
import Group from "@/app/model/Group"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"

const actionColumn = rawActionColumn as ColumnDef<Group>
const selectColumn = rawSelectColumn as ColumnDef<Group>

export const columns: ColumnDef<Group>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "groupname",
    accessorKey: "groupname",
    enableHiding: false,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Group Name" />
    ),
  },
  {
    id: "authorities",
    accessorKey: "authorities",
    enableHiding: true,
    enableSorting: false,
    size: 280,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Authorities" />
    ),
    cell: ({ getValue }) => (
      <div className="font-medium">{getValue()?.toString() ?? ''}</div>
    )
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  groupname: true,
  authorities: false
}