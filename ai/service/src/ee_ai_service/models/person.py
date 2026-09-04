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
from pydantic_extra_types.country import CountryAlpha2

from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.linkable_entity import LinkableEntity
from ee_ai_service.utils.name import Name

class Person(LinkableEntity):
    # The dicriminator value.
    entityKind: EntityKind = Field(EntityKind.PERSON, frozen = True)

    # The person's title(s).
    title: str | None = None
    # The person's first name(s).
    firstName: str | None = None
    # The person's nickname.
    nickname: str | None = None
    # The prefix to the person's last name.
    prefix: str | None = None
    # The person's last name.
    lastName: str | None = None
    # The suffix to the person's name.
    suffix: str | None = None
    # Last name alias.
    alias: str | None = None
    # Brief biography, notes, etc.
    notes: str | None = None
    # Academic qualifications.
    qualifications: str | None = None
    # The primary country associated with the person.
    country: CountryAlpha2 | None = None
    # Whether the person's credentials have been checked.
    checked: bool | None = None
    # Whether the person has authored any peer-reviewed publications.
    published: bool | None = None

    def to_name(self) -> Name:
        return Name(
            title = self.title,
            first_names = self.firstName,
            nickname = self.nickname,
            prefix = self.prefix,
            last_name = self.lastName,
            suffix = self.suffix,
            alias = self.alias,
        )
