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

export default function DashboardDocPage() {
  return (
    <article className="prose">
      <h2>Dashboard Page</h2>
      <Image src="/doc/dashboard.png" alt="Screen-shot of the Dashboard page" width={640} height={341} />
      <p>The Dashboard page displays detailed statistics on the overall state of the database.
        It has two sections:
      </p>
      <h3>Topic & Record Links</h3>
      <p>The top section takes the form of a table containing all the Topics, with columns to show the number of
        Claims, Declarations, Persons, Publications and Quotations linked to that Topic, and the total count of records
        linked to the Topic. Clicking the number in any of these 'count' cells takes you to the appropriate page, with a
        filter configured to display the matching records linked to the Topic in the clicked row. For authenticated
        users, the table can be filtered to show just Topics and linked records with a particular status. All users can
        switch between a flat Topic display and a hierarchical Topic display, where sub-Topics appear as child rows,
        recursively. There is also a refresh button to retrieve the latest statistics from the database.
      </p>
      <h3>Record Statistics</h3>
      <p>The bottom section is a series of large buttons showing the total number of Topics, Claims, Declarations,
        Persons, Publications and Quotations. Clicking a button takes you to the corresponding page where these records
        can be viewed in detail.</p>
    </article>
  )
}