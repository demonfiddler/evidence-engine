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

from datetime import date as Date
from pydantic import HttpUrl, field_validator
from pydantic_extra_types.country import CountryAlpha2

from ee_ai_service.models.enums.declaration_kind import DeclarationKind
from ee_ai_service.models.inputs.tracked_entity_input import TrackedEntityInput
from ee_ai_service.models.validators import validate_not_future

class DeclarationInput(TrackedEntityInput):
    kind: DeclarationKind
    title: str
    date: Date
    country: CountryAlpha2 | None
    url: HttpUrl | None
    signatories: str | None
    notes: str | None

    @field_validator("date")
    def check_date(cls, v):
        return validate_not_future(v)
