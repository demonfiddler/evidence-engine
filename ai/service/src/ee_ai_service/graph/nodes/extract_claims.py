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
from pydantic import ValidationError
from textwrap import dedent

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.runtime.runtime_state import RuntimeState

async def extract_claims(state: CompletePublicationState, *, runtime: RuntimeState) -> CompletePublicationState:
    prompt = dedent(f"""
    You are an information extraction engine.

    Extract scientific claims from the text below. Extract only claims that:
    - are significant, surprising, or headline-grabbing;
    - contradict or challenge current thinking or orthodoxy;
    - are semantically distinct from each other;
    Ignore mundane, routine, or widely accepted statements.

    OUTPUT REQUIREMENTS:
    - Return ONLY a JSON array.
    - Each array element MUST be an object.
    - Each object MUST have exactly two string fields: text and notes.
    - If no claims are found, return an empty JSON array: [].
    - Populate ONLY the 'text' and 'notes' fields.
    - Keep the 'text' field under 500 characters.
    - Use the 'notes' field only for brief clarifications when needed.
    - Do not include any fields other than 'text' and 'notes'.
    - Do not include multiple claims that are broadly equivalent.
    - Do not include any text outside the JSON array.
    - Do not wrap the array in a JSON object.

    TEXT:
    {state.publication.abstract}
    """)

    raw = await runtime.inference_client.ainvoke(prompt)
    data = json.loads(raw)
    state.extracted_claims = []
    for item in data:
        try:
            state.extracted_claims.append(ClaimInput.model_validate(item))
        except ValidationError:
            item["text"] = item["text"][:500]
            state.extracted_claims.append(ClaimInput.model_validate(item))

    return state
