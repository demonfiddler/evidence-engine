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
from datetime import datetime
# N.B. Although unused here, Optional must be imported to avoid Pydantic failures during model_rebuild() calls on recursive models.
from typing import Optional

from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.enums.transaction_kind import TransactionKind
from ee_ai_service.models.base_entity import BaseEntity
from ee_ai_service.models.id import ID
from ee_ai_service.models.user import User

class Log(BaseEntity):
    # The date-time stamp.
    timestamp: datetime | None = None
    # The user who made the change.
    user: User | None = None
    # The kind of transaction.
    transactionKind: TransactionKind | None = None
    # The kind of entity affected.
    entityKind: EntityKind | None = None
    # The ID of the entity affected.
    entityId: ID | None = None
    # The kind of entity linked/unlinked (where applicable).
    linkedEntityKind: EntityKind | None = None
    # The ID of the entity linked/unlinked (where applicable).
    linkedEntityId: ID | None = None
