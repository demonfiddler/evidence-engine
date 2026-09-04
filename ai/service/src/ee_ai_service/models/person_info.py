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
from pydantic import BaseModel
from pydantic_extra_types.country import CountryAlpha2

class PersonInfo(BaseModel):
    # The person's formal full name, with format '<title> <first_names_or_initials> '<nickname>' <prefix> <last_name> <suffix> <post_nominals>', omitting empty fields.
    name: str | None = None
    # The person's ORCID researcher ID if known, otherwise null.
    orcid: str | None = None
    # The uppercase ISO-3166-1 alpha-2 code for the country with which the person is primarily associated if known, otherwise null.
    country: CountryAlpha2 | None = None
    # Brief biographical highlights including specialisms, professional positions held, institutional affiliations if known, otherwise null.
    notes: str | None = None
    # Academic qualifications held, one per Unix line, each with format '<degree> in <subject> from <institution> (<year>).' if known, otherwise null.
    qualifications: str | None = None
