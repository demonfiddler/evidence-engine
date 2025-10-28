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

import Comment from "@/app/model/Comment"
import DataTableFilterProps from "../data-table/data-table-filter"
import DataTableViewOptions from "../data-table/data-table-view-options"
import { CommentQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
import { formatDate, isEqual } from "@/lib/utils"
import { READ_USERS } from "@/lib/graphql-queries"
import { useQuery } from "@apollo/client/react"
import { toast } from "sonner"
import IPage from "@/app/model/IPage"
import User from "@/app/model/User"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { CalendarIcon, ChevronDownIcon } from "@heroicons/react/24/outline"
import Spinner from "../misc/spinner"
import InputEx from "../ext/input-ex"
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { RotateCw } from "lucide-react"
import { filter, LoggerEx } from "@/lib/logger"
import SelectTriggerEx from "../ext/select-ex"
import { Checkbox } from "@/components/ui/checkbox"
import LabelEx from "../ext/label-ex"
import Search from "./search"
import useAuth from "@/hooks/use-auth"
import { QueryResult } from "@/lib/graphql-utils"

const logger = new LoggerEx(filter, "[CommentTableFilter] ")

export default function CommentTableFilter(
  {
    table,
    refetch,
    loadingPathWithSearchParams,
  }: DataTableFilterProps<Comment>) {
  logger.debug("render")

  const {user} = useAuth()
  const {queries, setFilter} = useContext(GlobalContext)
  const {filter} = queries["Comment"] as QueryState<CommentQueryFilter>
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  const [recordId, setRecordId] = useState(filter.recordId ?? '')
  const [targetKind, setTargetKind] = useState(filter.targetKind ?? '')
  const [targetId, setTargetId] = useState(filter.targetId ?? '')
  const [parentId, setParentId] = useState(filter.parentId ?? '')
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [from, setFrom] = useState<Date | undefined>(filter.from)
  const [to, setTo] = useState<Date | undefined>(filter.to)
  const [fromOpen, setFromOpen] = useState(false)
  const [toOpen, setToOpen] = useState(false)

  const updateFilter = useCallback((
    status: string,
    text: string,
    advanced: boolean,
    recordId: string,
    targetKind: string,
    targetId: string,
    parentId: string,
    userId: string,
    from: Date | undefined,
    to: Date | undefined) => {

    logger.trace("updateFilter: status=%s, text=%s, advanced=%s, recordId=%s, targetKind=%s, targetId=%s, parentId=%s, userId=%s, from=%s, to=%s",
      status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
    if (!loadingPathWithSearchParams) {
      const newFilter = {
        status: status ? [status] : undefined,
        text: text || undefined,
        advancedSearch: advanced || undefined,
        recordId: recordId || undefined,
        targetKind: targetKind || undefined,
        targetId: targetId || undefined,
        parentId: parentId || undefined,
        userId: userId || undefined,
        from, // : from || undefined,
        to, // : to || undefined,
      } as CommentQueryFilter
      if (!isEqual(newFilter as CommentQueryFilter, filter)) {
        logger.trace("updateFilter from %o to %o", filter, newFilter)
        setFilter("Comment", newFilter)
      }
    }
  }, [loadingPathWithSearchParams, filter, setFilter])

  // If the filter changes, refresh the UI to match.
  const prevFilter = useRef<CommentQueryFilter>({})
  useEffect(() => {
    logger.trace("effect2 (1)")
    if (!isEqual(filter, prevFilter.current)) {
      logger.trace("effect2 (2): filter changed from %o to %o", prevFilter.current, filter)
      prevFilter.current = filter

      if (status !== (filter?.status?.[0] ?? ''))
        setStatus(filter?.status?.[0] ?? '')
      if (text !== (filter?.text ?? ''))
        setText(filter?.text ?? '')
      if (advanced !== !!filter?.advancedSearch)
        setAdvanced(!!filter?.advancedSearch)
      if (recordId !== (filter?.recordId ?? ''))
        setRecordId(filter?.recordId ?? '')
      if (targetKind !== (filter?.targetKind/*?.[0]*/ ?? ''))
        setTargetKind(filter?.targetKind ?? '')
      if (targetId !== (filter?.targetId ?? ''))
        setTargetId(filter?.targetId ?? '')
      if (parentId !== (filter?.parentId ?? ''))
        setParentId(filter?.parentId ?? '')
      if (userId !== filter?.userId)
        setUserId(filter?.userId ?? '')
      if (from?.valueOf() !== filter?.from?.valueOf())
        setFrom(filter?.from)
      if (to?.valueOf() !== filter?.to?.valueOf())
        setTo(filter?.to)
    }
  }, [filter, status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to])

  const handleStatusChange = useCallback((status: string) => {
    logger.trace("handleStatusChange: status='%s'", status)
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to])

  const handleTextChange = useCallback((text: string) => {
    logger.trace("handleTextChange: text='%s'", text)
    setText(text)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, advanced, recordId, targetKind, targetId, parentId, userId, from, to])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    logger.trace("handleAdvancedSearchChange: advanced=%s", advanced)
    setAdvanced(advanced)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, recordId, targetKind, targetId, parentId, userId, from, to])

  const handleRecordIdChange = useCallback((recordId: string) => {
    logger.trace("handleRecordIdChange: recordId='%s'", recordId)
    setRecordId(recordId)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, targetKind, targetId, parentId, userId, from, to])

  const handleTargetKindChange = useCallback((targetKind: string) => {
    targetKind = targetKind === "ALL" ? '' : targetKind
    setTargetKind(targetKind)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetId, parentId, from, to])

  const handleTargetIdChange = useCallback((targetId: string) => {
    setTargetId(targetId)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetKind, parentId, from, to])

  const handleParentIdChange = useCallback((parentId: string) => {
    setParentId(parentId)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetKind, targetId, userId, from, to])

  const handleUserIdChange = useCallback((userId: string) => {
    userId = userId === "ALL" ? '' : userId
    setUserId(userId)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetKind, targetId, parentId, from, to])

  const handleFromChange = useCallback((from?: Date) => {
    setFrom(from)
    setFromOpen(false)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetKind, targetId, parentId, userId, to])

  const handleToChange = useCallback((to?: Date) => {
    setTo(to)
    setToOpen(false)
    updateFilter(status, text, advanced, recordId, targetKind, targetId, parentId, userId, from, to)
  }, [updateFilter, status, text, advanced, recordId, targetKind, targetId, parentId, userId, from])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    setRecordId('')
    setTargetKind('')
    setTargetId('')
    setParentId('')
    setUserId('')
    setFrom(undefined)
    setTo(undefined)
    updateFilter('', '', false, '', '', '', '', '', undefined, undefined)
  }, [setUserId, setFrom, setTo, updateFilter])

  const result = useQuery(
    READ_USERS,
    {
      variables: {
        pageSort: {
          sort: {
            orders: [
              { property: "username" }
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
  const users = (result.data as QueryResult<IPage<User>>)?.users?.content

  return (
    <div className="flex flex-col gap-2">
      <Spinner loading={result.loading} className="absolute inset-0 bg-black/20 z-50" />
      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-2">
          {
            user
            ? <Select
              value={status ?? ''}
              onValueChange={handleStatusChange}
            >
              <SelectTriggerEx id="status" help="Filter the table to show only comments with this status">
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
          <Search id="searchComments" value={text} onChangeValue={handleTextChange} />
          <Checkbox
            id="advancedComments"
            checked={advanced}
            onCheckedChange={handleAdvancedSearchChange}
          />
          <LabelEx htmlFor="advancedComments" help="Use advanced text search syntax. See MariaDB documentation at https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode">Advanced</LabelEx>
          <InputEx
            id="recordIdComments"
            outerClassName="w-40"
            className="text-right"
            placeholder="Record ID"
            value={recordId}
            onChange={(e) => handleRecordIdChange(e.target.value)}
            delay={500}
            clearOnEscape={true}
            help="Filter the table to show only the record with the specified ID. Other filters are retained but ignored."
          />
          <Select
            value={targetKind ?? ''}
            onValueChange={handleTargetKindChange}
          >
            <SelectTriggerEx id="recordKindComments" help="Filter the table to show only comments on records of the specified kind.">
              <SelectValue placeholder="Target Kind" />
            </SelectTriggerEx>
            <SelectContent>
              {
                targetKind
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
            id="targetIdComments"
            outerClassName="w-40"
            className="text-right"
            placeholder="Target ID"
            value={recordId}
            onChange={(e) => handleTargetIdChange(e.target.value)}
            delay={500}
            clearOnEscape={true}
            help="Filter the table to show only comments on the record with the specified ID."
          />
          <InputEx
            id="parentIdComments"
            outerClassName="w-40"
            className="text-right"
            placeholder="Parent ID"
            value={recordId}
            onChange={(e) => handleParentIdChange(e.target.value)}
            delay={500}
            clearOnEscape={true}
            help="Filter the table to show only replies to the comment with the specified ID."
          />
          <Select
            value={userId ?? ''}
            onValueChange={handleUserIdChange}
          >
            <SelectTriggerEx id="userIdComments" help="Filter the table to show only comments made by the selected user.">
              <SelectValue placeholder="User" />
            </SelectTriggerEx>
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
        </div>
        <div className="flex gap-2">
          <Popover open={fromOpen} onOpenChange={setFromOpen}>
            <PopoverTrigger id="fromComments" asChild>
              <ButtonEx
                variant={"outline"}
                className="justify-start text-left font-normal"
                help="Filter the table to show only comments created or updated on or after the specified date."
              >
                <CalendarIcon />
                {from ? (
                  formatDate(from)
                ) : (
                  <span className="text-gray-500">Start Date</span>
                )}
                <ChevronDownIcon className="text-gray-500" />
              </ButtonEx>
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
            <PopoverTrigger id="toComments" asChild>
              <ButtonEx
                variant={"outline"}
                className="justify-start text-left font-normal"
                help="Filter the table to show only comments created or updated on or before the specified date."
              >
                <CalendarIcon />
                {to ? (
                  formatDate(to)
                ) : (
                  <span className="text-gray-500">End Date</span>
                )}
                <ChevronDownIcon className="text-gray-500" />
              </ButtonEx>
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
          <ButtonEx
            id="refreshComments"
            variant="outline"
            help="Refresh the table using the same filter and pagination settings."
            onClick={() => refetch()}
          >
            <RotateCw />
          </ButtonEx>
          <ButtonEx
            id="resetComments"
            outerClassName="flex-grow"
            variant="outline"
            onClick={handleReset}
            help="Clear all filters."
          >
            Reset
          </ButtonEx>
          <DataTableViewOptions table={table} />
        </div>
      </div>
    </div>
  )
}

CommentTableFilter.whyDidYouRender = true