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

export default function QuotationsDocPage() {
  return (
    <article className="prose">
      <h2>Quotations Page</h2>
      <Image src="/doc/quotations-table.png" alt="Screen-shot of the Quotations table" width={640} height={258} />
      <p>The Quotations page contains a paginated, filterable, sortable table showing all Quotations.</p>
      <a id="filter" /><h3>Filter Sections</h3>
      <p>The filter section at the top of the page is the standard Topic and Master Record filter as described
        {" "}<Link href="/doc/filter">here</Link>.</p>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls
        as described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Quotations Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Quotee</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The full name of the person being quoted</td>
          </tr>
          <tr>
            <td>Date</td>
            <td>Date</td>
            <td>Yes</td>
            <td>The date when the words were first spoken or written</td>
          </tr>
          <tr>
            <td>Source</td>
            <td>Text</td>
            <td>No</td>
            <td>Where the words were spoken or written</td>
          </tr>
          <tr>
            <td>URL</td>
            <td>URL</td>
            <td>No</td>
            <td>The online web address of the quotation</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Free-form contributor notes on the quotation</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Quotations Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/quotations-details.png" alt="Screen-shot of the Quotations details section" width={640} height={318} />
      <p>The Details section contains the standard Tracking and Linking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Quotation Details form shows the following fields:
        Quotee, Date, Source, URL, Notes, as described in{" "}<a href="#columns">Columns</a>{" "}above.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}