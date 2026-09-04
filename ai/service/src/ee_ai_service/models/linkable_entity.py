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
from pydantic import ConfigDict
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.page import Page
from ee_ai_service.models.tracked_entity import TrackedEntity

class LinkableEntity(TrackedEntity):
    model_config = ConfigDict(abstract=True)

    # The 'linked-from' entity
    fromEntityLinks: Page[EntityLink] | None = None
    # The 'linked-to' entity
    toEntityLinks: Page[EntityLink] | None = None
