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
import Link from "next/link"

export default function FilterDocPage() {
  return (
    <article className="prose">
      <h2>Master Filter Section</h2>
      <Image src="/doc/filter.png" alt="Screen-shot of master filter section" width={640} height={184} />
      <p>Pages for <Link href="/doc/records/#linkable-record-types">linkable record types</Link>{" "}
        (<Link href="/doc/claims/">Claims</Link>, <Link href="/doc/declarations/">Declarations</Link>,
        <Link href="/doc/persons/">Persons</Link>, <Link href="/doc/publications/">Publications</Link> and{" "}
        <Link href="/doc/quotations/">Quotations</Link>) include a collapsible section that provides the ability to
        filter all linkable record tables in two ways.</p>
      <h3>Topic Link</h3>
      <p>The Topic Link filter allows you to select a Topic of interest, so that lists of linkable records only
        include only those records which are linked with the selected Topic. If the 'Include records linked to sub-topics'
        checkbox is ticked, the list will also include records which are linked to any sub-topics of the selected topic,
        at any nesting depth.</p>
      <h3>Master Record Link</h3>
      <p>The Master Record Link filter allows you to specify a record type of interest, so that when a record of that type
        is selected on the corresponding page, other lists of linkable records include only those records which are linked
        with the selected 'Master Record'. When such a record is selected in the table on the corresponding page, the identifier
        and some text from the selected record are displayed in Master Record Link section. The 'Go to' button will take you
        to the selected Master Record.</p>
      <h3>Master Checkbox</h3>
      <p>When the 'Show only linked records' checkbox is ticked, both Topic and Master Record Link filters are active, and
        lists of linkable records include only those records which are linked with the selected Topic and/or Master Record.
        When the checkbox is unticked, both filters are inactive. The 'Show only linked records' tick status is reflected
        in the Filter by Links section even when it is collapsed.</p>
    </article>
  )
}