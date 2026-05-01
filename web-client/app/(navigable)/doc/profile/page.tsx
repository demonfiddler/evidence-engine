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

export default function ProfileDocPage() {
  return (
    <article className="prose">
      <h2>Profile Dialog</h2>
      <Image src="/doc/profile.png" alt="Screen-shot of the Profile dialog" width={512} height={850} />
      <p>The 'Profile' dialog allows you to manage the following user profile settings:</p>
      <dl>
        <dt>User ID</dt>
        <dd>Your user ID (read-only)</dd>
        <dt>Username</dt>
        <dd>Your username (read-only)</dd>
        <dt>First name</dt>
        <dd>Your first/given name (required)</dd>
        <dt>Last name</dt>
        <dd>Your last/surname (required)</dd>
        <dt>Email</dt>
        <dd>Your email address (required)</dd>
        <dt>Country</dt>
        <dd>Your country of residence (required)</dd>
        <dt>Notes</dt>
        <dd>Free-form notes about your background, education and qualifications, accomplishments, activities, etc.</dd>
        <dt>Granted Authorities</dt>
        <dd>The authorities (permissions) granted to you, either directly or via group memberships (read-only)</dd>
      </dl>
    </article>
  )
}