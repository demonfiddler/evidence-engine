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

"""defines HTTP endpoints"""

from pydantic import BaseModel, Field
from fastapi import APIRouter, Request

from ee_ai_service.graph.state.complete_persons_state import CompletePersonsResult, CompletePersonsState
from ee_ai_service.graph.state.complete_publications_state import CompletePublicationsResult, CompletePublicationsState
from ee_ai_service.graph.workflows.complete_persons import CompletePersons
from ee_ai_service.graph.workflows.complete_publications import CompletePublications
from ee_ai_service.models.claim import Claim
from ee_ai_service.models.entity_link import EntityLink
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.person import Person

router = APIRouter(prefix="/ai", tags=["ai"])

class CompletePersonsApiResponse(BaseModel):
    """Response returned by CompletePersons workflow."""
    persons: list[Person] = Field(default_factory=list)

@router.post("/persons/complete", response_model = CompletePersonsApiResponse)
async def complete_persons(request: Request, filter: LinkableEntityQueryFilter):
    runtime = request.app.state.runtime
    workflow = CompletePersons(runtime)
    state = CompletePersonsState(filter = filter)
    workflow.validate(state, runtime)
    final_state = await workflow.run(state)
    return CompletePersonsApiResponse(persons = final_state.persons)

class CompletePublicationsApiResponse(BaseModel):
    """Response returned by CompletePublications workflow."""
    claims_added: list[Claim] = Field(default_factory=list)
    authors_added: list[Person] = Field(default_factory=list)
    links_added: list[EntityLink] = Field(default_factory=list)

@router.post("/publications/complete", response_model = CompletePublicationsApiResponse)
async def complete_publications(request: Request, filter: LinkableEntityQueryFilter):
    runtime = request.app.state.runtime
    workflow = CompletePublications(runtime)
    state = CompletePublicationsState(filter = filter)
    workflow.validate(state, runtime)
    final_state = await workflow.run(state)
    return CompletePublicationsApiResponse(
        claims_added = final_state.claims_added,
        authors_added = final_state.authors_added,
        links_added = final_state.links_added
    )
