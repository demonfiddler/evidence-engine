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
import DataTableFilterProps from "../data-table/data-table-filter"
import DataTableViewOptions from "../data-table/data-table-view-options"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { GlobalContext, QueryState } from "@/lib/context"
import useAuth from "@/hooks/use-auth"
import InputEx from "../ext/input-ex"
import LabelEx from "../ext/label-ex"
import ButtonEx from "../ext/button-ex"
import { RotateCwIcon } from "lucide-react"
import { isEqual } from "@/lib/utils"
import { filter, LoggerEx } from "@/lib/logger"
import ExportDialog from "../dialog/export-dialog"

const logger = new LoggerEx(filter, "[TopicTableFilter] ")

export default function TopicTableFilter({
  table,
  refetch,
  loadingPathWithSearchParams,
}: DataTableFilterProps<Topic>) {
  logger.debug("render")

  const {user} = useAuth()
  const {queries, setFilter} = useContext(GlobalContext)
  const {filter} = queries["Topic"] as QueryState<TopicQueryFilter>
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  const [treeView, setTreeView] = useState(filter.parentId === "-1")
  const [recordId, setRecordId] = useState(filter.recordId ?? '')

  const updateFilter = useCallback((status: string, text: string, advanced: boolean, treeView: boolean, recordId: string) => {
    logger.trace("updateFilter: status=%s, text=%s, advanced=%s, recursive=%s, recordId=%s", status, text, advanced, treeView, recordId)
    if (!loadingPathWithSearchParams) {
      const newFilter = {
        status: status ? [status] : undefined,
        text: text || undefined,
        advancedSearch: advanced || undefined,
        parentId: treeView ? "-1" : undefined,
        recordId: recordId || undefined,
      } as TopicQueryFilter
      if (!isEqual(newFilter, filter)) {
        logger.trace("updateFilter from %o to %o", filter, newFilter)
        setFilter("Topic", newFilter)
      }
    }
  }, [loadingPathWithSearchParams, filter, setFilter])

  // If the filter changes, refresh the UI to match.
  const prevFilter = useRef<TopicQueryFilter>({})
  useEffect(() => {
    logger.trace("effect1 (1)")
    if (!isEqual(filter, prevFilter.current)) {
      logger.trace("effect1 (2): filter changed from %o to %o", prevFilter.current, filter)
      prevFilter.current = filter

      if (status !== (filter?.status?.[0] ?? ''))
        setStatus(filter?.status?.[0] ?? '')
      if (text !== (filter?.text ?? ''))
        setText(filter?.text ?? '')
      if (advanced !== !!filter?.advancedSearch)
        setAdvanced(!!filter?.advancedSearch)
      if (treeView !== (filter?.parentId ===  "-1"))
        setTreeView(filter?.parentId ===  "-1")
      if (recordId !== (filter?.recordId ?? ''))
        setRecordId(filter?.recordId ?? '')
    }
  }, [filter, status, text, advanced, treeView, recordId])

  const handleStatusChange = useCallback((status: string) => {
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, treeView, recordId)
  }, [updateFilter, text, advanced, treeView, recordId])

  const handleTextChange = useCallback((text: string) => {
    setText(text)
    updateFilter(status, text, advanced, treeView, recordId)
  }, [updateFilter, status, advanced, treeView, recordId])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    setAdvanced(advanced)
    updateFilter(status, text, advanced, treeView, recordId)
  }, [updateFilter, status, text, treeView, recordId])

  const handleTreeViewChange = useCallback((treeView: boolean) => {
    setTreeView(treeView)
    updateFilter(status, text, advanced, treeView, recordId)
  }, [updateFilter, status, text, advanced, recordId])

  const handleRecordIdChange = useCallback((recordId: string) => {
    setRecordId(recordId)
    updateFilter(status, text, advanced, treeView, recordId)
  }, [updateFilter])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    setTreeView(true)
    setRecordId('')
    updateFilter('', '', false, true, '')
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
        <Search id="search" value={text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          checked={advanced}
          onCheckedChange={handleAdvancedSearchChange}
        />
        <LabelEx htmlFor="advanced" help="Use advanced text search syntax">Advanced</LabelEx>
        <Checkbox
          id="recursive"
          checked={treeView}
          onCheckedChange={handleTreeViewChange}
        />
        <LabelEx htmlFor="recursive" help="Show topics as an expandable tree">Tree view</LabelEx>
        <InputEx
          id="recordId"
          outerClassName="w-28"
          className="text-right"
          placeholder="Record ID"
          value={recordId}
          onChange={(e) => handleRecordIdChange(e.target.value)}
          delay={500}
          clearOnEscape={true}
          help="Filter the table to show only the record with the specified ID. Other filters are retained but ignored."
        />
        <ButtonEx
          id="refresh"
          variant="outline"
          help="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCwIcon />
        </ButtonEx>
        <ButtonEx
          id="reset"
          outerClassName="flex-grow"
          variant="outline"
          onClick={handleReset}
          help="Clear all filters"
        >
          Reset
        </ButtonEx>
        <ExportDialog recordKind="Topic" />
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}

TopicTableFilter.whyDidYouRender = true