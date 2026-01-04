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

import { CommentQueryFilter } from "@/app/model/schema"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { useCallback, useContext, useState } from "react"
import { formatDate, isEqual } from "@/lib/utils"
import { READ_USERS } from "@/lib/graphql-queries"
import { useQuery } from "@apollo/client/react"
import { toast } from "sonner"
import IPage from "@/app/model/IPage"
import User from "@/app/model/User"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import Spinner from "../misc/spinner"
import { GlobalContext, QueryState } from "@/lib/context"
import ButtonEx from "../ext/button-ex"
import { filter, LoggerEx } from "@/lib/logger"
import SelectTriggerEx from "../ext/select-ex"
import { Checkbox } from "@/components/ui/checkbox"
import LabelEx from "../ext/label-ex"
import Search from "./search"
import useAuth from "@/hooks/use-auth"
import { QueryResult } from "@/lib/graphql-utils"
import { CalendarIcon, ChevronDownIcon, RotateCwIcon } from "lucide-react"

const logger = new LoggerEx(filter, "[CommentDialogFilter] ")

export default function CommentDialogFilter(
  {
    targetId,
    refetch,
  }: {
    targetId: string | undefined
    refetch: () => void
  }) {
  logger.debug("render")

  const {user} = useAuth()
  const {queries, setFilter} = useContext(GlobalContext)
  const {filter} = queries.Comment as QueryState<CommentQueryFilter>
  const [status, setStatus] = useState(filter.status?.[0] ?? '')
  const [text, setText] = useState(filter.text ?? '')
  const [advanced, setAdvanced] = useState(filter.advancedSearch ?? false)
  const [userId, setUserId] = useState(filter.userId ?? '')
  const [from, setFrom] = useState<Date | undefined>(filter.from)
  const [to, setTo] = useState<Date | undefined>(filter.to)
  const [fromOpen, setFromOpen] = useState(false)
  const [toOpen, setToOpen] = useState(false)

  const updateFilter = useCallback((
    status: string,
    text: string,
    advanced: boolean,
    userId: string,
    from: Date | undefined,
    to: Date | undefined) => {

    logger.trace("updateFilter: targetId=%s, status=%s, text=%s, advanced=%s, userId=%s, from=%s, to=%s",
      targetId, status, text, advanced, userId, from, to)
    const newFilter = {
      status: status ? [status] : undefined,
      text: text || undefined,
      advancedSearch: advanced || undefined,
      targetId,
      userId: userId || undefined,
      from,
      to,
    } as CommentQueryFilter
    if (!isEqual(newFilter as CommentQueryFilter, filter)) {
      logger.trace("updateFilter from %o to %o", filter, newFilter)
      setFilter("Comment", newFilter)
    }
  }, [targetId, filter, setFilter])

  const handleStatusChange = useCallback((status: string) => {
    logger.trace("handleStatusChange: status='%s'", status)
    status = status === "ALL" ? '' : status
    setStatus(status)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, text, advanced, userId, from, to])

  const handleTextChange = useCallback((text: string) => {
    logger.trace("handleTextChange: text='%s'", text)
    setText(text)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, status, advanced, userId, from, to])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    logger.trace("handleAdvancedSearchChange: advanced=%s", advanced)
    setAdvanced(advanced)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, status, text, userId, from, to])

  const handleUserIdChange = useCallback((userId: string) => {
    userId = userId === "ALL" ? '' : userId
    setUserId(userId)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, status, text, advanced, from, to])

  const handleFromChange = useCallback((from?: Date) => {
    setFrom(from)
    setFromOpen(false)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, status, text, advanced, userId, to])

  const handleToChange = useCallback((to?: Date) => {
    setTo(to)
    setToOpen(false)
    updateFilter(status, text, advanced, userId, from, to)
  }, [updateFilter, status, text, advanced, userId, from])

  const handleReset = useCallback(() => {
    setStatus('')
    setText('')
    setAdvanced(false)
    setUserId('')
    setFrom(undefined)
    setTo(undefined)
    updateFilter('', '', false, '', undefined, undefined)
  }, [updateFilter])

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
    <div className="flex flex-row flex-wrap items-center max-w-full gap-2">
      <Spinner loading={result.loading} className="absolute inset-0 bg-black/20 z-50" />
      <Search id="searchCommentsDlg" className="w-5/8" value={text} onChangeValue={handleTextChange} />
      <Checkbox
        id="cf-advanced"
        checked={advanced}
        onCheckedChange={handleAdvancedSearchChange}
      />
      <LabelEx htmlFor="cf-advanced" help="Use advanced text search syntax. See MariaDB documentation at https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode">Advanced</LabelEx>
      {
        user
          ? <Select
            value={status ?? ''}
            onValueChange={handleStatusChange}
          >
            <SelectTriggerEx
              id="cf-status"
              help="Filter the list to show only comments with this status"
            >
              <SelectValue placeholder="Status" />
            </SelectTriggerEx>
            <SelectContent>
              {
                status
                  ? <SelectItem value="ALL">-Clear-</SelectItem>
                  : null
              }
              <SelectItem value="DEL">Deleted</SelectItem>
              <SelectItem value="DRA">Draft</SelectItem>
              <SelectItem value="PUB">Published</SelectItem>
              <SelectItem value="SUS">Suspended</SelectItem>
            </SelectContent>
          </Select>
          : null
      }
      <Select
        value={userId ?? ''}
        onValueChange={handleUserIdChange}
      >
        <SelectTriggerEx
          id="cf-userId"
          help="Filter the list to show only comments made by the selected user."
        >
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
      <Popover open={fromOpen} onOpenChange={setFromOpen}>
        <PopoverTrigger id="cf-from" asChild>
          <ButtonEx
            variant={"outline"}
            className="justify-start text-left font-normal"
            help="Filter the list to show only comments created or updated on or after the specified date."
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
        <PopoverTrigger id="cf-to" asChild>
          <ButtonEx
            variant={"outline"}
            className="justify-start text-left font-normal"
            help="Filter the list to show only comments created or updated on or before the specified date."
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
        variant="outline"
        help="Refresh the table using the same filter and pagination settings."
        onClick={() => refetch()}
      >
        <RotateCwIcon />
      </ButtonEx>
      <ButtonEx
        outerClassName="flex-grow"
        variant="outline"
        onClick={handleReset}
        help="Clear all filters."
      >
        Reset
      </ButtonEx>
    </div>
  )
}

CommentDialogFilter.whyDidYouRender = true