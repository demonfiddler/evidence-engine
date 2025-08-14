import { gql } from "@apollo/client"

//alert("ENTER graphql-queries.ts")

const FRAGMENT_PAGE_FIELDS = gql`
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

const FRAGMENT_TRACKED_ENTITY_FIELDS = gql`
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
  # log
  # {
  #   # ...pageFields
  #   content {
  #     id
  #     timestamp
  #     transactionKind
  #     user {
  #       username
  #     }
  #     linkedEntityKind
  #     linkedEntityId
  #   }
  # }
}
`
//alert("(2) graphql-queries.ts")

const FRAGMENT_TRACKED_ENTITY_FIELDS_POLYMORPHIC = gql`
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

const FRAGMENT_LINKED_ENTITY_FIELDS = gql`
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

const FRAGMENT_LINKABLE_ENTITY_FIELDS = gql`
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
      ...trackedEntityFields
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
      ...trackedEntityFields
      fromEntityLocations
      toEntityLocations
    }
  }
}
`
//alert("(4) graphql-queries.ts")

const FRAGMENT_LINKABLE_ENTITY_FIELDS_POLYMORPHIC = gql`
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

const FRAGMENT_CLAIM_FIELDS = gql`
fragment claimFields on Claim {
  date
  text
  notes
}
`
//alert("(6) graphql-queries.ts")

const FRAGMENT_DECLARATION_FIELDS = gql`
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

const FRAGMENT_ENTITY_LINK_FIELDS = gql`
fragment entityLinkFields on EntityLink {
  fromEntity {
    ...trackedEntityFields
    ...linkableEntityFields
  }
  toEntity {
    ...trackedEntityFields
    ...linkableEntityFields
  }
  ...trackedEntityFields
  fromEntityLocations
  toEntityLocations
}
`
//alert("(8) graphql-queries.ts")

//alert("(9) graphql-queries.ts")

const FRAGMENT_LOG_FIELDS = gql`
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

const FRAGMENT_PERSON_FIELDS = gql`
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
  country(format: ALPHA_2)
  rating
  checked
  published
}
`
//alert("(11) graphql-queries.ts")

//alert("(12) graphql-queries.ts")

const FRAGMENT_PUBLISHER_FIELDS = gql`
fragment publisherFields on Publisher {
  name
  location
  country(format: ALPHA_2)
  url
  journalCount
}
`

const FRAGMENT_JOURNAL_FIELDS = gql`
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

const FRAGMENT_PUBLICATION_FIELDS = gql`
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

const FRAGMENT_QUOTATION_FIELDS = gql`
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

const FRAGMENT_TOPIC_FIELDS = gql`
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

const FRAGMENT_SUBTOPIC_FIELDS = gql`
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

const FRAGMENT_TOPIC_FIELDS_RECURSIVE = gql`
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

const FRAGMENT_TOPIC_HIERARCHY_FIELDS = gql`
fragment topicHierarchyFields on Topic {
  ...subtopicFields
  ...subtopicFieldsRecursive
}
`
//alert("(15) graphql-queries.ts")

const FRAGMENT_USER_FIELDS = gql`
fragment userFields on User {
  # id
  username
  firstName
  lastName
  email
  password
  country(format: ALPHA_2)
  notes
  authorities(aggregation:OWN, format:SHORT)
  # groups {
  #   ...trackedEntityFields
  #   ...groupFields
  # }
}
`

const FRAGMENT_GROUP_FIELDS = gql`
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
export const READ_CLAIM_BY_ID = gql`
query {
  claimById(id: 4){
    ...trackedEntityFields
    # ...linkableEntityFields
    ...claimFields
  }
}
`
//alert("(17) graphql-queries.ts")

export const READ_DECLARATION_BY_ID = gql`
query {
  declarationById(id: 3){
    ...trackedEntityFields
    # ...linkableEntityFields
    ...declarationFields
  }
}
`
//alert("(18) graphql-queries.ts")

export const READ_ENTITY_LINK_BY_ID = gql`
query {
  entityLinkById(id: 1) {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`
//alert("(19) graphql-queries.ts")

export const READ_JOURNAL_BY_ID = gql`
query {
  journalById(id: 24) {
    ...trackedEntityFields
    ...journalFields
  }
}
`
//alert("(20) graphql-queries.ts")

export const READ_PERSON_BY_ID = gql`
query {
  personById() {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...personFields
  }
}
`
//alert("(21) graphql-queries.ts")

export const READ_PUBLICATION_BY_ID = gql`
query {
  publicationById() {
    ...trackedEntityFields
    ...publicationFields
  }
}
`
//alert("(22) graphql-queries.ts")

export const READ_PUBLISHER_BY_ID = gql`
query {
  publisherById(id: 23757) {
    ...trackedEntityFields
    ...publisherFields
  }
}
`
//alert("(23) graphql-queries.ts")

export const READ_QUOTATION_BY_ID = gql`
query {
  quotationById(id: 1) {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...quotationFields
  }
}
`
//alert("(24) graphql-queries.ts")

export const READ_TOPIC_BY_ID = gql`
query {
  topicById(id: 1) {
    ...trackedEntityFields
    # ...linkableEntityFields
    ...topicFields
  }
}
`
//alert("(25) graphql-queries.ts")

export const READ_USER_BY_ID = gql`
query {
  userById(id: 0) {
    ...trackedEntityFields
    ...userFields
  }
}
`
//alert("(26) graphql-queries.ts")
*/

export const READ_CLAIMS = gql`
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

export const CREATE_CLAIM = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_CLAIM_FIELDS}
mutation CreateClaim($input: ClaimInput!) {
  createClaim(claim: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }
}
`

export const UPDATE_CLAIM = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_CLAIM_FIELDS}
mutation UpdateClaim($input: ClaimInput!) {
  updateClaim(claim: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }
}
`

export const DELETE_CLAIM = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_CLAIM_FIELDS}
mutation DeleteClaim($id: ID!) {
  deleteClaim(claimId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }
}
`

export const READ_DECLARATIONS = gql`
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

export const CREATE_DECLARATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_DECLARATION_FIELDS}
mutation CreateDeclaration($input: DeclarationInput!) {
  createDeclaration(declaration: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }
}
`

export const UPDATE_DECLARATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_DECLARATION_FIELDS}
mutation UpdateDeclaration($input: DeclarationInput!) {
  updateDeclaration(declaration: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }
}
`

export const DELETE_DECLARATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_DECLARATION_FIELDS}
mutation DeleteDeclaration($id: ID!) {
  deleteDeclaration(declarationId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }
}
`

export const READ_ENTITY_LINKS = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
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

export const CREATE_ENTITY_LINK = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
mutation CreateEntityLink($input: EntityLinkInput!) {
  createEntityLink(entityLink: $input)
  {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`

export const UPDATE_ENTITY_LINK = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
mutation UpdateEntityLink($input: EntityLinkInput!) {
  updateEntityLink(entityLink: $input)
  {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`

export const DELETE_ENTITY_LINK = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_ENTITY_LINK_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
mutation DeleteEntityLink($entityLinkId: ID!) {
  deleteEntityLink(entityLinkId: $entityLinkId)
  {
    ...trackedEntityFields
    ...entityLinkFields
  }
}
`

export const READ_JOURNALS = gql`
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

export const CREATE_JOURNAL = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
mutation CreateJournal($input: JournalInput!) {
  createJournal(journal: $input) {
    ...trackedEntityFields
    ...journalFields
  }
}
`

export const UPDATE_JOURNAL = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
mutation UpdateJournal($input: JournalInput!) {
  updateJournal(journal: $input) {
    ...trackedEntityFields
    ...journalFields
  }
}
`

export const DELETE_JOURNAL = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_JOURNAL_FIELDS}
mutation DeleteJournal($id: ID!) {
  deleteJournal(journalId: $id) {
    ...trackedEntityFields
    ...journalFields
  }
}
`

export const READ_LOGS = gql`
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

export const READ_PERSONS = gql`
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

export const CREATE_PERSON = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PERSON_FIELDS}
mutation CreatePerson($input: PersonInput!) {
  createPerson(person: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }
}
`

export const UPDATE_PERSON = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PERSON_FIELDS}
mutation UpdatePerson($input: PersonInput!) {
  updatePerson(person: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }
}
`

export const DELETE_PERSON = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PERSON_FIELDS}
mutation DeletePerson($id: ID!) {
  deletePerson(personId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }
}
`

export const READ_PUBLICATIONS = gql`
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

export const CREATE_PUBLICATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PUBLICATION_FIELDS}
mutation CreatePublication($input: PublicationInput!) {
  createPublication(publication: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }
}
`

export const UPDATE_PUBLICATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PUBLICATION_FIELDS}
mutation UpdatePublication($input: PublicationInput!) {
  updatePublication(publication: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }
}
`

export const DELETE_PUBLICATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_PUBLICATION_FIELDS}
mutation DeletePublication($id: ID!) {
  deletePublication(publicationId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }
}
`

export const READ_PUBLISHERS = gql`
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

export const CREATE_PUBLISHER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_PUBLISHER_FIELDS}
mutation CreatePublisher($input: PublisherInput!) {
  createPublisher(publisher: $input) {
    ...trackedEntityFields
    ...publisherFields
  }
}
`

export const UPDATE_PUBLISHER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_PUBLISHER_FIELDS}
mutation UpdatePublisher($input: PublisherInput!) {
  updatePublisher(publisher: $input) {
    ...trackedEntityFields
    ...publisherFields
  }
}
`

export const DELETE_PUBLISHER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_PUBLISHER_FIELDS}
mutation DeletePublisher($id: ID!) {
  deletePublisher(publisherId: $id) {
    ...trackedEntityFields
    ...publisherFields
  }
}
`

export const READ_QUOTATIONS = gql`
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

export const CREATE_QUOTATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_QUOTATION_FIELDS}
mutation CreateQuotation($input: QuotationInput!) {
  createQuotation(quotation: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }
}
`

export const UPDATE_QUOTATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_QUOTATION_FIELDS}
mutation UpdateQuotation($input: QuotationInput!) {
  updateQuotation(quotation: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }
}
`

export const DELETE_QUOTATION = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_QUOTATION_FIELDS}
mutation DeleteQuotation($id: ID!) {
  deleteQuotation(quotationId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }
}
`

export const READ_TOPICS = gql`
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

export const READ_TOPIC_HIERARCHY = gql`
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
`

export const CREATE_TOPIC = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_TOPIC_FIELDS}
mutation CreateTopic($input: TopicInput!) {
  createTopic(topic: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }
}
`

export const UPDATE_TOPIC = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_TOPIC_FIELDS}
mutation UpdateTopic($input: TopicInput!) {
  updateTopic(topic: $input) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }
}
`

export const DELETE_TOPIC = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_LINKABLE_ENTITY_FIELDS}
${FRAGMENT_LINKED_ENTITY_FIELDS}
${FRAGMENT_TOPIC_FIELDS}
mutation DeleteTopic($id: ID!) {
  deleteTopic(topicId: $id) {
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }
}
`

export const READ_USERS = gql`
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

export const CREATE_USER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
mutation CreateUser($input: UserInput!) {
  createUser(user: $input) {
    ...trackedEntityFields
    ...userFields
  }
}
`

export const UPDATE_USER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
mutation UpdateUser($input: UserInput!) {
  updateUser(user: $input) {
    ...trackedEntityFields
    ...userFields
  }
}
`

export const DELETE_USER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
mutation DeleteUser($id: ID!) {
  deleteUser(userId: $id) {
    ...trackedEntityFields
    ...userFields
  }
}
`

export const GRANT_USER_AUTHORITIES = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
mutation GrantUserAuthorities($userId: ID!, $authorities: [AuthorityKind!]!) {
  grantUserAuthorities(userId: $userId, authorities: $authorities) {
    ...trackedEntityFields
    ...userFields
  }
}
`

export const REVOKE_USER_AUTHORITIES = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
mutation RevokeUserAuthorities($userId: ID!, $authorities: [AuthorityKind!]!) {
  revokeUserAuthorities(userId: $userId, authorities: $authorities) {
    ...trackedEntityFields
    ...userFields
  }
}
`

export const READ_GROUPS = gql`
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

export const CREATE_GROUP = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation CreateGroup($input: GroupInput!) {
  createGroup(group: $input) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const UPDATE_GROUP = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation UpdateGroup($input: GroupInput!) {
  updateGroup(group: $input) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const DELETE_GROUP = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation DeleteGroup($id: ID!) {
  deleteGroup(groupId: $id) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const ADD_GROUP_MEMBER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation AddGroupMember($groupId: ID!, $userId: ID!) {
  addGroupMember(groupId: $groupId, userId: $userId) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const REMOVE_GROUP_MEMBER = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation RemoveGroupMember($groupId: ID!, $userId: ID!) {
  removeGroupMember(groupId: $groupId, userId: $userId) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const GRANT_GROUP_AUTHORITIES = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation GrantGroupAuthorities($groupId: ID!, $authorities: [AuthorityKind!]!) {
  grantGroupAuthorities(groupId: $groupId, authorities: $authorities) {
    ...trackedEntityFields
    ...groupFields
  }
}
`

export const REVOKE_GROUP_AUTHORITIES = gql`
${FRAGMENT_TRACKED_ENTITY_FIELDS}
${FRAGMENT_USER_FIELDS}
${FRAGMENT_GROUP_FIELDS}
mutation RevokeGroupAuthorities($groupId: ID!, $authorities: [AuthorityKind!]!) {
  revokeGroupAuthorities(groupId: $groupId, authorities: $authorities) {
    ...trackedEntityFields
    ...groupFields
  }
}
`
