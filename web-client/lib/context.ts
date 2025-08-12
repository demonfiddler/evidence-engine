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

import { createContext } from 'react';

import ILinkableEntity from "../app/model/ILinkableEntity"
import RecordKind from "../app/model/RecordKind"
import Topic from "../app/model/Topic"
import Authority from '@/app/model/Authority';
import User from '@/app/model/User';
import {
  ColumnOrderState,
  ColumnSizingState,
  PaginationState,
  SortingState,
  Updater,
  VisibilityState
} from '@tanstack/react-table';

export type MasterLinkState = {
  masterTopicId?: string
  masterTopicDescription?: string | null
  masterTopicPath?: string | null
  masterRecordId?: string
  masterRecordLabel?: string
  masterRecordKind: RecordKind
}

export type MasterLinkStateSetters = {
  setMasterTopic: (topic?: Topic) => void
  setMasterRecord: (recordKind: RecordKind, record?: ILinkableEntity) => void
  setMasterRecordKind: (recordKind: RecordKind) => void
}

export type SelectedRecord = {
  id: string
  label: string
}

export type SelectedRecordsMap = {
  [k in RecordKind]?: SelectedRecord | undefined
}

export type SelectedRecordsState = {
  selectedRecords: SelectedRecordsMap
}

export type SelectedRecordsStateSetters = {
  setSelectedRecord: (recordKind: RecordKind, record?: ILinkableEntity) => void
}

export type SecurityState = {
  username?: string
  authorities?: Authority[]
}

export type SecurityStateSetters = {
  setSecurityPrincipal: (user?: User) => void
}

export type UsersPageRadioState = "users" | "members"
export type SecurityPageTabState = "groups" | "users"

export type QueryState<TFilter> = {
  filter: TFilter
  sorting: SortingState
  pagination: PaginationState
  selectedLinkId?: string
  showOnlyLinkedRecords?: boolean
  showUsersOrMembers?: UsersPageRadioState
  activeTab?: SecurityPageTabState
}

export type QueryStatesMap = {
  [k in RecordKind]?: QueryState<any>
}
// export type QueryStatesMap = {
//   None?: QueryState<any>
//   Claim?: QueryState<LinkableEntityQueryFilter>
//   Declaration?: QueryState<LinkableEntityQueryFilter>
//   Group?: QueryState<TrackedEntityQueryFilter>
//   Journal?: QueryState<TrackedEntityQueryFilter>
//   Log?: QueryState<LogQueryFilter>
//   Person?: QueryState<LinkableEntityQueryFilter>
//   Publication?: QueryState<LinkableEntityQueryFilter>
//   Publisher?: QueryState<TrackedEntityQueryFilter>
//   Quotation?: QueryState<LinkableEntityQueryFilter>
//   Topic?: QueryState<TrackedEntityQueryFilter>
//   User?: QueryState<TrackedEntityQueryFilter>
// }

export type QueryStates = {
  queries: QueryStatesMap
}

export type QueryStateSetters = {
  setFilter: <TFilter>(recordKind: RecordKind, filter: TFilter) => void
  setSorting: (recordKind: RecordKind, sorting: Updater<SortingState>) => void
  setPagination: (recordKind: RecordKind, pagination: Updater<PaginationState>) => void
  setSelectedLinkId: (recordKind: RecordKind, selectedLinkId?: string) => void
  setShowOnlyLinkedRecords: (recordKind: RecordKind, showOnlyLinkedRecords: boolean) => void
  setShowUsersOrMembers: (showUsersOrMembers: UsersPageRadioState) => void
  setActiveTab: (activeTab : SecurityPageTabState) => void
}

export type SizeState = {
  [x: string]: number
}

export type ColumnState = {
  visibility: VisibilityState
  sizing: SizeState
  order: ColumnOrderState
}

export type ColumnStatesMap = {
  [k in RecordKind]?: ColumnState
}

export type ColumnStates = {
  columns: ColumnStatesMap
}

export type ColumnStateSetters = {
  setColumnVisibility: (recordKind: RecordKind, visibility: Updater<VisibilityState>) => void
  setColumnSizing: (recordKind: RecordKind, sizing: ColumnSizingState) => void
  setColumnOrder: (recordKind: RecordKind, order: ColumnOrderState) => void
}

export type ToggleState = {
  sidebarOpen: boolean
  linkFilterOpen: boolean
  trackingDetailsOpen: boolean
}

export type ToggleStateSetters = {
  setSidebarOpen: (sidebarOpen: boolean) => void
  setLinkFilterOpen: (linkFilterOpen: boolean) => void
  setTrackingDetailsOpen: (trackingDetailsOpen: boolean) => void
}

export type StorageState = {
  modified: boolean
}

export type StorageStateFns = {
  storeAppState: () => void
}

export type AppState =
  StorageState &
  SecurityState &
  ToggleState &
  MasterLinkState &
  SelectedRecordsState &
  ColumnStates &
  QueryStates

export type AppStateSetters =
  StorageStateFns &
  SecurityStateSetters &
  ToggleStateSetters &
  MasterLinkStateSetters &
  SelectedRecordsStateSetters &
  ColumnStateSetters &
  QueryStateSetters
  
export type GlobalContextType = AppState & AppStateSetters

/**
 * Global context is stored in the browser's session or local storage. It includes the following settings:
 * - sidebar state: open or closed;
 * - master entity link filter panel state: open or closed;
 * - tracking and linking details panel: open or closed;
 * - master entity link filter settings;
 * - selected records context;
 * - data table column settings (displayed, order, widths) for each page;
 * - query filter, sort and pagination settings for each page.
 *
 * In addition, it provides mutator functions for setting various aspects of the global context.
 */
export const GlobalContext = createContext<GlobalContextType>({
  modified: false,
  sidebarOpen: false,
  linkFilterOpen: false,
  trackingDetailsOpen: false,
  masterRecordKind: "None",
  selectedRecords: {},
  columns: {},
  queries: {},
  setSecurityPrincipal: () => {throw new Error("setSecurityPrincipal() not supported in default GlobalContext")},
  setSidebarOpen: () => {throw new Error("setSidebarOpen() not supported in default GlobalContext")},
  setLinkFilterOpen: () => {throw new Error("setLinkFilterOpen() not supported in default GlobalContext")},
  setTrackingDetailsOpen: () => {throw new Error("setTrackingDetailsOpen() not supported in default GlobalContext")},
  setMasterTopic: () => {throw new Error("setMasterTopic() not supported in default GlobalContext")},
  setMasterRecord: () => {throw new Error("setMasterRecord() not supported in default GlobalContext")},
  setMasterRecordKind: () => {throw new Error("setMasterRecordKind() not supported in default GlobalContext")},
  setSelectedRecord: () => {throw new Error("setSelectedRecord() not supported in default GlobalContext")},
  setColumnVisibility: () => {throw new Error("setColumnVisibility() not supported in default GlobalContext")},
  setColumnSizing: () => {throw new Error("setColumnSize() not supported in default GlobalContext")},
  setColumnOrder: () => {throw new Error("setColumnOrder() not supported in default GlobalContext")},
  setFilter: () => {throw new Error("setFilter() not supported in default GlobalContext")},
  setSorting: () => {throw new Error("setSort() not supported in default GlobalContext")},
  setPagination: () => {throw new Error("setPagination() not supported in default GlobalContext")},
  setSelectedLinkId: () => {throw new Error("setSelectedLinkId() not supported in default GlobalContext")},
  setShowOnlyLinkedRecords: () => {throw new Error("setShowOnlyLinkedRecords() not supported in default GlobalContext")},
  setShowUsersOrMembers: () => {throw new Error("setShowUsersOrMembers() not supported in default GlobalContext")},
  storeAppState: () => {throw new Error("storeAppState() not supported in default GlobalContext")},
  setActiveTab: () => {throw new Error("setActiveTab() not supported in default GlobalContext")},
})
