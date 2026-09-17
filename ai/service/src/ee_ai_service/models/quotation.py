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
from pydantic import HttpUrl, field_validator
from typing import Literal, override

from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.linkable_entity import LinkableEntity
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.models.validators import validate_not_future

class Quotation(LinkableEntity):
    # The dicriminator value.
    entityKind: Literal[EntityKind.QUOTATION] = EntityKind.QUOTATION

    # The text of the quotation.
    text: str | None = None
    # The person(s) who made the quotation.
    quotee: str | None = None
    # The quotation date.
    date: Date | None = None
    # The quotation source.
    source: str | None = None
    # The URL for the quotation online.
    url: HttpUrl | None = None
    # Notes on the quotation.
    notes: str | None = None

    @field_validator("date")
    def check_date(cls, v):
        return validate_not_future(v)

    @override
    def info(self) -> RecordInfo:
        return RecordInfo(
            id = self.id,
            text = self.text,
            notes = self.notes
        )
