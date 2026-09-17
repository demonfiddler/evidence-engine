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
from datetime import date as Date
from pydantic import Field, HttpUrl, field_validator
from pydantic_extra_types.country import CountryAlpha2
from typing import Annotated, Literal, Optional, override


from ee_ai_service.models.enums.declaration_kind import DeclarationKind
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.linkable_entity import LinkableEntity
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.models.validators import validate_not_future

class Declaration(LinkableEntity):
    # The dicriminator value.
    entityKind: Literal[EntityKind.DECLARATION] = EntityKind.DECLARATION

    # The date the declaration was published.
    date: Date | None = None
    # The kind of declaration (coded).
    kind: DeclarationKind | None = None
    # The kind of declaration (human readable).
    kindLabel: str | None = None
    # The declaration name or title.
    title: str | None = None
    # The country to which the declaration relates. TODO: handle 'WD'.
    country: CountryAlpha2 | None = None
    # The URL for the declaration online.
    url: HttpUrl | None = None
    # Flag to indicate that url content is cached on this application server.
    cached: bool | None = None
    # Names of persons who signed the declaration, one per line.
    signatories: str | None = None
    # The number of signatories.
    signatoryCount: Annotated[Optional[int], Field(ge=0)] = None
    # Added notes about the declaration.
    notes: str | None = None

    @field_validator("date")
    def check_date(cls, v):
        return validate_not_future(v)

    @override
    def info(self) -> RecordInfo:
        return RecordInfo(
            id = self.id,
            text = self.title,
            notes = self.notes
        )
