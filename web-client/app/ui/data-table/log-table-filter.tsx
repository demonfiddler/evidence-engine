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

import Log from "@/app/model/Log";
import { DataTableFilterProps, DataTableViewOptions } from "./data-table-view-options";
import { LogQueryFilter } from "@/app/model/schema";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useCallback, useState } from "react";
import { formatDate } from "@/lib/utils";
import { READ_USERS } from "@/lib/graphql-queries";
import { useQuery } from "@apollo/client";
import { toast } from "sonner";
import IPage from "@/app/model/IPage";
import User from "@/app/model/User";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { Button } from "@/components/ui/button";
import { CalendarIcon, ChevronDownIcon } from "@heroicons/react/24/outline";
import Spinner from "../misc/spinner";
import InputEx from "../misc/input-ex";

export default function LogTableFilter(
  {
    table,
    onFilterChange
  } : DataTableFilterProps<Log, LogQueryFilter>) {

  const [from, setFrom] = useState<Date|undefined>()
  const [fromOpen, setFromOpen] = useState(false)
  const [to, setTo] = useState<Date|undefined>()
  const [toOpen, setToOpen] = useState(false)
  const [userId, setUserId] = useState('')
  const [transactionKind, setTransactionKind] = useState('')
  const [entityKind, setEntityKind] = useState('')
  const [entityId, setEntityId] = useState('')

  const updateFilter = useCallback((
    entityKind: string,
    entityId: string,
    userId: string,
    transactionKind: string,
    from: Date|undefined,
    to: Date|undefined) => {
      const filter = {
        entityKind: entityKind || undefined,
        entityId: entityId || undefined,
        userId: userId || undefined,
        transactionKinds: transactionKind ? [transactionKind] : undefined,
        from: from || undefined,
        to: to || undefined
      } as LogQueryFilter
      onFilterChange(filter)
    }, [entityKind, entityId, userId, transactionKind, from, to, onFilterChange])

  const handleFromChange = useCallback((from?: Date) => {
    setFrom(from)
    setFromOpen(false)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

  const handleToChange = useCallback((to?: Date) => {
    setTo(to)
    setToOpen(false)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

  const handleUserIdChange = useCallback((userId: string) => {
    userId = userId === "ALL" ? '' : userId
    setUserId(userId)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

  const handleTransactionKindChange = useCallback((transactionKind: string) => {
    transactionKind = transactionKind === "ALL" ? '' : transactionKind
    setTransactionKind(transactionKind)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

  const handleEntityKindChange = useCallback((entityKind: string) => {
    entityKind = entityKind === "ALL" ? '' : entityKind
    setEntityKind(entityKind)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

  const handleEntityIdChange = useCallback((entityId: string | number | readonly string[] | undefined) => {
    entityId = entityId?.toString() ?? ''
    setEntityId(entityId)
    updateFilter(entityKind, entityId, userId, transactionKind, from, to)
  }, [entityKind, entityId, userId, transactionKind, from, to, updateFilter])

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
    console.error(result.error)
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
                formatDate(from, "PPP")
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
                formatDate(to, "PPP")
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
            <SelectGroup>
              <SelectLabel>Users</SelectLabel>
              {
                userId
                ? <SelectItem value="ALL">-Clear-</SelectItem>
                : null
              }
              {
                users?.map(user => <SelectItem key={user.id} value={user.id ?? ''}>{user.username}</SelectItem>)
              }
            </SelectGroup>
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
            <SelectGroup>
              <SelectLabel>Transaction</SelectLabel>
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
            </SelectGroup>
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
            <SelectGroup>
              <SelectLabel>Record Kinds</SelectLabel>
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
            </SelectGroup>
          </SelectContent>
        </Select>
        <InputEx
          className="w-30 text-right"
          placeholder="Record ID"
          value={entityId}
          onChangeValue={handleEntityIdChange}
        />
        <Button variant="outline" title="Clear all filters" onClick={handleReset}>Reset</Button>
        <DataTableViewOptions table={table} />
      </div>
    </div>
  )
}