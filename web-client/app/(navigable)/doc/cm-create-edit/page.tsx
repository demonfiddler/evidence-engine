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

export default function CreateEditDocPage() {
  return (
    <article className="prose">
      <h2>Create and Editing Records</h2>
      <p>The Evidence Engine web interface supports the creation of new records and the editing of existing records by
        authenticated users with the <Link href="/doc/cm-security#authorities">requisite authorities</Link> (permissions).
        To create new records you must have <b>Create</b> authority. To edit existing records you must have <b>Update</b>
        {" "}authority. See <Link href="/doc/details/#form-commands">Form Commands</Link> for further information.
      </p>
    </article>
  )
}