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

export type MasterLinkContextBase = {
  masterTopicId?: BigInt | string
  masterTopicDescription?: string
  masterTopicPath?: string
  masterRecordId?: BigInt | string
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
  id: BigInt | string
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