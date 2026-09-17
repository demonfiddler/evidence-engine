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

from logging import getLogger, DEBUG

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.embedding import cosine, max_cosine

logger = getLogger(__name__)

async def reconcile_claims(state: CompletePublicationState, *, config) -> dict[str, any]:
    """Reconcile existing and extracted claims using embeddings to compute semantic similarity. N.B. This is deterministic."""

    if not state.extracted_claims:
        return {}

    # Determine which existing claims are already linked to the publication.
    linked_claims = [
        link.fromEntity
        for link in state.publication.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.CLAIM
    ]
    linked_claim_ids = [c.id for c in linked_claims]

    runtime: RuntimeState = config["metadata"]["runtime"]

    # Determine which existing claims are linked to the specified topic (if any), but not to the publication.
    filter = LinkableEntityQueryFilter(topicId = state.topic_id, recursive = True) if state.topic_id is not None else None
    page = await runtime.graphql_client.claims(filter = filter)
    existing_claims = page.content
    existing_claims = [c for c in existing_claims if c.id not in linked_claim_ids]

    existing_vectors = await runtime.embedding_client.aembed_documents([c.text for c in existing_claims])
    linked_vectors = await runtime.embedding_client.aembed_documents([c.text for c in linked_claims])
    extracted_vectors = await runtime.embedding_client.aembed_documents([c.text for c in state.extracted_claims])

    # Determine which existing claims to link and which new ones to add.
    existing_indices_to_link = []
    extracted_indices_to_add = []
    for i, extracted_vector in enumerate(extracted_vectors):
        skip = False
        logger.debug(f"Comparing '{state.extracted_claims[i].text}' with:")
        for j, existing_vector in enumerate(existing_vectors):
            # If an extracted claim matches an existing claim, we need to link to the existing claim rather than create a new one.
            similarity = cosine(extracted_vector, existing_vector)
            logger.debug(f"  Claim#{existing_claims[j].id} = {similarity}")
            if similarity >= runtime.config.existing_threshold:
                existing_indices_to_link.append(j)
                skip = True

        # Skip an extracted claim that matched one or more existing claims, as we will link it.
        if skip:
            continue

        # Ignore any extracted claim that is too similar to any existing linked claim.
        if max_cosine(extracted_vector, linked_vectors) >= runtime.config.existing_threshold:
            continue

        # Ignore any extracted claim that is too similar to any previously accepted extracted claim.
        if any(cosine(extracted_vector, extracted_vectors[j]) >= runtime.config.discovered_threshold for j in extracted_indices_to_add):
            continue

        extracted_indices_to_add.append(i)

    claim_ids_to_link: list[ID] = [existing_claims[i].id for i in existing_indices_to_link]
    claims_to_add: list[ClaimInput] = [state.extracted_claims[i] for i in extracted_indices_to_add]

    logger.info(f"Reconciled {len(state.extracted_claims)} extracted Claims with {len(linked_claims)}/{len(existing_claims)} existing linked/unlinked Claims, resulting in {len(claim_ids_to_link)} existing Claims to link and {len(claims_to_add)} new Claims to add.")
    if logger.isEnabledFor(DEBUG):
        logger.deubg(f"Claims to add: {claims_to_add}")

    return {"claim_ids_to_link": claim_ids_to_link, "claims_to_add": claims_to_add}
