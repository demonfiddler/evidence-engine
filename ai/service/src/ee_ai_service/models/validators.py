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

from datetime import date, datetime, timezone

def validate_not_future(value: date | datetime | None):
    if value is None:
        return value

    # datetime → compare with current datetime
    if isinstance(value, datetime):
        # Normalize timezone-aware datetimes
        if value.tzinfo is not None:
            now = datetime.now(timezone.utc)
            value = value.astimezone(timezone.utc)
        else:
            now = datetime.now()

        if value > now:
            raise ValueError("Datetime cannot be in the future")

        return value

    # date → compare with today's date
    if isinstance(value, date):
        if value > date.today():
            raise ValueError("Date cannot be in the future")

        return value

    # Should never happen, but keeps things safe
    raise TypeError("Expected date or datetime")
