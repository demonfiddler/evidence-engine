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

import { z } from "zod/v4"
import { Log } from "./log"
import { IBaseEntity } from "./base-entity"

export const EntityKind = z.enum([
  "CLA",
  "COU",
  "DEC",
  "GRP",
  "JOU",
  "LNK",
  "PER",
  "PUB",
  "PBR",
  "QUO",
  "TOP",
  "USR",
])

export const Status = z.enum([
  "DEL",
  "DRA",
  "PUB",
  "SUS",
])

export const ITrackedEntity = IBaseEntity.extend({
  entityKind: EntityKind,
  status: Status,
  created: z.iso.date(),
  // createdByUser: User,
  updated: z.iso.date().optional(),
  // updatedByUser: User.optional(),
  log: z.array(Log),
})

export type ITrackedEntityFieldValues = z.infer<typeof ITrackedEntity>