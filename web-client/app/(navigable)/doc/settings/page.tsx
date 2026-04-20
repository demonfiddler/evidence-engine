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

export default function SettingsDocPage() {
  return (
    <article className="prose">
      <h2>Settings Dialog</h2>
      <Image src="/doc/settings.png" alt="Screen-shot of the Settings dialog" width={420} height={206} />
      <p>The 'Settings' dialog allows you to reset your preferences. Such preferences include:</p>
      <ul>
        <li>Master Topic/record filter</li>
        <li>Master Topic/record filter section visibility</li>
        <li>Tracking & Linking section visibility</li>
        <li>All record-type-specific table:
          <ul>
            <li>filters</li>
            <li>column sort settings</li>
            <li>pagination settings</li>
          </ul>
        </li>
      </ul>
      <p>There are two command buttons:</p>
      <dl>
        <dt>Clear</dt>
        <dd>Resets all preferences to their default values and closes the dialog</dd>
        <dt>Close</dt>
        <dd>Closes the dialog without changing your preferences</dd>
      </dl>
    </article>
  )
}