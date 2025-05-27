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
import Log from "@/app/model/Log"
import { columns as baseEntityColumns, columnVisibility as baseEntityColumnVisibility } from "./base-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { formatDate } from "@/lib/utils"
import User from "@/app/model/User"

const actionColumn = rawActionColumn as ColumnDef<Log>
const selectColumn = rawSelectColumn as ColumnDef<Log>

const ownColumns1: ColumnDef<Log>[] = [
  selectColumn,
  ... baseEntityColumns,
  {
    id: "timestamp",
    accessorKey: "timestamp",
    enableHiding: true,
    enableSorting: true,
    size: 220,
    // enableColumnFilter: false,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Timestamp" />
    ),
    cell: ({ row }) => (
      <div className="font-medium">{formatDate(row.getValue("timestamp"))}</div>
    )
  },
  {
    id: "user",
    accessorKey: "user",
    enableHiding: true,
    enableSorting: true,
    size: 140,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="User" />
    ),
    cell: ({cell}) => (
      (cell.getValue() as User)?.username
    )
  },
  {
    id: "transactionKind",
    accessorKey: "transactionKind",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Transaction" />
    )
  }
]

const ownColumns2: ColumnDef<Log>[] = [
  {
    id: "linkedEntityKind",
    accessorKey: "linkedEntityKind",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Linked Record Kind" />
    ),
  },
  {
    id: "linkedEntityId",
    accessorKey: "linkedEntityId",
    enableHiding: true,
    enableSorting: true,
    size: 170,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Linked Entity ID" />
    ),
    meta: {
      className: "text-right"
    }
  },
]

export const ownColumns: ColumnDef<Log>[] = [
  ...ownColumns1,
  ...ownColumns2,
  actionColumn
]

export const columns: ColumnDef<Log>[] = [
  ...ownColumns1,
  {
    id: "entityKind",
    accessorKey: "entityKind",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Record Kind" />
    ),
  },
  {
    id: "entityId",
    accessorKey: "entityId",
    enableHiding: true,
    enableSorting: true,
    size: 120,
    header: ({ table, header, column }) => (
      <DataTableColumnHeader key={header.id} table={table} header={header} column={column} title="Entity ID" />
    ),
    meta: {
      className: "text-right"
    }
  },
  ...ownColumns2
]

export const columnVisibility = {
  ... baseEntityColumnVisibility,
  timestamp: true,
  user: true,
  transactionKind: true,
  entityKind: true,
  entityId: true,
  linkedEntityKind: false,
  linkedEntityId: false
}