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

export default function PublishersDocPage() {
  return (
    <article className="prose">
      <h2>Publishers Page</h2>
      <Image src="/doc/publishers-table.png" alt="Screen-shot of the Publishers table" width={640} height={167} />
      <p>The Publishers page contains a paginated, filterable, sortable table showing all Publishers.</p>
      <a id="filter" /><h3>Filter Section</h3>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Publishers Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Name</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The Publisher's registered company or trading name</td>
          </tr>
          <tr>
            <td>Journal Count</td>
            <td>Number</td>
            <td>No</td>
            <td>The number of Journals published by this organisation</td>
          </tr>
          <tr>
            <td>Location</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The Publisher's location(s) (city/region)</td>
          </tr>
          <tr>
            <td>Country</td>
            <td>Reference</td>
            <td>Yes</td>
            <td>The Publisher's country, if known and singular</td>
          </tr>
          <tr>
            <td>URL</td>
            <td>URL</td>
            <td>No</td>
            <td>The Publisher's online web address</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Free-form contributor notes about the Publisher</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Publishers Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/publishers-details.png" alt="Screen-shot of the Publishers details section" width={640} height={464} />
      <p>The Details section contains the standard Tracking part as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Publisher Details form shows the following fields:
        Journal Count, Name, Location, Country, URL, Notes as described in{" "}<a href="#columns">Columns</a>{" "}above.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}