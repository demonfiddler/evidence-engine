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
import User from "@/app/model/User"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"

const actionColumn = rawActionColumn as ColumnDef<User>
const selectColumn = rawSelectColumn as ColumnDef<User>

export const columns: ColumnDef<User>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "username",
    accessorKey: "username",
    enableHiding: false,
    enableSorting: true,
    size: 150,
    header: "Username",
  },
  {
    id: "firstName",
    accessorKey: "firstName",
    enableHiding: true,
    enableSorting: true,
    size: 152,
    header: "First Name",
  },
  {
    id: "lastName",
    accessorKey: "lastName",
    enableHiding: true,
    enableSorting: true,
    size: 152,
    header: "Last Name",
  },
  {
    id: "email",
    accessorKey: "email",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: "Email",
  },
  {
    id: "country",
    accessorKey: "country",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: "Country",
  },
  {
    id: "password",
    accessorKey: "password",
    enableHiding: true,
    enableSorting: false,
    size: 200,
    header: "Password",
  },
  {
    id: "authorities",
    accessorKey: "authorities",
    enableHiding: true,
    enableSorting: true,
    size: 280,
    header: "Authorities",
    cell: ({ cell, getValue }) => (
      <div key={cell.id} className="font-medium">{getValue()?.toString() ?? ''}</div>
    ),
    meta: {
      className: "text-center"
    }
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