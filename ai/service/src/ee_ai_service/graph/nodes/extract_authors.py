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

from logging import getLogger
from regex import compile, split

from ee_ai_service.graph.state.complete_publication_state import CompletePublicationState
from ee_ai_service.utils.name import Name

logger = getLogger(__name__)

_NEWLINE = compile(r"[\r\n]+")

async def extract_authors(state: CompletePublicationState) -> dict[str, any]:
    """Extract authors from a publication's author string and parse them into Name objects."""

    # Extract raw names, one per line, filtering out any blank lines.
    raw_names: list[str] = split(_NEWLINE, state.publication.authors)
    i = len(raw_names) - 1
    while i >= 0:
        if len(raw_names[i].strip()) == 0:
            raw_names.pop(i)
        i -= 1
    extracted_authors: list[Name] = [Name.parse(raw_name) for raw_name in raw_names]

    logger.info(f"Extracted {len(extracted_authors)} authors from Publication#{state.publication.id}: {state.publication.title}")

    return {"extracted_authors": extracted_authors}
