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

import { Checkbox } from "@/components/ui/checkbox"
import Search from "./search"
import { DataTableFilterProps, DataTableViewOptions } from "../data-table/data-table-view-options"
import { getRecordLinkProperties } from "@/lib/utils"
import { useCallback, useContext, useEffect, useState } from "react"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { LinkableEntityQueryFilter } from "@/app/model/schema"
import { GlobalContext, QueryState } from "@/lib/context"
import SelectTriggerEx from "../ext/select-ex"
import ButtonEx from "../ext/button-ex"
import LabelEx from "../ext/label-ex"

export default function LinkableEntityTableFilter<TData, TFilter>({
  table,
  recordKind,
  isLinkableEntity,
}: DataTableFilterProps<TData>) {

  const {
    masterTopicId,
    masterRecordKind,
    masterRecordId,
    showOnlyLinkedRecords,
    queries,
    setFilter,
  } = useContext(GlobalContext)
  const onFilterChange = useCallback((filter: any) => {
    setFilter(recordKind, filter)
  }, [setFilter])
  const queryState = queries[recordKind] as QueryState<LinkableEntityQueryFilter>
  const {filter} = queryState
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)

  const updateFilter = useCallback((status: string, text: string, advanced: boolean) => {
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
  }, [masterTopicId, masterRecordKind, masterRecordId, showOnlyLinkedRecords, onFilterChange])

  useEffect(() => {
    updateFilter(status, text, advanced)
  }, [masterTopicId, masterRecordKind, masterRecordId, showOnlyLinkedRecords])

  const handleStatusChange = useCallback((status: string) => {
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced)
  }, [setStatus, updateFilter, text, advanced, showOnlyLinkedRecords])

  const handleTextChange = useCallback((text: string) => {
    setText(text)
    updateFilter(status, text, advanced)
  }, [setText, updateFilter, status, advanced, showOnlyLinkedRecords])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    setAdvanced(advanced)
    updateFilter(status, text, advanced)
  }, [setAdvanced, updateFilter, status, text, showOnlyLinkedRecords])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    updateFilter('', '', false)
  }, [setStatus, setText, setAdvanced, updateFilter])

  // If the user resets all settings, refresh the UI to match.
  useEffect(() => {
    const newFilter = queries[recordKind]?.filter
    if (status !== (newFilter.status?.[0] ?? ''))
      setStatus(newFilter.status?.[0] ?? '')
    if (text !== (newFilter.text ?? ''))
      // FIXME: this doesn't clear the displayed value.
      setText(newFilter.text ?? '')
    if (advanced !== !!newFilter.advancedSearch)
      setAdvanced(!!newFilter.advancedSearch)
  }, [queries[recordKind]?.filter])

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <Select
          value={status ?? ''}
          onValueChange={handleStatusChange}
        >
          <SelectTriggerEx id="status" help="Filter the table to show only records with this status">
            <SelectValue placeholder="Status" />
          </SelectTriggerEx>
          <SelectContent>
            {
              status
              ? <SelectItem value="ALL">-Clear-</SelectItem>
              : null
            }
            <SelectItem value="DRA">Draft</SelectItem>
            <SelectItem value="PUB">Published</SelectItem>
            <SelectItem value="SUS">Suspended</SelectItem>
            <SelectItem value="DEL">Deleted</SelectItem>
          </SelectContent>
        </Select>
        <Search value={text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          checked={advanced}
          onCheckedChange={handleAdvancedSearchChange}
        />
        <LabelEx htmlFor="advanced" help="Use advanced text search syntax">Advanced</LabelEx>
        <ButtonEx
          outerClassName="flex-grow"
          variant="outline"
          help="Clear all filters"
          onClick={handleReset}
        >
          Reset
        </ButtonEx>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}