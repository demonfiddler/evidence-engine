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

import { TopicQueryFilter } from "@/app/model/schema"
import { useCallback } from "react"
import { QueryFilterLogic } from "./use-page-logic"
import { createFilterImpl, createSearchParamsImpl } from "@/lib/utils"

type FilterValue = boolean | string | string[] | undefined

function convertQueryValue(key: string, value: string): FilterValue {
  switch (key) {
    case "status":
      return value ? [value] : undefined
    case "advancedSearch":
      return value === "true" || undefined
    default:
      return value || undefined
  }
}

function convertFilterValue(key: string, value: FilterValue) : string | undefined {
  switch (key) {
    case "status": {
      const array = value as string[] | undefined
      return array && array.length != 0 ? array[0] : undefined
    }
    case "advancedSearch": {
      const b = value as boolean
      return b ? "true" : undefined
    }
    default:
      return value?.toString()
  }
}

export default function useTopicQueryFilter() : QueryFilterLogic<TopicQueryFilter> {
  const createFilter = useCallback((searchParams: URLSearchParams) => {
    return createFilterImpl<TopicQueryFilter, FilterValue>(searchParams, convertQueryValue)
  }, [])

  const createSearchParams = useCallback((filter: TopicQueryFilter) => {
    return createSearchParamsImpl<TopicQueryFilter, FilterValue>(filter, convertFilterValue)
  }, [])

  return { createFilter, createSearchParams }
}