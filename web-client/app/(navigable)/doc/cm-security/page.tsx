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

import Link from "next/link"

export default function SecurityDocPage() {
  return (
    <article className="prose">
      <a id="security" /><h2>Security</h2>
      <p>The Evidence Engine web interface can be freely used in read-only mode by anyone, without having to sign in.
        There are in addition various content management capabilities that are only accessible to authenticated users
        with the requisite authorities.
      </p>
      <a id="authentication" /><h3>Authentication</h3>
      <p>Authentication is the process of <Link href="/doc/sign-in/">signing into</Link> the system with{" "}
        <b>credentials</b> consisting of a{" "}<b>username</b>{" "}and a {" "}<b>password</b>. Such credentials are
        issued by the system administrator on an individual basis&mdash;there is no online sign-up / register facility,
        because the ability to change the contents of the database must be strictly limited to people who are known
        personally and trusted by the administrators.
      </p>
      <a id="authorisation" /><h3>Authorisation</h3>
      <p>Authorisation is the process of controlling which users are permitted to perform which tasks. It is achieved by
        {" "}<Link href="/doc/security/"><b>granting authorities</b></Link>{" "}to users or (more usually) adding them as
        members of one or more groups that have been granted the requisite authorities. An authenticated user can view
        their granted authorities in the <Link href="/doc/profile/">Profile Dialog</Link>.
      </p>
      <a id="authorities" /><h4>Authorities</h4>
      <p>The following grantable authorities exist:</p>
      <dl>
        <dt>Administer (ADM)</dt>
        <dd>The ability to administer the system, manage users and groups, grant authorities and restore the database from a backup set.</dd>
        <dt>Change (CHG)</dt>
        <dd>The ability to change the status of existing records.</dd>
        <dt>Comment (COM)</dt>
        <dd>The ability to comment on existing records.</dd>
        <dt>Create (CRE)</dt>
        <dd>The ability to create new records.</dd>
        <dt>Link (LNK)</dt>
        <dd>The ability to link or unlink existing records.</dd>
        <dt>Read (REA)</dt>
        <dd>The ability to read existing records*.</dd>
        <dt>Update (UPD)</dt>
        <dd>The ability to edit existing records.</dd>
        <dt>Upload (UPL)</dt>
        <dd>The ability to create new records by mass-importing them from a file.</dd>
      </dl>
      <br/>
      <span className="text-sm">
        * Read permission is the minimum authority that can be granted. It is necessary because Spring Security treats a
        security principal with no granted authorities as an unauthenticated user.
      </span>
    </article>
  )
}