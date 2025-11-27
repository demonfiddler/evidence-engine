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
import DataTableViewOptions from "../data-table/data-table-view-options"
import DataTableFilterProps from "../data-table/data-table-filter"
import { LinkableEntityQueryFilterIdProperty, getEntityKind, getRecordLinkProperties, isEqual } from "@/lib/utils"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { LinkableEntityQueryFilter } from "@/app/model/schema"
import { GlobalContext, QueryState } from "@/lib/context"
import SelectTriggerEx from "../ext/select-ex"
import ButtonEx from "../ext/button-ex"
import LabelEx from "../ext/label-ex"
import useAuth from "@/hooks/use-auth"
import InputEx from "../ext/input-ex"
import { RotateCw } from "lucide-react"
import RecordKind from "@/app/model/RecordKind"
import { filter, LoggerEx } from "@/lib/logger"
import { anything } from "@/types/types"
import ExportDialog from "../dialog/export-dialog"
import ImportDialog from "../dialog/import-dialog"

const logger = new LoggerEx(filter, "[LinkableEntityTableFilter] ")

export default function LinkableEntityTableFilter<TData, TFilter>({
  table,
  recordKind,
  isLinkableEntity,
  refetch,
  loadingPathWithSearchParams,
  importAccept,
}: DataTableFilterProps<TData>) {
  logger.debug("render")

  const {
    masterTopicId,
    masterTopicRecursive,
    masterRecordKind,
    masterRecordId,
    showOnlyLinkedRecords,
    queries,
    setFilter,
  } = useContext(GlobalContext)
  const {user} = useAuth()
  const queryState = queries[recordKind] as QueryState<LinkableEntityQueryFilter>
  const {filter} = queryState
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  const [recordId, setRecordId] = useState(filter.recordId ?? '')

  const updateFilter = useCallback((status: string, text: string, advanced: boolean, recordId: string) => {
    logger.trace("updateFilter: status='%s', text='%s', advanced=%s, recordId='%s'", status, text, advanced, recordId)
    if (!loadingPathWithSearchParams) {
      const newFilter = {
        status: status ? [status] : undefined,
        text: text || undefined,
        advancedSearch: text && advanced || undefined,
        recordId: recordId || undefined,
      } as TFilter
      if (isLinkableEntity && showOnlyLinkedRecords) {
        const leFilter = newFilter as LinkableEntityQueryFilter
        if (masterTopicId) {
          leFilter.topicId = masterTopicId
          leFilter.recursive = masterTopicRecursive
        }
        if (masterRecordId) {
          const [, otherRecordKindProperty,, otherRecordIdProperty] = getRecordLinkProperties(recordKind, masterRecordKind)
          if (otherRecordIdProperty) {
            leFilter[otherRecordKindProperty as LinkableEntityQueryFilterIdProperty] = getEntityKind(masterRecordKind)
            leFilter[otherRecordIdProperty as LinkableEntityQueryFilterIdProperty] = masterRecordId
          }
        }
      }
      if (!isEqual(newFilter as LinkableEntityQueryFilter, filter)) {
        logger.trace("updateFilter from %o to %o", filter, newFilter as anything)
        setFilter(recordKind, newFilter)
      }
    }
  }, [loadingPathWithSearchParams, isLinkableEntity, recordKind, filter, masterTopicId, masterTopicRecursive, masterRecordKind, masterRecordId, showOnlyLinkedRecords, setFilter])

  // If the filter changes, refresh the UI to match.
  const prevFilter = useRef<LinkableEntityQueryFilter>({})
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
      if (recordId !== (filter?.recordId ?? ''))
        setRecordId(filter?.recordId ?? '')
    }
  }, [filter, status, text, advanced, recordId]) // previously [queries[recordKind]?.filter]

  // If MasterEntityFilter is already set or changes, update the filter.
  const prevMasterTopicId = useRef<string>(undefined)
  const prevMasterTopicRecursive = useRef<boolean>(true)
  const prevMasterRecordKind = useRef<RecordKind>("None")
  const prevMasterRecordId = useRef<string>(undefined)
  const prevShowOnlyLinkedRecords = useRef<boolean>(false)
  useEffect(() => {
    logger.trace("effect2 (1)")
    if (masterTopicId !== prevMasterTopicId.current ||
      masterTopicRecursive !== prevMasterTopicRecursive.current ||
      masterRecordKind !== prevMasterRecordKind.current ||
      masterRecordId !== prevMasterRecordId.current ||
      showOnlyLinkedRecords !== prevShowOnlyLinkedRecords.current) {

      logger.trace("effect2 (2)")

      prevMasterTopicId.current = masterTopicId
      prevMasterTopicRecursive.current = masterTopicRecursive
      prevMasterRecordKind.current = masterRecordKind
      prevMasterRecordId.current = masterRecordId
      prevShowOnlyLinkedRecords.current = showOnlyLinkedRecords
      updateFilter(status, text, advanced, recordId)
    }
  }, [updateFilter, status, text, advanced, recordId, masterTopicId, masterTopicRecursive, masterRecordKind, masterRecordId, showOnlyLinkedRecords])

  const handleStatusChange = useCallback((status: string) => {
    logger.trace("handleStatusChange: status='%s'", status)
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, text, advanced, recordId])

  const handleTextChange = useCallback((text: string) => {
    logger.trace("handleTextChange: text='%s'", text)
    setText(text)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, status, advanced, recordId])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    logger.trace("handleAdvancedSearchChange: advanced=%s", advanced)
    setAdvanced(advanced)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter, status, text, recordId])

  const handleRecordIdChange = useCallback((recordId: string) => {
    logger.trace("handleRecordIdChange: recordId='%s'", recordId)
    setRecordId(recordId)
    updateFilter(status, text, advanced, recordId)
  }, [updateFilter])

  const handleReset = useCallback(() => {
    logger.trace("handleReset")
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
            <SelectTriggerEx
              id="status"
              help="Filter the table to show only records with this status"
              title="Filter the table to show only records with this status"
            >
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
          : null
        }
        <Search id="searchText" value={text} onChangeValue={handleTextChange} />
        {/* See https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode */}
        <Checkbox
          id="advanced"
          checked={advanced}
          onCheckedChange={handleAdvancedSearchChange}
          title="Use advanced text search syntax. See info hover tip to the right."
        />
        <LabelEx htmlFor="advanced" help="Use advanced text search syntax. See MariaDB documentation at https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode">Advanced</LabelEx>
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
          title="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCw />
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
        <ImportDialog
          recordKind={recordKind}
          accept={importAccept}
        />
        <ExportDialog recordKind={recordKind} />
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}

LinkableEntityTableFilter.whyDidYouRender = true