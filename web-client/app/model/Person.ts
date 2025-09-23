/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

import ILinkableEntity from "./ILinkableEntity"

export default interface Person extends ILinkableEntity {
  /** The person's title(s). */
  title?: string | null
  /** The person's first name(s). */
  firstName?: string | null
  /** The person's nickname. */
  nickname?: string | null
  /** The prefix to the person's last name. */
  prefix?: string | null
  /** The person's last name. */
  lastName?: string
  /** The suffix to the person's name. */
  suffix?: string | null
  /** Last name alias. */
  alias?: string | null
  /** Brief biography, notes, etc. */
  notes?: string | null
  /** Academic qualifications. */
  qualifications?: string | null
  /** The primary country associated with the person. */
  country?: string | null
  /** Whether the person's credentials have been checked. */
  checked?: boolean
  /** Whether the person has authored any peer-reviewed publications. */
  published?: boolean
}