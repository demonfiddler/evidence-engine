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

export default interface Topic extends ILinkableEntity {
  /** The topic label for display in the user interface. */
  label?: string
  /** The topic description. */
  description?: string | null
  /** The full path to the topic. */
  path?: string | null
  /** The parent topic, if any. */
  parent?: Topic | null
  /** The ID of the parent topic, if any. */
  parentId?: string | null
  /** The sub-topics. */
  children?: Topic[]
}