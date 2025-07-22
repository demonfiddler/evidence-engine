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

export const PublicationKindSchema = z.enum([
  "ABST",
  "ADVS",
  "AGGR",
  "ANCIENT",
  "ART",
  "BILL",
  "BLOG",
  "BOOK",
  "CASE",
  "CHAP",
  "CHART",
  "CLSWK",
  "COMP",
  "CONF",
  "CPAPER",
  "CTLG",
  "DATA",
  "DBASE",
  "DICT",
  "EBOOK",
  "ECHAP",
  "EDBOOK",
  "EJOUR",
  "ELEC",
  "ENCYC",
  "EQUA",
  "FIGURE",
  "GEN",
  "GOVDOC",
  "GRANT",
  "HEAR",
  "ICOMM",
  "INPR",
  "JFULL",
  "JOUR",
  "LEGAL",
  "MANSCPT",
  "MAP",
  "MGZN",
  "MPCT",
  "MULTI",
  "MUSIC",
  "NEWS",
  "PAMP",
  "PAT",
  "PCOMM",
  "RPRT",
  "SER",
  "SLIDE",
  "SOUND",
  "STAND",
  "STAT",
  "THES",
  "UNBILL",
  "UNPB",
  "VIDEO",
  "WEB"
])

export const PublicationSchema = z.object({
  title: z.string().min(10).max(200),
  authors: z.string().min(10).max(2000),
  journalId: z.string().regex(/^\d*$/).optional(),
  kind: PublicationKindSchema,
  date: z.iso.date().or(z.date()).optional(),
  year: z.string().regex(/^(?:19|20)\d{2}$/).or(z.uint32().min(1900).max(2099)),
  abstract: z.string().optional(),
  notes: z.string().optional(),
  peerReviewed: z.boolean(),
  doi: z.string().max(255).optional(),
  isbn: z.string().max(20).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  cached: z.boolean(),
  accessed: z.iso.date().or(z.date()).optional()
})

export type PublicationFieldValues = z.infer<typeof PublicationSchema>
export type PublicationKind = z.infer<typeof PublicationKindSchema>