import { gql } from "@apollo/client"

//alert("ENTER graphql-queries.ts")

export const FRAGMENT_PAGE_FIELDS = gql`
fragment pageFields on IPage {
    hasContent
    isEmpty
    number
    size
    numberOfElements
    totalPages
    totalElements
    isFirst
    isLast
    hasNext
    hasPrevious
}
`
//alert("(1) graphql-queries.ts")

export const FRAGMENT_TRACKED_ENTITY_FIELDS = gql`
fragment trackedEntityFields on ITrackedEntity {
  ...on IBaseEntity {
    id
  }
  entityKind
  status
  created
  createdByUser {
    username
  }
  updated
  updatedByUser {
    username
  }
  log
  {
    # ...pageFields
    content {
      id
      timestamp
      transactionKind
      user {
        username
      }
      linkedEntityKind
      linkedEntityId
    }
  }
}
`
//alert("(2) graphql-queries.ts")

export const FRAGMENT_TRACKED_ENTITY_FIELDS_POLYMORPHIC = gql`
fragment trackedEntityFieldsPolymorphic on ITrackedEntity {
  ...trackedEntityFields
  ...on EntityLink {
    ...entityLinkFields
  }
  ...on Journal {
    ...journalFields
  }
  ...on Publisher {
    ...publisherFields
  }
  ...on User {
    ...userFields
  }
}
`
//alert("(3) graphql-queries.ts")

export const FRAGMENT_LINKED_ENTITY_FIELDS = gql`
fragment linkedEntityFields on ILinkableEntity {
  ... on IBaseEntity {
    id
  }
  ... on ITrackedEntity {
    entityKind
  }
  ...on Claim {
    text
  }
  ...on Declaration {
    title
  }
  ...on Person {
    firstName
    prefix
    lastName
    suffix
  }
  ...on Publication {
    title
  }
  ...on Quotation {
    text
  }
  ...on Topic {
    label
  }
}
`

export const FRAGMENT_LINKABLE_ENTITY_FIELDS = gql`
fragment linkableEntityFields on ILinkableEntity {
  fromEntityLinks
  # (
  #   filter: {
  #     topicId: 0
  #     recursive: false
  #     fromEntityKind: CLA
  #     fromEntityId: 0
  #     toEntityKind: PUB
  #     toEntityId: 0
  #     status: [DRA]
  #     text: "test"
  #     advancedSearch: false
  #   }
  #   pageSort: {
  #     sort: {
  #       orders: [
  #         {
  #           property: "id"
  #           direction: ASC
  #           ignoreCase: false
  #           nullHandling: NATIVE
  #         }
  #       ]
  #     }
  #   }
  # )
  {
    # ...pageFields
    content {
      id
      toEntity {
        ...linkedEntityFields
      }
      fromEntityLocations
      toEntityLocations
    }
  }
  toEntityLinks
  # (
  #   filter: {
  #     topicId: 0
  #     recursive: false
  #     fromEntityKind: CLA
  #     fromEntityId: 0
  #     toEntityKind: PUB
  #     toEntityId: 0
  #     status: [DRA]
  #     text: "test"
  #     advancedSearch: false
  #   }
  #   pageSort: {
  #     sort: {
  #       orders: [
  #         {
  #           property: "id"
  #           direction: ASC
  #           ignoreCase: false
  #           nullHandling: NATIVE
  #         }
  #       ]
  #     }
  #   }
  # )
  {
    # ...pageFields
    content {
      id
      fromEntity {
        ...linkedEntityFields
      }
      fromEntityLocations
      toEntityLocations
    }
  }
}
`
//alert("(4) graphql-queries.ts")

export const FRAGMENT_LINKABLE_ENTITY_FIELDS_POLYMORPHIC = gql`
fragment linkableEntityFieldsPolymorphic on ILinkableEntity {
  ...trackedEntityFields
  ...linkableEntityFields
  ...on Claim {
    ...claimFields
  }
  ...on Declaration {
    ...declarationFields
  }
  ...on Person {
    ...personFields
  }
  ...on Publication {
    ...publicationFields
  }
  ...on Quotation {
    ...quotationFields
  }
}
`
//alert("(5) graphql-queries.ts")

export const FRAGMENT_CLAIM_FIELDS = gql`
fragment claimFields on Claim {
  date
  text
  notes
}
`
//alert("(6) graphql-queries.ts")

export const FRAGMENT_DECLARATION_FIELDS = gql`
fragment declarationFields on Declaration {
  kind(format: SHORT)
  title
  date
  country(format: ALPHA_2)
  url
  cached
  signatories
  signatoryCount
  notes
}
`
//alert("(7) graphql-queries.ts")

export const FRAGMENT_ENTITY_LINK_FIELDS = gql`
fragment entityLinkFields on EntityLink {
  fromEntity {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...linkableEntityFieldsPolymorphic
  }
  toEntity {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...linkableEntityFieldsPolymorphic
  }
  fromEntityLocations
  toEntityLocations
}
`
//alert("(8) graphql-queries.ts")

//alert("(9) graphql-queries.ts")

export const FRAGMENT_LOG_FIELDS = gql`
fragment logFields on Log {
  id
  timestamp
  user {
    username
  }
  transactionKind
  entityKind
  entityId
  linkedEntityKind
  linkedEntityId
}
`
//alert("(10) graphql-queries.ts")

export const FRAGMENT_PERSON_FIELDS = gql`
fragment personFields on Person {
  title
  firstName
  nickname
  prefix
  lastName
  suffix
  alias
  qualifications
  notes
  country
  rating
  checked
  published
}
`
//alert("(11) graphql-queries.ts")

//alert("(12) graphql-queries.ts")

export const FRAGMENT_PUBLISHER_FIELDS = gql`
fragment publisherFields on Publisher {
  name
  location
  country
  url
}
`

export const FRAGMENT_JOURNAL_FIELDS = gql`
${FRAGMENT_PUBLISHER_FIELDS}
fragment journalFields on Journal {
  title
  abbreviation
  url
  issn
  publisher {
    ...publisherFields
  }
  notes
}
`

export const FRAGMENT_PUBLICATION_FIELDS = gql`
fragment publicationFields on Publication {
  title
  authors
  journal {
    ...journalFields
  }
  kind(format: SHORT)
  date
  year
  abstract
  notes
  peerReviewed
  doi
  isbn
  url
  cached
  accessed
}
`
//alert("(13) graphql-queries.ts")

export const FRAGMENT_QUOTATION_FIELDS = gql`
fragment quotationFields on Quotation {
  text
  quotee
  date
  source
  url
  notes
}
`
//alert("(14) graphql-queries.ts")

export const FRAGMENT_TOPIC_FIELDS = gql`
fragment topicFields on Topic {
  id
  label
  description
  parent {
    id
    label
  }
  children {
    id
    label
  }
}
`

export const FRAGMENT_SUBTOPIC_FIELDS = gql`
fragment subtopicFields on Topic {
  ...trackedEntityFields
  ...linkableEntityFields
  label
  description
  # TODO: try experiment with recursive query:
  # children {
  #   ...subtopicFields
  # }
}
`

export const FRAGMENT_TOPIC_FIELDS_RECURSIVE = gql`
fragment subtopicFieldsRecursive on Topic {
  children {
    ...subtopicFields
    children {
      ...subtopicFields
      children {
        ...subtopicFields
        children {
          ...subtopicFields
          children {
            ...subtopicFields
            children {
              ...subtopicFields
              children {
                ...subtopicFields
                children {
                  ...subtopicFields
                  children {
                    ...subtopicFields
                    children {
                      ...subtopicFields
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
`

export const FRAGMENT_TOPIC_HIERARCHY_FIELDS = gql`
fragment topicHierarchyFields on Topic {
  ...subtopicFields
  ...subtopicFieldsRecursive
}
`
//alert("(15) graphql-queries.ts")

export const FRAGMENT_USER_FIELDS = gql`
fragment userFields on User {
  # id
  username
  firstName
  lastName
  email
  password
  authorities(aggregation:OWN, format:SHORT)
  # groups {
  #   ...trackedEntityFields
  #   ...groupFields
  # }
}
`

export const FRAGMENT_GROUP_FIELDS = gql`
fragment groupFields on Group {
  groupname
  authorities(format:SHORT)
  members {
    ...trackedEntityFields
    ...userFields
  }
}
`

//alert("(16) graphql-queries.ts")
/*
export const QUERY_CLAIM_BY_ID = gql`
query {
  claimById(id: 4){
    ...trackedEntityFields
    # ...linkableEntityFields
    ...claimFields
  }
}
`
//alert("(17) graphql-queries.ts")

export const QUERY_DECLARATION_BY_ID = gql`
query {
  declarationById(id: 3){
    ...trackedEntityFields
    # ...linkableEntityFields
    ...declarationFields
  }
}
`
//alert("(18) graphql-queries.ts")

export const QUERY_ENTITY_LINK_BY_ID = gql`
query {
  entityLinkById(id: 1) {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`
//alert("(19) graphql-queries.ts")

export const QUERY_JOURNAL_BY_ID = gql`
query {
  journalById(id: 24) {
    ...trackedEntityFields
    ...journalFields
  }
}
`
//alert("(20) graphql-queries.ts")

export const QUERY_PERSON_BY_ID = gql`
query {
  personById() {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...personFields
  }
}
`
//alert("(21) graphql-queries.ts")

export const QUERY_PUBLICATION_BY_ID = gql`
query {
  publicationById() {
    ...trackedEntityFields
    ...publicationFields
  }
}
`
//alert("(22) graphql-queries.ts")

export const QUERY_PUBLISHER_BY_ID = gql`
query {
  publisherById(id: 23757) {
    ...trackedEntityFields
    ...publisherFields
  }
}
`
//alert("(23) graphql-queries.ts")

export const QUERY_QUOTATION_BY_ID = gql`
query {
  quotationById(id: 1) {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...quotationFields
  }
}
`
//alert("(24) graphql-queries.ts")

export const QUERY_TOPIC_BY_ID = gql`
query {
  topicById(id: 1) {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...topicFields
  }
}
`
//alert("(25) graphql-queries.ts")

export const QUERY_USER_BY_ID = gql`
query {
  userById(id: 0) {
    ...trackedEntityFields
    ...userFields
  }
}
`
//alert("(26) graphql-queries.ts")
*/

export const QUERY_CLAIMS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_CLAIM_FIELDS}
query Claims($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {
  claims
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...claimFields
    }
  }
}
`
//alert("(27) graphql-queries.ts")

export const QUERY_DECLARATIONS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_DECLARATION_FIELDS}
query Declarations($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {
  declarations
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...declarationFields
    }
  }
}
`
//alert("(28) graphql-queries.ts")

export const QUERY_ENTITY_LINKS = gql`
#${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
query {
  entityLinks
  # (
  #   filter: {
  #     fromEntityKind: FFF
  #     fromEntityId: 0
  #     toEntityKind: TTT
  #     toEntityId: 0
  #     status: SSS
  #     text: "test"
  #     advancedSearch: false
  #   }
  # )
  {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`
//alert("(29) graphql-queries.ts")

export const QUERY_JOURNALS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
query Journals($filter: TrackedEntityQueryFilter, $pageSort: PageableInput) {
  journals
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...journalFields
    }
  }
}
`
//alert("(30) graphql-queries.ts")

export const QUERY_LOG = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_LOG_FIELDS}
query Log($filter: LogQueryFilter, $pageSort: PageableInput) {
  log
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...logFields
    }
  }
}
`
//alert("(31) graphql-queries.ts")

export const QUERY_PERSONS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PERSON_FIELDS}
query Persons($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {
  persons
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...personFields
    }
  }
}
`
//alert("(32) graphql-queries.ts")

export const QUERY_PUBLICATIONS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PUBLICATION_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
query Publications($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {
  publications
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...publicationFields
    }
  }
}
`
//alert("(33) graphql-queries.ts")

export const QUERY_PUBLISHERS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_PUBLISHER_FIELDS}
query Publishers($filter: TrackedEntityQueryFilter, $pageSort: PageableInput) {
  publishers
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...publisherFields
    }
  }
}
`
//alert("(34) graphql-queries.ts")

export const QUERY_QUOTATIONS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_QUOTATION_FIELDS}
query Quotations($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {
  quotations
  (
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...quotationFields
    }
  }
}
`
//alert("(35) graphql-queries.ts")

export const QUERY_TOPICS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_TOPIC_FIELDS}
query {
  topics
  # (
  #   filter: {
  #     parentId: 0
  #     recursive: false
  #     status: SSS
  #     text: "test"
  #     advancedSearch: false
  #   }
  #   pageSort: {
  #     pageNumber: 1
  #     pageSize: 3
  #     sort: {
  #       orders: {
  #         property: "date"
  #         direction: DESC
  #         # nullHandling: NULLS_LAST
  #         # ignoreCase: false
  #       }
  #     }
  #   }
  # )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...linkableEntityFields
      ...topicFields
    }
  }
}
`

export const QUERY_TOPIC_HIERARCHY = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TOPIC_HIERARCHY_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_SUBTOPIC_FIELDS}
${FRAGMENT_TOPIC_FIELDS_RECURSIVE}
query TopicHierarchy($filter: TopicQueryFilter, $pageSort: PageableInput) {
  topics(
    filter: $filter,
    pageSort: $pageSort
  ) {
    ...pageFields
    content {
      ...topicHierarchyFields
    }
  }
}
`//alert("(36) graphql-queries.ts")

export const QUERY_USERS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
query Users(
    $filter: TrackedEntityQueryFilter,
    $pageSort: PageableInput
){
  users(
    filter: $filter
    pageSort: $pageSort
  ){
    ...pageFields
    content {
      ...trackedEntityFields
      ...userFields
    }
  }
}
`

export const QUERY_GROUPS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
query Groups(
    $filter: TrackedEntityQueryFilter,
    $pageSort: PageableInput
  ) {
  groups(
    filter: $filter
    pageSort: $pageSort
  )
  {
    ...pageFields
    content {
      ...trackedEntityFields
      ...groupFields
    }
  }
}
`

/*
export const QUERY_USERS_GROUPS = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
query UsersAndGroups(
    $userFilter: TrackedEntityQueryFilter,
    $userPageSort: PageableInput,
    $groupFilter: TrackedEntityQueryFilter,
    $groupPageSort: PageableInput
  ) {
  groups(
    filter: $groupFilter
    pageSort: $groupPageSort
  )
  {
    ...pageFields
    content {
      # ...trackedEntityFields
      # ...on IBaseEntity {
        id
      # }
      entityKind
      status
      created
      createdByUser {
        username
      }
      updated
      updatedByUser {
        username
      }
      log
      {
        # ...pageFields
        content {
          timestamp
          transactionKind
          user {
            username
          }
          linkedEntityKind
          linkedEntityId
        }
      }
      ...groupFields
    }
  }
  users(
    filter: $userFilter
    pageSort: $userPageSort
  )
  {
    ...pageFields
    content {
      # ...trackedEntityFields
      # ...on IBaseEntity {
        id
      # }
      entityKind
      status
      created
      createdByUser {
        username
      }
      updated
      updatedByUser {
        username
      }
      log
      {
        # ...pageFields
        content {
          timestamp
          transactionKind
          user {
            username
          }
          linkedEntityKind
          linkedEntityId
        }
      }
      ...userFields
    }
  }
}
`
*/

/*
export const QUERY_ALL = gql`
${FRAGMENT_PAGE_FIELDS}
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_CLAIM_FIELDS}
${FRAGMENT_DECLARATION_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
${FRAGMENT_PERSON_FIELDS}
${FRAGMENT_PUBLICATION_FIELDS}
${FRAGMENT_PUBLISHER_FIELDS}
${FRAGMENT_QUOTATION_FIELDS}
${FRAGMENT_TOPIC_FIELDS}
${FRAGMENT_USER_FIELDS}
query {
  claims
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...claimFields
    }
  }

  declarations
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...declarationFields
    }
  }

  entityLinks
  {
    content {
      ...trackedEntityFields
      ...entityLinkFields
    }
  }

  journals
  {
    content {
      ...trackedEntityFields
      ...journalFields
    }
  }

  persons
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...personFields
    }
  }

  publications
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...publicationFields
    }
  }

  publishers
  {
    content {
      ...trackedEntityFields
      ...publisherFields
    }
  }

  quotations
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...quotationFields
    }
  }

  topics
  {
    content {
      ...trackedEntityFields
      # ...linkableEntityFields
      ...topicFields
    }
  }

  users
  {
    content {
      ...trackedEntityFields
      ...userFields
    }
  }
}`
*/

//alert("EXIT graphql-queries.ts")