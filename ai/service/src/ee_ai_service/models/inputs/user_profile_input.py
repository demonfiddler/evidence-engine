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

from pydantic import BaseModel
from pydantic_extra_types.country import CountryAlpha2

from ee_ai_service.models.id import ID

class UserProfileInput(BaseModel):
    """The immutable, unique user identifier (system-assigned)."""
    id: ID
    """The user's first name."""
    firstName: str
    """The user's last name."""
    lastName: str
    """The user's email address."""
    email: str
    """The user's country of residence (ISO-3166-1 alpha-2 code)."""
    country: CountryAlpha2
    """Added notes about the user."""
    notes: str
