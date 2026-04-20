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

export default function LogDocPage() {
  return (
    <article className="prose">
      <h2>Log Page</h2>
      <Image src="/doc/logs-table.png" alt="Screen-shot of the Log table" width={640} height={202} />
      <p>The Log page contains a paginated, filterable, sortable, customisable table showing all log entries for all
        records.</p>
      <a id="filter" /><h3>Filter Section</h3>
      <p>The filter section contains the standard Refresh, Clear and Column Selector controls as described{" "}
        <Link href="/doc/tables/#filter">here</Link>, and additionally:</p>
      <dl>
        <dt>Start Date</dt>
        <dd>Only log entries with a Timestamp on or after this date will be displayed.</dd>
        <dt>End Date</dt>
        <dd>Only log entries with a Timestamp or before this date will be displayed.</dd>
        <dt>Username</dt>
        <dd>Only log entries triggered by the User with this username will be displayed.</dd>
        <dt>Record Kind</dt>
        <dd>Only log entries pertaining to this type of record will be displayed.</dd>
        <dt>Record ID</dt>
        <dd>Only log entries pertaining to the record with this identifier will be displayed.</dd>
      </dl>
      <a id="columns" /><h3>Columns</h3>
      <p>The Log Page table supports the following columns:</p>
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
            <td>ID</td>
            <td>Digits</td>
            <td>No</td>
            <td>The unique identifier of the log entry</td>
          </tr>
          <tr>
            <td>Timestamp</td>
            <td>Date/time</td>
            <td>Yes</td>
            <td>The date/time at which the log entry was made</td>
          </tr>
          <tr>
            <td>User</td>
            <td>Username</td>
            <td>Yes</td>
            <td>The user who triggered the log entry</td>
          </tr>
          <tr>
            <td>Record Kind</td>
            <td>String</td>
            <td>Yes</td>
            <td>The kind of record to which the log entry pertains</td>
          </tr>
          <tr>
            <td>Record ID</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The identifier of the record to which the log entry pertains</td>
          </tr>
          <tr>
            <td>Linked Record Kind</td>
            <td>String</td>
            <td>Yes</td>
            <td>The kind of the other record linked/unlinked</td>
          </tr>
          <tr>
            <td>Linked Record ID</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The identifier of the other record linked/unlinked</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Log Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/logs-details.png" alt="Screen-shot of the Log details section" width={640} height={183} />
      <p>The Details section shows the following fields: Timestamp, Transaction Kind, Username, Record Kind, Record ID, Linked Record Kind, Linked Record ID.</p>
      <a id="log-dialog" /><h2>Log Dialog</h2>
      <Image src="/doc/logs-dialog.png" alt="Screen-shot of the Log dialog" width={640} height={335} />
      <p>The Log dialog contains a paginated, filterable, sortable, customisable table that shows all log entries for a
        specific record. There is no details section.</p>
      <h3>Filter Section</h3>
      <p>The filter section contains the standard Refresh, Clear and Column Selector controls as described&nbsp;
        <Link href="/doc/tables/#filter">here</Link>, and in addition the Start Date, End Date, Username, Transaction
        Kind controls as described{" "}<a href="#filter">above</a>.</p>
      <h3>Columns</h3>
      <p>The Log Dialog table supports the following columns: Timestamp, User, Transaction, Linked Record Kind, Linked Record ID, as described in{" "}<a href="#columns">Columns</a>{" "}above.</p>
      <h3>Paginator</h3>
      <p>The Log Dialog table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
    </article>
  )
}