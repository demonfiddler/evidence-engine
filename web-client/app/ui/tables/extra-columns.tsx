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
import { ChevronDownIcon, ChevronUpIcon, MoreHorizontalIcon } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  // DropdownMenuPortal,
  // DropdownMenuSub,
  // DropdownMenuSubContent,
  // DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Checkbox } from "@/components/ui/checkbox"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import IBaseEntity from "@/app/model/IBaseEntity"
import ITrackedEntity from "@/app/model/ITrackedEntity"
import { useCallback, useContext } from "react"
// import Publication from "@/app/model/Publication"
import { GlobalContext } from "@/lib/context"

// type CitationKind = "MLA" | "APA" | "CHI" | "AMA" | "CSE"

export const actionColumn: ColumnDef<ITrackedEntity> = {
  id: "action",
  enableSorting: false,
  enableHiding: false,
  enableResizing: false,
  size: 72,
  header: "Actions",
  cell: ({ cell, row }) => {
    const {setCommentsDialogOpen, setLogDialogOpen} = useContext(GlobalContext)
    const handleViewComments = useCallback(() => {
      if (!row.getIsSelected())
        row.toggleSelected()
      setCommentsDialogOpen(true)
    }, [row, setCommentsDialogOpen])
    const handleViewLog = useCallback(() => {
      if (!row.getIsSelected())
        row.toggleSelected()
      setLogDialogOpen(true)
    }, [row, setLogDialogOpen])
    // const handleCite = useCallback((publication: Publication, kind: CitationKind) => {
    //   if (!row.getIsSelected())
    //     row.toggleSelected()
    //   toast.info(`Cite: ${kind}`)
    // }, [row])

    return (
      <DropdownMenu key={cell.id}>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="justify-self-center h-8 w-8 p-0">
            <span className="sr-only">Open menu</span>
            <MoreHorizontalIcon className="h-4 w-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={handleViewComments}>Comments...</DropdownMenuItem>
          <DropdownMenuItem onClick={handleViewLog}>Log...</DropdownMenuItem>
          {/*
            row.original.entityKind === "Publication"
            ? <DropdownMenuSub>
              <DropdownMenuSubTrigger>Cite</DropdownMenuSubTrigger>
              <DropdownMenuPortal>
                <DropdownMenuSubContent>
                <DropdownMenuItem onClick={() => handleCite(row.original as Publication, "MLA")}>MLA</DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleCite(row.original as Publication, "APA")}>APA</DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleCite(row.original as Publication, "CHI")}>Chicago</DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleCite(row.original as Publication, "AMA")}>AMA</DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleCite(row.original as Publication, "CSE")}>CSE</DropdownMenuItem>
                </DropdownMenuSubContent>
              </DropdownMenuPortal>
            </DropdownMenuSub>
            : null
          */}
        </DropdownMenuContent>
      </DropdownMenu>
    )
  },
  meta: {
    className: "text-center"
  }
}

export const expandColumn: ColumnDef<IBaseEntity> = {
  id: "expand",
  enableSorting: false,
  enableHiding: false,
  enableResizing: false,
  size: 50,
  header: ({ table }) => (
      table.getCanSomeRowsExpand()
        ? <Button variant="ghost" className="w-8 h-8" onClick={table.getToggleAllRowsExpandedHandler()}>
          {table.getIsAllRowsExpanded() ? <ChevronUpIcon /> : <ChevronDownIcon />}
        </Button>
        : null
  ),
  cell: ({ row, cell }) =>
    row.getCanExpand() ?
      <Button key={cell.id} variant="ghost" className="w-8 h-8" onClick={row.getToggleExpandedHandler()}>
          {row.getIsExpanded() ? <ChevronUpIcon /> : <ChevronDownIcon />}
      </Button>
      : null,
  meta: {
    className: "text-center"
  }
}

export const selectColumn: ColumnDef<IBaseEntity> = {
  id: "select",
  enableSorting: false,
  enableHiding: false,
  enableResizing: false,
  size: 32,
  header: ({ table }) => (
    table.options.enableMultiRowSelection
    ? <Checkbox
      checked={
        table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && "indeterminate")
      }
      onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
      aria-label="Select all"
    />
    : null
  ),
  cell: ({ table, row, cell }) => (
    table.options.enableMultiRowSelection
    ? <Checkbox
      key={cell.id}
      checked={row.getIsSelected()}
      onCheckedChange={(value) => row.toggleSelected(!!value)}
      aria-label="Select row"
    />
    : <RadioGroup key={cell.id}>
      <RadioGroupItem
        value={row.id}
        checked={row.getIsSelected()}
        onClick={() => row.toggleSelected()}
        aria-label="Select row"
      />
    </RadioGroup>
  ),
  meta: {
    className: "text-center"
  }
}
