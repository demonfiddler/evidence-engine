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

from enum import StrEnum

from StrTokenizer import StrTokenizer
from ee_ai_service.models.enums.publication_kind import PublicationKind
from ee_ai_service.models.publication import Publication
from ee_ai_service.utils.name import Name

class CitationFormat(StrEnum):
    CSE_NAME_YEAR = "cse-name-year"

def _cse_name_year(publication) -> str:
    sb = []
    authors = []
    tok = StrTokenizer(publication.authors, "\r\n")
    while tok.hasMoreTokens():
        authors.append(tok.nextToken())
    for i in range(4):
        if i < len(authors):
            if i > 0:
                sb.append(", ")
            name = Name.parse(authors[i])
            sb.append(name.format("%l %I"))
        else:
            break
    if len(authors) > 4:
        sb.append(" et al")
    sb.append(".")
    if publication.year is not None:
        sb.append(f" {publication.year}.")
    sb.append(f" {publication.title}.")
    if publication.journal is not None:
        sb.append(f" {publication.journal.title}.")
    if publication.doi is not None:
        sb.append(f" doi: {publication.doi}")
    return "".join(sb)

def cite(publication: Publication, format: CitationFormat = CitationFormat.CSE_NAME_YEAR) -> str:
    citation = None
    match format:
        case CitationFormat.CSE_NAME_YEAR:
            citation = _cse_name_year(publication)
    return citation
