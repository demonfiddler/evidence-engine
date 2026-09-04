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

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.graph.state.complete_publications_state import CompletePublicationsState
from ee_ai_service.graph.workflows.complete_publication import CompletePublication
from ee_ai_service.runtime.runtime_state import RuntimeState

async def complete_publication(state: CompletePublicationsState, *, runtime: RuntimeState) -> CompletePublicationsState:
    publication = state.publications[state.index]
    substate = CompletePublicationState(publication = publication, topic_id = state.filter.topicId)
    subflow = CompletePublication(runtime)
    subflow.validate(substate, runtime)

    result = await subflow.run(substate)
    state.result.authors_added.extend(result.authors_added)
    state.result.claims_added.extend(result.claims_added)
    state.result.links_added.extend(result.links_added)

    return state
