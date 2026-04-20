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

export default function SignInDocPage() {
  return (
    <article className="prose">
      <h2>Sign-in Dialog</h2>
      <Image src="/doc/sign-in.png" alt="Screen-shot of the Sign-in dialog" width={420} height={286} />
      <p>The 'Sign in' dialog allows you to authenticate your session. This unlocks access to the extended functionality
        available to authenticated users, and additional functionality according to your granted authorities. The dialog
        has two input fields:</p>
      <dl>
        <dt>Username</dt>
        <dd>Your username</dd>
        <dt>Password</dt>
        <dd>Your password</dd>
      </dl>
      <p>The 'Sign in' command button attempts to sign in with the supplied credentials. If an error occurs, it is
        reported inline in the dialog; otherwise, the dialog closes and all future interactions will be executed as
        authenticated, with any granted authorities in force. The authentication token expires after four hours,
        following which you should re-authenticate if you wish to continue editing.</p>
    </article>
  )
}