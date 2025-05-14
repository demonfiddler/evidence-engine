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
import Person from "@/app/model/Person"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { Checkbox } from "@/components/ui/checkbox"

const actionColumn = rawActionColumn as ColumnDef<Person>
const selectColumn = rawSelectColumn as ColumnDef<Person>

export const columns: ColumnDef<Person>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    id: "title",
    accessorKey: "title",
    enableHiding: true,
    enableSorting: true,
    size: 90,
    // enableColumnFilter: false,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
  },
  {
    id: "firstName",
    accessorKey: "firstName",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="First Name(s)" />
    ),
  },
  {
    id: "nickname",
    accessorKey: "nickname",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Nickname" />
    ),
  },
  {
    id: "prefix",
    accessorKey: "prefix",
    enableHiding: true,
    enableSorting: true,
    size: 100,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Prefix" />
    ),
  },
  {
    id: "lastName",
    accessorKey: "lastName",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Last Name" />
    ),
  },
  {
    id: "suffix",
    accessorKey: "suffix",
    enableHiding: true,
    enableSorting: true,
    size: 100,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Suffix" />
    ),
  },
  {
    id: "alias",
    accessorKey: "alias",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Alias" />
    ),
  },
  {
    id: "qualifications",
    accessorKey: "qualifications",
    enableHiding: true,
    enableSorting: true,
    size: 500,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Qualifications" />
    ),
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 500,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Notes" />
    ),
  },
  {
    id: "country",
    accessorKey: "country",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Country" />
    ),
  },
  {
    id: "rating",
    accessorKey: "rating",
    enableHiding: true,
    enableSorting: true,
    size: 100,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Rating" />
    ),
  },
  {
    id: "checked",
    accessorKey: "checked",
    enableHiding: true,
    enableSorting: true,
    size: 128,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Checked" />
    ),
    cell: ({row}) => (
      <Checkbox
        checked={row.original.checked}
        aria-label="Credentials checked"
      />
    )
  },
  {
    id: "published",
    accessorKey: "published",
    enableHiding: true,
    enableSorting: true,
    size: 128,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Published" />
    ),
    cell: ({row}) => (
      <Checkbox
        checked={row.original.published}
        aria-label="Author of peer-reviewed publications"
      />
    ),
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  title: true,
  firstName: true,
  nickname: false,
  prefix: false,
  lastName: true,
  suffix: false,
  alias: false,
  qualifications: false,
  notes: false,
  country: true,
  rating: true,
  checked: true,
  published: true
}