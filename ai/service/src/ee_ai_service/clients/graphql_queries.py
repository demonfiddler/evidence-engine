# ----------------------------------------------------------------------------------------------------------------------
#  Evidence Engine: A system for managing evidence on arbitrary scientific topics.
#  Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
#  Copyright © 2024-26 Adrian Price. All rights reserved.
#
#  This file is part of Evidence Engine.
#
#  Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
#  GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
#  or (at your option) any later version.
#
#  Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
#  If not, see <https://www.gnu.org/licenses/>. 
# ----------------------------------------------------------------------------------------------------------------------

from gql import gql

from ee_ai_service.clients.graphql_fragments import *

CURRENT_USER = gql(f"""
query CurrentUser {{
  currentUser {{
    id
    username
    firstName
    lastName
    authorities(aggregation:ALL, format:SHORT)
  }}
}}
""")

READ_CLAIM_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
query ClaimById($id: ID) {{
  claimById(id: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }}
}}
""")

READ_CLAIMS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
query Claims($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {{
  claims
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...claimFields
    }}
  }}
}}
""")

READ_COMMENT_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_COMMENT_FIELDS}
query CommentById($id: ID) {{
  commentById(id: $id) {{
    ...trackedEntityFields
    ...commentFields
  }}
}}
""")

READ_COMMENTS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_COMMENT_FIELDS}
{FRAGMENT_LABEL_FIELDS}
query Comments($filter: CommentQueryFilter, $pageSort: PageableInput) {{
  comments
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...commentFields
    }}
  }}
}}
""")

READ_OWNED_COMMENTS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_OWNED_COMMENT_FIELDS}
query Comments($filter: CommentQueryFilter, $pageSort: PageableInput) {{
  comments
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...ownedCommentFields
    }}
  }}
}}
""")

# READ_COUNTRIES = gql(f"""
# {FRAGMENT_COUNTRY_FIELDS}
# query Countries {{
#   countries {{
#     ...countryFields
#   }}
# }}
# """)

READ_DECLARATION_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
query DeclarationById($id: ID) {{
  declarationById(id: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }}
}}
""")

READ_DECLARATIONS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
query Declarations($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {{
  declarations
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...declarationFields
    }}
  }}
}}
""")

READ_ENTITY_LINK_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_ENTITY_LINK_FIELDS}
query EntityLinkById($id: ID) {{
  entityLinkById(id: $id) {{
    ...trackedEntityFields
    ...entityLinkFields
  }}
}}
""")

READ_ENTITY_LINKS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_ENTITY_LINK_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
query EntityLinks($filter: EntityLinkQueryFilter, $pageSort: PageableInput) {{
  entityLinks
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...entityLinkFields
    }}
  }}
}}
""")

READ_GROUP_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_GROUP_FIELDS}
query GroupById($id: ID) {{
  groupById(id: $id) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

READ_GROUPS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
query Groups(
    $filter: TrackedEntityQueryFilter,
    $pageSort: PageableInput
  ) {{
  groups(
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...groupFields
    }}
  }}
}}
""")

READ_JOURNAL_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
query JournalById($id: ID) {{
  journalById(id: $id) {{
    ...trackedEntityFields
    ...journalFields
  }}
}}
""")

READ_JOURNALS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
query Journals($filter: TrackedEntityQueryFilter, $pageSort: PageableInput) {{
  journals
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...journalFields
    }}
  }}
}}
""")

READ_LOGS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_LOG_FIELDS}
query Log($filter: LogQueryFilter, $pageSort: PageableInput) {{
  log
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...logFields
    }}
  }}
}}
""")

READ_PERSON_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_PERSON_FIELDS}
query PersonById($id: ID) {{
  personById(id: $id) {{
    ...trackedEntityFields
    # ...linkableEntityFields
    ...personFields
  }}
}}
""")

READ_PERSONS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PERSON_FIELDS}
query Persons($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {{
  persons
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...personFields
    }}
  }}
}}
""")

READ_PUBLICATION_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
query PublicationById($id: ID) {{
  publicationById(id: $id) {{
    ...trackedEntityFields
    ...publicationFields
  }}
}}
""")

READ_PUBLICATIONS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
query Publications($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {{
  publications
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...publicationFields
    }}
  }}
}}
""")

READ_PUBLISHER_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
query PublisherById($id: ID) {{
  publisherById(id: $id) {{
    ...trackedEntityFields
    ...publisherFields
  }}
}}
""")

READ_PUBLISHERS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
query Publishers($filter: TrackedEntityQueryFilter, $pageSort: PageableInput) {{
  publishers
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...publisherFields
    }}
  }}
}}
""")

READ_QUOTATION_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
query QuotationById($id: ID) {{
  quotationById(id: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }}
}}
""")

READ_QUOTATIONS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
query Quotations($filter: LinkableEntityQueryFilter, $pageSort: PageableInput) {{
  quotations
  (
    filter: $filter
    pageSort: $pageSort
  )
  {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...quotationFields
    }}
  }}
}}
""")

READ_TOPIC_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
query TopicById($id: ID) {{
  topicById(id: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }}
}}
""")

READ_TOPICS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
query Topics($filter: TopicQueryFilter, $pageSort: PageableInput) {{
  topics(
    filter: $filter
    pageSort: $pageSort
  ) {{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...linkableEntityFields
      ...topicFields
    }}
  }}
}}
""")

READ_TOPIC_HIERARCHY = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TOPIC_HIERARCHY_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_SUBTOPIC_FIELDS}
{FRAGMENT_TOPIC_FIELDS_RECURSIVE}
query TopicHierarchy($filter: TopicQueryFilter, $pageSort: PageableInput) {{
  topics(
    filter: $filter,
    pageSort: $pageSort
  ) {{
    ...pageFields
    content {{
      ...topicHierarchyFields
    }}
  }}
}}
""")

READ_USER_BY_ID = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
query UserById($id: ID) {{
  userById(id: $id) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

READ_USERS = gql(f"""
{FRAGMENT_PAGE_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
query Users(
    $filter: TrackedEntityQueryFilter,
    $pageSort: PageableInput
){{
  users(
    filter: $filter
    pageSort: $pageSort
  ){{
    ...pageFields
    content {{
      ...trackedEntityFields
      ...userFields
    }}
  }}
}}
""")

READ_ENTITY_STATISTICS = gql(f"""
{FRAGMENT_ENTITY_STATS_FIELDS}
query ReadEntityStatistics($filter: StatisticsQueryFilter) {{
  entityStatistics(filter: $filter) {{
    ...entityStatsFields
  }}
}}
""")

READ_TOPIC_STATISTICS = gql(f"""
{FRAGMENT_ENTITY_STATS_FIELDS}
{FRAGMENT_TOPIC_STATS_FIELDS}
query ReadTopicStatistics($filter: StatisticsQueryFilter) {{
  topicStatistics(filter: $filter) {{
    ...topicStatsFields
  }}
}}
""")

READ_TOPIC_STATISTICS_HIERARCHY = gql(f"""
{FRAGMENT_ENTITY_STATS_FIELDS}
{FRAGMENT_TOPIC_STATS_FIELDS}
{FRAGMENT_TOPIC_STATS_FIELDS_RECURSIVE}
{FRAGMENT_TOPIC_STATS_HIERARCHY_FIELDS}
query TopicHierarchy($filter: StatisticsQueryFilter) {{
  topicStatistics(filter: $filter) {{
    ...topicStatsHierarchyFields
  }}
}}
""")

READ_ALL_STATISTICS = gql(f"""
{FRAGMENT_ENTITY_STATS_FIELDS}
{FRAGMENT_TOPIC_STATS_FIELDS}
{FRAGMENT_TOPIC_STATS_FIELDS_RECURSIVE}
{FRAGMENT_TOPIC_STATS_HIERARCHY_FIELDS}
query AllStatistics($filter: StatisticsQueryFilter) {{
  topicStatistics(filter: $filter) {{
    ...topicStatsHierarchyFields
  }}
  entityStatistics(filter: $filter) {{
    ...entityStatsFields
  }}
}}
""")

READ_ENTITY_AUDIT = gql(f"""
query EntityAudit($id: ID!) {{
  audit(id: $id) {{
    entity {{
      ...on IBaseEntity {{
        id
      }}
      entityKind(format: SHORT)
      status(format: LONG)
    }}
    fieldAudit {{
      fields {{
        fieldName
        message
        severity
        pass
      }}
      groups {{
        fields {{
          fieldName
          message
          severity
          pass
        }}
        message
        severity
        pass
      }}
      pass
    }}
    linkAudit {{
      links {{
        linkedEntityKind(format: LONG)
        min
        actual
        pass
      }}
      groups {{
        links {{
          linkedEntityKind(format: LONG)
          min
          actual
          pass
        }}
        pass
      }}
      pass
    }}
    pass
  }}
}}
""")
