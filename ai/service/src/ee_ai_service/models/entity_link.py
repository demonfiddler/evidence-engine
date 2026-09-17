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
from pydantic import Field
from typing import Annotated, Literal, Optional, override

from ee_ai_service.models.claim import Claim
from ee_ai_service.models.declaration import Declaration
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.person import Person
from ee_ai_service.models.publication import Publication
from ee_ai_service.models.quotation import Quotation
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.models.topic import Topic
from ee_ai_service.models.tracked_entity import TrackedEntity

LinkableUnion = Annotated[
    Claim | Declaration | Person | Publication | Quotation | Topic,
    Field(discriminator="entityKind")
]

class EntityLink(TrackedEntity):
    # The dicriminator value.
    entityKind: Literal[EntityKind.ENTITY_LINK] = EntityKind.ENTITY_LINK

    fromEntity: Optional[LinkableUnion] = None
    fromEntityLocations: str | None = None
    toEntity: Optional[LinkableUnion] = None
    toEntityLocations: str | None = None

    @override
    def info(self) -> RecordInfo:
        return RecordInfo(
            id = self.id,
            text = f"EntityLink{{{self.fromEntity.entityKind}#{self.fromEntity.id} to {self.toEntity.entityKind}#{self.toEntity.id}}}"
        )
