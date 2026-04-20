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

export default function SecurityDocPage() {
  return (
    <article className="prose">
      <h2>Security</h2>
      <p>The Security page contains two sub-pages, Groups and Users, accessed via the Tab control at the top of the page.</p>
      <a href="groups" /><h3>The Groups Sub-page</h3>
      <Image src="/doc/groups-table.png" alt="Screen-shot of the Security/Groups sub-page table" width={640} height={278} />
      <p>The Groups page contains a paginated, filterable, sortable table showing all Groups.</p>
      <a id="group-filter" /><h4>Filter Section</h4>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="group-columns" /><h4>Columns</h4>
      <p>The Groups Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Group Name</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The name for the group, consisting of letters only</td>
          </tr>
          <tr>
            <td>Authorities</td>
            <td>References</td>
            <td>No</td>
            <td>Authorities granted to the group, from: Administer, Change, Comment, Create, Link, Read, Update, Upload</td>
          </tr>
        </tbody>
      </table>
      <h4>Paginator</h4>
      <p>The Groups Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h4>Details Section</h4>
      <Image src="/doc/groups-details.png" alt="Screen-shot of the Security/Groups details section" width={640} height={160} />
      <p>The Details section contains the standard Tracking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the Group Details form shows the following fields:
        Group Name, Authorities, as described in{" "}<a href="#group-columns">Columns</a>{" "}above.
      </p>
      <a href="users" /><h3>The Users Sub-page</h3>
      <Image src="/doc/users-table.png" alt="Screen-shot of the Security/Users sub-page table" width={640} height={329} />
      <p>The Users page contains a paginated, filterable, sortable table showing all Users. You can toggle between
        showing the Users and showing the members of the Group selected on the Groups page.
      </p>
      <a id="user-filter" /><h4>Filter Section</h4>
      <p>The table filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>.</p>
      <a id="user-columns" /><h4>Columns</h4>
      <p>The Users Page table supports the standard ID, Status, Rating, Created, Created By, Updated, Updated By
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
            <td>Username</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The user's username, consisting of lowercase letters only. Cannot be changed once it has been set.</td>
          </tr>
          <tr>
            <td>First Name</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The user's first or given name</td>
          </tr>
          <tr>
            <td>Last Name</td>
            <td>Text</td>
            <td>Yes</td>
            <td>The user's last name / surname</td>
          </tr>
          <tr>
            <td>Email</td>
            <td>Text</td>
            <td>No</td>
            <td>The user's email address</td>
          </tr>
          <tr>
            <td>Password</td>
            <td>Hash</td>
            <td>No</td>
            <td>bcrypt hash of user's password</td>
          </tr>
          <tr>
            <td>Country</td>
            <td>Reference</td>
            <td>No</td>
            <td>The user's country of residence</td>
          </tr>
          <tr>
            <td>Notes</td>
            <td>Text</td>
            <td>No</td>
            <td>Free-form administrator notes on the User</td>
          </tr>
          <tr>
            <td>Authorities</td>
            <td>References</td>
            <td>No</td>
            <td>Authorities granted to the user</td>
          </tr>
        </tbody>
      </table>
      <h4>Paginator</h4>
      <p>The Users Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h4>Details Section</h4>
      <Image src="/doc/users-details.png" alt="Screen-shot of the Security/Users details section" width={640} height={467} />
      <p>The Details section contains the standard Tracking and Linking parts as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the User Details form shows the following fields:
        Username, First Name, Last Name, Email, Password, Country, Notes, Authorities, as described in{" "}
        <a href="#user-columns">Columns</a>{" "}above.
      </p>
      <p><b>N.B. It is strongly discouraged to grant authorities to an individual user. Instead, add the User to a new
        or existing Group that has the appropriate authorities.</b></p>
    </article>
  )
}