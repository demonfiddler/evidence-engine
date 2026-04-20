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

export default function LoggingLevelsDocPage() {
  return (
    <article className="prose">
      <h2>Logging Levels Dialog</h2>
      <Image src="/doc/logging-levels.png" alt="Screen-shot of the Logging Levels dialog" width={640} height={401} />
      <p>The Logging Levels dialog provides the means to set various internal logging levels for diagnostic purposes.
        Most components of the system emit logging output as an aid to debugging and diagnostics. The Logging Levels
        dialog is activated by typing the <kbd>Ctrl+Alt+L</kbd> key combination and can be dismissed with the Escape
        key. To view the logging output, open the browser's console panel.
      </p>
      <p>There are ten logger types:</p>
      <dl>
        <dt>Components</dt>
        <dd>Used by React JSX application components</dd>
        <dt>Details</dt>
        <dd>Used by details sections of master-detail pages</dd>
        <dt>Dialogs</dt>
        <dd>Used by application dialogs</dd>
        <dt>Filters</dt>
        <dd>Used by table filter sections</dd>
        <dt>Hooks</dt>
        <dd>Used by React application hook code</dd>
        <dt>Layouts</dt>
        <dd>Used by Next.js App Router layout.tsx components</dd>
        <dt>Pages</dt>
        <dd>Used by Next.js App Router page.tsx components</dd>
        <dt>Tables</dt>
        <dd>Used by table-related application components</dd>
        <dt>Utilities</dt>
        <dd>Used by application-level utility code</dd>
      </dl>
      <p>Supported logging levels are:</p>
      <dl>
        <dt>trace</dt>
        <dd>The most detailed output, typically includes variable values</dd>
        <dt>debug</dt>
        <dd>Debugging output, typically at function call level</dd>
        <dt>info</dt>
        <dd>Informational output; this is the default setting for all loggers</dd>
        <dt>warn</dt>
        <dd>Warnings about unexpected conditions encountered</dd>
        <dt>error</dt>
        <dd>Serious errors that may need attention by a software engineer</dd>
        <dt>silent</dt>
        <dd>Logging entirely suppressed</dd>
      </dl>
    </article>
  )
}