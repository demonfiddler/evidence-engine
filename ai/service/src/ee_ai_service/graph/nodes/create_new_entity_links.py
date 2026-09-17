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

from logging import getLogger

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.inputs.entity_link_input import EntityLinkInput
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def create_new_entity_links(state: CompletePublicationState, *, config) -> dict[str, any]:
    """Creates new links between Publications, Claims, Persons and the filter Topic."""

    links_added: list[RecordInfo] = []

    if state.create_links:
        runtime: RuntimeState = config["metadata"]["runtime"]

        # Claim -> Publication
        for id in state.claim_ids_to_link:
            input = EntityLinkInput(fromEntityId = id, toEntityId = state.publication.id, toEntityLocation = "abstract")
            link = await runtime.graphql_client.createEntityLink(input)
            links_added.append(link.info())
        for claim in state.claims_added:
            input = EntityLinkInput(fromEntityId = claim.id, toEntityId = state.publication.id, toEntityLocation = "abstract")
            link = await runtime.graphql_client.createEntityLink(input)
            links_added.append(link.info())

        # Publication -> Person
        for id in state.person_ids_to_link:
            input = EntityLinkInput(fromEntityId = state.publication.id, toEntityId = id)
            link = await runtime.graphql_client.createEntityLink(input)
            links_added.append(link.info())
        for person in state.persons_added:
            input = EntityLinkInput(fromEntityId = state.publication.id, toEntityId = person.id)
            link = await runtime.graphql_client.createEntityLink(input)
            links_added.append(link.info())

        # Claim -> Person
        for person in state.persons_added:
            for id in state.claim_ids_to_link:
                input = EntityLinkInput(fromEntityId = id, toEntityId = person.id)
                link = await runtime.graphql_client.createEntityLink(input)
                links_added.append(link.info())
            for claim in state.claims_added:
                input = EntityLinkInput(fromEntityId = claim.id, toEntityId = person.id)
                link = await runtime.graphql_client.createEntityLink(input)
                links_added.append(link.info())

        if state.topic_id is not None:
            # Topic -> Claim
            for claim in state.claims_added:
                input = EntityLinkInput(fromEntityId = state.topic_id, toEntityId = claim.id)
                link = await runtime.graphql_client.createEntityLink(input)
                links_added.append(link.info())

            # Topic -> Person
            for person in state.persons_added:
                input = EntityLinkInput(fromEntityId = state.topic_id, toEntityId = person.id)
                link = await runtime.graphql_client.createEntityLink(input)
                links_added.append(link.info())

        logger.info(f"Created {len(links_added)} new EntityLinks for Publication#{state.publication.id}: {state.publication.title}")
    else:
        logger.info(f"Skipping creation of new EntityLinks for Publication#{state.publication.id}: {state.publication.title}")

    return {"links_added": links_added}
