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

import { CheckIcon } from "lucide-react"
import Link from "next/link"

export default function DataQualityDocPage() {
    return (
      <article className="prose">
        <a id="data-quality" /><h2>Data Quality</h2>
        <p>Evidence Engine is designed to be a high-quality research tool for curating scientific and other evidence.
          However, as always, the quality of the data determines the degree to which that goal is attained.
          &quot;Garbage in&mdash;garbage out&quot;, as the old computer science adage goes. The database holds a
          variety of fields for each record type, and record links reflecting the relationships between the records.
          These attributes are the primary determinants of data quality, so it is incumbent upon contributors to ensure
          that fields and record links are as complete and accurate as possible before
          {" "}<Link href="/doc/status/">publishing</Link> a record.
        </p>
        <a id="fields" /><h3>Fields</h3>
        <p>Every record kind supports a set of fields chosen for their value to researchers. The fields help to identify
          the record uniquely, to verify its authenticity, and to express the record's relevance and meaning. Every
          effort should be made to ensure that the fields are as complete and accurate as possible. Some fields are
          required and cannot be left blank, but the optional fields should also be filled out if at all possible.
        </p>
        <a id="record-links" /><h3>Record Links</h3>
        <p>Links between records of different kinds reflect the relationships between records. For example, a
          Publication makes certain Claims (represented as Publication-Claim links) and has authors (represented as
          Publication-Person links). Claims, in turn, pertain to Topics (represented as Claim-Topic links).
          Declarations have signatories (represented as Declaration-Person links). The table below shows the full
          set of supported linkages. Record Links are many-to-many relationships: that is to say, a record of one kind
          can have many links to records of another kind, and vice-versa, though only one link between any particular
          pair of records is allowed.
        </p>
        <a id="topic-links" /><h4>Topic Links</h4>
        <p>Topic Links are a special case because of topics can have sub-topics, which can have sub-sub-topics, up to a
          maximum nesting depth of ten. A record can be linked to multiple <b>unrelated</b> topics but it cannot be
          linked to multiple topics which are ancestors/descendants of each other.
        </p>
        <p>The following table shows which links are allowed between pairs of records of the linkable record kinds.</p>
        <table>
          <caption className="caption-bottom">Supported Record Links</caption>
          <colgroup>
            <col span={1} style={{}} />
            <col span={6} style={{}} />
          </colgroup>
          <thead>
            <tr>
              <th className="align-top" rowSpan={2}>TO Record Kind</th>
              <th className="text-center" colSpan={6}>FROM Record Kind</th>
            </tr>
            <tr>
              <th>Claim</th>
              <th>Declaration</th>
              <th>Person</th>
              <th>Publication</th>
              <th>Quotation</th>
              <th>Topic</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th>Claim</th>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
            </tr>
            <tr>
              <th>Declaration</th>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
            </tr>
            <tr>
              <th>Person</th>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
            </tr>
            <tr>
              <th>Publication</th>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td></td>
              <td></td>
              <td></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
            </tr>
            <tr>
              <th>Quotation</th>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
              <td></td>
              <td></td>
              <td></td>
              <td><CheckIcon className="w-4 h-4 mx-auto stroke-4 stroke-green-600" /></td>
            </tr>
          </tbody>
        </table>
      </article>
    )
}