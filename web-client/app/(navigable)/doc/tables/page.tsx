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

import { ChevronsUpDownIcon, Columns3Icon, DownloadIcon, GripVerticalIcon, Tally1Icon, UploadIcon, XIcon } from "lucide-react"
import Image from "next/image"
import Link from "next/link"

export default function TablesDocPage() {
  return (
    <article className="prose">
      <h2>Tables</h2>
      <Image src="/doc/table.png" alt="Screen-shot of a typical table" width={640} height={274} />
      <h3>Header</h3>
      <p>The table header provides list filtering capabilities, export, import and column selection features.</p>
      <a id="filter" /><h4>Filter</h4>
      <p>The table filter is tailored to the kind of record displayed in the table. The image above shows the filter for
      trackable records such as Journals and Publishers, and linkable records such as Claims, Declarations, Persons,
      Publications and Quotations. The filter acts in conjunction with the <Link href="/doc/filter/">Master Filter</Link>,
      if that is active. The table filter includes:</p>
      <h5>Status selector</h5>
      <p>Only records with the selected status (Draft, Published, Suspended or Deleted) will be displayed. This
        component is only visible to authenticated (signed-in) users, as unauthenticated users are only permitted to see
        Published records.</p>
      <h5>Text Search Input</h5>
      <p>Filters the list to show only records containing the specified text. This performs a case-insensitive match
        against all text fields, only matching whole words unless 'Advanced' is checked. The Advanced ('Boolean mode')
        text search syntax supports more powerful text searching. See
        {" "}<Link href="https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode" target="_blank">here</Link>
        {" "}for full details. The value can be cleared by clicking the built-in <XIcon className="inline" />Clear
        button or typing the <kbd>Escape</kbd> key.</p>
      <h5>Record ID input</h5>
      <p>Filters the list to show only the one record with the specified identifier. While this field is set, other filter
        settings are preserved but unused. This capability is also used internally by the application for navigation.
        The value can be cleared by clicking the built-in <XIcon className="inline" />Clear button or typing the
        <kbd>Escape</kbd> key.</p>
      <h5>Refresh button</h5>
      <p>Re-queries the database to retrieve the latest results. This can be useful if frequent updates are in progress.
      </p>
      <h5>Clear button</h5>
      <p>Clears all filter settings (apart from the <Link href="/doc/filter/">master filters</Link> at the top of the
        page).</p>
      <h4><DownloadIcon className="inline" />&nbsp;Export/Download</h4>
      <p>Exports the list to a downloadable file. Various formats are supported.
        Not all options are available for all formats.</p>
      <ul>
        <li>Format: CSV, HTML, PDF (all record types) and RIS (Publications only)</li>
        <li>Include: Table and/or Details</li>
        <li>Pages: Current or All</li>
        <li>Paper Size: A4 or A3</li>
        <li>Orientation: Portrait or Landscape</li>
        <li>Font Size: 8 - 16 Pt</li>
      </ul>
      <h4><UploadIcon className="inline" />&nbsp;Import</h4>
      <p>Publications support import/upload from a RIS-format text file.</p>
      <h4><Columns3Icon className="inline" />&nbsp;Column Selector</h4>
      <p>Selects the columns to display in the table. Some columns are common to all tracked record types, such as
        ID, Status, Rating, Created, Created By, Updated, Updated By, and other columns are specific to the type of
        record displayed by the table. By default, not all available columns are displayed, as this can make the table
        too wide for easy use.
      </p>
      <h3>Column Headers</h3>
      <p>Each column selected for display has its own column header, which supports various operations:</p>
      <ul>
        <li>Ordering - columns can be reordered by dragging and dropping the <GripVerticalIcon className="inline" />
          grab handle to a new location in the table header</li>
        <li>Sizing - columns can be reordered by dragging the flyover{" "}<Tally1Icon className="inline stroke-4" />
          resize handle at the right of the column header</li>
        <li>Sorting - the table can be sorted on a given column by clicking the<ChevronsUpDownIcon className="inline" />
          up-down chevron to the right of that column's header text. The icon changes to reflect whether the column is
          unsorted, sorted ascending or sorted descending. Clicking repeatedly cycles between the three sort options.
          Use Shift+Click to sort on multiple columns.
        </li>
      </ul>
      <h3>Body</h3>
      <p>The table body shows one record per row, with the cell contents formatted for legibility.</p>
      <h4>Record Selector</h4>
      <p>The first column shows a radio button for each row. Clicking the radio button selects the corresponding record.
        The selected record is displayed in full in the <Link href="/doc/detail/">Details Section</Link> below the table.
      </p>
      <a id="columns" /><h3>Columns</h3>
      <p>All tables (apart from Log) support the following columns in addition to their record-type-specific columns:</p>
      <table>
        <thead>
          <tr>
            <th>Field</th>
            <th>Type</th>
            <th>Default Visible</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Select</td>
            <td>Yes/No</td>
            <td>Yes</td>
            <td>Radio button to select the record in that row</td>
          </tr>
          <tr>
            <td>ID</td>
            <td>Digits</td>
            <td>Yes</td>
            <td>The unique record identifier</td>
          </tr>
          <tr>
            <td>Status</td>
            <td>Reference</td>
            <td>Yes</td>
            <td>The record status</td>
          </tr>
          <tr>
            <td>Rating</td>
            <td>1..5</td>
            <td>Varies</td>
            <td>The record star rating</td>
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
            <td>Updated</td>
            <td>Date/time</td>
            <td>No</td>
            <td>The date/time at which the record was updated</td>
          </tr>
          <tr>
            <td>Updated By</td>
            <td>Reference</td>
            <td>No</td>
            <td>The User who last updated the record</td>
          </tr>
          <tr>
            <td>Actions</td>
            <td>Menu</td>
            <td>Yes</td>
            <td>A popup menu with commands to show the Comments or Log for the record</td>
          </tr>
        </tbody>
      </table>
      <h4>Actions</h4>
      <p>The last column displays a popup menu that allows you to view the comments or log for the record.</p>
      <a id="paginator" /><h3>Paginator</h3>
      <Image src="/doc/paginator.png" alt="Screen-shot of a table paginator" width={640} height={42} />
      <p>The table is paginated for reasons of performance and ease of use.</p>
      <h4>Page Size</h4>
      <p>Specifies how many records to show per page: 5, 10, 20, 50 or 100.</p>
      <h4>Page Number</h4>
      <p>Show the current page number. By typing in this field you can navigate to a specific page in the table.</p>
      <h4>First/Previous/Next/Last Buttons</h4>
      <p>Navigates to the corresponding page in the table.</p>
    </article>
  )
}