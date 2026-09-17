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

import json
from logging import getLogger, DEBUG
from textwrap import dedent

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.string import indent_lines

logger = getLogger(__name__)

async def reconcile_description(state: CompletePersonState, *, config) -> dict[str, any]:
    """Reconcile existing and discovered descriptions of a scientist using an LLM call. N.B. This can be non-deterministic with smaller models."""

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
    - Do NOT include academic qualifications;
    - Do NOT duplicate information.

    BEGIN EXISTING DESCRIPTION
    {indent_lines(state.person.notes, indent_first = False) if state.person.notes else "(none)"}
    END EXISTING DESCRIPTION

    BEGIN DISCOVERED DESCRIPTION
    {indent_lines(state.person_info.notes, indent_first = False) if state.person_info.notes else "(none)"}
    END DISCOVERED DESCRIPTION
    """)

    runtime: RuntimeState = config["metadata"]["runtime"]
    ai_message = await runtime.inference_client.ainvoke(prompt)
    reconciled_notes: str = ai_message.content

    logger.info(f"Reconciled existing notes with discovered notes for Person#{state.person.id}: {state.person.firstName} {state.person.lastName}")
    logger.debug(f"Notes: {reconciled_notes}")

    return {"reconciled_notes": reconciled_notes}
