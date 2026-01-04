/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import { Rateable } from "./tracked-entity"

export const PersonSchema = Rateable.extend({
  title: z.string().max(10).optional(),
  firstName: z.string().max(80),
  nickname: z.string().max(40).optional(),
  prefix: z.string().max(20).optional(),
  lastName: z.string().min(1).max(40),
  suffix: z.string().max(16).optional(),
  alias: z.string().max(40).optional(),
  notes: z.string().optional(),
  qualifications: z.string().optional(),
  country: z.string().uppercase().length(2).optional(),
  checked: z.boolean(),
  published: z.boolean().or(z.literal("indeterminate")),
})

export type PersonFieldValues = z.infer<typeof PersonSchema>