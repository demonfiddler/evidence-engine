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

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.inputs.entity_link_input import EntityLinkInput
from ee_ai_service.runtime.runtime_state import RuntimeState

async def create_new_entity_links(state: CompletePublicationState, *, runtime: RuntimeState) -> CompletePublicationState:
    """Creates new links between Publications, Claims, Persons and the filter Topic."""

    # Claim -> Publication
    for claim in state.result.claims_added:
        input = EntityLinkInput(fromEntityId = claim.id, toEntityId = state.publication.id, toEntityLocation = "abstract")
        link = await runtime.graphql_client.createEntityLink(input)
        state.result.links_added.append(link)

    # Publication -> Person
    for person in state.result.authors_added:
        input = EntityLinkInput(fromEntityId = state.publication.id, toEntityId = person.id)
        link = await runtime.graphql_client.createEntityLink(input)
        state.result.links_added.append(link)

    # Claim -> Person
    if state.result.claims_added and state.result.authors_added:
        for claim in state.result.claims_added:
            for person in state.result.authors_added:
                input = EntityLinkInput(fromEntityId = claim.id, toEntityId = person.id)
                link = await runtime.graphql_client.createEntityLink(input)
                state.result.links_added.append(link)

    if state.topic_id is not None:
        # Topic -> Claim
        for claim in state.claims_added:
            input = EntityLinkInput(fromEntityId = state.topic_id, toEntityId = claim.id)
            link = await runtime.graphql_client.createEntityLink(input)
            state.result.links_added.append(link)

        # Topic -> Person
        for person in state.result.authors_added:
            input = EntityLinkInput(fromEntityId = state.topic_id, toEntityId = person.id)
            link = await runtime.graphql_client.createEntityLink(input)
            state.result.links_added.append(link)

    return state
