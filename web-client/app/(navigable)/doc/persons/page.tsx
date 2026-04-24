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

export default function PersonsDocPage() {
  return (
    <article className="prose">
      <h2>Persons Page</h2>
      <Image src="/doc/persons-table.png" alt="Screen-shot of the Persons table" width={640} height={253} />
      <p>The Persons page contains a paginated, filterable, sortable table showing all Persons.</p>
      <a id="filter" /><h3>Filter Sections</h3>
      <p>The filter section at the top of the page is the standard Topic and Master Record filter as described
        {" "}<Link href="/doc/filter">here</Link>.</p>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described{" "}<Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="columns" /><h3>Columns</h3>
      <p>The Persons Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Title</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The person's formal honorific title (Dr., Prof., Sir, etc.), <b>with periods</b> for abbreviations</td>
          </tr>
          <tr>
            <td>First Name(s)</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The person's given forename(s) or initials</td>
          </tr>
          <tr>
            <td>Nickname</td>
            <td>Text</td>
            <td>No</td>
            <td>The person's informal nickname</td>
          </tr>
          <tr>
            <td>Prefix</td>
            <td>Text</td>
            <td>No</td>
            <td>The prefix to the person's surname (de, van, zu, etc.)</td>
          </tr>
          <tr>
            <td>Last Name</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The person's surname/family name, excluding any prefix or suffix</td>
          </tr>
          <tr>
            <td>Suffix</td>
            <td>Text</td>
            <td>No</td>
            <td>The suffix to the person's surname (Jnr, Snr, III, etc.)</td>
          </tr>
          <tr>
            <td>Alias</td>
            <td>Text</td>
            <td>No</td>
            <td>An alternative name by which the person is also known; distinct from Nickname</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Biographical and other notes about the person</td>
          </tr>
          <tr>
            <td>Qualifications</td>
            <td>Text</td>
            <td>No</td>
            <td>The person's formal academic qualifications, including degree, subject, graduation year and academic
              institution. Degree abbreviations should include periods but no spaces (e.g.,  B.A., M.Sc., Ph.D., etc.)
            </td>
          </tr>
          <tr>
            <td>Country</td>
            <td>String</td>
            <td>Yes</td>
            <td>The country with which the person is primarily associated</td>
          </tr>
          <tr>
            <td>Checked</td>
            <td>Yes/No</td>
            <td>Yes</td>
            <td>Whether the person's details and credentials have been checked</td>
          </tr>
          <tr>
            <td>Published</td>
            <td>Yes/No/Unknown</td>
            <td>Yes</td>
            <td>Whether the person has authored peer-reviewed publications</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Persons Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/persons-details.png" alt="Screen-shot of the Person details section" width={640} height={293} />
      <p>The Details section contains the standard Tracking and Linking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Person Details form shows the following fields:
        Rating, Title, First Name(s), Nickname, Prefix, Last Name, Suffix, Alias, Notes, Qualifications, Country,
        Checked, Published, as described{" "}<a href="#columns">above</a>.
      </p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}