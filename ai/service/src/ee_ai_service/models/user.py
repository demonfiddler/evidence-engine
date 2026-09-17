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
from ee_ai_service.models.record_info import RecordInfo
from pydantic import Field
from pydantic_extra_types.country import CountryAlpha2
from typing import TYPE_CHECKING, override
# N.B. Although unused here, Optional must be imported to avoid Pydantic failures during model_rebuild() calls on recursive models.
from typing import Literal, Optional

if TYPE_CHECKING:
    from ee_ai_service.models.group import Group
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.security_principal import SecurityPrincipal

class User(SecurityPrincipal):
    # The dicriminator value.
    entityKind: Literal[EntityKind.USER] = EntityKind.USER

    # The (mutable?) unique user name (user-assigned).
    username: str | None = None
    # The user's first name.
    firstName: str | None = None
    # The user's last name.
    lastName: str | None = None
    # The user's email address.
    email: str | None = None
    # The user's country of habitation.
    country: CountryAlpha2 | None = None
    # Added notes on the user.
    notes: str | None = None
    # A hash of the user's password.
    password: str | None = None
    # The groups of which the user is a member.
    groups: list[Group] | None = None

    @override
    def info(self) -> RecordInfo:
        return RecordInfo(
            id = self.id,
            text = self.username
        )
