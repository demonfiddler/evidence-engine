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

import Log from "@/app/model/Log"
import DataTableFilterProps from "../data-table/data-table-filter"
import DataTableViewOptions from "../data-table/data-table-view-options"
import { LogQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ChangeEvent, useCallback, useContext, useEffect, useRef, useState } from "react"
import { formatDate, isEqual } from "@/lib/utils"
import { READ_USERS } from "@/lib/graphql-queries"
import { useQuery } from "@apollo/client"
import { toast } from "sonner"
import IPage from "@/app/model/IPage"
import User from "@/app/model/User"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Button } from "@/components/ui/button"
import { CalendarIcon, ChevronDownIcon } from "@heroicons/react/24/outline"
import Spinner from "../misc/spinner"
import InputEx from "../ext/input-ex"
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { RotateCw } from "lucide-react"
import { filter, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(filter, "[LogTableFilter] ")

export default function LogTableFilter(
  {
    table,
    refetch,
    loadingPathWithSearchParams,
  } : DataTableFilterProps<Log>) {
  logger.debug("render")

  const {queries, setFilter} = useContext(GlobalContext)
  const {filter} = queries["Log"] as QueryState<LogQueryFilter>
  const [from, setFrom] = useState<Date|undefined>(filter.from)
  const [fromOpen, setFromOpen] = useState(false)
  const [to, setTo] = useState<Date|undefined>(filter.to)
  const [toOpen, setToOpen] = useState(false)
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [transactionKind, setTransactionKind] = useState(filter.transactionKinds?.[0] ?? '')
  const [entityKind, setEntityKind] = useState(filter.entityKind ?? '')
  const [entityId, setEntityId] = useState(filter.entityId ?? '')

  const updateFilter = useCallback((
    entityKind: string,
    entityId: string,
    userId: string,
    transactionKind: string,
    from: Date | undefined,
    to: Date | undefined) => {
      logger.trace("updateFilter: entityKind=%s, entityId=%s, userId=%s, transactionKind=%s, from=%s, to=%s", entityKind, entityId, userId, transactionKind, from, to)
      if (!loadingPathWithSearchParams) {
        const newFilter = {
          entityKind: entityKind || undefined,
          entityId: entityId || undefined,
          userId: userId || undefined,
          transactionKinds: transactionKind ? [transactionKind] : undefined,
          from,
          to,
        } as LogQueryFilter
        if (!isEqual(newFilter as LogQueryFilter, filter)) {
          logger.trace("updateFilter from %o to %o", filter, newFilter)
          setFilter("Log", newFilter)
        }
      }
    }, [loadingPathWithSearchParams, filter, setFilter])

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

  const handleReset = useCallback(() => {
    setFrom(undefined)
    setTo(undefined)
    setUserId('')
    setTransactionKind('')
    setEntityKind('')
    setEntityId('')
    updateFilter('', '', '', '', undefined, undefined)
  }, [setFrom, setTo, setUserId, setTransactionKind, updateFilter])

  const result = useQuery(
    READ_USERS,
    {
      variables: {
        pageSort: {
          sort: {
            orders: [
              {property: "username"}
            ]
          }
        }
      },
    }
  )
  if (result.error) {
    // TODO: display user-friendly error notification
    toast.error(`Operation failed:\n\n${result.error.message}`)
    logger.error("Operation failed: %o", result.error)
  }
  const users = (result.data?.users as IPage<User>)?.content

  return (
    <div className="flex flex-col gap-2">
      <Spinner loading={result.loading} className="absolute inset-0 bg-black/20 z-50" />
      <div className="flex items-center gap-2">
        <Popover open={fromOpen} onOpenChange={setFromOpen}>
          <PopoverTrigger id="from" asChild>
            <Button
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
        <Select
          value={userId ?? ''}
          onValueChange={handleUserIdChange}
        >
          <SelectTrigger id="userId">
            <SelectValue placeholder="User" />
          </SelectTrigger>
          <SelectContent>
            {
              userId
              ? <SelectItem value="ALL">-Clear-</SelectItem>
              : null
            }
            {
              users?.map(user => <SelectItem key={user.id} value={user.id ?? ''}>{user.username}</SelectItem>)
            }
          </SelectContent>
        </Select>
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
            <SelectItem value="CRE">Created</SelectItem>
            <SelectItem value="UPD">Updated</SelectItem>
            <SelectItem value="DEL">Deleted</SelectItem>
            <SelectItem value="LNK">Linked</SelectItem>
            <SelectItem value="UNL">Unlinked</SelectItem>
          </SelectContent>
        </Select>
        <Select
          value={entityKind ?? ''}
          onValueChange={handleEntityKindChange}
        >
          <SelectTrigger id="kind">
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
          className="w-30 text-right"
          placeholder="Record ID"
          value={entityId}
          onChange={handleEntityIdChange}
          delay={500}
          clearOnEscape={true}
          help="Filter the table to show only the record with the specified ID. Other filters are retained but ignored."
        />
        <ButtonEx
          variant="outline"
          help="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCw />
        </ButtonEx>
        <Button variant="outline" title="Clear all filters" onClick={handleReset}>Reset</Button>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}

LogTableFilter.whyDidYouRender = true