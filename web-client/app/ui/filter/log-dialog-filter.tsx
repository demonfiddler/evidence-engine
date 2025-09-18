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
import DataTableViewOptions from "../data-table/data-table-view-options"
import DataTableFilterProps from "../data-table/data-table-filter"
import { LogQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
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
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { RotateCw } from "lucide-react"

export default function LogDialogFilter(
  {
    table,
    auxRecordId,
    refetch,
  } : DataTableFilterProps<Log>) {

  const {queries, setFilter} = useContext(GlobalContext)
  const {filter} = queries["Log"] as QueryState<LogQueryFilter>
  const [from, setFrom] = useState<Date|undefined>(filter.from)
  const [fromOpen, setFromOpen] = useState(false)
  const [to, setTo] = useState<Date|undefined>( filter.to)
  const [toOpen, setToOpen] = useState(false)
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [transactionKind, setTransactionKind] = useState(filter.transactionKinds?.[0] ?? '')

  const updateFilter = useCallback((
    userId: string,
    transactionKind: string,
    from: Date|undefined,
    to: Date|undefined) => {
      // console.log(`LogDialogFilter.updateFilter: userId=${userId}, transactionKind='${transactionKind}', from='${from}', to='${to}'`)
      const newFilter = {
        entityId: auxRecordId,
        userId: userId || undefined,
        transactionKinds: transactionKind ? [transactionKind] : undefined,
        from: from || undefined,
        to: to || undefined
      } as LogQueryFilter
      if (!isEqual(newFilter, filter)) {
        // console.log(`LogDialogFilter.updateFilter from ${JSON.stringify(filter)} to ${JSON.stringify(newFilter)}`)
        setFilter("Log", newFilter)
      }
  }, [auxRecordId, filter, setFilter])

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

  const handleReset = useCallback(() => {
    setFrom(undefined)
    setTo(undefined)
    setUserId('')
    setTransactionKind('')
    updateFilter('', '', undefined, undefined)
  }, [updateFilter])

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
    console.error(result.error)
  }
  const users = (result.data?.users as IPage<User>)?.content

  return (
    <div className="flex flex-col gap-2">
      <Spinner loading={result.loading} className="absolute inset-0 bg-black/20 z-50" />
      <div className="flex items-center gap-2">
        <Popover open={fromOpen} onOpenChange={setFromOpen}>
          <PopoverTrigger id="filter-from" asChild>
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
          <PopoverTrigger id="filter-to" asChild>
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
          <SelectTrigger id="filter-userId">
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
            <SelectItem value="UPD">Updated</SelectItem>
            <SelectItem value="DEL">Deleted</SelectItem>
            <SelectItem value="LNK">Linked</SelectItem>
            <SelectItem value="UNL">Unlinked</SelectItem>
          </SelectContent>
        </Select>
        <ButtonEx
          variant="outline"
          help="Refresh the table using the same filter and pagination settings."
          onClick={() => refetch()}
        >
          <RotateCw />
        </ButtonEx>
        <ButtonEx
          outerClassName="flex-grow"
          variant="outline"
          onClick={handleReset}
          help="Clear all filters"
        >
          Reset
        </ButtonEx>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}