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

import { DownloadIcon } from "lucide-react"
import Image from "next/image"

export default function ExportDocPage() {
  return (
    <article className="prose">
      <h2><DownloadIcon className="inline" />&nbsp;Export Dialog</h2>
      <Image src="/doc/export.png" alt="Screen-shot of the Export dialog" width={420} height={420} />
      <p>The Export dialog allows you to download a table's contents in various formats. Your browser will download the
        file. The dialog provides the following options:</p>
      <dl>
        <dt>Format</dt>
        <dd>CSV, HTML, PDF, RIS (Publications only)</dd>
        <dt>Include</dt>
        <dd>Table, Details</dd>
        <dt>Pages</dt>
        <dd>Current or All pages</dd>
        <dt>Paper Size</dt>
        <dd>A3 or A4</dd>
        <dt>Orientation</dt>
        <dd>Portrait or Landscape</dd>
        <dt>Font Size</dt>
        <dd>8&ndash;16 Pt.</dd>
      </dl>
    </article>
  )
}