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

from copy import copy
from gql import Client, GraphQLRequest
from gql.transport.httpx import HTTPXAsyncTransport
from pydantic import BaseModel, TypeAdapter

from ee_ai_service.clients.graphql_queries import *
from ee_ai_service.clients.graphql_mutations import *
from ee_ai_service.models.audit import EntityAudit
from ee_ai_service.models.auth_payload import AuthPayload
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.comment import Comment
from ee_ai_service.models.declaration import Declaration
from ee_ai_service.models.enums.authority_kind import AuthorityKind
from ee_ai_service.models.enums.status_kind import StatusKind
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.group import Group
from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.models.inputs.comment_input import CommentInput
from ee_ai_service.models.inputs.comment_query_filter import CommentQueryFilter
from ee_ai_service.models.inputs.declaration_input import DeclarationInput
from ee_ai_service.models.inputs.entity_link_input import EntityLinkInput
from ee_ai_service.models.inputs.entity_link_query_filter import EntityLinkQueryFilter
from ee_ai_service.models.inputs.group_input import GroupInput
from ee_ai_service.models.inputs.journal_input import JournalInput
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.inputs.log_query_filter import LogQueryFilter
from ee_ai_service.models.inputs.pageable_input import PageableInput
from ee_ai_service.models.inputs.person_input import PersonInput
from ee_ai_service.models.inputs.publication_input import PublicationInput
from ee_ai_service.models.inputs.publisher_input import PublisherInput
from ee_ai_service.models.inputs.quotation_input import QuotationInput
from ee_ai_service.models.inputs.statistics_query_filter import StatisticsQueryFilter
from ee_ai_service.models.inputs.topic_input import TopicInput
from ee_ai_service.models.inputs.topic_query_filter import TopicQueryFilter
from ee_ai_service.models.inputs.tracked_entity_query_filter import TrackedEntityQueryFilter
from ee_ai_service.models.inputs.user_input import UserInput
from ee_ai_service.models.inputs.user_password_input import UserPasswordInput
from ee_ai_service.models.inputs.user_profile_input import UserProfileInput
from ee_ai_service.models.journal import Journal
from ee_ai_service.models.log import Log
from ee_ai_service.models.page import Page
from ee_ai_service.models.person import Person
from ee_ai_service.models.publication import Publication
from ee_ai_service.models.publisher import Publisher
from ee_ai_service.models.quotation import Quotation
from ee_ai_service.models.statistics import EntityStatistics, TopicStatistics
from ee_ai_service.models.topic import Topic
from ee_ai_service.models.tracked_entity import TrackedEntity
from ee_ai_service.models.user import User
from ee_ai_service.runtime.config import Config

class EEGraphQLClient:
    """
    Strongly typed GraphQL client for the Evidence Engine API.
    Uses gql for transport + query execution.
    """

    def __init__(self, config: Config):
        """
        Constructs a new instance.
        Args:
            config: The application-wide configuration to use.
        """
        self.config = config
        self.auth = None
        self.create_client()

    def create_client(self) -> None:
        headers = {} if self.auth is None or not "token" in self.auth else {"Authorization": f"Bearer {self.auth.token}"}
        transport = HTTPXAsyncTransport(
            url = self.config.ee_graphql_url,
            timeout = 10.0,
            verify = True,
            headers = headers
        )

        self.client = Client(
            transport = transport,
            fetch_schema_from_transport = False,
        )

    def bind_query_args(self, request: GraphQLRequest, **variables: dict[str, any]) -> GraphQLRequest:
        """
        Shallow clones a GraphQLRequest with new variable values. Empty variables are not bound, and Pydantic models
        are converted to JSON-compatible dictionaries before binding to the request.
        Args:
            request: The GraphQLRequest to clone.
            variables: The variables and values to bind to the new request.
        Returns:
            A shallow clone of request, with variable_values set to kwargs.
        """
        # Delete empty variables and convert Pydantic models to JSON.
        for key, value in variables.items():
            if value is None:
                del variables[key]
            elif isinstance(value, BaseModel):
                variables[key] = value.model_dump()
        request = copy(request)
        request.variable_values = variables
        return request

    # --------------------
    # CLAIMS
    # --------------------

    async def claimById(self, id: ID) -> Claim:
        """
        Fetches a Claim given its identifier.
        Args:
            id: The ID of the Claim to fetch.
        Returns:
            The requested Claim.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_CLAIM_BY_ID, id = id))
        return Claim.model_validate(result["claimById"])

    async def claims(self, filter: LinkableEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Claim]:
        """
        Fetches a paged list of Claims.
        Args:
            filter: Selects which Claims to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Claims.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_CLAIMS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["claims"])

    async def createClaim(self, input: ClaimInput) -> Claim:
        """
        Creates a new Claim.
        Args:
            input: Values with which to initialise the new Claim.
        Returns:
            The new Claim.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_CLAIM, input = input))
        return Claim.model_validate(result["createClaim"])

    async def updateClaim(self, input: ClaimInput) -> Claim:
        """
        Updates an existing Claim.
        Args:
            input: Values with which to update the existing Claim.
        Returns:
            The updated Claim.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_CLAIM, input = input))
        return Claim.model_validate(result["updateClaim"])

    async def deleteClaim(self, id: ID) -> Claim:
        """
        Deletes an existing Claim.
        Args:
            id: The ID of the Claim to delete.
        Returns:
            The deleted Claim.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_CLAIM, id = id))
        return Claim.model_validate(result["deleteClaim"])

    # --------------------
    # COMMENTS
    # --------------------

    async def commentById(self, id: ID) -> Comment:
        """
        Fetches a Comment given its identifier.
        Args:
            id: The ID of the Comment to fetch.
        Returns:
            The requested Comment.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_COMMENT_BY_ID, id = id))
        return Comment.model_validate(result["commentById"])

    async def comments(self, filter: CommentQueryFilter = None, pageSort: PageableInput = None) -> Page[Comment]:
        """
        Fetches a paged list of Comments.
        Args:
            filter: Selects which Comments to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Comments.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_COMMENTS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["comments"])

    async def createComment(self, input: CommentInput) -> Comment:
        """
        Creates a new Comment.
        Args:
            input: Values with which to initialise the new Comment.
        Returns:
            The new Comment.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_COMMENT, input = input))
        return Comment.model_validate(result["createComment"])

    async def updateComment(self, input: CommentInput) -> Comment:
        """
        Updates an existing Comment.
        Args:
            input: Values with which to update the existing Comment.
        Returns:
            The updated Comment.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_COMMENT, input = input))
        return Comment.model_validate(result["updateComment"])

    async def deleteComment(self, id: ID) -> Comment:
        """
        Deletes an existing Comment.
        Args:
            id: The ID of the Comment to delete.
        Returns:
            The deleted Comment.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_COMMENT, id = id))
        return Comment.model_validate(result["deleteComment"])

    # --------------------
    # COUNTRIES
    # --------------------

    # "Fetches a paged list of countries"
    # countries(
    #     "Filters results."
    #     filter: CountryQueryFilter,
    #     "Sorts and/or paginates results."
    #     pageSort: PageableInput
    # ): CountryPage!

    # --------------------
    # DECLARATIONS
    # --------------------

    async def declarationById(self, id: ID) -> Declaration:
        """
        Fetches a Declaration given its identifier.
        Args:
            id: The ID of the Declaration to fetch.
        Returns:
            The requested Declaration.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_DECLARATION_BY_ID, id = id))
        return Declaration.model_validate(result["declarationById"])

    async def declarations(self, filter: LinkableEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Declaration]:
        """
        Fetches a paged list of Declarations.
        Args:
            filter: Selects which Declarations to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Declarations.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_DECLARATIONS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["declarations"])


    async def createDeclaration(self, input: DeclarationInput) -> Declaration:
        """
        Creates a new Declaration.
        Args:
            input: Values with which to initialise the new Declaration.
        Returns:
            The new Declaration.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_DECLARATION, input = input))
        return Declaration.model_validate(result["createDeclaration"])

    async def updateDeclaration(self, input: DeclarationInput) -> Declaration:
        """
        Updates an existing Declaration.
        Args:
            input: Values with which to update the existing Declaration.
        Returns:
            The updated Declaration.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_DECLARATION, input = input))
        return Declaration.model_validate(result["updateDeclaration"])

    async def deleteDeclaration(self, id: ID) -> Declaration:
        """
        Deletes an existing Declaration.
        Args:
            id: The ID of the Declaration to delete.
        Returns:
            The deleted Declaration.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_DECLARATION, id = id))
        return Declaration.model_validate(result["deleteDeclaration"])

    # --------------------
    # ENTITY LINKS
    # --------------------

    # "Returns an entity link given its identifier."
    # entityLinkById(id: ID!): EntityLink

    # "Returns an entity link given its from- and to-entity identifiers."
    # entityLinkByEntityIds(fromEntityId: ID!, toEntityId: ID!): EntityLink

    async def entity_links(self, filter: EntityLinkQueryFilter = None, pageSort: PageableInput = None) -> Page[EntityLink]:
        """
        Fetches a paged list of EntityLinks.
        Args:
            filter: Selects which EntityLinks to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of EntityLinks.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_ENTITY_LINKS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["entityLinks"])

    async def createEntityLink(self, input: EntityLinkInput) -> EntityLink:
        """
        Creates a new EntityLink.
        Args:
            input: Values with which to initialise the new EntityLink.
        Returns:
            The new EntityLink.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_ENTITY_LINK, input = input))
        return EntityLink.model_validate(result["createEntityLink"])

    async def updateEntityLink(self, input: EntityLinkInput) -> EntityLink:
        """
        Updates an existing EntityLink.
        Args:
            input: Values with which to update the existing EntityLink.
        Returns:
            The updated EntityLink.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_ENTITY_LINK, input = input))
        return EntityLink.model_validate(result["updateEntityLink"])

    async def deleteEntityLink(self, entityLinkId: ID, hard: bool = False) -> EntityLink:
        """
        Deletes an existing EntityLink.
        Args:
            id: The ID of the EntityLink to delete.
        Returns:
            The deleted EntityLink.
        """
        result = await self.client.execute_async(DELETE_ENTITY_LINK, entityLinkId = entityLinkId, hard = hard)
        return EntityLink.model_validate(result["deleteEntityLink"])

    # --------------------
    # GROUPS
    # --------------------

    async def groupById(self, id: ID) -> Group:
        """
        Fetches a Group given its identifier. Requires ADM authority.
        Args:
            id: The ID of the Group to fetch.
        Returns:
            The requested Group.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_GROUP_BY_ID, id = id))
        return Group.model_validate(result["groupById"])

    # "Fetches a Group given its groupname."
    # groupByGroupname(groupname: String!): Group @auth(authority: [ADM])

    async def groups(self, filter: TrackedEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Group]:
        """
        Fetches a paged list of Groups.
        Args:
            filter: Selects which Groups to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Groups.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_GROUPS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["groups"])

    async def createGroup(self, input: GroupInput) -> Group:
        """
        Creates a new Group.
        Args:
            input: Values with which to initialise the new Group.
        Returns:
            The new Group.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_GROUP, input = input))
        return Group.model_validate(result["createGroup"])

    async def updateGroup(self, input: GroupInput) -> Group:
        """
        Updates an existing Group.
        Args:
            input: Values with which to update the existing Group.
        Returns:
            The updated Group.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_GROUP, input = input))
        return Group.model_validate(result["updateGroup"])

    async def deleteGroup(self, id: ID) -> Group:
        """
        Deletes an existing Group.
        Args:
            id: The ID of the Group to delete.
        Returns:
            The deleted Group.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_GROUP, id = id))
        return Group.model_validate(result["deleteGroup"])

    async def addGroupMember(self, groupId: ID, userId: ID) -> Group:
        """
        Adds a User to a Group.
        Args:
            groupId: The ID of the Group.
            userId: The ID of the User to add.
        Returns:
            The updated Group.
        """
        result = await self.client.execute_async(self.bind_query_args(ADD_GROUP_MEMBER, groupId = groupId, userId = userId))
        return Group.model_validate(result["addGroupMember"])

    async def removeGroupMember(self, groupId: ID, userId: ID) -> Group:
        """
        Removes a User from a Group.
        Args:
            groupId: The ID of the Group.
            userId: The ID of the User to remove.
        Returns:
            The updated Group.
        """
        result = await self.client.execute_async(self.bind_query_args(REMOVE_GROUP_MEMBER, groupId = groupId, userId = userId))
        return Group.model_validate(result["removeGroupMember"])

    async def grantGroupAuthorities(self, groupId: ID, authorities: list[AuthorityKind]) -> Group:
        """
        Grants Authorities to a group. The specified Authorities are added to any existing ones.
        Args:
            groupId: The ID of the Group to update.
            authorities: The Authorities to grant.
        Returns:
            The updated Group.
        """
        result = await self.client.execute_async(self.bind_query_args(GRANT_GROUP_AUTHORITIES, groupId = groupId, authorities = authorities))
        return Group.model_validate(result["grantGroupAuthorities"])

    async def revokeGroupAuthorities(self, groupId: ID, authorities: list[AuthorityKind]) -> Group:
        """
        Revokes Authorities from a Group. The specified Authorities are removed from the group; other Authorities remain intact.
        Args:
            groupId: The ID of the Group to update.
            authorities: The Authorities to grant.
        Returns:
            The updated Group.
        """
        result = await self.client.execute_async(self.bind_query_args(REVOKE_GROUP_AUTHORITIES, groupId = groupId, authorities = authorities))
        return Group.model_validate(result["revokeGroupAuthorities"])

    # --------------------
    # JOURNALS
    # --------------------

    async def journalById(self, id: ID) -> Journal:
        """
        Fetches a Journal given its identifier.
        Args:
            id: The ID of the Journal to fetch.
        Returns:
            The requested Journal.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_JOURNAL_BY_ID, id = id))
        return Journal.model_validate(result["journalById"])

    async def journals(self, filter: TrackedEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Journal]:
        """
        Fetches a paged list of Journals.
        Args:
            filter: Selects which Journals to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Journals.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_JOURNALS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["journals"])

    async def createJournal(self, input: JournalInput) -> Journal:
        """
        Creates a new Journal.
        Args:
            input: Values with which to initialise the new Journal.
        Returns:
            The new Journal.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_JOURNAL, input = input))
        return Journal.model_validate(result["createJournal"])

    async def updateJournal(self, input: JournalInput) -> Journal:
        """
        Updates an existing Journal.
        Args:
            input: Values with which to update the existing Journal.
        Returns:
            The updated Journal.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_JOURNAL, input = input))
        return Journal.model_validate(result["updateJournal"])

    async def deleteJournal(self, id: ID) -> Journal:
        """
        Deletes an existing Journal.
        Args:
            id: The ID of the Journal to delete.
        Returns:
            The deleted Journal.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_JOURNAL, id = id))
        return Journal.model_validate(result["deleteJournal"])

    # --------------------
    # LOGS
    # --------------------

    async def log(self, filter: LogQueryFilter = None, pageSort: PageableInput = None) -> Page[Log]:
        """
        Fetches a paged list of Log entries.
        Args:
            filter: Selects which Logs to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Logs.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_LOGS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["logs"])

    # --------------------
    # PERSONS
    # --------------------

    async def personById(self, id: ID) -> Person:
        """
        Fetches a Person given its identifier.
        Args:
            id: The ID of the Person to fetch.
        Returns:
            The requested Person.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PERSON_BY_ID, id = id))
        return Person.model_validate(result["personById"])

    async def persons(self, filter: LinkableEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Person]:
        """
        Fetches a paged list of Persons.
        Args:
            filter: Selects which Persons to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Persons.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PERSONS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["persons"])

    async def createPerson(self, input: PersonInput) -> Person:
        """
        Creates a new Person.
        Args:
            input: Values with which to initialise the new Person.
        Returns:
            The new Person.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_PERSON, input = input))
        return Person.model_validate(result["createPerson"])

    async def updatePerson(self, input: PersonInput) -> Person:
        """
        Updates an existing Person.
        Args:
            input: Values with which to update the existing Person.
        Returns:
            The updated Person.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_PERSON, input = input))
        return EntityLink.model_validate(result["updateEntityLink"])

    async def deletePerson(self, id: ID) -> Person:
        """
        Deletes an existing Person.
        Args:
            id: The ID of the Person to delete.
        Returns:
            The deleted Person.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_PERSON, id = id))
        return Person.model_validate(result["deletePerson"])

    # --------------------
    # PUBLICATIONS
    # --------------------

    async def publicationById(self, id: ID) -> Publication:
        """
        Fetches a Publication given its identifier.
        Args:
            id: The ID of the Publication to fetch.
        Returns:
            The requested Publication.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PUBLICATION_BY_ID, id = id))
        return Publication.model_validate(result["publicationById"])

    async def publications(self, filter: LinkableEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Publication]:
        """
        Fetches a paged list of Publications.
        Args:
            filter: Selects which Publications to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Publications.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PUBLICATIONS, filter = filter, pageSort = pageSort))
        return Page[Publication].model_validate(result["publications"])

    async def createPublication(self, input: PublicationInput) -> Publication:
        """
        Creates a new Publication.
        Args:
            input: Values with which to initialise the new Publication.
        Returns:
            The new Publication.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_PUBLICATION, input = input))
        return Publication.model_validate(result["createPublication"])

    async def updatePublication(self, input: PublicationInput) -> Publication:
        """
        Updates an existing Publication.
        Args:
            input: Values with which to update the existing Publication.
        Returns:
            The updated Publication.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_PUBLICATION, input = input))
        return Publication.model_validate(result["updatePublication"])

    async def deletePublication(self, id: ID) -> Publication:
        """
        Deletes an existing Publication.
        Args:
            id: The ID of the Publication to delete.
        Returns:
            The deleted Publication.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_PUBLICATION, id = id))
        return Publication.model_validate(result["deletePublication"])

    # --------------------
    # PUBLISHERS
    # --------------------

    async def publisherById(self, id: ID) -> Publisher:
        """
        Fetches a Publisher given its identifier.
        Args:
            id: The ID of the Publisher to fetch.
        Returns:
            The requested Publisher.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PUBLISHER_BY_ID, id = id))
        return Publisher.model_validate(result["publisherById"])

    async def publishers(self, filter: TrackedEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Publisher]:
        """
        Fetches a paged list of Publishers.
        Args:
            filter: Selects which Publishers to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Publishers.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_PUBLISHERS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["publishers"])

    async def createPublisher(self, input: PublisherInput) -> Publisher:
        """
        Creates a new Publisher.
        Args:
            input: Values with which to initialise the new Publisher.
        Returns:
            The new Publisher.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_PUBLISHER, input = input))
        return Publisher.model_validate(result["createPublisher"])

    async def updatePublisher(self, input: PublisherInput) -> Publisher:
        """
        Updates an existing Publisher.
        Args:
            input: Values with which to update the existing Publisher.
        Returns:
            The updated Publisher.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_PUBLISHER, input = input))
        return Publisher.model_validate(result["updatePublisher"])

    async def deletePublisher(self, id: ID) -> Publisher:
        """
        Deletes an existing Publisher.
        Args:
            id: The ID of the Publisher to delete.
        Returns:
            The deleted Publisher.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_PUBLISHER, id = id))
        return Publisher.model_validate(result["deletePublisher"])

    # --------------------
    # QUOTATIONS
    # --------------------

    async def quotationById(self, id: ID) -> Quotation:
        """
        Fetches a Quotation given its identifier. Requires ADM authority.
        Args:
            id: The ID of the Quotation to fetch.
        Returns:
            The requested Quotation.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_QUOTATION_BY_ID, id = id))
        return Quotation.model_validate(result["quotationById"])

    async def quotations(self, filter: LinkableEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[Quotation]:
        """
        Fetches a paged list of Quotations.
        Args:
            filter: Selects which Quotations to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Quotations.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_QUOTATIONS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["quotations"])

    async def createQuotation(self, input: QuotationInput) -> Quotation:
        """
        Creates a new Quotation.
        Args:
            input: Values with which to initialise the new Quotation.
        Returns:
            The new Quotation.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_QUOTATION, input = input))
        return Quotation.model_validate(result["createQuotation"])

    async def updateQuotation(self, input: QuotationInput) -> Quotation:
        """
        Updates an existing Quotation.
        Args:
            input: Values with which to update the existing Quotation.
        Returns:
            The updated Quotation.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_QUOTATION, input = input))
        return Quotation.model_validate(result["updateQuotation"])

    async def deleteQuotation(self, id: ID) -> Quotation:
        """
        Deletes an existing Quotation.
        Args:
            id: The ID of the Quotation to delete.
        Returns:
            The deleted Quotation.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_QUOTATION, id = id))
        return Quotation.model_validate(result["deleteQuotation"])

    # --------------------
    # TOPICS
    # --------------------

    async def topicById(self, id: ID) -> Topic:
        """
        Fetches a Topic given its identifier.
        Args:
            id: The ID of the Topic to fetch.
        Returns:
            The requested Topic.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_TOPIC_BY_ID, id = id))
        return Topic.model_validate(result["topicById"])

    async def topics(self, filter: TopicQueryFilter = None, pageSort: PageableInput = None) -> Page[Topic]:
        """
        Fetches a paged list of Topics.
        Args:
            filter: Selects which Topics to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Topics.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_TOPICS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["topics"])

    # "Fetches a Topic given its identifier."
    # topicById(id: ID!): Topic

    async def createTopic(self, input: TopicInput) -> Topic:
        """
        Creates a new Topic.
        Args:
            input: Values with which to initialise the new Topic.
        Returns:
            The new Topic.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_TOPIC, input = input))
        return Topic.model_validate(result["createTopic"])

    async def updateTopic(self, input: TopicInput) -> Topic:
        """
        Updates an existing Topic.
        Args:
            input: Values with which to update the existing Topic.
        Returns:
            The updated Topic.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_TOPIC, input = input))
        return Topic.model_validate(result["updateTopic"])

    async def deleteTopic(self, id: ID) -> Topic:
        """
        Deletes an existing Topic.
        Args:
            id: The ID of the Topic to delete.
        Returns:
            The deleted Topic.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_TOPIC, id = id))
        return Topic.model_validate(result["deleteTopic"])

    # --------------------
    # USERS
    # --------------------

    async def userById(self, id: ID) -> User:
        """
        Fetches a User given its identifier. Requires ADM authority.
        Args:
            id: The ID of the User to fetch.
        Returns:
            The requested User.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_USER_BY_ID, id = id))
        return User.model_validate(result["userById"])

    # "Fetches a User given its username."
    # userByUsername(username: String!): User @auth(authority: [ADM])

    # "Returns the currently logged-in User."
    # currentUser: User

    async def users(self, filter: TrackedEntityQueryFilter = None, pageSort: PageableInput = None) -> Page[User]:
        """
        Fetches a paged list of Users.
        Args:
            filter: Selects which Users to return.
            pageSort: Paginates the results and optionally specifies custom ordering.
        Returns:
            The requested pagefull of Users.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_USERS, filter = filter, pageSort = pageSort))
        return Page.model_validate(result["users"])

    async def createUser(self, input: UserInput) -> User:
        """
        Creates a new User.
        Args:
            input: Values with which to initialise the new User.
        Returns:
            The new User.
        """
        result = await self.client.execute_async(self.bind_query_args(CREATE_USER, input = input))
        return User.model_validate(result["createUser"])

    async def updateUser(self, input: UserInput) -> User:
        """
        Updates an existing User.
        Args:
            input: Values with which to update the existing User.
        Returns:
            The updated User.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_USER, input = input))
        return User.model_validate(result["updateUser"])

    async def updateUserPassword(self, input: UserPasswordInput) -> User:
        """
        Updates an existing user's password.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_USER_PASSWORD, input = input))
        return User.model_validate(result["updateUserPassword"])

    async def updateUserProfile(self, input: UserProfileInput) -> User:
        """
        Updates an existing user's profile.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_USER_PROFILE, input = input))
        return User.model_validate(result["updateUserPassword"])

    async def grantUserAuthorities(self, userId: ID, authorities: list[AuthorityKind]) -> User:
        """
        Grants authorities to a user. The specified authorities are added to any existing ones.
        """
        result = await self.client.execute_async(self.bind_query_args(GRANT_USER_AUTHORITIES, userId = userId, authorities = authorities))
        return User.model_validate(result["grantUserAuthorities"])

    async def revokeUserAuthorities(self, userId: ID, authorities: list[AuthorityKind]) -> User:
        """
        Revokes authorities from a user. The specified authorities are removed from the user; other authorities remain intact.
        """
        result = await self.client.execute_async(self.bind_query_args(REVOKE_USER_AUTHORITIES, userId = userId, authorities = authorities))
        return User.model_validate(result["revokeUserAuthorities"])

    async def deleteUser(self, id: ID) -> User:
        """
        Deletes an existing User.
        Args:
            id: The ID of the User to delete.
        Returns:
            The deleted User.
        """
        result = await self.client.execute_async(self.bind_query_args(DELETE_USER, id = id))
        return User.model_validate(result["deleteUser"])

    # --------------------
    # MISCELLANEOUS
    # --------------------

    async def audit(self, id: ID) -> EntityAudit:
        """
        Returns audit information on the specified entity.
        Args:
            id: The ID of the entity to audit.
        Returns:
            An object to audit the entity's fields and links.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_ENTITY_AUDIT, id = id))
        return EntityAudit.model_validate(result["audit"])

    async def login(self, username: str, password: str) -> AuthPayload:
        """
        Authenticates the client using JSON Web Token (JWT).
        Args:
            username: The login user name.
            password: The password to use.
        Returns:
            An object containing the JWT authentication token to use in subsequent API requests, and the authenticated User object.
        """
        result = await self.client.execute_async(self.bind_query_args(LOGIN, username = username, password = password))
        if not "login" in result:
            raise Exception(f"Failed to authenticate {username}.")
        self.auth = AuthPayload.model_validate(result["login"])
        self.create_client() # re-create client with Authorization header
        return self.auth

    def logout(self) -> None:
        """
        Logs out by clearing the authentication token.
        """
        self.auth = None
        self.create_client()

    async def entityStatistics(self, filter: StatisticsQueryFilter) -> list[EntityStatistics]:
        """"
        Returns statistics on records of all supported entity kinds.
        Args:
            filter: If specified, only records matching the filter will be counted.
        Returns:
            An object containing record counts for all supported entity kinds.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_ENTITY_STATISTICS, filter = filter))
        adapter = TypeAdapter(list[EntityStatistics])
        return adapter.validate_python(result["entityStatistics"])

    async def topicStatistics(self, filter: StatisticsQueryFilter) -> list[TopicStatistics]:
        """"
        Returns statistics on records linked to existing Topic(s).
        Args:
            filter: If specified, only records matching the filter will be counted.
        Returns:
            An object containing record counts for all supported entity kinds, organised by their linked Topics.
        """
        result = await self.client.execute_async(self.bind_query_args(READ_TOPIC_STATISTICS, filter = filter))
        adapter = TypeAdapter(list[TopicStatistics])
        return adapter.validate_python(result["topicStatistics"])

    async def setEntityStatus(self, entityId: ID, status: StatusKind) -> TrackedEntity:
        """
        Sets entity status.
        Args:
            entityId: The ID of the record to update.
            status: The new status to set.
        Returns:
            The updated record.
        """
        result = await self.client.execute_async(self.bind_query_args(UPDATE_ENTITY_STATUS, entityId = entityId, status = status))
        return TrackedEntity.model_validate(result["setEntityStatus"])
