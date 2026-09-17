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
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def create_new_claims(state: CompletePublicationState, *, config) -> dict[str, any]:
    """Create new claims in the database using the provided claim information."""

    claims_added: list[RecordInfo] = []
    if state.create_claims:
        runtime: RuntimeState = config["metadata"]["runtime"]
        for input in state.claims_to_add:
            claim = await runtime.graphql_client.createClaim(input)
            claims_added.append(claim.info())

        logger.info(f"Created {len(claims_added)} new Claims for Publication#{state.publication.id}: {state.publication.title}")
    else:
        logger.info(f"Skipping creation of new Claims for Publication#{state.publication.id}: {state.publication.title}")

    return {"claims_added": claims_added}
