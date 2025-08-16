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

if (process.env.NODE_ENV === 'development') {
  // const wdyr = require('../wdyr');
}

import '@/app/ui/global.css'
import { inter } from '@/app/ui/fonts'
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
} from '@/lib/context'
import RecordKind from './model/RecordKind'
import ILinkableEntity from './model/ILinkableEntity'
import { getRecordLabel } from '@/lib/utils'
import { useCallback, useEffect, useRef } from 'react'
import { useSessionStorage } from 'usehooks-ts'
import Topic from './model/Topic'
import User from './model/User'
import { ApolloProvider } from '@apollo/client'
import { apolloClient } from '@/lib/graphql-utils'
import { AuthProvider } from '@/hooks/use-auth'
import { useImmerReducer } from "use-immer"
import { ColumnOrderState, ColumnSizingState, PaginationState, SortingState, VisibilityState } from '@tanstack/react-table'
import IBaseEntity from './model/IBaseEntity'
import { LinkableEntityQueryFilter, LogQueryFilter, TrackedEntityQueryFilter } from './model/schema'
import { usePathname } from 'next/navigation'
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

const defaultColumnSettings = {
  None: {columns: [], visibility: {}},
  Claim: {columns: claimColumns, visibility: claimColumnVisibility},
  Declaration: {columns: declarationColumns, visibility: declarationColumnVisibility},
  Group: {columns: groupColumns, visibility: groupColumnVisibility},
  Journal: {columns: journalColumns, visibility: journalColumnVisibility},
  Log: {columns: logColumns, visibility: logColumnVisibility},
  Person: {columns: personColumns, visibility: personColumnVisibility},
  Publication: {columns: publicationColumns, visibility: publicationColumnVisibility},
  Publisher: {columns: publisherColumns, visibility: publisherColumnVisibility},
  Quotation: {columns: quotationColumns, visibility: quotationColumnVisibility},
  Topic: {columns: topicColumns, visibility: topicColumnVisibility},
  User: {columns: userColumns, visibility: userColumnVisibility},
}

function defaultAppState() {
  return {
    modified: false,
    sidebarOpen: true,
    linkFilterOpen: false,
    trackingDetailsOpen: false,
    masterRecordKind: "None",
    selectedRecords: {},
    columns: defaultColumnStatesMap(),
    queries: defaultQueryStatesMap(),
  } as AppState
}

function defaultColumnStatesMap() {
  return {
    None: defaultColumnState("None"),
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
    None: defaultQueryState<any>(),
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
  value: any
}

type RecordKindValue<TValue> = {
  recordKind: RecordKind
  value: TValue
}

type RecordKindValueOpt<TValue> = Omit<RecordKindValue<TValue>, "value"> & {
  value?: TValue
}

function reducer(draft: AppState, action: ReducerArg) {
  draft.modified = true
  switch (action.command) {
    case "flush":
      draft.modified = false
      break
    case "setSecurityPrincipal": {
      const user = action.value as User
      draft.username = user?.username
      draft.authorities = user?.authorities
      break
    }
    case "setSidebarOpen": {
      draft.sidebarOpen = action.value
      break
    }
    case "setLinkFilterOpen": {
      draft.linkFilterOpen = action.value
      break
    }
    case "setTrackingDetailsOpen": {
      draft.trackingDetailsOpen = action.value
      break
    }
    case "setMasterTopic": {
      const topic = action.value as Topic
      draft.masterTopicId = topic?.id
      draft.masterTopicDescription = topic?.description
      draft.masterTopicPath = topic?.path
      break
    }
    case "setMasterRecord": {
      const value = action.value as RecordKindValueOpt<SelectedRecord>
      draft.masterRecordKind = value.recordKind
      draft.masterRecordId = value.value?.id
      draft.masterRecordLabel = value.value?.label
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
      const {recordKind, value: filter} = action.value as RecordKindValue<any>
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
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<any>
      query.selectedLinkId = selectedLinkId
      break
    }
    case "setShowOnlyLinkedRecords": {
      const {recordKind, value: showOnlyLinkedRecords} = action.value as RecordKindValue<boolean>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<any>
      query.showOnlyLinkedRecords = showOnlyLinkedRecords
      break
    }
    case "setShowUsersOrMembers": {
      const {recordKind, value: showUsersOrMembers} = action.value as RecordKindValue<UsersPageRadioState>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<any>
      query.showUsersOrMembers = showUsersOrMembers
      break
    }
    case "setActiveTab": {
      const {recordKind, value: activeTab} = action.value as RecordKindValue<SecurityPageTabState>
      const query = draft.queries[recordKind] ??= defaultQueryState() as QueryState<any>
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
      console.log(`Navigating from ${prevPath.current} to ${pathname}`)
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
  const [appStateSs, storeAppStateSs] = useSessionStorage<AppState>('app-state', defaultAppState)
  const [appState, dispatch] = useImmerReducer<AppState, ReducerArg>(reducer, appStateSs)

  const setSecurityPrincipal = useCallback((user?: User) => {
    dispatch({command: "setSecurityPrincipal", value: user})
  }, [dispatch])

  const setSidebarOpen = useCallback((sidebarOpen: boolean) => {
    dispatch({command: "setSidebarOpen", value: sidebarOpen})
  }, [dispatch])

  const setLinkFilterOpen = useCallback((linkFilterOpen: boolean) => {
    dispatch({command: "setLinkFilterOpen", value: linkFilterOpen})
  }, [dispatch])

  const setTrackingDetailsOpen = useCallback((trackingDetailsOpen: boolean) => {
    dispatch({command: "setTrackingDetailsOpen", value: trackingDetailsOpen})
  }, [dispatch])

  const setMasterTopic = useCallback((masterTopic?: Topic) => {
    dispatch({command: "setMasterTopic", value: masterTopic})
  }, [dispatch])

  const setMasterRecord = useCallback((recordKind: RecordKind, record: IBaseEntity) => {
    dispatch({command: "setMasterRecord", value: {recordKind, value: record}})
  }, [dispatch])

  const setMasterRecordKind = useCallback((recordKind: RecordKind) => {
    const record = appState.selectedRecords[recordKind]
    dispatch({command: "setMasterRecord", value: {recordKind, value: record}})
  }, [dispatch, appState])

  const setSelectedRecord = useCallback((recordKind: RecordKind, record?: ILinkableEntity) => {
    dispatch({command: "setSelectedRecord", value: {recordKind, value: record}})
  }, [dispatch])

  const setColumnVisibility = useCallback((recordKind: RecordKind, visibility: VisibilityState) => {
    dispatch({command: "setColumnVisibility", value: {recordKind, value: visibility}})
  }, [dispatch])

  const setColumnSizing = useCallback((recordKind: RecordKind, sizing: ColumnSizingState) => {
    dispatch({command: "setColumnSizing", value: {recordKind, value: sizing}})
  }, [dispatch])

  const setColumnOrder = useCallback((recordKind: RecordKind, order: ColumnOrderState) => {
    dispatch({command: "setColumnOrder", value: {recordKind, value: order}})
  }, [dispatch])

  const setSorting = useCallback((recordKind: RecordKind, sorting: SortingState) => {
    dispatch({command: "setSorting", value: {recordKind, value: sorting}})
  }, [dispatch])

  const setFilter = useCallback((recordKind: RecordKind, filter: any) => {
    dispatch({command: "setFilter", value: {recordKind, value: filter}})
  }, [dispatch])

  const setPagination = useCallback((recordKind: RecordKind, pagination: PaginationState) => {
    dispatch({command: "setPagination", value: {recordKind, value: pagination}})
  }, [dispatch])

  const setSelectedLinkId= useCallback((recordKind: RecordKind, selectedLinkId?: string) => {
    dispatch({command: "setSelectedLinkId", value: {recordKind, value: selectedLinkId}})
  }, [dispatch])

  const setShowOnlyLinkedRecords = useCallback((recordKind: RecordKind, showOnlyLinkedRecords: boolean) => {
    dispatch({command: "setShowOnlyLinkedRecords", value: {recordKind, value: showOnlyLinkedRecords}})
  }, [dispatch])

  const setShowUsersOrMembers = useCallback((showUsersOrMembers: UsersPageRadioState) => {
    dispatch({command: "setShowUsersOrMembers", value: {recordKind: "User", value: showUsersOrMembers}})
  }, [dispatch])

  const setActiveTab = useCallback((activeTab: SecurityPageTabState) => {
    dispatch({command: "setActiveTab", value: {recordKind: "Group", value: activeTab}})
  }, [dispatch])

  const storeAppState = useCallback(() => {
    if (appState.modified) {
      // storeAppStateSs(appState)
      dispatch({command: "flush", value: undefined})
      console.log("Flushed app state to session storage (would have!)")
    }
  }, [appState])

  const globalContext = {
    ...appState,
    setSecurityPrincipal,
    setSidebarOpen,
    setLinkFilterOpen,
    setTrackingDetailsOpen,
    setMasterTopic,
    setMasterRecord,
    setMasterRecordKind,
    setSelectedRecord,
    setColumnVisibility,
    setColumnSizing,
    setColumnOrder,
    setFilter,
    setSorting,
    setPagination,
    setSelectedLinkId,
    setShowOnlyLinkedRecords,
    setShowUsersOrMembers,
    setActiveTab,
    storeAppState,
  } as GlobalContextType

  // On navigation, store the application state to session storage.
  // useEffect(() => {
  //   return () => {
  //     storeAppState()
  //   }
  // }, [])
  // const router = useRouter()
  // useEffect(() => {
  //   router.events.on('routeChangeStart', storeAppState)
  //   return () => router.events.off('routeChangeStart', storeAppState)
  // }, [router, storeAppState])
  // useEffect(() => {
  //   router.events.on('routeChangeStart', storeAppState)
  //   return () => router.events.off('routeChangeStart', storeAppState)
  // }, [router, storeAppState])

  return (
    <html lang="en">
      <body className={`${inter.className} antialiased`}>
        <FlushOnPathChange flushFn={storeAppState} />
        <AuthProvider>
          <ApolloProvider client={apolloClient}>
            <GlobalContext value={globalContext}>
              <Toaster position="top-center" expand />
              {children}
            </GlobalContext>
          </ApolloProvider>
        </AuthProvider>
      </body>
    </html>
  )
}
