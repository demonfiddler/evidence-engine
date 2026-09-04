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
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from ee_ai_service.models.topic import Topic
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.id import ID
from ee_ai_service.models.linkable_entity import LinkableEntity

class Topic(LinkableEntity):
    # The dicriminator value.
    entityKind: EntityKind = Field(EntityKind.TOPIC, frozen=True)

    # The topic label for display in the user interface.
    label: str | None = None
    # The topic description.
    description: str | None = None
    # The full path to the topic.
    path: str | None = None
    # The parent topic, if any.
    parent: Topic | None = None
    # The ID of the parent topic, if any. N.B: this field is not part of the GraphQL type.
    parentId: ID | None = None
    # The sub-topics.
    children: list[Topic] | None = None
