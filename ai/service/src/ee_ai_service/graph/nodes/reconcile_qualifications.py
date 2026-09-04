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

async def reconcile_qualifications(state: CompletePersonState, *, runtime: RuntimeState) -> CompletePersonState:
    prompt = dedent(f"""
    You are an agent who specializes in documenting the academic qualifications of scientists.

    Merge the existing qualifications of a scientist with additional information discovered by another researcher.

    INPUT FORMATS:
    Both existing qualifications and discovered qualifications are text strings.

    OUTPUT REQUIREMENTS:
    - Merge the existing qualifications and discovered qualifications together into a single succinct summary;
    - Output one qualification per Unix line;
    - Order the qualifications by year, or by academic level if year is unknown;
    - Format each qualification as: '<degree> in <subject> from <institution> (<year>).';
    - If <institution> is a university, state both the college and the university names;
    - Do not output exact duplicate qualifications.

    BEGIN EXISTING DESCRIPTION
    {state.person.qualifications}
    END EXISTING DESCRIPTION

    BEGIN DISCOVERED DESCRIPTION
    {state.qualifications}
    END DISCOVERED DESCRIPTION
    """)

    state.qualifications = await runtime.inference_client.ainvoke(prompt)

    return state
