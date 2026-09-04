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
from textwrap import dedent

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.graph.tools.web_search import web_search
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.person_info import PersonInfo
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.citation import cite

async def research_person(state: CompletePersonState, *, runtime: RuntimeState) -> CompletePersonState:
    publications = [
        el.fromEntity
        for el in state.Person.toEntityLinks
        if el.fromEntity.entityKind == EntityKind.PUBLICATION
    ]
    citations = [
        cite(p) + "\n"
        for p in publications
    ]
    citations = "".join(citations)

    prompt = dedent(f"""
    You are a science researcher.

    Research the following person: {state.person.to_name().to_string()}. Use the hints below to disambiguate.

    HINTS:
    This person is thought to have authored the following publications:
    {citations}

    The following is known about his person:
    {state.person.notes if state.person.notes else "(nothing)"}

    This person has the following academic qualifications:
    {state.person.qualifications if state.person.qualifications else "(unknown)"}

    OUTPUT REQUIREMENTS:
    - Return ONLY a JSON object.
    - The object MUST have exactly five string fields:
        - name: The person's formal full name, with format '<title> <first_names_or_initials> '<nickname>' <prefix> <last_name> <suffix> <post_nominals>', omitting empty fields;
        - orcid: The person's ORCID researcher ID if known, otherwise null;
        - country: The uppercase ISO-3166-1 alpha-2 code for the country with which the person is primarily associated if known, otherwise null;
        - notes: Brief biographical highlights including specialisms, professional positions held, institutional affiliations if known, otherwise null;
        - qualifications: Academic qualifications held, one per Unix line, each with format '<degree> in <subject> from <institution> (<year>).' if known, otherwise null.
    - Populate ONLY the 'name', 'orcid', 'country', 'notes' and 'qualifications' fields.
    - If no relevant information is found, return an empty object: {{}}.
    - Do not include any fields other than 'name', 'orcid', 'country', 'notes' and 'qualifications'.
    - Do not include any text outside the JSON object.
    """)

    web_search_client = runtime.inference_client.bind_tools([web_search])
    raw = await web_search_client.ainvoke(prompt)
    state.person_info = PersonInfo.model_validate(json.loads(raw))

    return state
