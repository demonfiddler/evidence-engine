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

export type MasterLinkContextBase = {
  masterTopicId?: string
  masterTopicDescription?: string | null
  masterTopicPath?: string | null
  masterRecordId?: string
  masterRecordLabel?: string
  masterRecordKind: RecordKind
}

export type MasterLinkContextType = MasterLinkContextBase & {
  setMasterTopic: (curMlCtx: MasterLinkContextType, masterTopic?: Topic) => void
  setMasterRecord: (curMlCtx: MasterLinkContextType, masterRecord?: ILinkableEntity) => void
  setMasterRecordKind: (curMlCtx: MasterLinkContextType, curSrCtx: SelectedRecordsContextType, masterRecordKind: RecordKind) => void
}

export const MasterLinkContext = createContext<MasterLinkContextType>({
  masterRecordKind: "None",
  setMasterTopic: () => {throw new Error("setMasterTopic() not supported in default MasterLinkContext")},
  setMasterRecord: () => {throw new Error("setMasterRecord() not supported in default MasterLinkContext")},
  setMasterRecordKind: () => {throw new Error("setMasterRecordKind() not supported in default MasterLinkContext")},
});

export type SelectedRecord = {
  id: string
  label: string
}

export type SelectedRecordsContextBase = {
  [K in RecordKind]?: SelectedRecord
}

export type SelectedRecordsContextType = SelectedRecordsContextBase & {
  setSelectedRecord: (curSrCtx: SelectedRecordsContextType, recordKind: RecordKind, record?: ILinkableEntity) => void
}

export const SelectedRecordsContext = createContext<SelectedRecordsContextType>({
  setSelectedRecord: () => {throw new Error("setSelectedRecord() not supported in default SelectedRecordsContext")}
})

export type SecurityContextBase = {
  username?: string
  authorities?: Authority[]
}

export type SecurityContextType = SecurityContextBase & {
  setSecurityContext: (secCtx: SecurityContextType, user?: User) => void
  // hasAuthority: (authority: Authority) => boolean
}

export const SecurityContext = createContext<SecurityContextType>({
  setSecurityContext: () => {throw new Error("setSecurityContext() not supported in default SecurityContext")}
  // hasAuthority: (authority: Authority) => authorities?.includes(authority)
})