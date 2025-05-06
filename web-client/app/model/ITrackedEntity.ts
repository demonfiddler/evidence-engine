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

import IBaseEntity from "./IBaseEntity";
import IPage from "./IPage";
import Log from "./Log";
import User from "./User";

export default interface ITrackedEntity extends IBaseEntity {
  /** The entity kind. */
  entityKind?: string;
  /** The entity status. */
  status?: string;
  /** When the record was created. */
  created?: Date | string
  /** The user who created the record. */
  createdByUser?: User
  /** When the record was last updated. */
  updated?: Date | string
  /** The user who last updated the record. */
  updatedByUser?: User
  /** Log of transactions involving the record. */
  log?: IPage<Log>
}