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
import Comment from "@/app/model/Comment"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"

const actionColumn = rawActionColumn as ColumnDef<Comment>
const selectColumn = rawSelectColumn as ColumnDef<Comment>

export const columns: ColumnDef<Comment>[] = [
  selectColumn,
  ... trackedEntityColumns as ColumnDef<Comment>[],
  {
    id: "targetKind",
    accessorKey: "target.entityKind",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    // enableColumnFilter: false,
    header: "Target Kind",
  },
  {
    id: "targetId",
    accessorKey: "target.id",
    enableHiding: true,
    enableSorting: true,
    size: 170,
    // enableColumnFilter: false,
    header: "Target ID",
  },
  {
    id: "parentId",
    accessorFn: (originalRow) => originalRow.parent?.id,
    enableHiding: true,
    enableSorting: true,
    size: 170,
    // enableColumnFilter: false,
    header: "Parent ID",
  },
  {
    id: "text",
    accessorKey: "text",
    enableHiding: true,
    enableSorting: true,
    size: 500,
    maxSize: 750,
    // enableColumnFilter: false,
    header: "Text",
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  rating: false,
  created: true,
  createdByUsername: true,
  targetKind: true,
  targetId: true,
  parentId: true,
  text: false,
}