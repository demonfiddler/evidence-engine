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
import User from "@/app/model/User"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"

const actionColumn = rawActionColumn as ColumnDef<User>
const selectColumn = rawSelectColumn as ColumnDef<User>

export const columns: ColumnDef<User>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    accessorKey: "username",
    enableHiding: false,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Username" />
    ),
  },
  {
    accessorKey: "firstName",
    enableHiding: false,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="First Name" />
    ),
  },
  {
    accessorKey: "lastName",
    enableHiding: false,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Last Name" />
    ),
  },
  {
    accessorKey: "email",
    enableHiding: false,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Email" />
    ),
  },
  {
    accessorKey: "country",
    enableHiding: true,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Country" />
    ),
  },
  {
    accessorKey: "password",
    enableHiding: false,
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="password" />
    ),
  },
  {
    accessorKey: "authorities",
    enableHiding: true,
    enableSorting: true,
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
  username: true,
  firstName: true,
  lastName: true,
  email: false,
  country: false,
  password: false,
  authorities: false
}