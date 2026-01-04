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
import { AuthoritiesSchema } from "./authority"
import { Rateable } from "./tracked-entity"

export const UserSchema = Rateable.extend({
  username: z.string().regex(/^[a-z0-9]*$/).min(1).max(50),
  password: z.string().regex(/^\{bcrypt\}\$[a-zA-Z0-9/$.]{59}$/).optional(),
  firstName: z.string().min(1).max(50),
  lastName: z.string().min(1).max(50),
  email: z.email().max(100),
  country: z.string().uppercase().length(2).optional(),
  notes: z.string().optional(),
  ...AuthoritiesSchema.shape
})

export type UserFieldValues = z.infer<typeof UserSchema>