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
    authorNames: str | None # TODO: rename to authors
    # authorIds: str[] | None # TODO: remove from schema.graphqls
    title: str
    journalId: ID | None
    publisherId: ID | None
    kind: PublicationKind
    date: Date | None
    year: PositiveInt | None
    keywords: str | None
    abstract: str | None
    notes: str | None
    peerReviewed: bool | None
    doi: str | None
    isbn: str | None
    pmcid: str | None
    pmid: str | None
    hsid: str | None
    arxivid: str | None
    biorxivid: str | None
    medrxivid: str | None
    ericid: str | None
    ihepid: str | None
    oaipmhid: str | None
    halid: str | None
    zenodoid: str | None
    scopuseid: str | None
    wsan: str | None
    pinfoan: str | None
    url: HttpUrl | None
    accessed: Date | str | None
    cached: bool

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
