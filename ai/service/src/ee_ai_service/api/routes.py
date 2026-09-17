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

from __future__ import annotations
from fastapi import APIRouter, Request

from ee_ai_service.api.schemata import CompletePersonsApiRequest, CompletePersonsApiResponse, CompletePublicationsApiRequest, CompletePublicationsApiResponse
from ee_ai_service.graph.state.complete_persons_state import CompletePersonsState
from ee_ai_service.graph.state.complete_publications_state import CompletePublicationsState
from ee_ai_service.graph.workflows.complete_persons import CompletePersons
from ee_ai_service.graph.workflows.complete_publications import CompletePublications

router = APIRouter(prefix="/ai", tags=["ai"])

@router.post("/persons/complete", response_model = CompletePersonsApiResponse)
async def complete_persons(request: Request, body: CompletePersonsApiRequest):
    runtime = request.app.state.runtime
    workflow = CompletePersons(runtime)
    state = CompletePersonsState(request = body)
    workflow.validate(state, runtime)
    final_state = await workflow.run(state)
    return CompletePersonsApiResponse(persons = final_state.persons_updated)

@router.post("/publications/complete", response_model = CompletePublicationsApiResponse)
async def complete_publications(request: Request, body: CompletePublicationsApiRequest):
    runtime = request.app.state.runtime
    workflow = CompletePublications(runtime)
    state = CompletePublicationsState(request = body)
    workflow.validate(state, runtime)
    final_state = await workflow.run(state)
    return CompletePublicationsApiResponse(
        claims_added = final_state.claims_added,
        persons_added = final_state.persons_added,
        links_added = final_state.links_added
    )
