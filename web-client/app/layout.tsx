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

import "@/app/ui/global.css"
import { inter } from "@/app/ui/fonts"
import { Toaster } from "@/components/ui/sonner"
import {
  GlobalContext,
  AppState,
  ColumnState,
  SizeState,
  GlobalContextType,
  SelectedRecord,
  UsersPageRadioState,
  QueryState,
  SecurityPageTabState
} from "@/lib/context"
import RecordKind from "./model/RecordKind"
import ILinkableEntity from "./model/ILinkableEntity"
import { getRecordLabel } from "@/lib/utils"
import { Suspense, useCallback, useEffect, useMemo, useRef, useState } from "react"
import { useSessionStorage } from "usehooks-ts"
import User from "./model/User"
import { ApolloProvider } from "@apollo/client/react"
import { apolloClient } from "@/lib/graphql-utils"
import { AuthProvider } from "@/hooks/use-auth"
import { useImmerReducer } from "use-immer"
import { ColumnOrderState, ColumnSizingState, PaginationState, SortingState, VisibilityState } from "@tanstack/react-table"
import IBaseEntity from "./model/IBaseEntity"
import { CommentQueryFilter, LinkableEntityQueryFilter, LogQueryFilter, QueryFilter, TrackedEntityQueryFilter } from "./model/schema"
import { usePathname } from "next/navigation"
import { columns as commentColumns, columnVisibility as commentColumnVisibility } from "@/app/ui/tables/comment-columns"
import { columns as claimColumns, columnVisibility as claimColumnVisibility } from "@/app/ui/tables/claim-columns"
import { columns as declarationColumns, columnVisibility as declarationColumnVisibility } from "@/app/ui/tables/declaration-columns"
import { columns as groupColumns, columnVisibility as groupColumnVisibility } from "@/app/ui/tables/group-columns"
import { columns as journalColumns, columnVisibility as journalColumnVisibility } from "@/app/ui/tables/journal-columns"
import { columns as logColumns, columnVisibility as logColumnVisibility } from "@/app/ui/tables/log-columns"
import { columns as personColumns, columnVisibility as personColumnVisibility } from "@/app/ui/tables/person-columns"
import { columns as publicationColumns, columnVisibility as publicationColumnVisibility } from "@/app/ui/tables/publication-columns"
import { columns as publisherColumns, columnVisibility as publisherColumnVisibility } from "@/app/ui/tables/publisher-columns"
import { columns as quotationColumns, columnVisibility as quotationColumnVisibility } from "@/app/ui/tables/quotation-columns"
import { columns as topicColumns, columnVisibility as topicColumnVisibility } from "@/app/ui/tables/topic-columns"
import { columns as userColumns, columnVisibility as userColumnVisibility } from "@/app/ui/tables/user-columns"
import { layout, LoggerEx } from "@/lib/logger"
import LoggingLevelDrawer from "./ui/misc/logging-level-drawer"

const logger = new LoggerEx(layout, "[RootLayout] ")

if (process.env.NODE_ENV === "development") {
  // require("../wdyr");
}

const defaultColumnSettings = {
  None: {columns: [], visibility: {}},
  Comment: {columns: commentColumns, visibility: commentColumnVisibility},
  Claim: {columns: claimColumns, visibility: claimColumnVisibility},
  Declaration: {columns: declarationColumns, visibility: declarationColumnVisibility},
  Group: {columns: groupColumns, visibility: groupColumnVisibility},
  Journal: {columns: journalColumns, visibility: journalColumnVisibility},
  Log: {columns: logColumns, visibility: logColumnVisibility},
  Person: {columns: personColumns, visibility: personColumnVisibility},
  Publication: {columns: publicationColumns, visibility: publicationColumnVisibility},
  Publisher: {columns: publisherColumns, visibility: publisherColumnVisibility},
  Quotation: {columns: quotationColumns, visibility: quotationColumnVisibility},
  RecordLink: {columns: [], visibility: {}},
  Topic: {columns: topicColumns, visibility: topicColumnVisibility},
  User: {columns: userColumns, visibility: userColumnVisibility},
}

function defaultAppState() {
  return {
    modified: false,
    sidebarOpen: true,
    linkFilterOpen: false,
    trackingDetailsOpen: false,
    masterTopicRecursive: true,
    masterRecordKind: "None",
    showOnlyLinkedRecords: false,
    selectedRecords: {},
    columns: defaultColumnStatesMap(),
    queries: defaultQueryStatesMap(),
  } as AppState
}

function defaultColumnStatesMap() {
  return {
    None: defaultColumnState("None"),
    Comment: defaultColumnState("Comment"),
    Claim: defaultColumnState("Claim"),
    Declaration: defaultColumnState("Declaration"),
    Group: defaultColumnState("Group"),
    Journal: defaultColumnState("Journal"),
    Log: defaultColumnState("Log"),
    Person: defaultColumnState("Person"),
    Publication: defaultColumnState("Publication"),
    Publisher: defaultColumnState("Publisher"),
    Quotation: defaultColumnState("Quotation"),
    Topic: defaultColumnState("Topic"),
    User: defaultColumnState("User"),
  }
}

function defaultColumnState(recordKind: RecordKind) {
  const {columns, visibility} = defaultColumnSettings[recordKind]
  const sizing = {} as SizeState
  const order = [] as ColumnOrderState
  columns.forEach(c => {
    if (c.id) {
      order.push(c.id)
      if (c.size)
        sizing[c.id] = c.size
    }
  })
  return {visibility, sizing, order} as ColumnState
}

function defaultQueryStatesMap() {
  return {
    None: defaultQueryState<never>(),
    Comment: defaultQueryState<CommentQueryFilter>(),
    Claim: defaultQueryState<LinkableEntityQueryFilter>(),
    Declaration: defaultQueryState<LinkableEntityQueryFilter>(),
    Group: defaultQueryState<TrackedEntityQueryFilter>(),
    Journal: defaultQueryState<TrackedEntityQueryFilter>(),
    Log: defaultQueryState<LogQueryFilter>(),
    Person: defaultQueryState<LinkableEntityQueryFilter>(),
    Publication: defaultQueryState<LinkableEntityQueryFilter>(),
    Publisher: defaultQueryState<TrackedEntityQueryFilter>(),
    Quotation: defaultQueryState<LinkableEntityQueryFilter>(),
    Topic: defaultQueryState<TrackedEntityQueryFilter>(),
    User: defaultQueryState<TrackedEntityQueryFilter>(),
  }
}

function defaultQueryState<TFilter>() {
  return {
    filter: {} as TFilter,
    sorting: [] as SortingState,
    pagination: defaultPagination(),
  }
}

function defaultPagination() {
  return {
    pageIndex: 0,
    pageSize: 10
  } as PaginationState
}

type ReducerArg = {
  command: string
  value:
    undefined |
    null |
    boolean |
    User |
    string |
    RecordKindValueOpt<SelectedRecord> |
    RecordKindValueOpt<IBaseEntity> |
    RecordKindValue<VisibilityState> |
    RecordKindValue<ColumnSizingState> |
    RecordKindValue<ColumnOrderState> |
    RecordKindValue<QueryFilter> |
    RecordKindValue<SortingState> |
    RecordKindValue<PaginationState> |
    RecordKindValue<string> |
    RecordKindValue<UsersPageRadioState> |
    RecordKindValue<SecurityPageTabState> |
    QueryState<QueryFilter>
}

type RecordKindValue<TValue> = {
  recordKind: RecordKind
  value: TValue
}

type RecordKindValueOpt<TValue> = Omit<RecordKindValue<TValue>, "value"> & {
  value?: TValue
}

function reducer(draft: AppState, action: ReducerArg) {
  logger.trace("reducer(action: %o)", action)
  draft.modified = true
  switch (action.command) {
    case "flush":
      draft.modified = false
      break
    case "setDefaults": {
      const defaults = defaultAppState()
      draft.modified = defaults.modified
      draft.sidebarOpen = defaults.sidebarOpen
      draft.linkFilterOpen = defaults.linkFilterOpen
      draft.trackingDetailsOpen = defaults.trackingDetailsOpen
      draft.masterTopicId = defaults.masterTopicId
      draft.masterTopicRecursive = defaults.masterTopicRecursive
      draft.masterRecordKind = defaults.masterRecordKind
      draft.masterRecordId = defaults.masterRecordId
      draft.showOnlyLinkedRecords = defaults.showOnlyLinkedRecords
      draft.queries = defaults.queries
      draft.columns = defaults.columns
      draft.selectedRecords = defaults.selectedRecords
      break
    }
    case "setSecurityPrincipal": {
      const user = action.value as User
      draft.username = user?.username
      draft.authorities = user?.authorities
      break
    }
    case "setSidebarOpen": {
      draft.sidebarOpen = action.value as boolean
      break
    }
    case "setLinkFilterOpen": {
      draft.linkFilterOpen = action.value as boolean
      break
    }
    case "setTrackingDetailsOpen": {
      draft.trackingDetailsOpen = action.value as boolean
      break
    }
    case "setMasterTopicId": {
      draft.masterTopicId = action.value as string
      break;
    }
    case "setMasterTopicRecursive": {
      draft.masterTopicRecursive = action.value as boolean
      break
    }
    case "setMasterRecord": {
      const record = action.value as RecordKindValueOpt<SelectedRecord>
      draft.masterRecordKind = record.recordKind
      draft.masterRecordId = record.value?.id
      draft.masterRecordLabel = record.value?.label
      break
    }
    case "setMasterRecordId": {
      const record = action.value as RecordKindValueOpt<string>
      draft.masterRecordKind = record.recordKind
      draft.masterRecordId = record.value
      draft.masterRecordLabel = `${record.recordKind}#${record.value}`
      break
    }
    case "setShowOnlyLinkedRecords": {
      draft.showOnlyLinkedRecords = action.value as boolean
      break
    }
    case "setSelectedRecord": {
      const {recordKind, value : record} = action.value as RecordKindValueOpt<IBaseEntity>
      if (record && !record.id)
        throw new Error("Missing record id")
      const label = getRecordLabel(recordKind, record) ?? ''
      draft.selectedRecords ??= {}
      draft.selectedRecords[recordKind] = record && record.id
        ? {id: record.id, label}
        : undefined
      if (recordKind == draft.masterRecordKind) {
        draft.masterRecordId = record?.id
        draft.masterRecordLabel = label
      }
      break
    }
    case "setColumnVisibility": {
      const {recordKind, value: visibility} = action.value as RecordKindValue<VisibilityState>
      const column = draft.columns[recordKind] ??= defaultColumnState(recordKind)
      for (const [k, v] of Object.entries(visibility))
        column.visibility[k] = v
      break
    }
    case "setColumnSizing": {
      const {recordKind, value: sizing} = action.value as RecordKindValue<ColumnSizingState>
      const column = draft.columns[recordKind] ??= defaultColumnState(recordKind)
      for (const [k, v] of Object.entries(sizing))
        column.sizing[k] = v
      break
    }
    case "setColumnOrder": {
      const {recordKind, value: order} = action.value as RecordKindValue<ColumnOrderState>
      const column = draft.columns[recordKind] ??= defaultColumnState(recordKind)
      column.order = order
      break
    }
    case "setFilter": {
      const {recordKind, value: filter} = action.value as RecordKindValue<QueryFilter>
      const query = draft.queries[recordKind] ??= defaultQueryState()
      query.filter = filter
      break
    }
    case "setSorting": {
      const {recordKind, value: sorting} = action.value as RecordKindValue<SortingState>
      const query = draft.queries[recordKind] ??= defaultQueryState()
      query.sorting = sorting
      break
    }
    case "setPagination": {
      const {recordKind, value: pagination} = action.value as RecordKindValue<PaginationState>
      const query = draft.queries[recordKind] ??= defaultQueryState()
      query.pagination = pagination
      break
    }
    case "setSelectedLinkId": {
      const {recordKind, value: selectedLinkId} = action.value as RecordKindValue<string>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<QueryFilter>
      query.selectedLinkId = selectedLinkId
      break
    }
    case "setShowUsersOrMembers": {
      const {recordKind, value: showUsersOrMembers} = action.value as RecordKindValue<UsersPageRadioState>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<QueryFilter>
      query.showUsersOrMembers = showUsersOrMembers
      break
    }
    case "setActiveTab": {
      const {recordKind, value: activeTab} = action.value as RecordKindValue<SecurityPageTabState>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<QueryFilter>
      query.activeTab = activeTab
      break
    }
    default:
      throw new Error(`Unknown command '${action.command}'`)
  }
}

function FlushOnPathChange({flushFn} : {flushFn: () => void}) {
  const pathname = usePathname();
  const prevPath = useRef(pathname);

  useEffect(() => {
    if (prevPath.current !== pathname) {
      logger.debug("Navigating from %s to %s", prevPath.current, pathname)
      flushFn();
      prevPath.current = pathname;
    }
  }, [pathname, flushFn]);

  return null;
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  logger.debug("render")

  const [appStateSs/*, storeAppStateSs*/] = useSessionStorage<AppState>("app-state", defaultAppState)
  const [appState, dispatch] = useImmerReducer<AppState, ReducerArg>(reducer, appStateSs)

  const setDefaults = useCallback(() => {
    dispatch({command: "setDefaults", value: null})
  }, [dispatch])

  // My attempts to introduce guard conditions to prevent unnecessary changes to the global context were unexpectedly
  // thwarted by the discovery that appState.<property> already holds the new value (at least for boolean toggles). I
  // have no idea how this is happening, since the only way to update the global context is by calling the dispatch
  // function, and that only happens in the set*() functions below.

  const setSecurityPrincipal = useCallback((user?: User) => {
    // if (!isEqual(user?.username, appState.username) || !isEqual(user?.authorities, appState.authorities))
    dispatch({command: "setSecurityPrincipal", value: user})
  }, [dispatch])

  const setSidebarOpen = useCallback((sidebarOpen: boolean) => {
    // if (sidebarOpen !== appState.sidebarOpen)
    dispatch({command: "setSidebarOpen", value: sidebarOpen})
  }, [dispatch])

  const setLinkFilterOpen = useCallback((linkFilterOpen: boolean) => {
    // if (linkFilterOpen !== appState.linkFilterOpen)
    dispatch({command: "setLinkFilterOpen", value: linkFilterOpen})
  }, [dispatch])

  const setTrackingDetailsOpen = useCallback((trackingDetailsOpen: boolean) => {
    // if (trackingDetailsOpen !== appState.trackingDetailsOpen)
    dispatch({command: "setTrackingDetailsOpen", value: trackingDetailsOpen})
  }, [dispatch])

  const setMasterTopicId = useCallback((masterTopicId: string | undefined) => {
    // if (masterTopicId !== appState.masterTopicId)
    dispatch({command: "setMasterTopicId", value: masterTopicId})
  }, [dispatch])

  const setMasterTopicRecursive = useCallback((recursive: boolean) => {
    // if (recursive !== appState.masterTopicRecursive)
    dispatch({command: "setMasterTopicRecursive", value: recursive})
  }, [dispatch])

  const setMasterRecord = useCallback((recordKind: RecordKind, record: IBaseEntity) => {
    // TODO: consider whether to handle label changes.
    // if (recordKind !== appState.masterRecordKind || record.id !== appState.masterRecordId)
    dispatch({command: "setMasterRecord", value: {recordKind, value: record}})
  }, [dispatch])

  const setMasterRecordId = useCallback((recordKind: RecordKind, id: string) => {
    // if (recordKind !== appState.masterRecordKind || id !== appState.masterRecordId)
    dispatch({command: "setMasterRecordId", value: {recordKind, value: id}})
  }, [dispatch])

  const setMasterRecordKind = useCallback((recordKind: RecordKind) => {
    // if (recordKind !== appState.masterRecordKind) {
    const record = appState.selectedRecords[recordKind]
    dispatch({command: "setMasterRecord", value: {recordKind, value: record}})
    // }
  }, [dispatch, appState])

  const setShowOnlyLinkedRecords = useCallback((showOnlyLinkedRecords: boolean) => {
    // if (showOnlyLinkedRecords !== appState.showOnlyLinkedRecords)
    dispatch({command: "setShowOnlyLinkedRecords", value: showOnlyLinkedRecords})
  }, [dispatch])

  const setSelectedRecord = useCallback((recordKind: RecordKind, record?: ILinkableEntity) => {
    // if (record?.id !== appState.selectedRecords[recordKind]?.id)
    dispatch({command: "setSelectedRecord", value: {recordKind, value: record}})
  }, [dispatch])

  const setColumnVisibility = useCallback((recordKind: RecordKind, visibility: VisibilityState) => {
    // if (!isEqual(visibility, appState.columns[recordKind]?.visibility))
    dispatch({command: "setColumnVisibility", value: {recordKind, value: visibility}})
  }, [dispatch])

  const setColumnSizing = useCallback((recordKind: RecordKind, sizing: ColumnSizingState) => {
    // if (!isEqual(sizing, appState.columns[recordKind]?.sizing))
    dispatch({command: "setColumnSizing", value: {recordKind, value: sizing}})
  }, [dispatch])

  const setColumnOrder = useCallback((recordKind: RecordKind, order: ColumnOrderState) => {
    // if (!isEqual(order, appState.columns[recordKind]?.order))
    dispatch({command: "setColumnOrder", value: {recordKind, value: order}})
  }, [dispatch])

  const setSorting = useCallback((recordKind: RecordKind, sorting: SortingState) => {
    // if (!isEqual(sorting, appState.queries[recordKind]?.sorting))
    dispatch({command: "setSorting", value: {recordKind, value: sorting}})
  }, [dispatch])

  const setFilter = useCallback((recordKind: RecordKind, filter: QueryFilter) => {
    // if (!isEqual(filter, appState.queries[recordKind]?.filter))
    dispatch({command: "setFilter", value: {recordKind, value: filter}})
  }, [dispatch])

  const setPagination = useCallback((recordKind: RecordKind, pagination: PaginationState) => {
    // if (!isEqual(pagination, appState.queries[recordKind]?.pagination))
    dispatch({command: "setPagination", value: {recordKind, value: pagination}})
  }, [dispatch])

  const setSelectedLinkId= useCallback((recordKind: RecordKind, selectedLinkId?: string) => {
    // if (!isEqual(selectedLinkId, appState.queries[recordKind]?.selectedLinkId))
    dispatch({command: "setSelectedLinkId", value: {recordKind, value: selectedLinkId}})
  }, [dispatch])

  const setShowUsersOrMembers = useCallback((showUsersOrMembers: UsersPageRadioState) => {
    // if (showUsersOrMembers !== appState.queries.User?.showUsersOrMembers)
    dispatch({command: "setShowUsersOrMembers", value: {recordKind: "User", value: showUsersOrMembers}})
  }, [dispatch])

  const setActiveSecurityPageTab = useCallback((activeTab: SecurityPageTabState) => {
    // if (activeTab !== appState.queries.Group?.activeTab)
    dispatch({command: "setActiveTab", value: {recordKind: "Group", value: activeTab}})
  }, [dispatch])

  const storeAppState = useCallback(() => {
    if (appState.modified) {
      // storeAppStateSs(appState)
      dispatch({command: "flush", value: undefined})
      logger.debug("Flushed app state to session storage (would have!)")
    }
  }, [dispatch, appState])

  const globalContext = useMemo(() => {
    logger.debug("memo: refreshing global context")
    return {
      ...appState,
      setDefaults,
      setSecurityPrincipal,
      setSidebarOpen,
      setLinkFilterOpen,
      setTrackingDetailsOpen,
      setMasterTopicId,
      setMasterTopicRecursive,
      setMasterRecordKind,
      setMasterRecord,
      setMasterRecordId,
      setShowOnlyLinkedRecords,
      setSelectedRecord,
      setColumnVisibility,
      setColumnSizing,
      setColumnOrder,
      setFilter,
      setSorting,
      setPagination,
      setSelectedLinkId,
      setShowUsersOrMembers,
      setActiveSecurityPageTab,
      storeAppState,
    }
  }, [
    appState,
    setDefaults,
    setSecurityPrincipal,
    setSidebarOpen,
    setLinkFilterOpen,
    setTrackingDetailsOpen,
    setMasterTopicId,
    setMasterTopicRecursive,
    setMasterRecordKind,
    setMasterRecord,
    setMasterRecordId,
    setShowOnlyLinkedRecords,
    setSelectedRecord,
    setColumnVisibility,
    setColumnSizing,
    setColumnOrder,
    setFilter,
    setSorting,
    setPagination,
    setSelectedLinkId,
    setShowUsersOrMembers,
    setActiveSecurityPageTab,
    storeAppState,
  ]) as GlobalContextType

  // On navigation, store the application state to session storage.
  // useEffect(() => {
  //   return () => {
  //     storeAppState()
  //   }
  // }, [])
  // const router = useRouter()
  // useEffect(() => {
  //   router.events.on("routeChangeStart", storeAppState)
  //   return () => router.events.off("routeChangeStart", storeAppState)
  // }, [router, storeAppState])

  const [logLevelsDrawerOpen, setLogLevelsDrawerOpen] = useState(false)
  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLBodyElement>) => {
    if (e.key === 'l' && e.ctrlKey && e.altKey)
      setLogLevelsDrawerOpen(!logLevelsDrawerOpen)
  }, [])

  return (
    <html lang="en">
      <head>
        <title>Evidence Engine</title>
      </head>
      <body className={`${inter.className} antialiased`} onKeyDown={handleKeyDown}>
        <FlushOnPathChange flushFn={storeAppState} />
        <ApolloProvider client={apolloClient}>
          <AuthProvider>
            <GlobalContext value={globalContext}>
              <Toaster position="top-center" expand />
              <Suspense>
                {children}
              </Suspense>
            </GlobalContext>
          </AuthProvider>
        </ApolloProvider>
        <LoggingLevelDrawer open={logLevelsDrawerOpen} onOpenChange={setLogLevelsDrawerOpen} />
      </body>
    </html>
  )
}
