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

from typing import override
from langgraph.graph import END, START

from ee_ai_service.graph.nodes.analyse_person_results import analyse_person_results
from ee_ai_service.graph.nodes.extract_person_facts import extract_person_facts
from ee_ai_service.graph.nodes.research_person import research_person
from ee_ai_service.graph.nodes.update_person import update_person
from ee_ai_service.graph.state.complete_person_state import CompletePersonResult, CompletePersonState
from ee_ai_service.graph.workflows.workflow import Workflow
from ee_ai_service.runtime.runtime_state import RuntimeState

class CompletePerson(Workflow[CompletePersonState, CompletePersonResult]):
    """A LangGraph workflow for completing a Person with a biographical description and academic qualifications, etc."""

    def __init__(self, runtime: RuntimeState):
        super().__init__(runtime)

    @override
    def register_nodes(self):
        self.graph.add_node("research_person", research_person)
        self.graph.add_node("extract_person_facts", extract_person_facts)
        self.graph.add_node("analyse_person_results", analyse_person_results)
        self.graph.add_node("update_person", update_person)

    @override
    def wire_edges(self):
        self.graph.add_edge(START, "research_person")
        self.graph.add_edge("research_person", "extract_person_facts")
        self.graph.add_edge("extract_person_facts", "analyse_person_results")
        self.graph.add_edge("analyse_person_results", "update_person")
        self.graph.add_edge("update_person", END)

    @override
    def get_result(self, state: dict[str, any]) -> CompletePersonResult:
        """Returns the result of the workflow, which is information about the completed Person."""
        return CompletePersonResult(
            person_updated = state["person"].info()
        )
