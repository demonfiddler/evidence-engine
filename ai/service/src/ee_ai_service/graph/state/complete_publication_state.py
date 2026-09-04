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
from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.claim_input import ClaimInput
from ee_ai_service.models.inputs.person_input import PersonInput
from ee_ai_service.models.publication import Publication
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.person import Person
from ee_ai_service.utils.name import Name

class CompletePublicationResult(BaseModel):
    authors_added: list[Person] = Field(default_factory=list)
    claims_added: list[Claim] = Field(default_factory=list)
    links_added: list[EntityLink] = Field(default_factory=list)

class CompletePublicationState(WorkflowState):
    """State used to complete a publication's claims and authors"""

    topic_id: ID | None
    publication: Publication
    extracted_claims: list[ClaimInput] = Field(default_factory=list)
    claims_to_add: list[ClaimInput] = Field(default_factory=list)
    extracted_authors: list[Name] = Field(default_factory=list)
    persons_to_add: list[PersonInput] = Field(default_factory=list)
    result: CompletePublicationResult = Field(default_factory=CompletePublicationResult)
