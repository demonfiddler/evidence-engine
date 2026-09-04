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

from functools import partial
from langgraph.graph import END, START
from typing import override

from ee_ai_service.graph.nodes.complete_publication import complete_publication
from ee_ai_service.graph.nodes.load_publications import load_publications
from ee_ai_service.graph.state.complete_publications_state import CompletePublicationsResult, CompletePublicationsState
from ee_ai_service.graph.workflows.workflow import Workflow
from ee_ai_service.runtime.runtime_state import RuntimeState

def advance_publication_index(state: CompletePublicationsState) -> CompletePublicationsState:
    state.index += 1

def loop_control(state: CompletePublicationsState):
    if state.index < state.publications:
        return "complete_publication"
    else:
        return END

class CompletePublications(Workflow[CompletePublicationsState, CompletePublicationsResult]):
    """A LangGraph workflow for completing a collection of Publications."""

    def __init__(self, runtime: RuntimeState):
        super().__init__(self, runtime)
    
    @override
    def register_nodes(self):
        self.graph.add_node("load_publications", partial(load_publications, runtime=self.runtime))
        self.graph.add_node("complete_publication", partial(complete_publication, runtime = self.runtime))
        self.graph.add_node("advance_publication_index", advance_publication_index)

    @override
    def wire_edges(self):
        self.graph.add_edge(START, "load_publications")
        self.graph.add_edge("load_publications", "complete_publication")
        self.graph.add_edge("complete_publication", "advance_publication_index")
        self.graph.add_conditional_edges("advance_publication_index", loop_control)

    @override
    def validate(self, state, runtime: RuntimeState):
        super.validate(state, runtime)
