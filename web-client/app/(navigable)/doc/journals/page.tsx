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

export default function JournalsDocPage() {
  return (
    <article className="prose">
      <h2>Journals Page</h2>
      <Image src="/doc/journals-table.png" alt="Screen-shot of the Journals table" width={640} height={256} />
      <p>The Journals page contains a paginated, filterable, sortable table showing all Journals.</p>
      <a id="filter" /><h3>Filter Section</h3>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Journals Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Yes/No/Unknown</td>
            <td>Yes</td>
            <td>Whether the Journal publishes peer-reviewed articles</td>
          </tr>
          <tr>
            <td>Title</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The journal's official name or title</td>
          </tr>
          <tr>
            <td>Abbreviation</td>
            <td>Text</td>
            <td>No</td>
            <td>The ISO 4 journal title abbreviation, with full stops after LTWA-abbreviated words</td>
          </tr>
          <tr>
            <td>ISSN</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The International Standard Serial Number (ISSN)</td>
          </tr>
          <tr>
            <td>Publisher</td>
            <td>Reference</td>
            <td>No</td>
            <td>The publisher of the journal</td>
          </tr>
          <tr>
            <td>URL</td>
            <td>URL</td>
            <td>No</td>
            <td>The journals's online web address</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Free-form contributor notes about the journal</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Journals Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/journals-details.png" alt="Screen-shot of the Journal details section" width={640} height={266} />
      <p>The Details section contains the standard Tracking part as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Journal Details form shows the following fields:
        Peer Reviewed, Title, Abbreviation, ISSN, Publisher, URL, Notes as described in{" "}
        <a href="#columns">Columns</a>{" "}above.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}