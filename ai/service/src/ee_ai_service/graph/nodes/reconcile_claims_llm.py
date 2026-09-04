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

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.runtime.runtime_state import RuntimeState

async def reconcile_claims_llm(state: CompletePublicationState, *, runtime: RuntimeState) -> CompletePublicationState:
    """Reconcile existing and discovered claims using an LLM call. N.B. This can be non-deterministic with smaller models."""

    existing_claims = [
        link.fromEntity
        for link in state.publication.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.CLAIM
    ]
    existing_claim_inputs = [
        ClaimInput(text = claim.text, notes = None)
        for claim in existing_claims
    ]

    prompt = dedent(f"""
    You are a statement deduplication and comparison engine.

    Compare the existing claims with the discovered claims. Compare ONLY the 'text' fields.

    Remove any discovered claims that:
    - are paraphrases or minor rephrasings of existing claims;
    - express the same meaning as an existing claim;
    - are semantically redundant with other discovered claims.
    Keep only discovered claims that are genuinely new and meaningfully distinct.

    INPUT FORMATS:
    - Existing claims are supplied as a JSON array.
    - Discovered claims are supplied as a JSON array.

    OUTPUT REQUIREMENTS:
    - You MUST return valid JSON. Do not think. Do not explain. Do not reason. Only produce JSON.
    - Return ONLY a JSON array of integers. Each integer is the 0-based index of a discovered claim that is meaningfully distinct from all existing claims.
    - If no discovered claims remain, return [];
    - Populate ONLY the 'text' and 'notes' fields;
    - Do not include any fields other than 'text' and 'notes';
    - Keep the 'text' field under 500 characters;
    - Use the 'notes' field only for brief clarifications when needed;
    - Do not include null fields;
    - Do not include missing fields;
    - Do not include any text outside the JSON array;
    - Do not wrap the array in a JSON object;
    - Do not provide an explanation of why you included a claim in the output;
    - Do not provide any note about now the comparison is implemented, or its limitations.

    BEGIN EXISTING CLAIMS
    {json.dumps([claim.model_dump() for claim in existing_claim_inputs])}
    END EXISTING CLAIMS

    BEGIN DISCOVERED CLAIMS
    {json.dumps([claim.model_dump() for claim in state.extracted_claims])}
    END DISCOVERED CLAIMS
    """)

    raw = await runtime.inference_client.ainvoke(prompt)
    indices = json.loads(raw)
    state.claims_to_add = [
        state.extracted_claims[i]
        for i in indices
    ]

    return state
