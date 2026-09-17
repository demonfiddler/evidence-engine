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

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.graph.workflows.workflow import RuntimeState
from ee_ai_service.models.entity_link import Person
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.inputs.person_input import PersonInput
from ee_ai_service.utils.name import MATCH_THRESHOLD, Name

logger = getLogger(__name__)

def fn_score(t: tuple):
    return t[1]

async def reconcile_authors(state: CompletePublicationState, *, config) -> dict[str, any]:
    """Reconcile extracted authors with existing authors in the database, and prepare new authors to be added."""

    if not state.extracted_authors:
        return {}

    # Determine which existing persons are already linked to the publication.
    linked_person_ids: list[ID] = [
        link.toEntity.id
        for link in state.publication.fromEntityLinks.content
        if link.toEntity.entityKind == EntityKind.PERSON
    ]

    # Determine which existing persons matching on last name are linked to the specified topic (if any).
    text = " ".join([n.last_name for n in state.extracted_authors])
    filter = LinkableEntityQueryFilter(text = text, topicId = state.topic_id, recursive = True) if state.topic_id is not None else None
    runtime: RuntimeState = config["metadata"]["runtime"]
    page = await runtime.graphql_client.persons(filter = filter)
    existing_persons = page.content

    person_ids_to_link: list[ID] = []
    persons_to_add: list[PersonInput] = []
    for extracted_author in state.extracted_authors:
        # Find the best match within existing persons who match on last name (per 'text' query filter used above).
        skip = False
        existing_matches = []
        for existing_person in existing_persons:
            # If an extracted author matches an existing person, we need to link to the existing person rather than create a new one.
            score = extracted_author.match_score(existing_person.name())
            if score >= MATCH_THRESHOLD:
                existing_matches.append((existing_person.id, score))
        # To disambiguate multiple matching names, sort matches by descending score.
        existing_matches.sort(key = fn_score, reverse = True)
        if len(existing_matches) > 0:
            # We found at least one matching person, so pick the best match and decide whether it needs to be linked.
            # N.B. If lesser-matching person(s) are already linked, we'll end up with multiple links to matching names.
            # Such a situation requires human resolution.
            id = existing_matches[0][0]
            if not id in linked_person_ids:
                person_ids_to_link.append(id)
            skip = True

        # Skip an extracted author that matched an existing person, as it's either already linked or about to be.
        if skip:
            continue

        # If no matching person was found, we must create it.
        persons_to_add.append(
            PersonInput(
                title = extracted_author.title,
                firstName = extracted_author.first_names,
                nickname = extracted_author.nickname,
                prefix = extracted_author.prefix,
                lastName = extracted_author.last_name,
                # alias = extracted_author.alias, # TODO: support alias
                suffix = extracted_author.suffix
            )
        )

    logger.info(f"Reconciled {len(state.extracted_authors)} extracted authors with {len(linked_person_ids)}/{len(existing_persons)} existing linked/unlinked persons, resulting in {len(person_ids_to_link)} existing Persons to link and {len(persons_to_add)} new Persons to add.")

    return {"person_ids_to_link": person_ids_to_link, "persons_to_add": persons_to_add}
