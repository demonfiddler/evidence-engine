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

export const JournalSchema = z.object({
  title: z.string().min(10).max(100),
  abbreviation: z.string().max(50).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  issn: z.string().regex(/(?:^[0-9]{4}-[0-9]{3}[0-9X]$)?/).optional(),
  publisherId: z.string().regex(/^\d*$/).optional(),
  notes: z.string().max(200).optional()
})

export type JournalFieldValues = z.infer<typeof JournalSchema>