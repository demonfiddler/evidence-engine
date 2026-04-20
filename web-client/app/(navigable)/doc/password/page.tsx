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

export default function PasswordDocPage() {
  return (
    <article className="prose">
      <h2>Password Dialog</h2>
      <Image src="/doc/password.png" alt="Screen-shot of the Password dialog" width={426} height={318} />
      <p>The 'Password' dialog allows you to change your password. It has two fields:</p>
      <dl>
        <dt>New password</dt>
        <dd>The new password (required). Must be at least eight characters long and contain an uppercase letter, a
          lowercase letter, a digit and a special symbol (!"£$%^&*()_-+= etc.")</dd>
        <dt>Re-enter password</dt>
        <dd>Must match 'New password' (required)</dd>
      </dl>
      <p>There are two command buttons:</p>
      <dl>
        <dt>Cancel</dt>
        <dd>Discards pending changes and closes the dialog</dd>
        <dt>Save</dt>
        <dd>Updates your password in the database and closes the dialog</dd>
      </dl>
    </article>
  )
}