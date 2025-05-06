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
import Topic from "@/app/model/Topic"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, expandColumn as rawExpandColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { ArrowTurnDownRightIcon } from "@heroicons/react/24/outline"

const actionColumn = rawActionColumn as ColumnDef<Topic>
const expandColumn = rawExpandColumn as ColumnDef<Topic>
const selectColumn = rawSelectColumn as ColumnDef<Topic>

export const columns: ColumnDef<Topic>[] = [
  selectColumn,
  ... trackedEntityColumns,
  {
    accessorKey: "label",
    enableHiding: true,
    enableSorting: true,
    // enableColumnFilter: false,
    header: ({ column }) => <DataTableColumnHeader column={column} title="Label" />,
    cell: ({ row, getValue }) => <div className="flex flex-row" style={{paddingLeft: `${row.depth}rem`}}>{row.depth > 0 ? <ArrowTurnDownRightIcon className="w-4 h-4" /> : <></>}{getValue<string>()}</div>
  },
  expandColumn,
  {
    accessorKey: "description",
    enableHiding: true,
    enableSorting: false,
    header: ({ column }) => <DataTableColumnHeader column={column} title="Description" />,
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  label: true,
  description: true,
}