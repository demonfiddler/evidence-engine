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

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.embedding import cosine, max_cosine

async def reconcile_claims(state: CompletePublicationState, *, runtime: RuntimeState) -> CompletePublicationState:
    """Reconcile existing and discovered claims using embeddings to compute semantic similarity. N.B. This is deterministic."""

    if not state.extracted_claims:
        state.claims_to_add = []
        return state

    existing_claims = [
        link.fromEntity
        for link in state.publication.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.CLAIM
    ]

    existing_vectors = await runtime.embedding_client.aembed_documents([c.text for c in existing_claims])
    extracted_vectors = await runtime.embedding_client.aembed_documents([c.text for c in state.extracted_claims])

    keep_indices = []
    for i, dv in enumerate(extracted_vectors):
        # Compare with existing claims
        if max_cosine(dv, existing_vectors) >= runtime.config.existing_threshold:
            continue

        # Compare with previously accepted extracted claims
        if any(cosine(dv, extracted_vectors[j]) >= runtime.config.discovered_threshold for j in keep_indices):
            continue

        keep_indices.append(i)

    state.claims_to_add = [state.extracted_claims[i] for i in keep_indices]

    return state
