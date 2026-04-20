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

export default function RationaleDocPage() {
  return (
    <article className="prose">
      <h2>Rationale</h2>
      <p>Mainstream narratives on certain key topics are frequently distorted by powerful geopolitical and commercial
        vested interests. Scientists, researchers, health practitioners, commentators, journalists and public figures
        contradicting the official story are typically vilified, derided, cancelled, defunded, debanked, suspended or
        fired. Key human endeavours such as scientific research, education, journalism, policy making, governance and
        regulation are captured and controlled by these vested interests.</p>
      <p>Consequently on a global scale, truth, health, freedom and justice are under threat as never before.</p>
      <p>The <b>Evidence Engine</b> online database is designed to:</p>
      <ul>
        <li><b>Focus</b> on truth, health, justice and freedom;</li>
        <li><b>Curate</b> high-quality contrarian scientific and other evidence, making it readily accessible to
          researchers and activists;</li>
        <li><b>Refute</b> anti-truth, anti-human, anti-freedom mainstream narratives.</li>
      </ul>
      <p>The database details scientific claims, publications, declarations, quotations and leading scientists,
        organised within an extensible hierarchy of topics.</p>
      <p>Information in these categories is presented in searchable, sortable, pageable lists that can be exported
        to various formats.</p>
      <p>The lists can be linked in various ways to show the relationships between records in each information category.
      </p>
    </article>
  )
}