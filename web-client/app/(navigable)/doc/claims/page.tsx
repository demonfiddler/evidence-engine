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

export default function ClaimsDocPage() {
  return (
    <article className="prose">
      <h2>Claims Page</h2>
      <Image src="/doc/claims-table.png" alt="Screen-shot of the Claims table" width={640} height={303} />
      <p>The Claims page contains a paginated, filterable, sortable table showing all Claims.</p>
      <a id="filter" /><h3>Filter Sections</h3>
      <p>The filter section at the top of the page is the standard Topic and Master Record filter as described
        {" "}<Link href="/doc/filter">here</Link>.</p>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described{" "}<Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Claims Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Date</td>
            <td>Date</td>
            <td>Yes</td>
            <td>The date on which the Claim was made</td>
          </tr>
          <tr>
            <td>Text</td>
            <td>Text</td>
            <td>No</td>
            <td>A textual statement of the Claim</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Notes about the Claim</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Claims Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/claims-details.png" alt="Screen-shot of the Claims details section" width={640} height={239} />
      <p>The Details section contains the standard Tracking and Linking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Claim Details form shows the following fields:
        Rating, Date, Text, Notes as described in{" "}<a href="#columns">Columns</a>{" "}above.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}