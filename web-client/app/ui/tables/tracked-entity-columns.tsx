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
import type ITrackedEntity from "@/app/model/ITrackedEntity"
import { columns as baseEntityColumns, columnVisibility as baseEntityColumnVisibility } from "./base-entity-columns"

export const columns: ColumnDef<ITrackedEntity>[] = [
  ...baseEntityColumns,
  {
    id: "status",
    accessorKey: "status",
    enableSorting: true,
    size: 108,
    header: "Status",
  },
  {
    id: "created",
    accessorKey: "created",
    enableSorting: true,
    size: 140,
    header: "Created",
    cell: ({ row, cell }) => {
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
      return <div id={cell.id} className="font-medium">{formatted}</div>
    },
  },
  {
    id: "createdByUsername",
    accessorKey: "createdByUser.username",
    enableSorting: true,
    size: 140,
    header: "Created by",
  },
  {
    id: "updated",
    accessorKey: "updated",
    enableSorting: true,
    size: 140,
    header: "Updated",
    cell: ({ cell, row }) => {
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
      return <div id={cell.id} className="font-medium">{formatted}</div>
    },
  },
  {
    id: "updatedByUsername",
    accessorKey: "updatedByUser.username",
    enableSorting: true,
    size: 140,
    header: "Updated by",
  },
]

export const columnVisibility = {
  ...baseEntityColumnVisibility,
  status: true,
  created: false,
  createdByUsername: false,
  updated: false,
  updatedByUsername: false
}