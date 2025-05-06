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

import ILinkableEntity from "./ILinkableEntity";

export default interface Person extends ILinkableEntity {
  /** The person's title(s). */
  title?: string
  /** The person's first name(s). */
  firstName?: string
  /** The person's nickname. */
  nickname?: string
  /** The prefix to the person's last name. */
  prefix?: string
  /** The person's last name. */
  lastName?: string
  /** The suffix to the person's name. */
  suffix?: string
  /** Last name alias. */
  alias?: string
  /** Brief biography, notes, etc. */
  notes?: string
  /** Academic qualifications. */
  qualifications?: string
  /** The primary country associated with the person. */
  country?: string
  /** A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc. */
  rating?: number
  /** Whether the person's credentials have been checked. */
  checked?: boolean
  /** Whether the person has authored any peer-reviewed publications. */
  published?: boolean
}