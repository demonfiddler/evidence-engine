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
import { Rateable } from "./tracked-entity"

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

export const PublicationSchema = Rateable.extend({
  title: z.string().min(10).max(200),
  authors: z.string().min(10).max(2000),
  journalId: z.string().length(0).or(z.string().regex(/^\d*$/)),
  kind: PublicationKindSchema,
  date: z.date().max(Date.now(), { error: "Publication date cannot be in the future" }).optional(),
  year: z.string().regex(/^(?:19|20)\d{2}$/).or(z.uint32().min(1900).max(2099)),
  abstract: z.string(),
  notes: z.string(),
  peerReviewed: z.boolean(),
  doi: z.string().length(0).or(z.string().min(8).max(100).regex(/^10\.\d{4,9}\/[-._;()/:A-Z0-9]+$/i)),
  isbn: z.string().length(0).or(z.string().min(10).max(17)/*.regex(/^(?:\d{9}X$|^\d{10})$/|/^(?:97[89])\d{10}$/)*/),
  pmid: z.string().length(0).or(z.string().min(1).max(10).regex(/^\d{1,10}$/)),
  hsid: z.string().length(0).or(z.string().min(5).max(12)/*.regex(/^\d+\.\d+\/\S+$/)*/),
  arxivid: z.string().length(0).or(z.string().min(9).max(15).regex(/^\d{4}\.\d{4,5}(v\d+)?$/)),
  biorxivid: z.string().length(0).or(z.string().min(9).max(20).regex(/^10\.1101\/\d+$/)),
  medrxivid: z.string().length(0).or(z.string().min(9).max(20).regex(/^10\.1101\/\d+$/)),
  ericid: z.string().length(0).or(z.string().length(8).regex(/^ED\d{6}$/i)),
  ihepid: z.string().length(0).or(z.string().min(6).max(10).regex(/^\d{6,10}$/)),
  oaipmhid: z.string().length(0).or(z.string().min(7).max(50).regex(/^oai:[^:]+:[^:]+$/)),
  halid: z.string().length(0).or(z.string().min(10).max(14).regex(/^hal-\d{6,10}$/)),
  zenodoid: z.string().length(0).or(z.string().min(6).max(10).regex(/^\d{6,10}$/)),
  scopuseid: z.string().length(0).or(z.string().min(8).max(16).regex(/^\d{8,16}$/)),
  wsan: z.string().length(0).or(z.string().min(15).max(25).regex(/^[A-Z0-9]{15,25}$/)),
  pinfoan: z.string().length(0).or(z.string().min(10).max(30).regex(/^[A-Z0-9\-]{10,30}$/)),
  url: z.string().length(0).or(z.url().min(10).max(200)),
  cached: z.boolean(),
  accessed: z.date().max(Date.now(), { error: "Accessed date cannot be in the future" }).optional()
})

export type PublicationFieldValues = z.infer<typeof PublicationSchema>
export type PublicationKind = z.infer<typeof PublicationKindSchema>