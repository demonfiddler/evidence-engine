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

import Group from "./Group"
import SecurityPrincipal from "./SecurityPrincipal"

export default interface User extends SecurityPrincipal {
  /** The (mutable?) unique user name (user-assigned). */
  username?: string
  /** The user's first name. */
  firstName?: string | null
  /** The user's last name. */
  lastName?: string | null
  /** The user's email address. */
  email?: string | null
  /** The user's country of habitation. */
  country?: string | null
  /** Added notes on the user. */
  notes?: string | null
  /** A hash of the user's password. */
  password?: string
  /** The groups of which the user is a member. */
  groups?: Group[]
}