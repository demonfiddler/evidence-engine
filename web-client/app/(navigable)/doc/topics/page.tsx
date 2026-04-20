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

export default function TopicsDocPage() {
  return (
    <article className="prose">
      <h2>Topics Page</h2>
      <Image src="/doc/topics-table.png" alt="Screen-shot of the Topics table" width={640} height={323} />
      <p>The Topics page contains a paginated, filterable, sortable table showing all Topics.</p>
      <a id="filter" /><h3>Filter Section</h3>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>, and additionally:</p>
      <dl>
        <dt>Tree View</dt>
        <dd>Toggles between a flat Topics list and a hierarchical list where Topics are expandable to reveal their
          sub-Topics, with labels indented as in the screen-shot above.</dd>
      </dl>
      <a id="columns" /><h3>Columns</h3>
      <p>The Topics Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Label</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The short topic label</td>
          </tr>
          <tr>
            <td>Description</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The long topic description</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Topics Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/topics-details.png" alt="Screen-shot of the Topic details section" width={640} height={250} />
      <p>The Details section contains the standard Tracking and Linking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Topic Details form shows the following fields:</p>
      <dl>
        <dt>Path</dt>
        <dd>Shows labels from the 'ancestor-self axis' as a breadcrumb trail</dd>
        <dt>Parent</dt>
        <dd>Shows/changes the parent Topic</dd>
      </dl>
      <p>along with Rating, Label and Description, as described in{" "}<a href="#columns">Columns</a>{" "}above.</p>
      <p>
        The Details section contains the standard New/Cancel, Edit/Save and Delete command buttons as described{" "}
        <Link href="/doc/details/#form-commands">here</Link>.
      </p>
    </article>
  )
}