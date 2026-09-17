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

from __future__ import annotations
from datetime import date as Date
from ee_ai_service.models.record_info import RecordInfo
from pydantic import Field, HttpUrl, PositiveInt, field_validator, model_validator
from typing import Literal, override

from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.enums.publication_kind import PublicationKind
from ee_ai_service.models.journal import Journal
from ee_ai_service.models.linkable_entity import LinkableEntity
from ee_ai_service.models.publisher import Publisher
from ee_ai_service.models.validators import validate_not_future

class Publication(LinkableEntity):
    # The dicriminator value.
    entityKind: Literal[EntityKind.PUBLICATION] = EntityKind.PUBLICATION

    # The publication title.
    title: str | None = None
    # The names of the authors, one per line.
    authors: str | None = None
    # The journal in which the publication appeared.
    journal: Journal | None = None
    # The publisher of the publication.
    publisher: Publisher | None = None
    # The publication kind (coded).
    kind: PublicationKind | None = None
    # The publication kind (human readable).
    kindLabel: str | None = None
    # The publication date.
    date: Date | None = None
    # The publication year.
    year: PositiveInt | None = None
    # Keywords per publication metadata.
    keywords: str | None = None
    # The publication abstract.
    abstract: str | None = None
    # User notes about the publication.
    notes: str | None = None
    # Whether the publication has been peer reviewed.
    peerReviewed: bool | None = None
    # The Digital Object Identifier.
    doi: str | None = None
    # The International Standard Book Number.
    isbn: str | None = None
    # The U.S. NIH National Library of Medicine's PubMed Central ID.
    pmcid: str | None = None
    # The U.S. NIH National Library of Medicine's PubMed ID.
    pmid: str | None = None
    # The Corporation for National Research Initiatives's Handle System ID.
    hsid: str | None = None
    # Cornell University Library's arXiv.org ID.
    arxivid: str | None = None
    # Cold Spring Harbor Laboratory's bioRxiv.org ID.
    biorxivid: str | None = None
    # Cold Spring Harbor Laboratory's medRxiv.org ID.
    medrxivid: str | None = None
    # U.S. Department of Education's ERIC database ID (niche).
    ericid: str | None = None
    # CERN's INSPIRE-HEP ID.
    ihepid: str | None = None
    # Open Archives Initiative's OAI-PMH ID.
    oaipmhid: str | None = None
    # CNRS (France)'s HAL ID.
    halid: str | None = None
    # CERN's Zenodo Record ID.
    zenodoid: str | None = None
    # Elsevier's SCOPUS database EID (proprietary).
    scopuseid: str | None = None
    # Clarivate's Web of Science Accession Number (UT) (proprietary).
    wsan: str | None = None
    # American Psychological Association's PsycINFO Accession Number (proprietary/niche).
    pinfoan: str | None = None
    # The URL for the publication online.
    url: HttpUrl | None = None
    # Flag to indicate that url content is cached on this application server.
    cached: bool | None = None
    # The date the publication was accessed when compiling the database.
    accessed: Date | None = None

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

    @override
    def info(self) -> RecordInfo:
        return RecordInfo(
            id = self.id,
            text = self.title,
            notes = self.notes
        )
