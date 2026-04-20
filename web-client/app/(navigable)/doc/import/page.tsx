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

import { UploadIcon } from "lucide-react"
import Image from "next/image"
import Link from "next/link"

export default function ImportDocPage() {
  return (
    <article className="prose">
      <h2><UploadIcon className="inline" />&nbsp;Import Dialog</h2>
      <Image src="/doc/import.png" alt="Screen-shot of the Import dialog" width={519} height={809} />
      <p>The Import dialog allows you to upload a table's contents in various formats. Your browser will upload the
        file. The dialog provides the following options:</p>
      <dl>
        <dt>Upload a file</dt>
        <dd>RIS (Publications only). Click to open your operating system's File Chooser dialog.</dd>
        <dt>Link imported records to</dt>
        <dd>Master Topic and/or Master Record, depending on which are set in the{" "}
          <Link href="/docs/filter/">Master Filter</Link>. The chechboxes are disabled if their corresponding filter is
          unset.</dd>
      </dl>
      <p>The following command buttons are present:</p>
      <dl>
        <dt>Import</dt>
        <dd>Import the file and provide an import summary</dd>
        <dt>Copy</dt>
        <dd>Copy the Import command's output for later analysis</dd>
        <dt>Close</dt>
        <dd>Close the Import dialog</dd>
      </dl>
    </article>
  )
}