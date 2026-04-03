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
import DataTableViewOptions from "../data-table/data-table-view-options"
import DataTableFilterProps from "../data-table/data-table-filter"
import { LogQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
import { formatDate, isEqual } from "@/lib/utils"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Button } from "@/components/ui/button"
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { filter, LoggerEx } from "@/lib/logger"
import { CalendarIcon, ChevronDownIcon, RotateCwIcon } from "lucide-react"
import UserCombobox from "../ext/user-combobox"

const logger = new LoggerEx(filter, "[LogDialogFilter] ")

export default function LogDialogFilter(
  {
    table,
    auxRecordId,
    refetch,
  } : DataTableFilterProps<Log>) {
  logger.debug("render")

  const {queries, setFilter, setPagination} = useContext(GlobalContext)
  const {filter, pagination} = queries.Log as QueryState<LogQueryFilter>
  const [from, setFrom] = useState<Date|undefined>(filter.from)
  const [fromOpen, setFromOpen] = useState(false)
  const [to, setTo] = useState<Date|undefined>( filter.to)
  const [toOpen, setToOpen] = useState(false)
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [transactionKind, setTransactionKind] = useState(filter.transactionKinds?.[0] ?? '')

  const updateFilter = useCallback((
    newUserId: string,
    newTransactionKind: string,
    newFrom: Date|undefined,
    newTo: Date|undefined) => {
      logger.trace("updateFilter: newUserId=%s, newTransactionKind=%s, newFrom=%s, newTo=%s", newUserId, newTransactionKind, newFrom, newTo)
      const newFilter = {
        entityId: auxRecordId,
        userId: newUserId || undefined,
        transactionKinds: newTransactionKind ? [newTransactionKind] : undefined,
        from: newFrom || undefined,
        to: newTo || undefined
      } as LogQueryFilter
      if (!isEqual(newFilter, filter)) {
        logger.trace("updateFilter from %o to %o", filter, newFilter)
        setFilter("Log", newFilter)
        if (pagination.pageIndex != 0) {
          logger.trace("updateFilter: reset pageIndex to 0")
          setPagination("Log", {pageIndex: 0, pageSize: pagination.pageSize});
        }
      }
  }, [auxRecordId, filter, setFilter, pagination, setPagination])

  const prevAuxRecordId = useRef<string>(undefined)
  useEffect(() => {
    if (auxRecordId !== prevAuxRecordId.current) {
      prevAuxRecordId.current = auxRecordId
      updateFilter(userId, transactionKind, from, to)
    }
  }, [updateFilter, userId, transactionKind, from, to, auxRecordId])

  const handleFromChange = useCallback((from?: Date) => {
    setFrom(from)
    setFromOpen(false)
    updateFilter(userId, transactionKind, from, to)
  }, [userId, transactionKind, to, updateFilter])

  const handleToChange = useCallback((to?: Date) => {
    setTo(to)
    setToOpen(false)
    updateFilter(userId, transactionKind, from, to)
  }, [userId, transactionKind, from, updateFilter])

  const handleUserIdChange = useCallback((userId: string) => {
    userId = userId === "ALL" ? '' : userId
    setUserId(userId)
    updateFilter(userId, transactionKind, from, to)
  }, [transactionKind, from, to, updateFilter])

  const handleTransactionKindChange = useCallback((transactionKind: string) => {
    transactionKind = transactionKind === "ALL" ? '' : transactionKind
    setTransactionKind(transactionKind)
    updateFilter(userId, transactionKind, from, to)
  }, [userId, from, to, updateFilter])

  const handleClear = useCallback(() => {
    setFrom(undefined)
    setTo(undefined)
    setUserId('')
    setTransactionKind('')
    updateFilter('', '', undefined, undefined)
  }, [updateFilter])

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <Popover open={fromOpen} onOpenChange={setFromOpen}>
          <PopoverTrigger id="filter-from" asChild>
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
          <PopoverTrigger id="filter-to" asChild>
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
          id="filter-userId"
          className="w-45"
          value={userId ?? ''}
          onValueChange={handleUserIdChange}
          // help="Filter the table to show only relating to the selected user."
        />
        <Select
          value={transactionKind ?? ''}
          onValueChange={handleTransactionKindChange}
        >
          <SelectTrigger id="filter-transactionKind">
            <SelectValue placeholder="Transaction" />
          </SelectTrigger>
          <SelectContent>
            {
              transactionKind
              ? <SelectItem value="ALL">-Clear-</SelectItem>
              : null
            }
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
        <ButtonEx
          type="button"
          variant="outline"
          help="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCwIcon />
        </ButtonEx>
        <ButtonEx
          type="button"
          variant="outline"
          outerClassName="flex-grow"
          onClick={handleClear}
          help="Clear all filters"
        >
          Clear
        </ButtonEx>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}

LogDialogFilter.whyDidYouRender = true