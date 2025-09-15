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

import { LinkableEntityQueryFilter } from "@/app/model/schema"
import { GlobalContext } from "@/lib/context"
import { createFilterImpl, createSearchParamsImpl, getRecordKind } from "@/lib/utils"
import { useCallback, useContext } from "react"
import { QueryFilterLogic } from "./use-page-logic"

type FilterValue = boolean | string | string[] | undefined

function convertQueryValue(queryKey: string, value: string): FilterValue {
  switch (queryKey) {
    case "status":
      return value ? [value] : undefined
    case "advancedSearch":
    case "recursive":
      return value === "true" || undefined
    default:
      return value || undefined
  }
}

function convertFilterValue(filterKey: string, value: FilterValue) : string | undefined {
  switch (filterKey) {
    case "status": {
      const array = value as string[] | undefined
      return array && array.length != 0 ? array[0] : undefined
    }
    case "advancedSearch":
    case "recursive": {
      const b = value as boolean
      return b ? "true" : undefined
    }
    default:
      return value?.toString()
  }
}

export default function useLinkableEntityQueryFilter() : QueryFilterLogic<LinkableEntityQueryFilter> {
  const {
    masterTopicId,
    masterTopicRecursive,
    showOnlyLinkedRecords,
    setMasterTopicId,
    setMasterTopicRecursive,
    setMasterRecordId,
    setShowOnlyLinkedRecords,
    setFilter,
  } = useContext(GlobalContext)

  const createFilter = useCallback((searchParams: URLSearchParams) => {
    const filter = createFilterImpl<LinkableEntityQueryFilter, FilterValue>(searchParams, convertQueryValue)

    // Update EntityLinkFilter fields to match searchParams.
    if (filter.topicId != masterTopicId)
      setMasterTopicId(filter.topicId)
    if (filter.recursive != masterTopicRecursive)
      setMasterTopicRecursive(!!filter.recursive)
    if (filter.fromEntityKind && filter.fromEntityId) {
      const newMasterRecordKind = getRecordKind(filter.fromEntityKind)
      setMasterRecordId(newMasterRecordKind, filter.fromEntityId)
    } else if (filter.toEntityKind && filter.toEntityId) {
      const newMasterRecordKind = getRecordKind(filter.toEntityKind)
      setMasterRecordId(newMasterRecordKind, filter.toEntityId)
    }
    const newShowOnlyLinkedRecords = !!(filter.topicId || filter.fromEntityId ||  filter.toEntityId)
    if (newShowOnlyLinkedRecords !== showOnlyLinkedRecords)
      setShowOnlyLinkedRecords(newShowOnlyLinkedRecords)

    return filter
  }, [showOnlyLinkedRecords, setFilter, setMasterTopicId, setMasterTopicRecursive, setMasterRecordId, setShowOnlyLinkedRecords])

  const createSearchParams = useCallback((filter: LinkableEntityQueryFilter) => {
    return createSearchParamsImpl<LinkableEntityQueryFilter, FilterValue>(filter, convertFilterValue)
  }, [])

  return {createFilter, createSearchParams}
}