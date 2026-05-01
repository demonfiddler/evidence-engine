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

import { MessageSquareQuoteIconEx } from "@/app/ui/icons"
import {
  BookOpenCheckIcon,
  Building2Icon,
  FileClockIcon,
  FolderTreeIcon,
  LibraryIcon,
  LinkIcon,
  MessagesSquareIcon,
  NewspaperIcon,
  ScrollTextIcon,
  UserIcon
} from "lucide-react"

export default function RecordsDocPage() {
  return (
    <article className="prose">
      <h2>Record Types</h2>
      <a id="linkable-record-types" /><h3>Linkable Record Types</h3>
      <p>Evidence Engine supports five main types of information record: Claim, Declaration, Person, Publication and Quotation,
        each of which can be linked to an extensible Topic hierarchy and cross-linked with linkable records of other
        types. Publications are supported by two further record types: Journal and Publisher.</p>
      <h4><BookOpenCheckIcon className="inline" />&nbsp;Claim</h4>
      <p>A Claim is an assertion of fact that can be supported by evidence of various kinds.</p>
      <h4><ScrollTextIcon className="inline" />&nbsp;Declaration</h4>
      <p>A Declaration is a public statement of consensus, support for certain views, and/or a call for certain actions.
        They are typically contrarian in nature, opposing some prevailing official narrative or orthodox position.
        There are three kinds: Declaration, Open Letter and Petition, though that distinction can be blurred.
        Declaration signatories can be represented in the database as Person records linked to the Declaration.</p>
      <h4><UserIcon className="inline" />&nbsp;Person</h4>
      <p>A Person record represents a subject matter expert who has publicly expressed contrarian views, by
        publishing information of a scientific or other evidential nature. The Persons in this database are typically
        eminent, highly qualified and may be recipients of prestigious awards.
        They include university professors, graduates at Ph.D. and higher levels, practising and credentialled professionals.
      </p>
      <h4><LibraryIcon className="inline" />&nbsp;Publication</h4>
      <p>A Publication represents material published in the form of books, papers and research studies in scientific journals,
        magazines and other periodicals. When a Publication appears in a Journal or other periodical or series, the Publication
        can make a direct reference to that Journal. It can also make a direct reference to the Publisher, which would either
        be the publisher of the Journal, or of the Publication itself in the case of books, etc.
        Publications can be of many kinds, which correspond to the allowed values of the&nbsp;
        <a href="https://en.wikipedia.org/wiki/RIS_(file_format)" target="_blank">RIS TY tag</a>.
        Publication authors can be represented in the database as Person records linked to the Publication record.</p>
      <h4><MessageSquareQuoteIconEx className="inline" />&nbsp;Quotation</h4>
      <p>A Quotation is a verbal or written statement or opinion made by an individual (the 'quotee'). The quotee can be modelled
        in the database as a Person record linked to the Quotation record.</p>
      <h4><FolderTreeIcon className="inline" />&nbsp;Topic</h4>
      <p>A Topic is a subject that can be linked with records of any of the aforementioned types. Topics can have sub-topics,
        to a maximum nesting depth of ten.</p>
      <a id="tracked-record-types" /><h3>Tracked Record Types</h3>
      <h4><NewspaperIcon className="inline" />&nbsp;Journal</h4>
      <p>A Journal is a periodical, typically scientific. It can hold a direct reference to its Publisher record in the database.</p>
      <h4><Building2Icon className="inline" />&nbsp;Publisher</h4>
      <p>A Publisher is an organisation or body that publishes scientific or other works. They include the big publishing houses,
        some of whom publish hundreds or thousands of journals, traditional book publishers, and universities and other academic
        research institutions.
      </p>
      <h4><LinkIcon className="inline" />&nbsp;Record Link</h4>
      <p>A Record Link represents an association between two records. Record Links are used indicate a claimant (the
        maker of a Claim), the fact that a Publication is evidence supporting a Claim, the signatories of a Declaration,
        the author of a Publication, the utterer of a Quotation, and so on.
      </p>
      <h4><MessagesSquareIcon className="inline" />&nbsp;Comment</h4>
      <p>Comments about a record, made by authenticated users. Such comments can be read by anyone.</p>
      <a id="other-record-types" /><h3>Other Record Types</h3>
      <h4><FileClockIcon className="inline" />&nbsp;Log</h4>
      <p>A Log is a timestamped history showing every change made to a record, who made it and when.
        Logs can be viewed by anyone.</p>
      <h2>Tracking Data</h2>
      <p>For all record types, the system supports the following table columns:</p>
      <table>
        <thead>
          <tr>
            <th>Field</th>
            <th>Type</th>
            <th>Visible by Default</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Identifier (ID)</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The immutable system-assigned unique identifier (a sequence of digits)</td>
          </tr>
          <tr>
            <td>Status</td>
            <td>Reference</td>
            <td>Yes</td>
            <td>Draft, Published, Suspended or Deleted</td>
          </tr>
          <tr>
            <td>Rating</td>
            <td>Digit 1..5</td>
            <td>Varies</td>
            <td>A five-star rating for the record</td>
          </tr>
          <tr>
            <td>Created</td>
            <td>Date/time</td>
            <td>No</td>
            <td>The date/time at which the record was created</td>
          </tr>
          <tr>
            <td>Created By</td>
            <td>Reference</td>
            <td>No</td>
            <td>The User who created the record</td>
          </tr>
          <tr>
            <td>Updated Date</td>
            <td>Date/time</td>
            <td>No</td>
            <td>The date/time at which the record was last updated</td>
          </tr>
          <tr>
            <td>Updated By</td>
            <td>Reference</td>
            <td>No</td>
            <td>The User who last updated the record</td>
          </tr>
        </tbody>
      </table>
      <p>and additionally in separate pages/dialogs:</p>
      <dl>
        <dt>Comments</dt><dd>as described under <a href="#tracked-record-types">Tracked Record Types</a></dd>
        <dt>Log</dt><dd>as described under <a href="#other-record-types">Other Record Types</a></dd>
      </dl>
   </article>
  )
}