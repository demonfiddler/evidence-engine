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
from abc import abstractmethod
from datetime import datetime
from ee_ai_service.models.record_info import RecordInfo
from pydantic import ConfigDict, Field
from typing import Annotated, TYPE_CHECKING, Optional

from ee_ai_service.models.base_entity import BaseEntity
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.enums.status_kind import StatusKind
from ee_ai_service.models.page import Page
if TYPE_CHECKING:
    from ee_ai_service.models.comment import Comment
    from ee_ai_service.models.log import Log
    from ee_ai_service.models.user import User

class TrackedEntity(BaseEntity):
    model_config = ConfigDict(
        abstract=True,
        discriminator="entityKind"
    )

    entityKind: EntityKind | None = None
    status: StatusKind | None = None
    rating: Annotated[Optional[int], Field(ge=1, le=5)] = None
    created: datetime | None = None
    createdByUser: Optional[User] = None
    updated: datetime | None = None
    updatedByUser: Optional[User] = None
    log: list[Log] | None = None
    comments: Page[Comment] | None = None

    @abstractmethod
    def info(self) -> RecordInfo:
        """Return the record info for this entity."""
        pass