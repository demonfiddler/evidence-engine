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

import Claim from "@/app/model/Claim"
import Declaration from "@/app/model/Declaration"
import Group from "@/app/model/Group"
import IPage from "@/app/model/IPage"
import ITrackedEntity from "@/app/model/ITrackedEntity"
import Journal from "@/app/model/Journal"
import Log from "@/app/model/Log"
import Person from "@/app/model/Person"
import Publication from "@/app/model/Publication"
import Publisher from "@/app/model/Publisher"
import Quotation from "@/app/model/Quotation"
import RecordKind from "@/app/model/RecordKind"
import Topic from "@/app/model/Topic"
import User from "@/app/model/User"
import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(date: Date | string | null | undefined, format?: string) {
  if (typeof date == "string")
    date = new Date(date)
  if (date && typeof date == "object")
    return date.toDateString()
  return ""
}

export function isLinkableEntity(recordKind: RecordKind) {
  switch (recordKind) {
    case "Claim":
    case "Declaration":
    case "Person":
    case "Publication":
    case "Quotation":
    case "Topic":
      return true
    default:
      return false
  }
}

export function getRecordLabel(recordKind: RecordKind | undefined, record?: ITrackedEntity): string | undefined {
  if (!recordKind || !record)
    return undefined

  switch (recordKind) {
    case "Claim": {
      const claim = record as Claim
      return `${recordKind} #${record?.id}: ${claim?.text}`
    }
    case "Declaration": {
      const declaration = record as Declaration
      return `${recordKind} #${record?.id}: ${declaration?.title}`
    }
    case "Journal": {
      const journal = record as Journal
      return `${recordKind} #${record?.id}: ${journal?.title}`
    }
    case "Log": {
      const log = record as Log
      return `${recordKind} #${record?.id}: ${log?.entityKind} #${log?.entityId} ${log?.transactionKind}`
    }
    case "Person": {
      const person = record as Person
      return `${recordKind} #${record?.id}: ${person?.firstName} ${person?.lastName}`
    }
    case "Publication": {
      const publication = record as Publication
      return `${recordKind} #${record?.id}: ${publication?.title}`
    }
    case "Publisher": {
      const publisher = record as Publisher
      return `${recordKind} #${record?.id}: ${publisher?.name}`
    }
    case "Quotation": {
      const quotation = record as Quotation
      return `${recordKind} #${record?.id}: ${quotation?.text}`
    }
    case "Topic": {
      const topic = record as Topic
      return `${recordKind} #${record?.id}: ${topic?.label}`
    }
    case "User": {
      const user = record as User
      return `${recordKind} #${record?.id}: ${user?.firstName} ${user?.lastName} (${user?.username})`
    }
    case "Group": {
      const group = record as Group
      return `${recordKind} #${record?.id}: ${group?.groupname}`
    }
    default:
      throw new Error(`Unrecognised record kind: ${recordKind}`);
  }
}

export function setTopicFields(path: string, parentId?: BigInt | string, topics?: Topic[]) {
  if (topics) {
    for (let t of topics) {
      t.path = (path.length > 0 ? path + " > " : path) + t.label;
      t.parentId = parentId;
      setTopicFields(t.path, t.id, t.children)
    }
  }
}

type Action = {
  recordId: BigInt | string
  command: string
  value: any
}
export type { Action }

export function pageReducer<T extends ITrackedEntity>(draft: IPage<T>, action: Action) {
  const recordIndex = draft.content.findIndex(c => c.id == action.recordId)
  switch (action.command) {
    case "add":
      draft.content.push(action.value)
      break
    case "update":
      if (recordIndex != -1)
        draft.content.splice(recordIndex, 1, action.value)
      break
    case "delete":
      // N.B. This is a hard delete that physically deletes the record. We plan to implement only a soft delete.
      if (recordIndex != -1)
        draft.content.splice(recordIndex, 1)
      break
  }
}

