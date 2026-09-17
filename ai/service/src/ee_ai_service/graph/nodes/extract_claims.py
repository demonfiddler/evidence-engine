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
from logging import getLogger
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.utils.string import indent_lines
from pydantic import ValidationError
from textwrap import dedent

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

async def extract_claims(state: CompletePublicationState, *, config) -> dict[str, any]:
    """Extract scientific claims from a publication's abstract using an LLM call."""

    # Build a numbered list of existing claims to avoid duplication in the extraction process.
    existing_claims = [
        link.fromEntity.text
        for link in state.publication.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.CLAIM
    ]
    for i in range(0, len(existing_claims)):
        existing_claims[i] = f"{i + 1}. {existing_claims[i]}"
    existing_claims = "\n    ".join(existing_claims)

    prompt = dedent(f"""
    You are an information extraction engine.

    Extract only the *major* scientific claims from the text below.
    Focus on high-level, conceptual findings rather than specific details.
    Ignore mundane, routine, or widely accepted statements.

    Extract only claims that:
    - are significant, surprising, or headline-grabbing;
    - contradict or challenge current thinking or orthodoxy.

    GENERALISATION REQUIREMENTS:
    - Prefer broad, conceptual summaries over fine-grained paraphrases.
    - Merge related or overlapping statements into a single generalised claim.
    - Ignore sub-claims, methodological details, and narrow distinctions.
    - Do NOT extract multiple claims that differ only in nuance or phrasing.
    - Aim for 3–5 major claims unless the text clearly contains fewer.

    THEMATIC CLUSTERING REQUIREMENTS:
    - First identify the major conceptual themes in the text.
    - Extract at most ONE claim per theme unless two claims differ in mechanism or implication.
    - Treat variations, examples, or consequences of the same theme as supporting details, not separate claims.
    - Collapse all closely related statements into a single generalised claim.
    - Do NOT produce multiple claims about a given theme unless they describe fundamentally different, unrelated phenomena.

    CONCEPTUAL EQUIVALENCE RULE:
    - Treat any statements that express the same underlying scientific finding as equivalent, even if phrased differently.
    - Do NOT produce multiple claims that differ only in wording, emphasis, or framing.

    CLAIM COMPRESSION RULE:
    - If multiple statements describe different consequences, examples, or manifestations of the same underlying phenomenon, compress them into a single claim.

    OUTPUT REQUIREMENTS:
    - Return ONLY a JSON array.
    - Each array element MUST be an object.
    - Each object MUST have exactly two string fields: text and notes.
    - If no claims are found, return an empty JSON array: [].
    - Populate ONLY the 'text' and 'notes' fields.
    - Keep the 'text' field under 500 characters.
    - Use the 'notes' field only for brief clarifications when needed.
    - Do NOT include any fields other than 'text' and 'notes'.
    - Do NOT include multiple claims that are broadly equivalent.
    - Do NOT include any text outside the JSON array.
    - Do NOT wrap the array in a JSON object.

    FORMAT STABILITY REQUIREMENTS:
    - Output MUST be valid JSON.
    - Do NOT use markdown fences (NO ```json ... ```).
    - Do NOT add any commentary, explanation, or preamble.
    - Do NOT add trailing commas.
    - Do NOT add whitespace before or after the JSON array.
    - The FIRST character of the output MUST be '['. DO NOT start with anything else.
    - The LAST character of the output MUST be ']'. DO NOT finish with anything else.

    DUPLICATION AVOIDANCE REQUIREMENTS:
    - Do NOT produce any claim that is conceptually equivalent to any of the existing claims below.
    - Do NOT produce narrower, broader, or differently‑phrased versions of the existing claims below.
    - Treat conceptual equivalence broadly: if a new claim expresses the same underlying scientific finding, even with different wording, framing, emphasis, or granularity, do NOT include it.
    - Do NOT produce consequences, examples, or restatements of existing claims.
    - Only produce claims that introduce a genuinely new scientific idea not covered by any existing claim.

    EXISTING CLAIMS:
    {existing_claims}

    TEXT:
    {indent_lines(state.publication.abstract, indent_first = False)}
    """)

    runtime: RuntimeState = config["metadata"]["runtime"]
    # For reasons unknown, the LLM sometimes returns invalid JSON. Retry up to 5 times if this happens.
    for retry in range(5):
        try:
            ai_message = await runtime.inference_client.ainvoke(prompt)
            data = json.loads(ai_message.content)
            break
        except Exception as e:
            logger.warning(f"Attempt {retry + 1} failed to extract claims: {e}")
            if retry == 4:
                raise e
    extracted_claims: list[Claim] = []
    for item in data:
        try:
            extracted_claims.append(ClaimInput.model_validate(item))
        except ValidationError:
            item["text"] = item["text"][:500]
            extracted_claims.append(ClaimInput.model_validate(item))

    logger.info(f"Extracted {len(extracted_claims)} claims from Publication#{state.publication.id}: {state.publication.title}")

    return {"extracted_claims": extracted_claims}
