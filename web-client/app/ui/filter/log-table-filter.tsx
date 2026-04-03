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

import Log from "@/app/model/Log"
import DataTableFilterProps from "../data-table/data-table-filter"
import DataTableViewOptions from "../data-table/data-table-view-options"
import { LogQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ChangeEvent, useCallback, useContext, useEffect, useRef, useState } from "react"
import { formatDate, isEqual } from "@/lib/utils"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Button } from "@/components/ui/button"
import InputEx from "../ext/input-ex"
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { filter, LoggerEx } from "@/lib/logger"
import ExportDialog from "../dialog/export-dialog"
import { CalendarIcon, ChevronDownIcon, RotateCwIcon } from "lucide-react"
import UserCombobox from "../ext/user-combobox"

const logger = new LoggerEx(filter, "[LogTableFilter] ")

export default function LogTableFilter(
  {
    table,
    refetch,
    loadingPathWithSearchParams,
  } : DataTableFilterProps<Log>) {
  logger.debug("render")

  const {queries, setFilter, setPagination} = useContext(GlobalContext)
  const {filter, pagination} = queries.Log as QueryState<LogQueryFilter>
  const [from, setFrom] = useState<Date|undefined>(filter.from)
  const [fromOpen, setFromOpen] = useState(false)
  const [to, setTo] = useState<Date|undefined>(filter.to)
  const [toOpen, setToOpen] = useState(false)
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [transactionKind, setTransactionKind] = useState(filter.transactionKinds?.[0] ?? '')
  const [entityKind, setEntityKind] = useState(filter.entityKind ?? '')
  const [entityId, setEntityId] = useState(filter.entityId ?? '')

  const updateFilter = useCallback((
    newEntityKind: string,
    newEntityId: string,
    newUserId: string,
    newTransactionKind: string,
    newFrom: Date | undefined,
    newTo: Date | undefined) => {
      logger.trace("updateFilter: newEntityKind=%s, newEntityId=%s, newUserId=%s, newTransactionKind=%s, newFrom=%s, newTo=%s",
        newEntityKind, newEntityId, newUserId, newTransactionKind, newFrom, newTo)
      if (!loadingPathWithSearchParams) {
        const newFilter = {
          entityKind: newEntityKind || undefined,
          entityId: newEntityId || undefined,
          userId: newUserId || undefined,
          transactionKinds: newTransactionKind ? [newTransactionKind] : undefined,
          from: newFrom,
          to: newTo,
        } as LogQueryFilter
        if (!isEqual(newFilter as LogQueryFilter, filter)) {
          logger.trace("updateFilter from %o to %o", filter, newFilter)
          setFilter("Log", newFilter)
          if (pagination.pageIndex != 0) {
            logger.trace("updateFilter: reset pageIndex to 0")
            setPagination("Log", {pageIndex: 0, pageSize: pagination.pageSize});
          }
        }
      }
    }, [loadingPathWithSearchParams, filter, setFilter, pagination, setPagination])

  // If the filter changes, refresh the UI to match.
  const prevFilter = useRef<LogQueryFilter>({})
  useEffect(() => {
    logger.trace("effect2 (1)")
    if (!isEqual(filter, prevFilter.current)) {
      logger.trace("effect2 (2): filter changed from %o to %o", prevFilter.current, filter)
      prevFilter.current = filter

      if (entityKind !== (filter?.entityKind?.[0] ?? ''))
        setEntityKind(filter?.entityKind ?? '')
      if (entityId !== (filter?.entityId ?? ''))
        setEntityId(filter?.entityId ?? '')
      if (userId !== filter?.userId)
        setUserId(filter?.userId ?? '')
      if (transactionKind !== (filter?.transactionKinds?.[0] ?? ''))
        setTransactionKind(filter?.transactionKinds?.[0] ?? '')
      if (from?.valueOf() !== filter?.from?.valueOf())
        setFrom(filter?.from)
      if (to?.valueOf() !== filter?.to?.valueOf())
        setTo(filter?.to)
    }
  }, [filter, entityKind, entityId, userId, transactionKind, from, to])

  const handleFromChange = useCallback((from?: Date) => {
    setFrom(from)
    setFromOpen(false)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, to, updateFilter])

  const handleToChange = useCallback((to?: Date) => {
    setTo(to)
    setToOpen(false)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, updateFilter])

  const handleUserIdChange = useCallback((userId: string) => {
    userId = userId === "ALL" ? '' : userId
    setUserId(userId)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, transactionKind, from, to, updateFilter])

  const handleTransactionKindChange = useCallback((transactionKind: string) => {
    transactionKind = transactionKind === "ALL" ? '' : transactionKind
    setTransactionKind(transactionKind)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, from, to, updateFilter])

  const handleEntityKindChange = useCallback((entityKind: string) => {
    entityKind = entityKind === "ALL" ? '' : entityKind
    setEntityKind(entityKind)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityId, userId, transactionKind, from, to, updateFilter])

  const handleEntityIdChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const entityId = e.target.value?.toString() ?? ''
    setEntityId(entityId)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, userId, transactionKind, from, to, updateFilter])

  const handleClear = useCallback(() => {
    setFrom(undefined)
    setTo(undefined)
    setUserId('')
    setTransactionKind('')
    setEntityKind('')
    setEntityId('')
    updateFilter('', '', '', '', undefined, undefined)
  }, [setFrom, setTo, setUserId, setTransactionKind, updateFilter])

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <Popover open={fromOpen} onOpenChange={setFromOpen}>
          <PopoverTrigger id="from" asChild>
            <Button
              type="button"
              variant={"outline"}
              className="justify-start text-left font-normal">
              <CalendarIcon />
              {from ? (
                formatDate(from)
              ) : (
                <span className="text-gray-500">Start Date</span>
              )}
              <ChevronDownIcon className="text-gray-500" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              captionLayout="dropdown"
              weekStartsOn={1}
              selected={from}
              onSelect={handleFromChange}
            />
          </PopoverContent>
        </Popover>
        <Popover open={toOpen} onOpenChange={setToOpen}>
          <PopoverTrigger id="to" asChild>
            <Button
              type="button"
              variant={"outline"}
              className="justify-start text-left font-normal">
              <CalendarIcon />
              {to ? (
                formatDate(to)
              ) : (
                <span className="text-gray-500">End Date</span>
              )}
              <ChevronDownIcon className="text-gray-500" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              captionLayout="dropdown"
              weekStartsOn={1}
              selected={to}
              onSelect={handleToChange}
            />
          </PopoverContent>
        </Popover>
        <UserCombobox
          id="userId"
          className="w-45"
          value={userId ?? ''}
          onValueChange={handleUserIdChange}
          // help="Filter the table to show only relating to the selected user."
        />
        <Select
          value={transactionKind ?? ''}
          onValueChange={handleTransactionKindChange}
        >
          <SelectTrigger id="transactionKind">
            <SelectValue placeholder="Transaction" />
          </SelectTrigger>
          <SelectContent>
            {
              transactionKind
              ? <SelectItem value="ALL">-Clear-</SelectItem>
              : null
            }
            <SelectItem value="COM">Commented</SelectItem>
            <SelectItem value="CRE">Created</SelectItem>
            <SelectItem value="DEL">Deleted</SelectItem>
            <SelectItem value="DRA">Drafted</SelectItem>
            <SelectItem value="LNK">Linked</SelectItem>
            <SelectItem value="PUB">Published</SelectItem>
            <SelectItem value="SUS">Suspended</SelectItem>
            <SelectItem value="UNL">Unlinked</SelectItem>
            <SelectItem value="UPD">Updated</SelectItem>
          </SelectContent>
        </Select>
        <Select
          value={entityKind ?? ''}
          onValueChange={handleEntityKindChange}
        >
          <SelectTrigger id="entityKind">
            <SelectValue placeholder="Record Kind" />
          </SelectTrigger>
          <SelectContent>
            {
              entityKind
              ? <SelectItem value="ALL">-Clear-</SelectItem>
              : null
            }
            <SelectItem value="CLA">Claim</SelectItem>
            <SelectItem value="DEC">Declaration</SelectItem>
            <SelectItem value="GRP">Group</SelectItem>
            <SelectItem value="JOU">Journal</SelectItem>
            <SelectItem value="LNK">Link</SelectItem>
            <SelectItem value="PER">Person</SelectItem>
            <SelectItem value="PUB">Publication</SelectItem>
            <SelectItem value="PBR">Publisher</SelectItem>
            <SelectItem value="QUO">Quotation</SelectItem>
            <SelectItem value="TOP">Topic</SelectItem>
            <SelectItem value="USR">User</SelectItem>
          </SelectContent>
        </Select>
        <InputEx
          id="recordId"
          outerClassName="w-40"
          className="text-right"
          placeholder="Record ID"
          value={entityId}
          onChange={handleEntityIdChange}
          delay={500}
          clear
          help="Filter the table to show only the record with the specified ID. Other filters are retained but ignored."
        />
        <ButtonEx
          id="refresh"
          type="button"
          variant="outline"
          help="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCwIcon />
        </ButtonEx>
        <Button
          id="reset"
          type="button"
          variant="outline"
          title="Clear all filters"
          onClick={handleClear}
        >
          Clear
        </Button>
        <ExportDialog recordKind="Log" />
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}

LogTableFilter.whyDidYouRender = true