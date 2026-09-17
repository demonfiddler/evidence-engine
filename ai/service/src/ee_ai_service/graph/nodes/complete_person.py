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

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.graph.state.complete_persons_state import CompletePersonsState
from ee_ai_service.graph.workflows.complete_person import CompletePerson
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def complete_person(state: CompletePersonsState, *, config) -> dict[str, any]:
    """Complete a person in the database using the provided person information."""

    person = state.persons[state.index]
    substate = CompletePersonState(person = person)
    runtime: RuntimeState = config["metadata"]["runtime"]
    subflow = CompletePerson(runtime)
    subflow.validate(substate, runtime)

    result = await subflow.run(substate)

    logger.info(f"Completed Person#{result.person_updated.id}: {result.person_updated.firstName} {result.person_updated.lastName}")

    return {"person_updated": [result.person_updated]}
