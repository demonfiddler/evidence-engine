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
from ee_ai_service.graph.state.complete_publications_state import CompletePublicationsState
from ee_ai_service.graph.workflows.complete_publication import CompletePublication
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def complete_publication(state: CompletePublicationsState, *, config) -> dict[str, any]:
    """Complete a publication in the database using the provided publication information."""

    result = {
        "persons_added": [],
        "claims_added": [],
        "links_added": []
    }

    if state.index < len(state.publications):
        runtime: RuntimeState = config["metadata"]["runtime"]

        publication = state.publications[state.index]
        substate = CompletePublicationState(
            publication = publication,
            create_claims = state.request.create_claims,
            create_authors = state.request.create_authors,
            create_links = state.request.create_links,
            topic_id = state.request.topic_id,
        )
        subflow = CompletePublication(runtime)
        subflow.validate(substate, runtime)

        final_state = await subflow.run(substate)

        logger.info(f"Completed Publication#{publication.id}: {publication.title}")

        result["persons_added"].extend(final_state.persons_added)
        result["claims_added"].extend(final_state.claims_added)
        result["links_added"].extend(final_state.links_added)

    return result