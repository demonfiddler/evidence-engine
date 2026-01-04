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

import { LogQueryFilter } from "@/app/model/schema"
import { useCallback } from "react"
import { QueryFilterLogic } from "./use-page-logic"
import { createFilterImpl, createSearchParamsImpl } from "@/lib/utils"
import { hook, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(hook, "[useLogQueryFilter] ")

type FilterValue = string | string[] | Date | undefined

function convertQueryValue(queryKey: string, value: string): FilterValue {
  switch (queryKey) {
    case "transactionKinds":
      return value ? [value] : undefined
    case "from":
    case "to":
      return value ? new Date(value) : undefined
    default:
      return value || undefined
  }
}

function convertFilterValue(filterKey: string, value: FilterValue) : string | undefined {
  switch (filterKey) {
    case "from":
    case "to": {
      const date = value as Date | undefined
      return date?.toISOString()
    }
    case "transactionKinds": {
      const array = value as string[] | undefined
      return array && array.length != 0 ? array[0] : undefined
    }
    default:
      return value?.toString()
  }
}

export default function useLogQueryFilter() : QueryFilterLogic<LogQueryFilter> {
  logger.debug("call")

  const createFilter = useCallback((searchParams: URLSearchParams) => {
    return createFilterImpl<LogQueryFilter, FilterValue>(searchParams, convertQueryValue)
  }, [])

  const createSearchParams = useCallback((filter: LogQueryFilter) => {
    return createSearchParamsImpl<LogQueryFilter, FilterValue>(filter, convertFilterValue)
  }, [])

  return { createFilter, createSearchParams }
}