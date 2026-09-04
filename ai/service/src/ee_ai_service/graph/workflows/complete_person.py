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
from typing import override
from ee_ai_service.graph.state.complete_person_state import CompletePersonResult, CompletePersonState
from langgraph.graph import END, START

from ee_ai_service.graph.nodes.reconcile_description import reconcile_description
from ee_ai_service.graph.nodes.reconcile_qualifications import reconcile_qualifications
from ee_ai_service.graph.nodes.research_person import research_person
from ee_ai_service.graph.nodes.update_person import update_person
from ee_ai_service.graph.workflows.workflow import Workflow
from ee_ai_service.runtime.runtime_state import RuntimeState

class CompletePerson(Workflow[CompletePersonState, CompletePersonResult]):
    """A LangGraph workflow for completing a Person with a biographical description and academic qualifications, etc."""

    def __init__(self, runtime: RuntimeState):
        super().__init__(runtime)

    @override
    def register_nodes(self):
        self.graph.add_node("research_person", partial(research_person, runtime = self.runtime))
        self.graph.add_node("reconcile_description", partial(reconcile_description, runtime = self.runtime))
        self.graph.add_node("reconcile_qualifications", partial(reconcile_qualifications, runtime = self.runtime))
        self.graph.add_node("update_person", partial(update_person, runtime = self.runtime))

    @override
    def wire_edges(self):
        self.graph.add_edge(START, "research_person")
        self.graph.add_edge("research_person", "reconcile_description")
        self.graph.add_edge("research_person", "reconcile_qualifications")
        self.graph.add_edge("reconcile_description", "update_person")
        self.graph.add_edge("reconcile_qualifications", "update_person")
        self.graph.add_edge("update_person", END)

    @override
    def validate(self, state, runtime: RuntimeState):
        super.validate(state, runtime)

# def create_complete_person_workflow(runtime: RuntimeState):
#     return CompletePerson(runtime)
