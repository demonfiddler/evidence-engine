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
import { expandColumn as rawExpandColumn } from "./extra-columns"
import { ArrowTurnDownRightIcon } from "@heroicons/react/24/outline"
import TopicStatistics from "@/app/model/TopicStatistics"

const expandColumn = rawExpandColumn as ColumnDef<TopicStatistics>

function getValue(item: TopicStatistics, entityKind: string) {
  return item.entityStatistics.find(e => e.entityKind === entityKind)?.count.toLocaleString() ?? "0"
}

export const columns: ColumnDef<TopicStatistics>[] = [
  {
    id: "id",
    accessorFn: row => row.topic.id,
    size: 100,
    header: "ID",
    meta: {
      "className": "text-right"
    }
  },
  {
    id: "status",
    accessorFn: row => row.topic.status,
    enableSorting: true,
    size: 130,
    header: "Status",
  },
  {
    id: "label",
    accessorFn: row => row.topic.label,
    size: 240,
    header: "Label",
    cell: ({ row, cell, getValue }) => <div key={cell.id} className="flex flex-row" style={{paddingLeft: `${row.depth}rem`}}>{row.depth > 0 ? <ArrowTurnDownRightIcon className="w-4 h-4" /> : <></>}{getValue<string>()}</div>
  },
  expandColumn,
  {
    id: "description",
    accessorFn: row => row.topic.description,
    size: 400,
    header: "Description",
  },
  {
    id: "claims",
    accessorFn: row => getValue(row, "CLA"),
    header: "Claims",
    meta: {
      href: "/claims",
      className: "text-right"
    },
  },
  {
    id: "declarations",
    accessorFn: row => getValue(row, "DEC"),
    header: "Declarations",
    meta: {
      href: "/declarations",
      className: "text-right"
    },
  },
  {
    id: "persons",
    accessorFn: row => getValue(row, "PER"),
    header: "Persons",
    meta: {
      href: "/persons",
      className: "text-right"
    },
  },
  {
    id: "publications",
    accessorFn: row => getValue(row, "PUB"),
    header: "Publications",
    meta: {
      href: "/publications",
      className: "text-right"
    },
  },
  {
    id: "quotations",
    accessorFn: row => getValue(row, "QUO"),
    header: "Quotations",
    meta: {
      href: "/quotations",
      className: "text-right"
    },
  },
  {
    id: "total",
    accessorFn: row => row.entityStatistics.reduce((total, stat) => total + stat.count, 0).toLocaleString() || 0,
    header: "Total",
    meta: {
      className: "text-right"
    },
  },
]

export const columnVisibility = {
  label: true,
  description: true,
}