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
import ITrackedEntity from "@/app/model/ITrackedEntity"
import Journal from "@/app/model/Journal"
import Log from "@/app/model/Log"
import Person from "@/app/model/Person"
import Publication from "@/app/model/Publication"
import Publisher from "@/app/model/Publisher"
import Quotation from "@/app/model/Quotation"
import RecordKind from "@/app/model/RecordKind"
import { StatusKind } from "@/app/model/schema"
import Topic from "@/app/model/Topic"
import User from "@/app/model/User"
import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import { READ_CLAIMS, READ_DECLARATIONS, READ_GROUPS, READ_JOURNALS, READ_LOGS, READ_PERSONS, READ_PUBLICATIONS, READ_PUBLISHERS, READ_QUOTATIONS, READ_TOPIC_HIERARCHY, READ_USERS } from "./graphql-queries"
import { DocumentNode } from "graphql"
import { Updater } from "@tanstack/react-table"

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

export function formatDateTime(date: Date | string | null | undefined, format?: string) {
  if (typeof date == "string")
    date = new Date(date)
  if (date && typeof date == "object")
    return `${date.toDateString()} ${date.toLocaleTimeString()}`
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
    case "Group": {
      const group = record as Group
      return `${recordKind} #${record?.id}: ${group?.groupname}`
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
    default:
      throw new Error(`Unrecognised record kind: ${recordKind}`);
  }
}

export function setTopicFields(path: string, parentId?: string, inTopics?: Topic[], outTopics?: Topic[]) {
  if (inTopics) {
    for (let inTopic of inTopics) {
      const outTopic = {
        ...inTopic,
        children: [],
        path: (path.length > 0 ? path + " > " : "") + inTopic.label,
        parentId: parentId,
      }
      setTopicFields(outTopic.path, inTopic.id, inTopic.children, outTopic.children)
      outTopics?.push(outTopic)
    }
  }
}

export const FROM_ENTITY_ID = "fromEntityId"
export const TO_ENTITY_ID = "toEntityId"
export const FROM_ENTITY_LOCATIONS = "fromEntityLocations"
export const TO_ENTITY_LOCATIONS = "toEntityLocations"
const NONE = [] as string[]
const FROM_TO = [FROM_ENTITY_ID, TO_ENTITY_ID, FROM_ENTITY_LOCATIONS, TO_ENTITY_LOCATIONS]
const TO_FROM = [TO_ENTITY_ID, FROM_ENTITY_ID, TO_ENTITY_LOCATIONS, FROM_ENTITY_LOCATIONS]
type MasterRecordLink = {[key in RecordKind]?: string[]}
type RecordLinks = {[key in RecordKind]?: MasterRecordLink}
const recordLinkProperties : RecordLinks = {
  Claim: {
    Claim: NONE,
    Declaration: FROM_TO,
    Person: FROM_TO,
    Publication: FROM_TO,
    Quotation: FROM_TO,
    Topic: TO_FROM
  },
  Declaration: {
    Claim: TO_FROM,
    Declaration: NONE,
    Person: FROM_TO,
    Publication: NONE,
    Quotation: FROM_TO,
    Topic: TO_FROM
  },
  Person: {
    Claim: TO_FROM,
    Declaration: TO_FROM,
    Person: NONE,
    Publication: TO_FROM,
    Quotation: TO_FROM,
    Topic: TO_FROM
  },
  Publication: {
    Claim: TO_FROM,
    Declaration: NONE,
    Person: FROM_TO,
    Publication: NONE,
    Quotation: TO_FROM,
    Topic: TO_FROM
  },
  Quotation: {
    Claim: TO_FROM,
    Declaration: TO_FROM,
    Person: FROM_TO,
    Publication: FROM_TO,
    Quotation: NONE,
    Topic: TO_FROM
  },
  Topic: {
    Claim: FROM_TO,
    Declaration: FROM_TO,
    Person: FROM_TO,
    Publication: FROM_TO,
    Quotation: FROM_TO,
    Topic: FROM_TO
  },
}

/**
 * Returns the to/from entity link properties.
 * @param thisRecordKind The record kind of 'this record'.
 * @param otherRecordKind The record kind of the 'other record'.
 * @returns A four-element array containing the property names
 * `[thisRecordIdProperty, otherRecordIdProperty, thisLocationsProperty, otherLocationsProperty]`,
 * or an empty array if no such link is supported.
 */
export function getRecordLinkProperties(thisRecordKind: RecordKind, otherRecordKind: RecordKind) : readonly string[] {
  return recordLinkProperties?.[thisRecordKind]?.[otherRecordKind] ?? NONE
}

const readQueryByRecordKind = {
  None: undefined,
  Claim: READ_CLAIMS,
  Declaration: READ_DECLARATIONS,
  Group: READ_GROUPS,
  Journal: READ_JOURNALS,
  Log: READ_LOGS,
  Person: READ_PERSONS,
  Publication: READ_PUBLICATIONS,
  Publisher: READ_PUBLISHERS,
  Quotation: READ_QUOTATIONS,
  Topic: READ_TOPIC_HIERARCHY,
  User: READ_USERS,
}

export function getReadQuery(recordKind: RecordKind) : DocumentNode | undefined {
  return readQueryByRecordKind[recordKind]
}

export function toDate(value?: string | Date | null) {
  return value instanceof Date
    ? value as Date
    : typeof value == "string"
      ? new Date(value)
      : ''
}

export function toIsoDateTimeString(value?: string | Date) {
  return value instanceof Date
    ? value.toISOString()
    : value && typeof value == "string"
      ? value
      : null
}

export function toIsoDateString(value?: string | Date) {
  let dateStr = value instanceof Date
    ? value.toISOString()
    : value && typeof value == "string"
      ? value
      : null
  return dateStr?.slice(0, 10) || null
}

export function toInteger(s?: string | number) {
  switch (typeof s) {
    case "string":
      return parseInt(s)
    case "number":
      return Math.floor(s)
    default:
      return null
  }
}

export function getValue<T>(updaterOrValue: Updater<T>, old: T) {
  return typeof updaterOrValue == "function"
    ? (updaterOrValue as (old: T) => T)(old)
    : updaterOrValue as T
}
