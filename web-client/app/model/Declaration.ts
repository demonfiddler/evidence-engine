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

export default interface Declaration extends ILinkableEntity {
  /** The kind of declaration. */
  kind?: string
  /** The declaration name or title. */
  title?: string
  /** The date the declaration was published. */
  date?: Date | string | null
  /** The country to which the declaration relates. */
  country?: string | null
  /** The URL for the declaration online. */
  url?: /*URL | */string | null
  /** Flag to indicate that url content is cached on this application server. */
  cached?: boolean
  /** Names of persons who signed the declaration, one per line. */
  signatories?: string | null
  /** The number of signatories. */
  signatoryCount?: number | null
  /** Added notes about the declaration. */
  notes?: string | null
}