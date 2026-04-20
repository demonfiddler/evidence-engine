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

export default function BackupDocPage() {
  return (
    <article className="prose">
      <h2>Backup &amp; Restore Page</h2>
      <Image src="/doc/backup.png" alt="Screen-shot of the Backup &amp; Restore page" width={640} height={383} />
      <p>The Backup &amp; Restore page supports the ability to download the contents of the database as a backup set or
        to restore the database from such a file. A backup set is a ZIP file containing compressed CSV files, one for
        each of the database tables in the backup.
      </p>
      <p>Backup scope can have the following values:</p>
      <dl>
        <dt>All</dt>
        <dd>Includes application data + static lookup tables. Restore completely replaces all existing content.</dd>
        <dt>Full</dt>
        <dd>Includes application data only. Restore completely replaces all existing content.</dd>
        <dt>Incremental</dt>
        <dd>Includes added or changed application data only. Restore adds/replaces only those records. Successive
        incremental backups form a numbered series starting with a full backup and can only be restored in order,
        starting with the full backup. The target database must not have been updated at any point from then on.</dd>
      </dl>
      <h3>Schema Version</h3>
      <p>The database includes a 'schema version', to track the evolution of its structure. A backup set includes the
        source schema version, and can only be restored to a target system with the same schema version.</p>
    </article>
  )
}