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

from datetime import datetime
from pydantic import Field, model_validator
from typing import Annotated, Optional

from ee_ai_service.models.id import ID
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.inputs.tracked_entity_query_filter import TrackedEntityQueryFilter

class CommentQueryFilter(TrackedEntityQueryFilter):
    targetKind: EntityKind | None = None
    targetId: ID | None = None
    parentId: ID | None = None
    userId: ID | None = None
    _from: Annotated[Optional[datetime], Field(alias="from")] = None
    to: datetime | None = None

    @model_validator(mode="after")
    def check_from_before_to(self):
        if self._from is not None and self.to is not None and self._from > self.to:
            raise ValueError("from must be before to")
        return self
