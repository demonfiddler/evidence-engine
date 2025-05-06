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
import type ITrackedEntity from "@/app/model/ITrackedEntity"

export const columns: ColumnDef<ITrackedEntity>[] = [
  {
    accessorKey: "id",
    enableSorting: true,
    // enableSortingRemoval: true, // mentioned in TanStack docs but not recognised by IDE.
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ID" />
    ),
  },
  {
    accessorKey: "status",
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Status" />
    ),
  },
  {
    accessorKey: "created",
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader className="" column={column} title="Created" />
    ),
    cell: ({ row }) => {
      const created = row.getValue("created");
      let createdDate;
      switch (typeof created) {
        case "string":
          createdDate = new Date(created);
          break;
        case "object":
          createdDate = created as Date;
      }
      const formatted = createdDate && createdDate.toDateString();
      return <div className="font-medium">{formatted}</div>
    },
  },
  {
    accessorKey: "createdByUser.username",
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader className="" column={column} title="Created by" />
    ),
  },
  {
    accessorKey: "updated",
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader className="" column={column} title="Updated" />
    ),
    cell: ({ row }) => {
      const updated = row.getValue("updated");
      let updatedDate;
      switch (typeof updated) {
        case "string":
          updatedDate = new Date(updated);
          break;
        case "object":
          updatedDate = updated as Date;
      }
      const formatted = updatedDate && updatedDate.toDateString();
      return <div className="font-medium">{formatted}</div>
    },
  },
  {
    accessorKey: "updatedByUser.username",
    enableSorting: true,
    header: ({ column }) => (
      <DataTableColumnHeader className="" column={column} title="Updated by" />
    ),
  },
]

export const columnVisibility = {
  id: true,
  status: true,
  created: false,
  createdByUser_username: false,
  updated: false,
  updatedByUser_username: false
}