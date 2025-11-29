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
import Journal from "@/app/model/Journal"
import { columns as trackedEntityColumns, columnVisibility as trackedEntityColumnVisibility } from "./tracked-entity-columns"
import { actionColumn as rawActionColumn, selectColumn as rawSelectColumn } from "./extra-columns"
import { Checkbox } from "@/components/ui/checkbox"
import { SquareCheckIcon, SquareIcon, SquareMinusIcon } from "lucide-react"
import CheckboxEx from "../ext/checkbox-ex"

const actionColumn = rawActionColumn as ColumnDef<Journal>
const selectColumn = rawSelectColumn as ColumnDef<Journal>

export const columns: ColumnDef<Journal>[] = [
  selectColumn,
  ... trackedEntityColumns as ColumnDef<Journal>[],
  {
    id: "title",
    accessorKey: "title",
    enableHiding: false,
    enableSorting: true,
    size: 400,
    // enableColumnFilter: false,
    header: "Title",
  },
  {
    id: "abbreviation",
    accessorKey: "abbreviation",
    enableHiding: true,
    enableSorting: true,
    size: 200,
    // enableColumnFilter: false,
    header: "Abbreviation",
  },
  {
    id: "url",
    accessorKey: "url",
    enableHiding: true,
    enableSorting: true,
    size: 300,
    header: "URL",
    cell: ({cell, getValue}) => (
      <a key={cell.id} href={getValue() as string} target="_blank">{getValue() as string}</a>
    )
  },
  {
    id: "issn",
    accessorKey: "issn",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: "ISSN",
  },
  {
    id: "publisher",
    accessorKey: "publisher",
    enableHiding: true,
    enableSorting: true,
    size: 150,
    header: "Publisher",
    cell: ({ row, cell }) => (
      <div key={cell.id} className="font-medium">{
        row.original.publisher
        ? `${row.original.publisher?.id} : ${row.original.publisher?.name}`
        : null
      }
      </div>
    )
  },
  {
    id: "notes",
    accessorKey: "notes",
    enableHiding: true,
    enableSorting: false,
    size: 400,
    header: "Notes",
  },
  {
    id: "peerReviewed",
    accessorKey: "peerReviewed",
    enableHiding: true,
    enableSorting: true,
    size: 164,
    header: "Peer Reviewed",
    // cell: ({row, cell}) => {
    //   row.original.peerReviewed == null
    //   ? <SquareMinusIcon />
    //   : row.original.peerReviewed == true
    //   ? <SquareCheckIcon />
    //   : <SquareIcon />
    // },
    cell: ({row, cell}) => (
      <CheckboxEx
        outerClassName="justify-center"
        disabled={true}
        checked={typeof row.original.peerReviewed === "boolean" ? row.original.peerReviewed : "indeterminate"}
      />
    ),
    meta: {
      className: "text-center"
    }
  },
  actionColumn
]

export const columnVisibility = {
  ... trackedEntityColumnVisibility,
  title: true,
  abbreviation: false,
  url: false,
  issn: true,
  publisher: false,
  notes: false
}