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
import { IBaseEntity } from "./base-entity"
import { EntityKind, Status } from "./enums"

const RATING_ERROR = "Rating must be a number between 1 and 5"

export const Rateable = z.object({
  rating: z.number().min(0, {error: RATING_ERROR}).max(5, {error: RATING_ERROR}).optional(),
})

export const ITrackedEntity = IBaseEntity.extend({
  entityKind: EntityKind,
  status: Status,
  rating: z.number().min(0, {error: RATING_ERROR}).max(5, {error: RATING_ERROR}).optional(),
  created: z.iso.date(),
  // createdByUser: User,
  updated: z.iso.date().optional(),
  // updatedByUser: User.optional(),
  // log: z.array(Log).optional(),
  // comments: z.array(Comment).optional(),
})

export type ITrackedEntityFieldValues = z.infer<typeof ITrackedEntity>