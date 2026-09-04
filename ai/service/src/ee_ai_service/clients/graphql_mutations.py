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

# Control what a wildcard import fetches:
__all__ = [
    "LOGIN",
    "UPDATE_ENTITY_STATUS",
    "CREATE_CLAIM",
    "UPDATE_CLAIM",
    "DELETE_CLAIM",
    "CREATE_COMMENT",
    "UPDATE_COMMENT",
    "DELETE_COMMENT",
    "CREATE_DECLARATION",
    "UPDATE_DECLARATION",
    "DELETE_DECLARATION",
    "CREATE_ENTITY_LINK",
    "UPDATE_ENTITY_LINK",
    "DELETE_ENTITY_LINK",
    "CREATE_JOURNAL",
    "UPDATE_JOURNAL",
    "DELETE_JOURNAL",
    "CREATE_PERSON",
    "UPDATE_PERSON",
    "DELETE_PERSON",
    "CREATE_PUBLICATION",
    "UPDATE_PUBLICATION",
    "DELETE_PUBLICATION",
    "CREATE_PUBLISHER",
    "UPDATE_PUBLISHER",
    "DELETE_PUBLISHER",
    "CREATE_QUOTATION",
    "UPDATE_QUOTATION",
    "DELETE_QUOTATION",
    "CREATE_TOPIC",
    "UPDATE_TOPIC",
    "DELETE_TOPIC",
    "CREATE_USER",
    "UPDATE_USER",
    "UPDATE_USER_PASSWORD",
    "UPDATE_USER_PROFILE",
    "DELETE_USER",
    "GRANT_USER_AUTHORITIES",
    "REVOKE_USER_AUTHORITIES",
    "CREATE_GROUP",
    "UPDATE_GROUP",
    "DELETE_GROUP",
    "ADD_GROUP_MEMBER",
    "REMOVE_GROUP_MEMBER",
    "GRANT_GROUP_AUTHORITIES",
    "REVOKE_GROUP_AUTHORITIES",
]

LOGIN = gql(f"""
mutation Login($username: String!, $password: String!) {{
  login(
    username: $username
    password: $password
  ) {{
    token
    user {{
        id
        username
        firstName
        lastName
        authorities(aggregation:ALL, format:SHORT)
    }}
  }}
}}
""")

UPDATE_ENTITY_STATUS = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
{FRAGMENT_COMMENT_FIELDS}
{FRAGMENT_LABEL_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
{FRAGMENT_GROUP_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PERSON_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_TRACKED_ENTITY_FIELDS_POLYMORPHIC}
mutation UpdateEntityStatus($entityId: ID!, $status: StatusKind!) {{
  setEntityStatus(
    entityId: $entityId
    status: $status
  ) {{
    ...trackedEntityFieldsPolymorphic
  }}
}}
""")

CREATE_CLAIM = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
mutation CreateClaim($input: ClaimInput!) {{
  createClaim(claim: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }}
}}
""")

UPDATE_CLAIM = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
mutation UpdateClaim($input: ClaimInput!) {{
  updateClaim(claim: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }}
}}
""")

DELETE_CLAIM = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_CLAIM_FIELDS}
mutation DeleteClaim($id: ID!) {{
  deleteClaim(claimId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...claimFields
  }}
}}
""")

CREATE_COMMENT = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_OWNED_COMMENT_FIELDS}
mutation CreateComment($input: CommentInput!) {{
  createComment(comment: $input) {{
    ...trackedEntityFields
    ...ownedCommentFields
  }}
}}
""")

UPDATE_COMMENT = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_OWNED_COMMENT_FIELDS}
mutation UpdateComment($input: CommentInput!) {{
  updateComment(comment: $input) {{
    ...trackedEntityFields
    ...ownedCommentFields
  }}
}}
""")

DELETE_COMMENT = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_OWNED_COMMENT_FIELDS}
mutation DeleteComment($id: ID!) {{
  deleteComment(commentId: $id) {{
    ...trackedEntityFields
    ...ownedCommentFields
  }}
}}
""")

CREATE_DECLARATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
mutation CreateDeclaration($input: DeclarationInput!) {{
  createDeclaration(declaration: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }}
}}
""")

UPDATE_DECLARATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
mutation UpdateDeclaration($input: DeclarationInput!) {{
  updateDeclaration(declaration: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }}
}}
""")

DELETE_DECLARATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_DECLARATION_FIELDS}
mutation DeleteDeclaration($id: ID!) {{
  deleteDeclaration(declarationId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...declarationFields
  }}
}}
""")

CREATE_ENTITY_LINK = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_ENTITY_LINK_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
mutation CreateEntityLink($input: EntityLinkInput!) {{
  createEntityLink(entityLink: $input)
  {{
    ...trackedEntityFields
    ...entityLinkFields
  }}
}}
""")

UPDATE_ENTITY_LINK = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_ENTITY_LINK_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
mutation UpdateEntityLink($input: EntityLinkInput!) {{
  updateEntityLink(entityLink: $input)
  {{
    ...trackedEntityFields
    ...entityLinkFields
  }}
}}
""")

DELETE_ENTITY_LINK = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_ENTITY_LINK_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
mutation DeleteEntityLink($entityLinkId: ID!) {{
  deleteEntityLink(entityLinkId: $entityLinkId)
  {{
    ...trackedEntityFields
    ...entityLinkFields
  }}
}}
""")

CREATE_JOURNAL = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation CreateJournal($input: JournalInput!) {{
  createJournal(journal: $input) {{
    ...trackedEntityFields
    ...journalFields
  }}
}}
""")

UPDATE_JOURNAL = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation UpdateJournal($input: JournalInput!) {{
  updateJournal(journal: $input) {{
    ...trackedEntityFields
    ...journalFields
  }}
}}
""")

DELETE_JOURNAL = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation DeleteJournal($id: ID!) {{
  deleteJournal(journalId: $id) {{
    ...trackedEntityFields
    ...journalFields
  }}
}}
""")

CREATE_PERSON = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PERSON_FIELDS}
mutation CreatePerson($input: PersonInput!) {{
  createPerson(person: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }}
}}
""")

UPDATE_PERSON = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PERSON_FIELDS}
mutation UpdatePerson($input: PersonInput!) {{
  updatePerson(person: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }}
}}
""")

DELETE_PERSON = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PERSON_FIELDS}
mutation DeletePerson($id: ID!) {{
  deletePerson(personId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...personFields
  }}
}}
""")

CREATE_PUBLICATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation CreatePublication($input: PublicationInput!) {{
  createPublication(publication: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }}
}}
""")

UPDATE_PUBLICATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation UpdatePublication($input: PublicationInput!) {{
  updatePublication(publication: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }}
}}
""")

DELETE_PUBLICATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_PUBLICATION_FIELDS}
{FRAGMENT_JOURNAL_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation DeletePublication($id: ID!) {{
  deletePublication(publicationId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...publicationFields
  }}
}}
""")

CREATE_PUBLISHER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation CreatePublisher($input: PublisherInput!) {{
  createPublisher(publisher: $input) {{
    ...trackedEntityFields
    ...publisherFields
  }}
}}
""")

UPDATE_PUBLISHER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation UpdatePublisher($input: PublisherInput!) {{
  updatePublisher(publisher: $input) {{
    ...trackedEntityFields
    ...publisherFields
  }}
}}
""")

DELETE_PUBLISHER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_PUBLISHER_FIELDS}
mutation DeletePublisher($id: ID!) {{
  deletePublisher(publisherId: $id) {{
    ...trackedEntityFields
    ...publisherFields
  }}
}}
""")

CREATE_QUOTATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
mutation CreateQuotation($input: QuotationInput!) {{
  createQuotation(quotation: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }}
}}
""")

UPDATE_QUOTATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
mutation UpdateQuotation($input: QuotationInput!) {{
  updateQuotation(quotation: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }}
}}
""")

DELETE_QUOTATION = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_QUOTATION_FIELDS}
mutation DeleteQuotation($id: ID!) {{
  deleteQuotation(quotationId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...quotationFields
  }}
}}
""")

CREATE_TOPIC = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
mutation CreateTopic($input: TopicInput!) {{
  createTopic(topic: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }}
}}
""")

UPDATE_TOPIC = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
mutation UpdateTopic($input: TopicInput!) {{
  updateTopic(topic: $input) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }}
}}
""")

DELETE_TOPIC = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_LINKABLE_ENTITY_FIELDS}
{FRAGMENT_LINKED_ENTITY_FIELDS}
{FRAGMENT_TOPIC_FIELDS}
mutation DeleteTopic($id: ID!) {{
  deleteTopic(topicId: $id) {{
    ...trackedEntityFields
    ...linkableEntityFields
    ...topicFields
  }}
}}
""")

CREATE_USER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation CreateUser($input: UserInput!) {{
  createUser(user: $input) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

UPDATE_USER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation UpdateUser($input: UserInput!) {{
  updateUser(user: $input) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

UPDATE_USER_PASSWORD = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation UpdateUserPassword($input: UserPasswordInput!) {{
  updateUserPassword(user: $input) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

UPDATE_USER_PROFILE = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation UpdateUserProfile($input: UserProfileInput!) {{
  updateUserProfile(user: $input) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

DELETE_USER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation DeleteUser($id: ID!) {{
  deleteUser(userId: $id) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

GRANT_USER_AUTHORITIES = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation GrantUserAuthorities($userId: ID!, $authorities: [AuthorityKind!]!) {{
  grantUserAuthorities(userId: $userId, authorities: $authorities) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

REVOKE_USER_AUTHORITIES = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
mutation RevokeUserAuthorities($userId: ID!, $authorities: [AuthorityKind!]!) {{
  revokeUserAuthorities(userId: $userId, authorities: $authorities) {{
    ...trackedEntityFields
    ...userFields
  }}
}}
""")

CREATE_GROUP = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation CreateGroup($input: GroupInput!) {{
  createGroup(group: $input) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

UPDATE_GROUP = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation UpdateGroup($input: GroupInput!) {{
  updateGroup(group: $input) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

DELETE_GROUP = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation DeleteGroup($id: ID!) {{
  deleteGroup(groupId: $id) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

ADD_GROUP_MEMBER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation AddGroupMember($groupId: ID!, $userId: ID!) {{
  addGroupMember(groupId: $groupId, userId: $userId) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

REMOVE_GROUP_MEMBER = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation RemoveGroupMember($groupId: ID!, $userId: ID!) {{
  removeGroupMember(groupId: $groupId, userId: $userId) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

GRANT_GROUP_AUTHORITIES = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation GrantGroupAuthorities($groupId: ID!, $authorities: [AuthorityKind!]!) {{
  grantGroupAuthorities(groupId: $groupId, authorities: $authorities) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

REVOKE_GROUP_AUTHORITIES = gql(f"""
{FRAGMENT_TRACKED_ENTITY_FIELDS}
{FRAGMENT_USER_FIELDS}
{FRAGMENT_GROUP_FIELDS}
mutation RevokeGroupAuthorities($groupId: ID!, $authorities: [AuthorityKind!]!) {{
  revokeGroupAuthorities(groupId: $groupId, authorities: $authorities) {{
    ...trackedEntityFields
    ...groupFields
  }}
}}
""")

