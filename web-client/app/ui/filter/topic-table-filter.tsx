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

import Topic from "@/app/model/Topic"
import { TopicQueryFilter } from "@/app/model/schema"
import { Checkbox } from "@/components/ui/checkbox"
import Search from "./search"
import { DataTableFilterProps, DataTableViewOptions } from "../data-table/data-table-view-options"
import { useCallback, useContext, useState } from "react"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { GlobalContext, QueryState } from "@/lib/context"
import { Button } from "@/components/ui/button"
import useAuth from "@/hooks/use-auth"
import InputEx from "../ext/input-ex"
import LabelEx from "../ext/label-ex"

export default function TopicTableFilter({
  table,
}: DataTableFilterProps<Topic>) {
  const {user} = useAuth()
  const {queries, setFilter} = useContext(GlobalContext)
  const onFilterChange = useCallback((filter: any) => {
    setFilter("Topic", filter)
  }, [setFilter])
  const {filter} = queries["Topic"] as QueryState<TopicQueryFilter>
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  const [recordId, setRecordId] = useState(filter.recordId ?? '')

  const updateFilter = useCallback((status: string, text: string, advanced: boolean, recordId: string) => {
    const filter = {
      status: status ? [status] : undefined,
      text: text || undefined,
      advancedSearch: text && advanced || undefined,
      recordId: recordId || undefined,
      parentId: recordId ? undefined : "-1",
    } as TopicQueryFilter
    onFilterChange(filter)
  }, [onFilterChange])

  const handleStatusChange = useCallback((status: string) => {
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, text, advanced, recordId])

  const handleTextChange = useCallback((text: string) => {
    setText(text)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, status, advanced, recordId])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    setAdvanced(advanced)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, status, text, recordId])

  const handleRecordIdChange = useCallback((recordId: string) => {
    if (recordId) {
      setStatus('')
      setText('')
      setAdvanced(false)
    }
    setRecordId(recordId)
    updateFilter('', '', false, recordId)
  }, [updateFilter])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    setRecordId('')
    updateFilter('', '', false, '')
  }, [updateFilter])

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        {
          user
          ? <Select
            value={status ?? ''}
            onValueChange={handleStatusChange}
          >
            <SelectTrigger id="kind">
              <SelectValue placeholder="Status" />
            </SelectTrigger>
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
          : null
        }
        <Search value={text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          title="Use advanced text search syntax"
          checked={advanced}
          onCheckedChange={handleAdvancedSearchChange}
        />
        <LabelEx htmlFor="advanced" help="Use advanced text search syntax">Advanced</LabelEx>
        <InputEx
          outerClassName="w-28"
          className="text-right"
          placeholder="Record ID"
          value={recordId}
          onChange={(e) => handleRecordIdChange(e.target.value)}
          delay={500}
          help="Filter the table to show only the record with the specified ID. Clears all other filters."
        />
        <Button variant="outline" title="Clear all filters" onClick={handleReset}>Reset</Button>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}