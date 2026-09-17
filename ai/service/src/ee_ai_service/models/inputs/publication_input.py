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

from datetime import date as Date
from pydantic import HttpUrl, PositiveInt, field_validator, model_validator

from ee_ai_service.models.enums.publication_kind import PublicationKind
from ee_ai_service.models.id import ID
from ee_ai_service.models.inputs.tracked_entity_input import TrackedEntityInput
from ee_ai_service.models.validators import validate_not_future

class PublicationInput(TrackedEntityInput):
    authorNames: str | None = None # TODO: rename to authors
    # authorIds: str[] | None = None # TODO: remove from schema.graphqls
    title: str
    journalId: ID | None = None
    publisherId: ID | None = None
    kind: PublicationKind
    date: Date | None = None
    year: PositiveInt | None = None
    keywords: str | None = None
    abstract: str | None = None
    notes: str | None = None
    peerReviewed: bool | None = None
    doi: str | None = None
    isbn: str | None = None
    pmcid: str | None = None
    pmid: str | None = None
    hsid: str | None = None
    arxivid: str | None = None
    biorxivid: str | None = None
    medrxivid: str | None = None
    ericid: str | None = None
    ihepid: str | None = None
    oaipmhid: str | None = None
    halid: str | None = None
    zenodoid: str | None = None
    scopuseid: str | None = None
    wsan: str | None = None
    pinfoan: str | None = None
    url: HttpUrl | None = None
    accessed: Date | str | None = None
    cached: bool = False

    @field_validator("date")
    def check_date(cls, v):
        return validate_not_future(v)

    @field_validator("year")
    def check_year(cls, v):
        if v is not None and v > Date.today().year:
            raise ValueError("year cannot be in the future")
        return v

    @field_validator("accessed")
    def check_accessed(cls, v):
        return validate_not_future(v)

    @model_validator(mode="after")
    def check_year_matches_date(self):
        if self.year is not None and self.date is not None and self.year != self.date.year:
            raise ValueError("year must match the year component of date")
        return self
