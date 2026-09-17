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

from ee_ai_service.graph.nodes.complete_person import complete_person
from ee_ai_service.graph.nodes.load_persons import load_persons
from ee_ai_service.graph.state.complete_persons_state import CompletePersonsResult, CompletePersonsState
from ee_ai_service.graph.workflows.workflow import Workflow
from ee_ai_service.runtime.runtime_state import RuntimeState

def advance_person_index(state: CompletePersonsState) -> dict[str, any]:
    return {"index": state.index + 1}

def loop_control(state: CompletePersonsState) -> str:
    if state.index < len(state.persons):
        return "complete_person"
    else:
        return END

class CompletePersons(Workflow[CompletePersonsState, CompletePersonsResult]):
    """A LangGraph workflow for completing a collection of Persons."""

    def __init__(self, runtime: RuntimeState):
        super().__init__(runtime)

    @override
    def register_nodes(self):
        self.graph.add_node("load_persons", load_persons)
        self.graph.add_node("complete_person", complete_person)
        self.graph.add_node("advance_person_index", advance_person_index)

    @override
    def wire_edges(self):
        self.graph.add_edge(START, "load_persons")
        self.graph.add_edge("load_persons", "complete_person")
        self.graph.add_edge("complete_person", "advance_person_index")
        self.graph.add_conditional_edges("advance_person_index", loop_control)

    @override
    def get_result(self, state: dict[str, any]) -> CompletePersonsResult:
        """Returns the result of the workflow, which includes a list of all persons completed."""
        return CompletePersonsResult(
            persons_updated = state["persons_updated"]
        )
