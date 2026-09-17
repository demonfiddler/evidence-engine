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
from ee_ai_service.models.inputs.person_input import PersonInput
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.name import Name

logger = getLogger(__name__)

async def update_person(state: CompletePersonState, *, config) -> dict[str, any]:
    """Update a person in the database using the provided person information."""

    name = state.person_info.name
    # state.person_info.notes = state.reconciled_notes
    # state.person_info.qualifications = state.reconciled_qualifications
    notes = "\n".join(state.person_info.notes + state.person_info.affiliations)
    qualifications = "\n".join(state.person_info.qualifications)
    input = PersonInput(
        id = state.person.id,
        title = name.title,
        firstName = name.first_names,
        nickname = name.nickname,
        prefix = name.prefix,
        lastName = name.last_name,
        suffix = name.suffix,
        alias = name.alias,
        notes = notes,
        qualifications = qualifications,
        country = state.person_info.country,
        # orcid = state.person_info.orcid,
        checked = state.person.checked,
        published = state.person.published,
        rating = state.person.rating,
    )

    runtime: RuntimeState = config["metadata"]["runtime"]
    person = await runtime.graphql_client.updatePerson(input)

    logger.info(f"Updated Person#{person.id}: {person.firstName} {person.lastName} with new information: {state.person_info}")


    return {"person": person}
