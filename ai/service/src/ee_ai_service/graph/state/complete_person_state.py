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
from ee_ai_service.models.person import Person
from ee_ai_service.models.person_facts import PersonFacts
from ee_ai_service.models.person_info import PersonInfo
from ee_ai_service.models.record_info import RecordInfo
from ee_ai_service.models.search_result import SearchResult

class CompletePersonResult(BaseModel):
    person_updated: RecordInfo

class CompletePersonState(WorkflowState):
    """State used to complete a person's description, qualifications, etc."""

    person: Person
    """The Person to complete."""

    person_info: PersonInfo | None = None
    """Information about the person, either extracted from person or gathered from research."""

    search_results: list[SearchResult] = Field(default_factory=list)
    """The results of a web search for the person."""

    facts_per_result: list[PersonFacts] = Field(default_factory=list)
    """Candidate facts about a person extracted from a given search result."""

    reconciled_notes: str | None = None
    """The combination of existing and discovered notes."""

    reconciled_qualifications: str | None = None
    """The combination of existing and discovered qualifications."""

    # Result fields
    person_updated: RecordInfo | None = None
    """Selected fields from the updated person."""
