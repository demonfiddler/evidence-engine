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

export const DeclarationKindSchema = z.enum([
  "DECL",
  "OPLE",
  "PETN",
])

export const DeclarationSchema = Rateable.extend({
  kind: DeclarationKindSchema,
  date: z.date({message: "Declaration date is required"})
    .max(Date.now(), {message: "Declaration date cannot be in the future"}),
  title: z.string().min(10).max(100),
  country: z.string().uppercase().length(2).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  cached: z.boolean(),
  signatories: z.string().optional(),
  signatoryCount: z.string().regex(/^\d*$/).or(z.uint32()).optional(),
  notes: z.string().optional()
})

export type DeclarationFieldValues = z.infer<typeof DeclarationSchema>
export type DeclarationKind = z.infer<typeof DeclarationKindSchema>