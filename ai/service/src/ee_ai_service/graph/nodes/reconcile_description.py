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

from textwrap import dedent

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.runtime.runtime_state import RuntimeState

async def reconcile_description(state: CompletePersonState, *, runtime: RuntimeState) -> CompletePersonState:
    prompt = dedent(f"""
    You are an agent who specializes in documenting the biographies of scientists.

    Merge the existing description of a scientist with additional information discovered by another researcher.

    INPUT FORMATS:
    Both existing description and discovered description are text strings.

    OUTPUT REQUIREMENTS:
    - Merge the existing description and discovered description together into a single succinct summary;
    - Include professional specialisms and research areas;
    - Include institutional affiliations;
    - Include positions held, especially professorships;
    - Do not include academic qualifications;
    - Do not duplicate information.

    BEGIN EXISTING DESCRIPTION
    {state.person.notes}
    END EXISTING DESCRIPTION

    BEGIN DISCOVERED DESCRIPTION
    {state.notes}
    END DISCOVERED DESCRIPTION
    """)

    state.notes = await runtime.inference_client.ainvoke(prompt)

    return state
