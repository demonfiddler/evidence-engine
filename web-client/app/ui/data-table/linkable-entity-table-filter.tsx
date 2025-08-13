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
import { DataTableFilterProps, DataTableViewOptions } from "./data-table-view-options";
import { getRecordLinkProperties } from "@/lib/utils";
import { useCallback, useContext, useEffect, useState } from "react";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { LinkableEntityQueryFilter } from "@/app/model/schema";
import { GlobalContext, QueryState } from "@/lib/context";
import { Button } from "@/components/ui/button";

export default function LinkableEntityTableFilter<TData, TFilter>({
  table,
  recordKind,
  isLinkableEntity,
}: DataTableFilterProps<TData>) {

  const {
    masterTopicId,
    masterRecordKind,
    masterRecordId,
    queries,
    setFilter,
    setShowOnlyLinkedRecords
  } = useContext(GlobalContext)
  const onFilterChange = useCallback((filter: any) => {
    setFilter(recordKind, filter)
  }, [setFilter])
  // This was an experiment in remembering the selected link, but it got too complicated.
  // const onShowOnlyLinkedRecordsChange = useCallback((filter: any) => {
  //   setShowOnlyLinkedRecords(recordKind, filter)
  // }, [setShowOnlyLinkedRecords])
  const queryState = queries[recordKind] as QueryState<LinkableEntityQueryFilter>
  const {filter} = queryState
  const showOnlyLinkedRecords = queryState.showOnlyLinkedRecords ?? false
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  // const [showOnlyLinkedRecords, setShowOnlyLinkedRecords] = useState(false)

  const updateFilter = useCallback((status: string, text: string, advanced: boolean, showOnlyLinkedRecords: boolean) => {
    const filter = {
      status: status ? [status] : undefined,
      text: text || undefined,
      advancedSearch: text && advanced || undefined
    } as TFilter
    if (isLinkableEntity && showOnlyLinkedRecords) {
      const leFilter = filter as LinkableEntityQueryFilter
      if (masterTopicId) {
        leFilter.topicId = masterTopicId
        leFilter.recursive = true
      }
      if (masterRecordId) {
        const [, otherRecordIdProperty] = getRecordLinkProperties(recordKind, masterRecordKind)
        if (otherRecordIdProperty)
          leFilter[otherRecordIdProperty] = masterRecordId // TODO: fix this TypeScript error
      }
    }
    onFilterChange(filter)
  }, [showOnlyLinkedRecords, masterTopicId, masterRecordKind, masterRecordId, onFilterChange])
  useEffect(() => {
    updateFilter(status, text, advanced, showOnlyLinkedRecords)
  }, [masterTopicId, masterRecordKind, masterRecordId, showOnlyLinkedRecords])

  const handleStatusChange = useCallback((status: string) => {
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, showOnlyLinkedRecords)
  }, [setStatus, updateFilter, text, advanced, showOnlyLinkedRecords])

  const handleTextChange = useCallback((text: string) => {
    setText(text)
    updateFilter(status, text, advanced, showOnlyLinkedRecords)
  }, [setText, updateFilter, status, advanced, showOnlyLinkedRecords])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    setAdvanced(advanced)
    updateFilter(status, text, advanced, showOnlyLinkedRecords)
  }, [setAdvanced, updateFilter, status, text, showOnlyLinkedRecords])

  const handleShowOnlyLinkedRecordsChange = useCallback((showOnlyLinkedRecords: boolean) => {
    setShowOnlyLinkedRecords(recordKind, showOnlyLinkedRecords)
    updateFilter(status, text, advanced, showOnlyLinkedRecords)
  }, [setShowOnlyLinkedRecords, updateFilter, status, text, advanced])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    setShowOnlyLinkedRecords(recordKind, false)
    updateFilter('', '', false, false)
  }, [setStatus, setText, setAdvanced, setShowOnlyLinkedRecords, updateFilter])

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <Select
          value={status ?? ''}
          onValueChange={handleStatusChange}
        >
          <SelectTrigger id="kind">
            <SelectValue placeholder="Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Status Kinds</SelectLabel>
              {
                status
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
        <Search value={text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          title="Use advanced text search syntax"
          checked={advanced}
          onCheckedChange={handleAdvancedSearchChange}
        />
        <label htmlFor="advanced">Advanced</label>
        {
          isLinkableEntity
          ? <>
              <Checkbox
                id="linkedOnly"
                title="Only show records linked to the current master record(s)"
                checked={showOnlyLinkedRecords}
                onCheckedChange={handleShowOnlyLinkedRecordsChange}
              />
              <label htmlFor="linkedOnly" className="flex-none">Show only linked records</label>
            </>
          : null
        }
        <Button variant="outline" title="Clear all filters" onClick={handleReset}>Reset</Button>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}