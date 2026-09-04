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

from pydantic import BaseModel, Field

from ee_ai_service.graph.workflows.workflow import WorkflowState
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.person import Person
from ee_ai_service.models.publication import Publication
from ee_ai_service.models.topic import Topic

class CompletePublicationsResult(BaseModel):
    """The result returned as workflow's output."""

    claims_added: list[Claim] = Field(default_factory=list)
    authors_added: list[Person] = Field(default_factory=list)
    links_added: list[EntityLink] = Field(default_factory=list)

class CompletePublicationsState(WorkflowState):
    """State used to complete claims and authors for collection of publications"""

    filter: LinkableEntityQueryFilter | None
    topic: Topic | None
    publications: list[Publication] = Field(default_factory=list)
    index: int = 0
    result: CompletePublicationsResult = Field(default_factory=CompletePublicationsResult)
