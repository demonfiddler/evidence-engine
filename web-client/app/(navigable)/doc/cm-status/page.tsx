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

export default function StatusDocPage() {
    return (
      <article className="prose">
        <h2>Record Status Lifecycle</h2>
        <p>When a record is first created, it has <b>Draft</b> status and is only visible to authenticated users. Once
          it is ready, it is promoted to <b>Published</b> status and thereby becomes publicly visible to unauthenticated
          users. If any doubt subsequently arises as to a record's suitability or veracity, it can be <b>Suspended</b>
          {" "}pending further investigation. If a record turns out to be unsuitable for public consumption, it can be <b>Deleted</b>.
          Deleted records are merely marked as such; they are not physically deleted from the database and can
          potentially be resurrected at some future date by re-publishing them. As with Draft records, Suspended and
          Deleted records are not visible to unauthenticated users.
        </p>
        <p>
        Records never exist in isolation: they are always part of a related set of records whose relationships are
        expressed as <Link href="/doc/cm-data-quality/#record-links">Record Links</Link>. Such related records are
        generally published once all of them are ready. This can be done <Link href="/doc/status/">manually</Link>
        {" "}or in batch mode using the <Link href="/doc/cm-cmdline/">Command Line Interface</Link>. In both cases
        the system audits the record to ensure that certain minimum quality criteria are met before publication is
        allowed.
        </p>
      </article>
    )
}