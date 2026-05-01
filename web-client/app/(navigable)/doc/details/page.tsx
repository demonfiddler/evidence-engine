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

import { ArrowRightIcon, RotateCwIcon } from "lucide-react"
import Image from "next/image"
import Link from "next/link"

export default function DetailsDocPage() {
  return (
    <article className="prose">
      <h2>Details Section</h2>
      <Image src="/doc/details.png" alt="Screen-shot of a typical details section" width={640} height={390} />
      <p>The Details section has three parts: a collapsible 'Tracking & Linking' section containing the{" "}
        <Link href="#tracking">Tracking</Link> and <Link href="#linking">Linking</Link> parts, and a third part labelled
        {" "}<Link href="#details">Details</Link>.</p>
      <a id="tracking" /><h3>Tracking</h3>
      <p>The Tracking section displays the following information. All fields are read-only and immutable.</p>
      <dl>
        <dt>Database ID</dt>
        <dd>The record's unique system-assigned identifier.</dd>
        <dt>Status</dt>
        <dd>The record status: Draft, Published, Suspended or Deleted</dd>
        <dt>Rating</dt>
        <dd>The record quality rating: 1 - 5 stars or blank. Admittedly somewhat arbitrary and subjective, at least until we formalise a ranking algorithm.</dd>
        <dt>Created by</dt>
        <dd>The username of the User who created the record</dd>
        <dt>Created on</dt>
        <dd>The date/time when the record was created</dd>
        <dt>Updated by</dt>
        <dd>The username of the User who last updated the record</dd>
        <dt>Updated on</dt>
        <dd>The date/time when the record was last updated</dd>
      </dl>
      <a id="tracking-commands" /><h4>Tracking Commands</h4>
      <p>The following command buttons are present:</p>
      <dl>
        <dt>Comments</dt>
        <dd>Opens the <Link href="/doc/comments/#comments-dialog">Comments</Link> Dialog to show any comments associated
          with the record</dd>
        <dt>Status</dt>
        <dd>Presents a popup menu for changing the record Status</dd>
        <dt>Log</dt>
        <dd>Opens the <Link href="/doc/log/#log-dialog">Log</Link> Dialog to show the Log entries associated with the record</dd>
      </dl>
      <a id="linking" /><h3>Linking</h3>
      <p>The Linking section displays information about Record Links involving the selected record. It is only displayed
        for <Link href="/doc/records/#linkable-record-types">Linkable records</Link> (Claims, Declarations, Persons,
        Publications, Quotations and Topics). It displays the following information:</p>
      <dl>
        <dt>Record links</dt>
        <dd>A searchable drop-down list of other records linked with the contextual record. The
          {" "}<ArrowRightIcon className="inline" />{" "}right arrow takes you to the page containing the other record and the
          {" "}<RotateCwIcon className="inline" />{" "}refresh button refreshes the list from the database.</dd>
        <dt>Link ID</dt>
        <dd>The unique identifier of the record link.</dd>
        <dt>Status</dt>
        <dd>The status of the record link.</dd>
        <dt>Created by</dt>
        <dd>The username of the User who created the record link.</dd>
        <dt>Created on</dt>
        <dd>The date/time when the record link was created.</dd>
        <dt>Updated by</dt>
        <dd>The username of the User who last updated the record link.</dd>
        <dt>Location(s) in this record</dt>
        <dd>Location(s) within this record that pertain to the record link, e.g., page or chapter number.</dd>
        <dt>Location(s) in other record</dt>
        <dd>Location(s) within the other record that pertain to the record link, e.g., page or chapter number.</dd>
      </dl>
      <a id="linking-commands" /><h4>Linking Commands</h4>
      <p>The following command buttons are present:</p>
      <dl>
        <dt>Manage</dt>
        <dd>Opens the <Link href="/doc/status/">Link Manager</Link> Dialog to manage the links associated with the record</dd>
        <dt>Log</dt>
        <dd>Opens the <Link href="/doc/log/">Log</Link> Dialog to show the Log entries associated with the record link</dd>
      </dl>
      <a id="details" /><h3>Details</h3>
      <p>All pages showing records include a form containing the record-type-specific fields. The form is used to
        display field values in read-only 'view' mode, and in 'edit' mode to create new records and edit existing ones.
        Some fields are constrained as to the kind of value they can accept, and while creating or editing show a red
        border and error message when their field valiation fails.</p>
      <a id="form-commands" /><h4>Form Commands</h4>
      <p>The following command buttons are present:</p>
      <dl>
        <dt>New/Cancel</dt>
        <dd>Create a new record (view mode) OR discard pending changes (create/edit mode). Requires authentication and
          'Create' authority.</dd>
        <dt>Edit/Save</dt>
        <dd>Edit the selected record (view mode) OR save pending changes (create/edit mode). Requires authentication and
          'Update' authority.</dd>
        <dt>Delete</dt>
        <dd>Delete the selected record (view mode) OR disabled (create/edit mode). Requires authentication and 'Delete' authority.</dd>
      </dl>
   </article>
  )
}