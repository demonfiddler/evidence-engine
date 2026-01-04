/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import { formatDateTime } from "@/lib/utils"
import StarRatingBasic from "@/components/commerce-ui/star-rating-basic"

export const columns: ColumnDef<ITrackedEntity>[] = [
  ...baseEntityColumns as ColumnDef<ITrackedEntity>[],
  {
    id: "status",
    accessorKey: "status",
    enableSorting: true,
    size: 130,
    header: "Status",
  },
  {
    id: "rating",
    accessorKey: "rating",
    enableHiding: true,
    enableSorting: true,
    size: 124,
    header: "Rating",
    cell: ({ getValue }) => {
      return (
        <StarRatingBasic
          className="place-self-center"
          disabled={true}
          value={getValue() as number ?? 0}
          maxStars={5}
          iconSize={12}
        />
      )
    },
  },
  {
    id: "created",
    accessorKey: "created",
    enableSorting: true,
    size: 164,
    header: "Created",
    cell: ({ row, cell }) => {
      return <div id={cell.id} className="font-medium">{formatDateTime(row.original.created)}</div>
    },
  },
  {
    id: "createdByUsername",
    accessorKey: "createdByUser.username",
    enableSorting: true,
    size: 152,
    header: "Created by",
  },
  {
    id: "updated",
    accessorKey: "updated",
    enableSorting: true,
    size: 164,
    header: "Updated",
    cell: ({ cell, row }) => {
      return <div id={cell.id} className="font-medium">{formatDateTime(row.original.updated)}</div>
    },
  },
  {
    id: "updatedByUsername",
    accessorKey: "updatedByUser.username",
    enableSorting: true,
    size: 156,
    header: "Updated by",
  },
]

export const columnVisibility = {
  ...baseEntityColumnVisibility,
  status: true,
  rating: true,
  created: false,
  createdByUsername: false,
  updated: false,
  updatedByUsername: false
}