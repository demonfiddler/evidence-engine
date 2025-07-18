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

import { Checkbox } from "@/components/ui/checkbox";
import Search from "../filter/search";
import { DataTableViewOptions, DataTableViewOptionsProps } from "./data-table-view-options";
import { SearchSettings } from "@/lib/utils";
import { Dispatch, SetStateAction } from "react";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
//import { Label } from "@/components/ui/label";
import { StatusKind } from "@/app/model/schema";

type DataTableFilterProps<TData> = DataTableViewOptionsProps<TData> & {
  isLinkableEntity: boolean
  search: SearchSettings
  onSearchChange: Dispatch<SetStateAction<SearchSettings>>
}

export default function DataTableFilter<TData>({
  table,
  isLinkableEntity,
  search,
  onSearchChange
}: DataTableFilterProps<TData>) {

  function handleStatusChange(status: string) {
    onSearchChange({
      ...search,
      status: (status && status != "ALL") ? status as StatusKind : undefined
    })
  }

  function handleTextChange(text?: string) {
    onSearchChange({
      ...search,
      text: text ?? undefined
    })
  }

  function handleAdvancedSearchChange(advancedSearch: boolean) {
    onSearchChange({
      ...search,
      advancedSearch
    })
  }

  function handleShowOnlyLinkedRecordsChange(showOnlyLinkedRecords: boolean) {
    onSearchChange({
      ...search,
      showOnlyLinkedRecords
    })
  }

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        {/* <Label>Status:</Label> */}
        <Select
          value={search.status ?? ''}
          onValueChange={handleStatusChange}
        >
          <SelectTrigger id="kind" className="">
            <SelectValue placeholder="Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Status Kinds</SelectLabel>
              {
                search.status
                ? <SelectItem value="ALL">-Clear-</SelectItem>
                : null
              }
              <SelectItem value="DRA">Draft</SelectItem>
              <SelectItem value="PUB">Published</SelectItem>
              <SelectItem value="SUS">Suspended</SelectItem>
              <SelectItem value="DEL">Deleted</SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
        <Search value={search.text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          title="Use advanced text search syntax"
          checked={search.advancedSearch}
          onCheckedChange={handleAdvancedSearchChange}
        />
        <label htmlFor="advanced">Advanced</label>
        {
          isLinkableEntity
          ? <>
              <Checkbox
                id="linkedOnly"
                title="Only show records linked to the current master record(s)"
                checked={search.showOnlyLinkedRecords}
                onCheckedChange={handleShowOnlyLinkedRecordsChange}
              />
              <label htmlFor="linkedOnly" className="flex-none">Show only linked records</label>
            </>
          : null
        }
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}