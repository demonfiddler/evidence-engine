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

'use client'

import Image from "next/image"
import Link from "next/link"

export default function PublicationsDocPage() {
  return (
    <article className="prose">
      <h2>Publications Page</h2>
      <Image src="/doc/publications-table.png" alt="Screen-shot of the Publications table" width={640} height={273} />
      <p>The Publications page contains a paginated, filterable, sortable table showing all Publications.</p>
      <a id="filter" /><h3>Filter Sections</h3>
      <p>The filter section at the top of the page is the standard Topic and Master Record filter as described
        {" "}<Link href="/doc/filter">here</Link>.</p>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Publications Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
        columns as described{" "}<a href="/doc/tables/#columns">here</a>, and additionally:</p>
      <table>
        <thead>
          <tr>
            <th>Field</th>
            <th>Type</th>
            <th>Visible by Default</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Peer Reviewed</td>
            <td>Yes/No</td>
            <td>No</td>
            <td>Whether the publication was peer-reviewed</td>
          </tr>
          <tr>
            <td>Cached</td>
            <td>Yes/No</td>
            <td>No</td>
            <td>Whether the publication content is cached on this server</td>
          </tr>
          <tr>
            <td>Title</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The publication name/title</td>
          </tr>
          <tr>
            <td>Kind</td>
            <td>String</td>
            <td>Yes</td>
            <td>The kind of publication (corresponds to the RIS 'TY' field)</td>
          </tr>
          <tr>
            <td>Publication Date</td>
            <td>Date</td>
            <td>No</td>
            <td>The date on which the publication was first published</td>
          </tr>
          <tr>
            <td>Publication Year</td>
            <td>Year</td>
            <td>Yes</td>
            <td>The year in which the publication was first published</td>
          </tr>
          <tr>
            <td>Journal</td>
            <td>Reference</td>
            <td>No</td>
            <td>The journal or series containing the publication</td>
          </tr>
          <tr>
            <td>Publisher</td>
            <td>Reference</td>
            <td>No</td>
            <td>The publisher of the publication, must agree with that of Journal if both are set</td>
          </tr>
          <tr>
            <td>Authors</td>
            <td>Text</td>
            <td>No</td>
            <td>The author(s) of the publication, one per line</td>
          </tr>
          <tr>
            <td>Keywords</td>
            <td>Text</td>
            <td>No</td>
            <td>Keywords per publication metadata</td>
          </tr>
          <tr>
            <td>Abstract</td>
            <td>Text</td>
            <td>No</td>
            <td>Concise summary of the publication</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Free-form contributor notes about the publication</td>
          </tr>
          <tr>
            <td>URL</td>
            <td>URL</td>
            <td>No</td>
            <td>The online web address</td>
          </tr>
          <tr>
            <td>Accessed</td>
            <td>Date</td>
            <td>No</td>
            <td>The date the Publication was last accessed by the contributor</td>
          </tr>
          <tr>
            <td>DOI</td>
            <td>Text</td>
            <td>No</td>
            <td>The Digital Object Identifier (DOI)</td>
          </tr>
          <tr>
            <td>ISBN</td>
            <td>Text</td>
            <td>Yes/No</td>
            <td>The International Standard Book Number (ISBN)</td>
          </tr>
          <tr>
            <td>PubMedCentral ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The U.S. NIH National Library of Medicine PubMed Central ID (PMCID)</td>
          </tr>
          <tr>
            <td>PubMed ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The U.S. NIH National Library of Medicine PubMed ID (PMID)</td>
          </tr>
          <tr>
            <td>Handle System ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Corporation for National Research Initiatives Handle System ID</td>
          </tr>
          <tr>
            <td>arXiv ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Cornell University Library arXiv.org ID</td>
          </tr>
          <tr>
            <td>bioRxiv ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Cold Spring Harbor Laboratory bioRxiv.org ID</td>
          </tr>
          <tr>
            <td>medRxiv ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Cold Spring Harbor Laboratory medRxiv.org ID</td>
          </tr>
          <tr>
            <td>ERIC ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The U.S. Department of Education ERIC database ID</td>
          </tr>
          <tr>
            <td>INSPIRE_HEP ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The CERN INSPIRE-HEP ID</td>
          </tr>
          <tr>
            <td>OAI-PMH ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Open Archives Initiative OAI-PMH ID</td>
          </tr>
          <tr>
            <td>HAL ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The CNRS (France) HAL ID</td>
          </tr>
          <tr>
            <td>Zenodo ID</td>
            <td>Text</td>
            <td>No</td>
            <td>The CERN Zenodo Record ID</td>
          </tr>
          <tr>
            <td>SCOPUS EID</td>
            <td>Text</td>
            <td>No</td>
            <td>The Elsevier SCOPUS database EID</td>
          </tr>
          <tr>
            <td>WS Accession Number</td>
            <td>Text</td>
            <td>No</td>
            <td>The Clarivate Web of Science Accession Number (UT)</td>
          </tr>
          <tr>
            <td>PsycINFO Accession Number</td>
            <td>Text</td>
            <td>No</td>
            <td>The American Psychological Association PsycINFO Accession Number</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Publications Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/publications-details.png" alt="Screen-shot of the Publications details section" width={640} height={540} />
      <p>The Details section contains the standard Tracking and Linking parts as described{" "}
        <Link href="/doc/details/#tracking">here</Link>, and the Publication Details form shows the following fields:
        Rating, Peer Reviewed, Cached, Title, Kind, Publication Date, Publication Year, Journal, Publisher, Authors,
        Keywords, Abstract, Notes, URL, Accessed, DOI, ISBN, PubMedCentral ID, PubMed ID, Handle System ID, arXiv ID,
        bioRxiv ID, medRxiv ID, ERIC ID, INSPIRE-HEP ID, OAI-PMH ID, HAL ID, Zenodo ID, SCOPUS EID, WS Accession Number,
        PsycINFO Accession Number, as described in{" "}<a href="#columns">Columns</a>{" "}above.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}