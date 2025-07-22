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

export const IPage = z.object({
  hasContent: z.boolean(),
  isEmpty: z.boolean(),
  number: z.uint32().min(0),
  size: z.uint32().min(0),
  numberOfElements: z.uint32().min(0),
  totalPages: z.uint32().min(0),
  totalElements: z.string().regex(/^\d+$/),
  isFirst: z.boolean(),
  isLast: z.boolean(),
  hasNext: z.boolean(),
  hasPrevious: z.boolean(),
  content: z.array(IBaseEntity),
})