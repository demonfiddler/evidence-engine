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

from __future__ import annotations
from pydantic import Field, HttpUrl

from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.publisher import Publisher
from ee_ai_service.models.tracked_entity import TrackedEntity

class Journal(TrackedEntity):
    # The dicriminator value.
    entityKind: EntityKind = Field(EntityKind.JOURNAL, frozen=True)

    # The full journal title.
    title: str | None = None
    # The official ISO 4 abbreviation.
    abbreviation: str | None = None
    # Web link to the journal's home page.
    url: HttpUrl | None = None
    # The International Standard Serial Number.
    issn: str | None = None
    # The journal publisher.
    publisher: Publisher | None = None
    # Notes about the journal.
    notes: str | None = None
    # Whether the journal publishes peer-reviewed articles.
    peerReviewed: bool | None = None
