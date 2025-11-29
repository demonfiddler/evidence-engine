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

import { DeclarationKind } from "../ui/validators/declaration"
import { PublicationKind } from "../ui/validators/publication"
import Authority from "./Authority"

type DirectionKind = "ASC" | "DESC"
type EntityKind = "CLA" | "COM" | "COU" | "DEC" | "GRP" | "JOU" | "LNK" | "PER" | "PUB" | "PBR" | "QUO" | "TOP" | "USR"
type NullHandlingKind = "NATIVE" | "NULLS_FIRST" | "NULLS_LAST"
type StatusKind = "DRA" | "PUB" | "SUS" | "DEL"
type TransactionKind = "CRE" | "UPD" | "DEL" | "LNK" | "UNL" | "COM"

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
  recordId?: string
}

export type CommentQueryFilter = TrackedEntityQueryFilter & {
  targetKind?: EntityKind
  targetId?: string
  parentId?: string
  userId?: string
  from?: Date
  to?: Date
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

export type QueryFilter =
  TrackedEntityQueryFilter |
  LinkableEntityQueryFilter |
  CommentQueryFilter |
  TopicQueryFilter |
  LogQueryFilter

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

export interface BaseEntityInput {
  id?: string
}

export interface TrackedEntityInput extends BaseEntityInput {
  rating: number | null
}

export interface ClaimInput extends TrackedEntityInput {
  text: string
  date: Date | string | null
  notes: string | null
}

export interface CommentInput extends TrackedEntityInput {
  targetId?: string
  parent?: Comment
  text: string
}

export interface DeclarationInput extends TrackedEntityInput {
  kind: DeclarationKind
  title: string
  date: Date | string
  country: string | null // TODO: use Country type
  url: string | null
  signatories: string | null
  notes: string | null
}

export interface EntityLinkInput extends TrackedEntityInput {
  fromEntityId: string
  fromEntityLocations: string | null
  toEntityId: string
  toEntityLocations: string | null
}

export interface JournalInput extends TrackedEntityInput {
  title: string
  abbreviation: string | null
  url: string | null
  issn: string | null
  publisherId: string | null
  notes: string | null
  peerReviewed: boolean | null
}

export interface PersonInput extends TrackedEntityInput {
  title: string | null
  firstName: string
  nickname: string | null
  prefix: string | null
  lastName: string
  suffix: string | null
  alias: string | null
  notes: string | null
  qualifications: string | null
  country: string | null // TODO: use Country type
  checked: boolean
  published: boolean
}

export interface PublicationInput extends TrackedEntityInput {
  authorNames: string | null // TODO: rename to authors
  // authorIds: string[] | null // TODO: remove from schema.graphqls
  title: string
  journalId: string | null
  kind: PublicationKind
  date: Date | string | null
  year: number | null
  keywords: string | null
  abstract: string | null
  notes: string | null
  peerReviewed: boolean
  doi: string | null
  isbn: string | null
  pmcid: string | null
  pmid: string | null
  hsid: string | null
  arxivid: string | null
  biorxivid: string | null
  medrxivid: string | null
  ericid: string | null
  ihepid: string | null
  oaipmhid: string | null
  halid: string | null
  zenodoid: string | null
  scopuseid: string | null
  wsan: string | null
  pinfoan: string | null
  url: string | null
  accessed: Date | string | null
  cached: boolean
}

export interface PublisherInput extends TrackedEntityInput {
  name: string
  location: string | null
  country: string | null // TODO: use Country type
  url: string | null
  journalCount: number | null
  notes: string | null
}

export interface QuotationInput extends TrackedEntityInput {
  text: string
  quotee: string
  date: Date | string | null
  source: string | null
  url: string | null
  notes: string | null
}

export interface TopicInput extends TrackedEntityInput {
  label: string
  description: string | null
  parentId: string | null
}

export interface SecurityPrincipalInput extends TrackedEntityInput {
  authorities: Authority[]
}

export interface UserInput extends SecurityPrincipalInput {
  username: string
  firstName: string
  lastName: string
  email: string
  password: string | null
  country: string | null
  notes: string | null
}

export interface UserPasswordInput {
  id: string
  password: string
}

export interface UserProfileInput {
  id: string
  firstName: string
  lastName: string
  email: string
  country: string
  notes: string | null
}

export interface GroupInput extends SecurityPrincipalInput {
  groupname: string
}
