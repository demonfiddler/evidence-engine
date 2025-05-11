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
import { MoreHorizontal } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Checkbox } from "@/components/ui/checkbox"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { ChevronDownIcon, ChevronUpIcon } from "@heroicons/react/24/outline"

import DataTableColumnHeader from "@/app/ui/data-table/data-table-column-header"

export const actionColumn: ColumnDef<any> = {
  id: "action",
  header: ({ column }) => (
    <DataTableColumnHeader className="justify-end" column={column} title="Actions" />
  ),
  cell: ({ row }) => {
    return (
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="h-8 w-8 p-0">
            <span className="sr-only">Open menu</span>
            <MoreHorizontal className="h-4 w-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuLabel>Actions</DropdownMenuLabel>
          <DropdownMenuItem>View Log</DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem>View xxx</DropdownMenuItem>
          <DropdownMenuItem>View yyy</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    )
  },
  enableSorting: false,
  enableHiding: false,
}

export const expandColumn: ColumnDef<any> = {
  id: "expand",
  header: ({ table }) => (
    table.getCanSomeRowsExpand() ?
      <Button variant="ghost" className="" onClick={table.getToggleAllRowsExpandedHandler()}>
        {table.getIsAllRowsExpanded() ? <ChevronUpIcon /> : <ChevronDownIcon />}
      </Button>
      : <></>
  ),
  cell: ({ row }) =>
    row.getCanExpand() ?
      <Button variant="ghost" onClick={row.getToggleExpandedHandler()}>
          {row.getIsExpanded() ? <ChevronUpIcon /> : <ChevronDownIcon />}
      </Button>
      : <></>,
  enableSorting: false,
  enableHiding: false,
}

export const selectColumn: ColumnDef<any> = {
  id: "select",
  header: ({ table }) => (
      table.options.enableMultiRowSelection ?
      <Checkbox
        checked={
            table.getIsAllPageRowsSelected() ||
            (table.getIsSomePageRowsSelected() && "indeterminate")
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
      : <></>
    ),
  cell: ({ table, row }) => (
    table.options.enableMultiRowSelection ?
    <Checkbox
      checked={row.getIsSelected()}
      onCheckedChange={(value) => row.toggleSelected(!!value)}
      aria-label="Select row"
    />
    :
    <RadioGroup>
      <RadioGroupItem
        value={row.id}
        checked={row.getIsSelected()}
        onClick={() => row.toggleSelected()}
        aria-label="Select row"
      />
    </RadioGroup>
  ),
  enableSorting: false,
  enableHiding: false,
}
