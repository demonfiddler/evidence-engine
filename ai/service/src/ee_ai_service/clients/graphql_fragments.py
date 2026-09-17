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

# Control what a wildcard import fetches:
__all__ = [
    "FRAGMENT_PAGE_FIELDS",
    "FRAGMENT_TRACKED_ENTITY_FIELDS",
    "FRAGMENT_LINKED_ENTITY_FIELDS",
    "FRAGMENT_LABEL_FIELDS",
    "FRAGMENT_LINKABLE_ENTITY_FIELDS",
    # "FRAGMENT_LINKABLE_ENTITY_FIELDS_POLYMORPHIC",
    "FRAGMENT_TRACKED_ENTITY_FIELDS_POLYMORPHIC",
    "FRAGMENT_CLAIM_FIELDS",
    "FRAGMENT_COMMENT_FIELDS",
    "FRAGMENT_OWNED_COMMENT_FIELDS",
    # "FRAGMENT_COUNTRY_FIELDS",
    "FRAGMENT_DECLARATION_FIELDS",
    "FRAGMENT_ENTITY_LINK_FIELDS",
    "FRAGMENT_LOG_FIELDS",
    "FRAGMENT_PERSON_FIELDS",
    "FRAGMENT_PUBLISHER_FIELDS",
    "FRAGMENT_JOURNAL_FIELDS",
    "FRAGMENT_PUBLICATION_FIELDS",
    "FRAGMENT_QUOTATION_FIELDS",
    "FRAGMENT_TOPIC_FIELDS",
    "FRAGMENT_SUBTOPIC_FIELDS",
    "FRAGMENT_TOPIC_FIELDS_RECURSIVE",
    "FRAGMENT_TOPIC_HIERARCHY_FIELDS",
    "FRAGMENT_USER_FIELDS",
    "FRAGMENT_GROUP_FIELDS",
    "FRAGMENT_ENTITY_STATS_FIELDS",
    "FRAGMENT_TOPIC_STATS_FIELDS",
    "FRAGMENT_TOPIC_STATS_FIELDS_RECURSIVE",
    "FRAGMENT_TOPIC_STATS_HIERARCHY_FIELDS",
]

FRAGMENT_PAGE_FIELDS = """
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
"""

FRAGMENT_TRACKED_ENTITY_FIELDS = """
fragment trackedEntityFields on ITrackedEntity {
  ...on IBaseEntity {
    id
  }
  entityKind(format: SHORT)
  status(format: SHORT)
  rating
  created
  createdByUser {
    firstName
    lastName
    username
  }
  updated
  updatedByUser {
    firstName
    lastName
    username
  }
  comments(pageSort: {requestCount: true}) {
    totalElements
  }
  # log {
  #   ...pageFields
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
  # comments {
  #   ...pageFields
  #   content {
  #     ...trackedEntityFields
  #     text
  #   }
  # }
}
"""

FRAGMENT_LINKED_ENTITY_FIELDS = """
fragment linkedEntityFields on ILinkableEntity {
  ...on IBaseEntity {
    id
  }
  ...on ITrackedEntity {
    entityKind(format: SHORT)
    status(format: SHORT)
  }
  ...on Claim {
    text
    notes
  }
  ...on Declaration {
    title
    notes
  }
  ...on Person {
    title
    firstName
    prefix
    lastName
    suffix
    notes
    qualifications
    country(format: ALPHA_2)
  }
  ...on Publication {
    title
    notes
    authors
  }
  ...on Quotation {
    text
    notes
  }
  ...on Topic {
    label
    description
  }
}
"""

FRAGMENT_LABEL_FIELDS = """
fragment labelFields on ITrackedEntity {
  ...on IBaseEntity {
    id
  }
  ...on ITrackedEntity {
    entityKind(format: SHORT)
  }
  ...on Claim {
    text
  }
  ...on Comment {
    text
  }
  ...on Declaration {
    title
  }
  ...on Group {
    groupname
  }
  ...on Journal {
    title
    abbreviation
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
  ...on Publisher {
    name
  }
  ...on Quotation {
    text
  }
  ...on Topic {
    label
  }
  ...on User {
    firstName
    lastName
    username
  }
}
"""

FRAGMENT_LINKABLE_ENTITY_FIELDS = """
fragment linkableEntityFields on ILinkableEntity {
  fromEntityLinks
  {
    content {
      ...trackedEntityFields
      toEntity {
        ...linkedEntityFields
      }
      fromEntityLocations
      toEntityLocations
    }
  }
  toEntityLinks
  {
    content {
      ...trackedEntityFields
      fromEntity {
        ...linkedEntityFields
      }
      fromEntityLocations
      toEntityLocations
    }
  }
}
"""

# FRAGMENT_LINKABLE_ENTITY_FIELDS_POLYMORPHIC = """
# fragment linkableEntityFieldsPolymorphic on ILinkableEntity {
#   ...trackedEntityFields
#   ...linkableEntityFields
#   ...on Claim {
#     ...claimFields
#   }
#   ...on Declaration {
#     ...declarationFields
#   }
#   ...on Person {
#     ...personFields
#   }
#   ...on Publication {
#     ...publicationFields
#   }
#   ...on Quotation {
#     ...quotationFields
#   }
#   ...on Topic {
#     ...topicFields
#   }
# }
# """

FRAGMENT_TRACKED_ENTITY_FIELDS_POLYMORPHIC = """
fragment trackedEntityFieldsPolymorphic on ITrackedEntity {
  ...trackedEntityFields
  ...on ILinkableEntity {
    ...linkableEntityFields
  }
  ...on Claim {
    ...claimFields
  }
  ...on Comment {
    ...commentFields
  }
  ...on Declaration {
    ...declarationFields
  }
  ...on Group {
    ...groupFields
  }
  ...on Journal {
    ...journalFields
  }
  ...on Person {
    ...personFields
  }
  ...on Publication {
    ...publicationFields
  }
  ...on Publisher {
    ...publisherFields
  }
  ...on Quotation {
    ...quotationFields
  }
  ...on Topic {
    ...topicFields
  }
  ...on User {
    ...userFields
  }
}
"""

FRAGMENT_CLAIM_FIELDS = """
fragment claimFields on Claim {
  date
  text
  notes
}
"""

FRAGMENT_COMMENT_FIELDS = """
fragment commentFields on Comment {
  target {
    ...labelFields
  }
  parent {
    id
    createdByUser {
      firstName
      lastName
      username
    }
    text
  }
  text
}
"""

FRAGMENT_OWNED_COMMENT_FIELDS = """
fragment ownedCommentFields on Comment {
  parent {
    id
    createdByUser {
      firstName
      lastName
      username
    }
    text
  }
  text
}
"""

# FRAGMENT_COUNTRY_FIELDS = """
# fragment countryFields on Country {
#   alpha_2
#   alpha_3
#   numeric
#   iso_name
#   common_name
#   year
#   cc_tld
#   notes
# }
# """

FRAGMENT_DECLARATION_FIELDS = """
fragment declarationFields on Declaration {
  kind(format: SHORT)
  kindLabel : kind(format: LONG)
  title
  date
  country(format: ALPHA_2)
  countryLabel: country(format: COMMON_NAME)
  url
  cached
  signatories
  signatoryCount
  notes
}
"""

FRAGMENT_ENTITY_LINK_FIELDS = """
fragment entityLinkFields on EntityLink {
  ...trackedEntityFields
  fromEntity {
    ...trackedEntityFields
    ...linkableEntityFields
  }
  fromEntityLocations
  toEntity {
    ...trackedEntityFields
    ...linkableEntityFields
  }
  toEntityLocations
}
"""

FRAGMENT_LOG_FIELDS = """
fragment logFields on Log {
  id
  timestamp
  user {
    username
  }
  transactionKind(format: SHORT)
  entityKind(format: SHORT)
  entityId
  linkedEntityKind(format: SHORT)
  linkedEntityId
}
"""

FRAGMENT_PERSON_FIELDS = """
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
  countryLabel: country(format: COMMON_NAME)
  checked
  published
}
"""

FRAGMENT_PUBLISHER_FIELDS = """
fragment publisherFields on Publisher {
  name
  location
  country(format: ALPHA_2)
  countryLabel: country(format: COMMON_NAME)
  url
  journalCount
  notes
}
"""

FRAGMENT_JOURNAL_FIELDS = """
fragment journalFields on Journal {
  title
  abbreviation
  url
  issn
  publisher {
    id
    ...publisherFields
  }
  notes
  peerReviewed
}
"""

FRAGMENT_PUBLICATION_FIELDS = """
fragment publicationFields on Publication {
  title
  authors
  journal {
    id
    ...journalFields
  }
  publisher {
    id
    ...publisherFields
  }
  kind(format: SHORT)
  kindLabel : kind(format: LONG)
  date
  year
  keywords
  abstract
  notes
  peerReviewed
  doi
  isbn
  pmcid
  pmid
  hsid
  arxivid
  biorxivid
  medrxivid
  ericid
  ihepid
  oaipmhid
  halid
  zenodoid
  scopuseid
  wsan
  pinfoan
  url
  cached
  accessed
}
"""

FRAGMENT_QUOTATION_FIELDS = """
fragment quotationFields on Quotation {
  text
  quotee
  date
  source
  url
  notes
}
"""

FRAGMENT_TOPIC_FIELDS = """
fragment topicFields on Topic {
  id
  label
  description
  parent {
    id
    label
  }
  # children {
  #   id
  #   label
  # }
}
"""

FRAGMENT_SUBTOPIC_FIELDS = """
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
"""

FRAGMENT_TOPIC_FIELDS_RECURSIVE = """
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
"""

FRAGMENT_TOPIC_HIERARCHY_FIELDS = """
fragment topicHierarchyFields on Topic {
  ...subtopicFields
  ...subtopicFieldsRecursive
}
"""

FRAGMENT_USER_FIELDS = """
fragment userFields on User {
  username
  firstName
  lastName
  email
  password
  country(format: ALPHA_2)
  countryLabel: country(format: COMMON_NAME)
  notes
  authorities(aggregation:OWN, format:SHORT)
  # groups {
  #   ...trackedEntityFields
  #   ...groupFields
  # }
}
"""

FRAGMENT_GROUP_FIELDS = """
fragment groupFields on Group {
  groupname
  authorities(format:SHORT)
  members {
    ...trackedEntityFields
    ...userFields
  }
}
"""

FRAGMENT_ENTITY_STATS_FIELDS = """
fragment entityStatsFields on EntityStatistics {
  entityKind(format: SHORT)
  count
}
"""

FRAGMENT_TOPIC_STATS_FIELDS = """
fragment topicStatsFields on TopicStatistics {
  topic {
    id
    status(format: SHORT)
    label
    description
  }
  entityStatistics {
    ...entityStatsFields
  }
}
"""

FRAGMENT_TOPIC_STATS_FIELDS_RECURSIVE = """
fragment topicStatsFieldsRecursive on TopicStatistics {
  children {
    ...topicStatsFields
    children {
      ...topicStatsFields
      children {
        ...topicStatsFields
        children {
          ...topicStatsFields
          children {
            ...topicStatsFields
            children {
              ...topicStatsFields
              children {
                ...topicStatsFields
                children {
                  ...topicStatsFields
                  children {
                    ...topicStatsFields
                    children {
                      ...topicStatsFields
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
"""

FRAGMENT_TOPIC_STATS_HIERARCHY_FIELDS = """
fragment topicStatsHierarchyFields on TopicStatistics {
  ...topicStatsFields
  ...topicStatsFieldsRecursive
}
"""

