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

import ILinkableEntity from "./ILinkableEntity"
import Journal from "./Journal"

export default interface Publication extends ILinkableEntity {
  /** The publication title. */
  title?: string
  /** The names of the authors, one per line. */
  authors?: string
  /** The journal in which the publication appeared. */
  journal?: Journal | null
  /** The publication kind. */
  kind?: string
  /** The publication date. */
  date?: Date | string | null
  /** The publication year. */
  year?: number | null
  /** The publication abstract. */
  abstract?: string | null
  /** User notes about the publication. */
  notes?: string | null
  /** Whether the publication has been peer reviewed. */
  peerReviewed?: boolean
  /** The Digital Object Identifier. */
  doi?: string | null
  /** The International Standard Book Number. */
  isbn?: string | null
  /** The U.S. National Library of Medicine's PubMedID. */
  pmid: string | null
  /** The Corporation for National Research Initiatives's Handle System ID. */
  hsid: string | null
  /** Cornell University Library's arXiv.org ID. */
  arxivid: string | null
  /** Cold Spring Harbor Laboratory's bioRxiv.org ID. */
  biorxivid: string | null
  /** Cold Spring Harbor Laboratory's medRxiv.org ID. */
  medrxivid: string | null
  /** U.S. Department of Education's ERIC database ID (niche). */
  ericid: string | null
  /** CERN's INSPIRE-HEP ID. */
  ihepid: string | null
  /** Open Archives Initiative's OAI-PMH ID. */
  oaipmhid: string | null
  /** CNRS (France)'s HAL ID. */
  halid: string | null
  /** CERN's Zenodo Record ID. */
  zenodoid: string | null
  /** Elsevier's SCOPUS database EID (proprietary). */
  scopuseid: string | null
  /** Clarivate's Web of Science Accession Number (UT) (proprietary). */
  wsan: string | null
  /** American Psychological Association's PsycINFO Accession Number (proprietary/niche). */
  pinfoan: string | null
  /** The URL for the publication online. */
  url?: /*URL | */string | null
  /** Flag to indicate that url content is cached on this application server. */
  cached?: boolean
  /** The date the publication was accessed when compiling the database. */
  accessed?: Date | string | null
}