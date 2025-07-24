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

type DirectionKind = "ASC" | "DESC"
type EntityKind = "CLA" | "COU" | "DEC" | "GRP" | "JOU" | "LNK" | "PER" | "PUB" | "PBR" | "QUO" | "TOP" | "USR"
type NullHandlingKind = "NATIVE" | "NULLS_FIRST" | "NULLS_LAST"
type StatusKind = "DRA" | "PUB" | "SUS" | "DEL"
type TransactionKind = "CRE" | "UPD" | "DEL" | "LNK" | "UNL"

export type {DirectionKind, EntityKind, NullHandlingKind, StatusKind, TransactionKind}

export type LogQueryFilter = {
  entityKind?: EntityKind
  entityId?: string
  userId?: string
  transactionKinds?: [TransactionKind]
  from?: Date
  to?: Date
}

export type TrackedEntityQueryFilter = {
  status?: StatusKind[]
  text?: string
  advancedSearch?: boolean
}

export type LinkableEntityQueryFilter = TrackedEntityQueryFilter & {
  topicId?: string
  recursive?: boolean
  fromEntityId?: string
  fromEntityKind?: EntityKind
  toEntityId?: string
  toEntityKind?: EntityKind
}

export type TopicQueryFilter = TrackedEntityQueryFilter & {
  parentId?: string
  recursive?: boolean
}

export type PageableInput = {
  pageNumber?: number
  pageSize?: number
  sort?: SortInput
}

export type SortInput = {
  orders: OrderInput[]
}

export type OrderInput = {
    property: string
    direction?: DirectionKind // = ASC
    ignoreCase?: boolean // = true (MariaDB default collation is case-insensitive)
    nullHandling?: NullHandlingKind // = NATIVE
}
