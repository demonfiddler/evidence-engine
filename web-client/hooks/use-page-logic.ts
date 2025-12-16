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

import IPage from "@/app/model/IPage"
import ITrackedEntity from "@/app/model/ITrackedEntity"
import { LinkableEntityKind, RecordKind } from "@/app/model/RecordKinds"
import { BaseEntityInput, LinkableEntityQueryFilter, LogQueryFilter, PageableInput, TrackedEntityQueryFilter } from "@/app/model/schema"
import { GlobalContext, QueryState } from "@/lib/context"
import { DocumentNode, ErrorLike } from "@apollo/client"
import { useMutation, useQuery } from "@apollo/client/react"
import { OperationTypeNode } from "graphql/language"
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { Dispatch, SetStateAction, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { FieldValues, useForm, UseFormReturn } from "react-hook-form"
import { toast } from "sonner"
import z from "zod/v4"
import IBaseEntity from "@/app/model/IBaseEntity"
import { DetailMode, DetailState } from "@/app/ui/details/detail-actions"
import Authority from "@/app/model/Authority"
import useAuth from "./use-auth"
import { introspect, MutationResult, QueryResult } from "@/lib/graphql-utils"
import { CREATE_ENTITY_LINK } from "@/lib/graphql-queries"
import { getRecordLinkProperties, isEmpty, isEqual } from "@/lib/utils"
import { hook, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(hook, "[usePageLogic] ")

export type Options = {[key: string]: any}
export type FormActionHandler<TFieldValues extends FieldValues> = (command: string, fieldValues?: TFieldValues, options?: Options) => void

type PageConfiguration<TData extends ITrackedEntity, TFilter, TFieldValues extends FieldValues, TInput extends BaseEntityInput> = {
  recordKind: RecordKind
  schema?: z.ZodObject<any>
  manualPagination: boolean
  manualSorting: boolean
  readQuery: DocumentNode
  createMutation?: DocumentNode
  updateMutation?: DocumentNode
  deleteMutation?: DocumentNode
  createFieldValues(record: TData | undefined) : TFieldValues
  createInput?(fieldValues: TFieldValues, id?: string) : TInput
  preparePage?(page: IPage<TData> | undefined) : IPage<TData> | undefined
  findRecord?(records: TData[] | undefined, id: string | undefined) : TData | undefined
  filterLogic?: QueryFilterLogic<TFilter>
}

type PageLogic<TData extends ITrackedEntity, TFieldValues extends FieldValues> = {
  loadingPathWithSearchParams: boolean
  loading: boolean
  page?: IPage<TData>
  selectedRecord?: TData
  handleRowSelectionChange: (recordId?: string) => void
  state: DetailState
  setMode: Dispatch<SetStateAction<DetailMode>>
  form: UseFormReturn<TFieldValues, any, TFieldValues>
  handleFormAction: FormActionHandler<TFieldValues>
  refetch: () => void
}

// type ReducerArg<T> = {
//   command: string
//   page: IPage<T>
//   record?: T
// }

function createDetailState(
  hasAuthority: (authority: Authority) => boolean,
  mode: DetailMode
): DetailState {

  const allowCreate = hasAuthority("CRE")
  const allowEdit = hasAuthority("UPD")
  const allowDelete = hasAuthority("DEL")
  const allowUpdate = allowCreate || allowEdit
  const allowLink = hasAuthority("LNK")
  const allowRead = true // hasAuthority("REA")
  const updating = mode == "create" || mode == "edit"

  return {
    mode,
    allowCreate,
    allowEdit,
    allowDelete,
    allowUpdate,
    allowLink,
    allowRead,
    updating,
  }
}

const defaultFindRecord = <T extends IBaseEntity>(records: T[] | undefined, id: string | undefined) : T | undefined => records?.find(r => r?.id === id)

export type QueryFilterConfiguration = {
  recordKind: RecordKind
}

export type QueryFilterLogic<TFilter> = {
  createFilter(searchParams: URLSearchParams) : TFilter
  createSearchParams(filter: TFilter) : URLSearchParams
}

function searchParamsEqual(p1: URLSearchParams, p2: URLSearchParams) {
  if (p1.size != p2.size)
    return false
  let equal = true
  p1.forEach((value: string, key: string) => {
    if (p2.get(key) !== value)
      equal &&= false
  })
  return equal
}

function fixFilter<TFilter extends TrackedEntityQueryFilter | LogQueryFilter>(filter: TFilter) : TFilter | undefined {
  let hasRecordId = false
  if (Object.hasOwn(filter, "recordId")) {
    // The filter includes a recordId, so we don't need to pass any of the other properties.
    const teFilter = filter as TrackedEntityQueryFilter
    hasRecordId = !!teFilter.recordId
    if (hasRecordId)
      filter = {recordId: teFilter.recordId} as TFilter
  }
  if (!hasRecordId &&
    Object.hasOwn(filter, "fromEntityKind") && Object.hasOwn(filter, "fromEntityId") ||
    Object.hasOwn(filter, "toEntityKind") && Object.hasOwn(filter, "toEntityId")) {

    // We don't need to pass fromEntityKind or toEntityKind, as fromEntityId or toEntityId will suffice.
    filter = {...filter}
    const leFilter = filter as LinkableEntityQueryFilter
    delete leFilter.fromEntityKind
    delete leFilter.toEntityKind
  }
  return isEmpty(filter) ? undefined : filter
}

export default function usePageLogic<
    TData extends IBaseEntity,
    TFieldValues extends FieldValues,
    TInput extends BaseEntityInput,
    TFilter extends TrackedEntityQueryFilter | LogQueryFilter,
  >({
    recordKind,
    schema,
    manualPagination,
    manualSorting,
    readQuery,
    createMutation,
    updateMutation,
    deleteMutation,
    createFieldValues,
    createInput,
    preparePage,
    findRecord,
    filterLogic,
  }
  : PageConfiguration<TData, TFilter, TFieldValues, TInput>
  ) : PageLogic<TData, TFieldValues> {
  logger.debug("call")

  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const {hasAuthority} = useAuth()
  const globalContext = useContext(GlobalContext)
  const {
    selectedRecords,
    queries,
    masterTopicId,
    masterRecordKind,
    masterRecordId,
    setFilter,
  } = globalContext
  const selectedRecordId = selectedRecords[recordKind]?.id
  const [mode, setMode] = useState<DetailMode>("view")
  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const setSelectedRecord = useCallback((record?: TData) => globalContext.setSelectedRecord(recordKind, record), [globalContext])

  // Get the current filter from the global context.
  const query = queries[recordKind] as QueryState<TFilter>
  const {filter, sorting, pagination} = query

  // Note whether this is the first-time render of a URL with a query string.
  const loadingPathWithSearchParamsRef = useRef(searchParams.size != 0)

  // Update the page URL's query string to reflect the current filter setting.
  const updateUrlQuery = useCallback(() => {
    if (!loadingPathWithSearchParamsRef.current && filterLogic?.createSearchParams) {
      const newSearchParams = filterLogic.createSearchParams(filter)
      if (!searchParamsEqual(newSearchParams, searchParams)) {
        // This call updates the address bar URL without doing a page reload.
        router.replace(`?${newSearchParams.toString()}`, { scroll: false })
        logger.trace("updateUrlQuery: Updated query from '%s' to '%s'", searchParams, newSearchParams)
      }
    }
  }, [loadingPathWithSearchParamsRef, filter, searchParams, router])

  // Whenever path changes, adjust filter or query string as appropriate.
  const prevPath = useRef('')
  useEffect(() => {
    logger.trace("effect2 (1)")
    if (pathname !== prevPath.current) {
      logger.trace("effect2 (2) path changed from '%s' to '%s'", prevPath.current, pathname)
      prevPath.current = pathname;

      // If navigating to a URL with a query string, replace filter to match query string and store in global context.
      if (loadingPathWithSearchParamsRef.current) {
        if (filterLogic?.createFilter) {
          const newFilter = filterLogic.createFilter(searchParams)
          if (!isEqual(newFilter, filter)) {
            logger.trace("effect2 (3): searchParams=?%s, so updating filter from %o to %o", searchParams, filter, newFilter)
            setFilter(recordKind, newFilter)
          }

          // We've now synchronised the filter to match the query string, so make sure we don't do it again.
          // Note that we have to delay flipping the guard value until all re-renders and refreshes have finished.
          let frameId1: number, frameId2: number
          frameId1 = requestAnimationFrame(() => {
            frameId2 = requestAnimationFrame(() => {
              // NOTE: there's no need to worry about cancelling the animation frames in the event that the component
              // unmounts before the frame has run, since all we are doing here is to set a field on a reference.
              logger.trace("effect2 (4): cleanup")
              loadingPathWithSearchParamsRef.current = false
            })
          })
        }
      // Otherwise, if a filter is present, update the query string to match the filter.
      } else if (!isEmpty(filter)) {
          updateUrlQuery()
      }
    }
  }, [loadingPathWithSearchParamsRef, pathname, filterLogic, setFilter, updateUrlQuery]);

  const prevPageSort = useRef<PageableInput>({})
  const pageSort = useMemo(() => {
    let newPageSort : PageableInput = manualPagination && pagination
      ? {
        pageNumber: pagination.pageIndex,
        pageSize: pagination.pageSize,
      }
      : {}
    if (manualSorting && sorting.length > 0) {
      newPageSort.sort = { orders: [] }
      for (const s of sorting) {
        newPageSort.sort?.orders.push({
          property: s.id,
          direction: s.desc ? "DESC" : undefined
        })
      }
    }
    if (manualPagination && !isEqual(newPageSort, prevPageSort.current)) {
      logger.trace("memo %ss pageSort changed from %o to %o", recordKind, prevPageSort.current, newPageSort)
      prevPageSort.current = newPageSort
    } else {
      newPageSort = prevPageSort.current
    }
    return newPageSort
  }, [manualPagination && pagination, manualSorting && sorting])

  const [readFieldName] = useMemo(() => introspect(readQuery, OperationTypeNode.QUERY), [readQuery])
  const [createFieldName, createVarName] = useMemo(() => introspect(createMutation, OperationTypeNode.MUTATION), [createMutation])
  const [, updateVarName] = useMemo(() => introspect(updateMutation, OperationTypeNode.MUTATION), [updateMutation])
  const [, deleteVarName] = useMemo(() => introspect(deleteMutation, OperationTypeNode.MUTATION), [deleteMutation])
  const [createOp, createResult] = createMutation ? useMutation(createMutation, { refetchQueries: [readQuery] }) : []
  const [updateOp, updateResult] = updateMutation ? useMutation(updateMutation, { refetchQueries: [readQuery] }) : []
  const [deleteOp, deleteResult] = deleteMutation ? useMutation(deleteMutation, { refetchQueries: [readQuery] }) : []
  const [linkOp, linkResult] = useMutation(CREATE_ENTITY_LINK, { refetchQueries: [readQuery] })
  const [error, setError] = useState<ErrorLike>()

  const readResult = useQuery(
    readQuery,
    {
      variables: {
        filter: fixFilter(filter),
        pageSort
      },
    }
  )
  const refetch = useCallback(() => {
    readResult.refetch({
        filter: fixFilter(filter),
        pageSort
    })
  }, [readResult, filter, pageSort])
  const prevReadErrorRef = useRef<ErrorLike | undefined>(undefined)
  useEffect(() => {
    if (readResult.error !== prevReadErrorRef.current) {
      prevReadErrorRef.current = readResult.error
      setError(readResult.error)
    }
  }, [readResult.error, prevReadErrorRef])

  // Whenever filter or pagination (actually) changes, update query string and ask Apollo to refetch.
  const prevFilter = useRef<TFilter>(filter);
  const prevPageSort2 = useRef<PageableInput>(pageSort);
  useEffect(() => {
    logger.trace("effect3 (1)")
    // Don't do this if we are still loading a path with search params or if neither filter nor pagination has changed.
    if (!loadingPathWithSearchParamsRef.current && (!isEqual(filter, prevFilter.current) ||
      manualPagination && !isEqual(pageSort, prevPageSort2.current))) {

      logger.trace("effect3 (2)")
      prevFilter.current = filter
      prevPageSort2.current = pageSort
      updateUrlQuery()
      refetch()
    }
  }, [loadingPathWithSearchParamsRef, filter, manualPagination && pageSort, updateUrlQuery, refetch]);

  const loading = (readResult.loading || createResult?.loading || updateResult?.loading || deleteResult?.loading || linkResult?.loading) ?? false

  if (error) {
    // TODO: display user-friendly error notification
    toast.error(`Operation failed:\n\n${error.message}`)
    logger.error("Operation failed: %o", error)
  }

  const data = (readResult.loading
    ? readResult.previousData
    : readResult.data) as QueryResult<IPage<TData>>
  let rawPage = data && readFieldName
    ? data[readFieldName]
    : undefined
  const page = useMemo(() => preparePage?.(rawPage) ?? rawPage, [preparePage, rawPage])

  // TODO: re-enable reducer logic to avoid query re-execution on completion of a mutation
  /*
  const reducer = useCallback((draft: Draft<IPage<T>>, action: ReducerArg<T>) => {
    // TODO: if provided, call getSubRows as passed to DataTable to find the array and index to update/delete.
    // TODO: refactor to allow a custom reducer to be passed in (needed for the hierarchical Topics page).
    const idx = action.page?.content.findIndex(r => r.id === action.record?.id) ?? -1
    // N.B. the following 'as Draft<T>' typecasts are needed to silence the TypeScript error
    // "Argument of type 'T' is not assignable to parameter of type 'Draft<T> ts(2345)'.
    // However, this approach is only valid because the record will not be further mutated.
    switch (action.command) {
      case "init":
        if (action.page)
          Object.assign(draft, action.page)
        break;
      case "create":
        draft.content.push(action.record as Draft<T>)
        // recomputePageFields(draft)
        break
      case "update":
        if (idx != -1 && action.record)
          draft.content.splice(idx, 1, action.record as Draft<T>)
        break
      case "delete":
        if (idx != -1 && action.record) {
          // On status mismatch, remove the record from the page content.
          if (search.status && search.status !== action.record?.status) {
            draft.content.splice(idx, 1)
          } else {
            // Otherwise, replace the record.
            draft.content.splice(idx, 1, action.record as Draft<T>)
            // Alternatively:
            // Object.assign(draft.content[idx], action.record)
          }
        }
        break
    }
  }, [search.status])
  const [page, dispatch] = useImmerReducer<IPage<T>, ReducerArg<T>>(reducer, rawPage ?? dummyPage)
  */

  const getRecord = useCallback((id: string | undefined) => {
    return (findRecord ?? defaultFindRecord)(page?.content, id)
  }, [findRecord, page])
  const selectedRecord = getRecord(selectedRecordId)

  const origFieldValues = useMemo(() => createFieldValues(selectedRecord), [createFieldValues, selectedRecord])
  const form = useForm<TFieldValues>({
    resolver: schema && standardSchemaResolver(schema),
    mode: "onChange",
    values: origFieldValues,
  })
  const handleFormAction = useCallback((command: string, fieldValues?: TFieldValues, options?: Options) => {
    setError(undefined)

    // TODO: update Apollo cache?
    switch (command) {
      case "new":
        form?.reset(createFieldValues(undefined))
        setSelectedRecord()
        break
      case "create":
        if (fieldValues) {
          createOp?.({
            variables: {
              [createVarName]: createInput?.(fieldValues)
            },
            onCompleted: (data) => {
              const newRecord = (data as MutationResult<TData>)[createFieldName]
              if (options) {
                const {
                  linkMasterTopic,
                  thisRecordLocationsForTopic,
                  linkMasterRecord,
                  thisRecordLocationsForMaster,
                  otherRecordLocationsForMaster,
                } = options
                const topicLinkInput = linkMasterTopic && masterTopicId
                  ? {
                      fromEntityId: masterTopicId,
                      fromEntityLocations: null,
                      toEntityId: newRecord.id,
                      toEntityLocations: thisRecordLocationsForTopic || null,
                    }
                  : undefined
                let masterRecordLinkInput
                if (linkMasterRecord && masterRecordId) {
                  const [
                    ,,
                    thisRecordIdProperty,
                    otherRecordIdProperty,
                    thisLocationsProperty,
                    otherLocationsProperty,
                  ] = getRecordLinkProperties(recordKind as LinkableEntityKind, masterRecordKind as LinkableEntityKind)
                  masterRecordLinkInput = {
                    [thisRecordIdProperty]: newRecord.id,
                    [otherRecordIdProperty]: masterRecordId,
                    [thisLocationsProperty]: thisRecordLocationsForMaster || null,
                    [otherLocationsProperty]: otherRecordLocationsForMaster || null,
                  }
                } else {
                  masterRecordLinkInput = undefined
                }
                if (topicLinkInput) {
                  const linkOpPromise = linkOp({variables: {input: topicLinkInput}})
                  if (masterRecordLinkInput) {
                    linkOpPromise.then(() => {
                      linkOp({variables: {input: masterRecordLinkInput}})
                    })
                  }
                } else if (masterRecordLinkInput) {
                  linkOp({variables: {input: masterRecordLinkInput}})
                }
              }
              // FIXME: this doesn't set the selection in the data table.
              setSelectedRecord(newRecord)
              setMode("view")
            },
            onError: (error) => setError(error),
          })
        }
        break
      case "update":
        if (fieldValues) {
          updateOp?.({
            variables: {
              [updateVarName]: createInput?.(fieldValues, selectedRecordId)
            },
            onCompleted: () => {
              setMode("view")
            },
            onError: (error) => setError(error),
          })
        }
        break
      case "delete":
        deleteOp?.({
          variables: {
            [deleteVarName]: selectedRecordId
          },
          onError: (error) => setError(error),
        })
      case "reset":
        form?.reset(fieldValues ?? origFieldValues)
        break
      default:
        logger.error("Unknown command: %s", command)
    }
    // Dependencies assume (reasonably) that createOp, updateOp, deleteOp, createVarName, updateVarName, deleteVarName
    // will never change during the page lifetime.
  }, [selectedRecordId, form, origFieldValues, masterTopicId, masterRecordKind, masterRecordId, createInput])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    if (mode !== "view") {
      toast.warning("Cannot change selection while editing a record. Use the Cancel or Save button in the details panel below.")
      // setSelectedRecordId(selectedRecordId)
      return
    }
    const record = getRecord(recordId)
    setSelectedRecord(record)
    form?.reset(createFieldValues(record))
  }, [form, mode, getRecord, setSelectedRecord])

  // TODO: re-enable reducer logic to avoid query re-execution on completion of a mutation
  /*
  // When a new page is available, dispatch the reducer.
  useEffect(() => {
    if (rawPage)
      dispatch({command: "init", page: rawPage});
  }, [rawPage, dispatch]);
  */

  const pageLogic = useMemo<PageLogic<TData, TFieldValues>>(() => {
    return {
      loadingPathWithSearchParams: loadingPathWithSearchParamsRef.current,
      loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      state,
      setMode,
      form,
      handleFormAction,
      refetch,
    }}, [
      loadingPathWithSearchParamsRef.current,
      readResult.loading,
      page,
      selectedRecord,
      handleRowSelectionChange,
      state,
      form,
      handleFormAction,
      refetch,
  ])

  return pageLogic
}
