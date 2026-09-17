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

from langgraph.graph import END, START
from typing import override

from ee_ai_service.graph.nodes.create_new_claims import create_new_claims
from ee_ai_service.graph.nodes.create_new_entity_links import create_new_entity_links
from ee_ai_service.graph.nodes.create_new_persons import create_new_persons
from ee_ai_service.graph.nodes.extract_authors import extract_authors
from ee_ai_service.graph.nodes.extract_claims import extract_claims
from ee_ai_service.graph.nodes.reconcile_authors import reconcile_authors
from ee_ai_service.graph.nodes.reconcile_claims import reconcile_claims
from ee_ai_service.graph.state.complete_publication_state import CompletePublicationResult, CompletePublicationState
from ee_ai_service.graph.workflows.workflow import Workflow
from ee_ai_service.runtime.runtime_state import RuntimeState

class CompletePublication(Workflow[CompletePublicationState, CompletePublicationResult]):
    """A LangGraph workflow for completing a Publication with claims and authors."""

    def __init__(self, runtime: RuntimeState):
        super().__init__(runtime)

    @override
    def register_nodes(self):
        self.graph.add_node("extract_claims", extract_claims)
        self.graph.add_node("reconcile_claims", reconcile_claims)
        self.graph.add_node("create_new_claims", create_new_claims)
        self.graph.add_node("extract_authors", extract_authors)
        self.graph.add_node("reconcile_authors", reconcile_authors)
        self.graph.add_node("create_new_persons", create_new_persons)
        self.graph.add_node("create_new_entity_links", create_new_entity_links)

    @override
    def wire_edges(self):
        self.graph.add_edge(START, "extract_claims")
        self.graph.add_edge("extract_claims", "reconcile_claims")
        self.graph.add_edge("reconcile_claims", "create_new_claims")
        self.graph.add_edge("create_new_claims", "create_new_entity_links")
        self.graph.add_edge(START, "extract_authors")
        self.graph.add_edge("extract_authors", "reconcile_authors")
        self.graph.add_edge("reconcile_authors", "create_new_persons")
        self.graph.add_edge("create_new_persons", "create_new_entity_links")
        self.graph.add_edge("create_new_entity_links", END)

    @override
    def get_result(self, state: dict[str, any]) -> CompletePublicationResult:
        """Returns the result of the workflow, which includes lists of all claims, authors and links added."""
        return CompletePublicationResult(
            claims_added = state["claims_added"],
            persons_added = state["persons_added"],
            links_added = state["links_added"]
        )
