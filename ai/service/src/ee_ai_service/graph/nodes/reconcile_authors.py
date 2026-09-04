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
from ee_ai_service.models.inputs.person_input import PersonInput
from ee_ai_service.models.person import Person

async def reconcile_authors(state: CompletePublicationState) -> CompletePublicationState:
    if not state.extracted_authors:
        return state

    existing_authors: list[Person] = [
        link.toEntity
        for link in state.publication.fromEntityLinks.content
        if link.toEntity.entityKind == EntityKind.PERSON
    ]
    state.existing_author_names = [
        author.to_name()
        for author in existing_authors
    ]
    for extracted_author in state.extracted_authors:
        if not any(extracted_author.matches(existing_author) for existing_author in existing_authors):
            state.persons_to_add.append(
                PersonInput(
                    title = extracted_author.title,
                    firstName = extracted_author.first_names,
                    nickname = extracted_author.nickname,
                    prefix = extracted_author.prefix,
                    lastName = extracted_author.last_name,
                    alias = extracted_author.alias,
                    suffix = extracted_author.suffix
                )
            )

    return state
