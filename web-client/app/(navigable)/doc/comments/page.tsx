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

import { PencilIcon, ReplyIcon, SearchIcon, SendHorizontalIcon } from "lucide-react"
import Image from "next/image"
import Link from "next/link"

export default function CommentsDocPage() {
  return (
    <article className="prose">
      <h2>Comments Page</h2>
      <Image src="/doc/comments-table.png" alt="Screen-shot of the Comments table" width={640} height={241} />
      <p>The Comments page contains a paginated, filterable, sortable table showing all comments for all records.</p>
      <a id="filter" /><h3>Filter Section</h3>
      <p>The filter section contains the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described <Link href="/doc/tables/#filter">here</Link>, and additionally:</p>
      <dl>
        <dt>Target Kind</dt>
        <dd>Only comments pertaining to this type of record will be displayed.</dd>
        <dt>Target ID</dt>
        <dd>Only comments pertaining to the record with this identifier will be displayed.</dd>
        <dt>Parent ID</dt>
        <dd>Only comments which are replies to the comment with this identifier will be displayed.</dd>
        <dt>Username</dt>
        <dd>Only comments posted by the User with this username will be displayed.</dd>
        <dt>Start Date</dt>
        <dd>Only comments created or updated on or after this date will be displayed.</dd>
        <dt>End Date</dt>
        <dd>Only comments created or updated on or before this date will be displayed.</dd>
      </dl>
      <a id="columns" /><h3>Columns</h3>
      <p>The Comments Page table supports the standard columns as described <a href="/doc/tables/#columns">here</a>,
        and additionally:</p>
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
            <td>Target Kind</td>
            <td>String</td>
            <td>Yes</td>
            <td>The kind of record to which the comment pertains</td>
          </tr>
          <tr>
            <td>Target ID</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The identifier of the record to which the comment pertains</td>
          </tr>
          <tr>
            <td>Parent ID</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The identifier of the comment to which this comment is a reply</td>
          </tr>
        </tbody>
      </table>
      <h3>Paginator</h3>
      <p>The Comments Page table is paginated, as described <a href="/doc/tables/#paginator">here</a>.</p>
      <h3>Details Section</h3>
      <Image src="/doc/comments-details.png" alt="Screen-shot of the Comment details section" width={640} height={215} />
      <p>The Details section contains the standard Tracking part as described
        {" "}<Link href="/doc/details/#tracking">here</Link>, and the comment details part shows:</p>
      <dl>
        <dt>Target</dt>
        <dd>Shows the target record type, identifier and text.</dd>
        <dt>Parent</dt>
        <dd>Shows the record type, identifier and text of the comment to which this is a reply.</dd>
        <dt>Text</dt>
        <dd>The body text of the comment.</dd>
      </dl>
      <a id="comments-dialog" /><h2>Comments Dialog</h2>
      <Image src="/doc/comments-dialog.png" alt="Screen-shot of the Comments dialog" width={240} height={363} />
      <p>The Comments dialog contains a scrollable, filterable list that appears on the right-hand edge of the browser
        window and shows all comments for a specific record. It is semi-modal and can be left open while interacting
        with the underlying page.</p>
      <h3>Filter Section</h3>
      <p>The filter section is toggled by the <SearchIcon className="inline" /> icon and contains
        the standard Search, Advanced, Status, Refresh and Clear controls as described&nbsp;
        <Link href="/doc/tables/#filter">here</Link>, and in addition the Username, Start Date and End Date controls as
        described{" "}<a href="#filter">above</a>.</p>
      <h3>Comments Section</h3>
      <p>The scrollable comments section shows all the comments represented as coloured rectangles. Each comment
        rectangle contains the comment text, the comment identifier and the time it was posted. There are also date
        stamps to group comments by date. There is a <ReplyIcon className="inline" /> reply icon that allows you to
        reply to that comment. The comment rectangle representing a reply includes the first few words of the original
        comment together with the name of the user who posted the original comment. Comments that you have posted remain
        <PencilIcon className="inline" /> editable for 48 hours following creation.</p>
      <h3>Comment Editor</h3>
      <p>The comment editor at the bottom is a text area with a <SendHorizontalIcon className="inline" /> Send button
        to post or update the comment. Needless to say, you can only edit your own comments.</p>
   </article>
  )
}