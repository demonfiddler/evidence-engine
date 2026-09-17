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

# N.B. Although unused here, Optional must be imported to avoid Pydantic failures during model_rebuild() calls on recursive models.
from typing import Optional

from ee_ai_service.models.base_entity import BaseEntity
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.comment import Comment
from ee_ai_service.models.declaration import Declaration
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.group import Group
from ee_ai_service.models.id import ID
from ee_ai_service.models.journal import Journal
from ee_ai_service.models.linkable_entity import LinkableEntity
from ee_ai_service.models.log import Log
from ee_ai_service.models.page import Page
from ee_ai_service.models.person import Person
from ee_ai_service.models.person_info import PersonInfo
from ee_ai_service.models.publication import Publication
from ee_ai_service.models.publisher import Publisher
from ee_ai_service.models.quotation import Quotation
from ee_ai_service.models.security_principal import SecurityPrincipal
from ee_ai_service.models.topic import Topic
from ee_ai_service.models.tracked_entity import TrackedEntity
from ee_ai_service.models.user import User

# Leaf models
BaseEntity.model_rebuild()
SecurityPrincipal.model_rebuild()
User.model_rebuild()
Group.model_rebuild()
Publisher.model_rebuild()
Journal.model_rebuild()
PersonInfo.model_rebuild()

# Mid-graph models
Log.model_rebuild()
TrackedEntity.model_rebuild()
Comment.model_rebuild()

# Recursive root models
EntityLink.model_rebuild()
LinkableEntity.model_rebuild()
Person.model_rebuild()
Claim.model_rebuild()
Declaration.model_rebuild()
Quotation.model_rebuild()
Publication.model_rebuild()
Topic.model_rebuild()
Page.model_rebuild()