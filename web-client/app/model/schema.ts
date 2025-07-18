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
  pageNumber: number
  pageSize: number
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
