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

"""Pydantic request/response models."""

from pydantic import BaseModel, Field

from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.inputs.pageable_input import PageableInput
from ee_ai_service.models.record_info import RecordInfo

class CompletePersonsApiRequest(BaseModel):
    """Request to the CompletePersons workflow."""
    filter: LinkableEntityQueryFilter | None = None
    pageSort: PageableInput | None = None

class CompletePersonsApiResponse(BaseModel):
    """Response returned by CompletePersons workflow."""
    persons: list[RecordInfo] = Field(default_factory=list)

class CompletePublicationsApiRequest(BaseModel):
    """Request to the CompletePublications workflow."""
    filter: LinkableEntityQueryFilter | None = None
    pageSort: PageableInput | None = None
    create_claims: bool = True
    create_authors: bool = True
    create_links: bool = True
    topic_id: ID | None = None

class CompletePublicationsApiResponse(BaseModel):
    """Response returned by CompletePublications workflow."""
    claims_added: list[RecordInfo] = Field(default_factory=list)
    persons_added: list[RecordInfo] = Field(default_factory=list)
    links_added: list[RecordInfo] = Field(default_factory=list)

