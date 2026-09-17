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

from ee_ai_service.graph.state.complete_persons_state import CompletePersonsState
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def load_persons(state: CompletePersonsState, *, config) -> dict[str, any]:
    """Load persons from the database using the provided filter and pagination information."""

    runtime: RuntimeState = config["metadata"]["runtime"]
    page = await runtime.graphql_client.persons(
        filter = state.request.filter,
        pageSort = state.request.pageSort
    )
    persons = page.content

    logger.info(f"Loaded {len(persons)} Persons from the database with filter: {state.request.filter} and pageSort: {state.request.pageSort}")

    return {"persons": persons}
