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

export default function StatusDocPage() {
  return (
    <article className="prose">
      <h2>Status / Link Manager Dialog</h2>
      <p>The Status / Link Manager dialog provides the means to manage record links and to publish records. It provides
        guidance on record quality and completeness, and enforces minimum criteria for publication readiness. The dialog
        header identifies the <i>contextual record</i>, the one currently selected in the table of the underlying page
        from which the dialog was opened. The dialog footer includes a statement of the record's current status and
        eligility for publication, along with a set of command buttons:</p>
        <dl>
          <dt>Previous</dt>
          <dd>Shows the previous tab in the dialog</dd>
          <dt>Close</dt>
          <dd>Closes the dialog</dd>
          <dt>Publish</dt>
          <dd>Publishes the record, disabled if ineligible.</dd>
          <dt>Next</dt>
          <dd>Shows the next tab in the dialog</dd>
        </dl>
      <p>The dialog has three tabs: <a href="#field-audit">Field Audit</a>,{" "}
        <a href="#link-audit">Link Audit</a> and <a href="#link-manager">Link Manager</a>, described in the paragraphs
        below.</p>
      <a id="field-audit" /><h3>Field Audit Tab</h3>
      <Image src="/doc/status-field-audit.png" alt="Screen-shot of the Field Audit tab" width={640} height={400} />
      <p>The Field Audit tab shows a table with the results of the Field Audit operation, which checks the record's
        field values against a set of rules and highlights any issues. The table has the following columns:</p>
      <dl>
        <dt>Field Name</dt>
        <dd>The name of the field being checked</dd>
        <dt>Rule</dt>
        <dd>The rule kind: 'suggested', 'required' or 'any of'. 'Suggested' rules <i>should</i> be honoured if possible,
          whereas 'required' rules <i>must</i> be honoured. 'Any of' represents a sub-group of rules, at least one of
          which should pass.</dd>
        <dt>Severity</dt>
        <dd>The rule severity: 'info', 'warning' or 'error'. 'Info'-level rules are purely informational and
          'warning'-level rules express a strong recommendation, whereas 'error'-level rules will prevent record
          publication if violated.
        </dd>
        <dt>Check</dt>
        <dd>A textual description of what the rule expects to find</dd>
        <dt>Result</dt>
        <dd>The result of the check: 'Pass' or 'Fail'.</dd>
      </dl>
      <p>The table footer shows the overall result across all rules. Note that only 'error'-level failures on 'required'
        rules cause an overall 'Fail' result, but in the interests of record completeness, consistency and overall
        quality, you should aim for as many 'Pass' results as possible.</p>
      <a id="link-audit" /><h3>Link Audit Tab</h3>
      <Image src="/doc/status-link-audit.png" alt="Screen-shot of the Link Audit tab" width={640} height={400} />
      <p>The Link Audit tab shows a table with the results of the Link Audit operation, which checks the record's
        links against a set of rules and highlights any issues. The table has the following columns:</p>
      <dl>
        <dt>Linked Record Kind</dt>
        <dd>The kind of record to which the contextual record should have links</dd>
        <dt>Rule</dt>
        <dd>The rule kind: 'suggested', 'required' or 'any of'. 'Suggested' rules <i>should</i> be honoured if possible,
          whereas 'required' rules <i>must</i> be honoured. 'Any of' represents a sub-group of rules, at least one of
          which should pass.</dd>
        <dt>Minimum</dt>
        <dd>The minimum number of such links that must be present</dd>
        <dt>Actual</dt>
        <dd>The number of such links actually present</dd>
        <dt>Result</dt>
        <dd>The result of the check: 'Pass' or 'Fail'</dd>
        <dt>Action</dt>
        <dd>The 'New...' button activates the Link Manager tab with the Linked Record Kind selected, ready to create new
          links</dd>
      </dl>
      <p></p>
      <a id="link-manager" /><h3>Link Manager Tab</h3>
      <Image src="/doc/status-link-manager.png" alt="Screen-shot of the Link Manager tab" width={640} height={400} />
      <p>The Link Manager tab provides the means to manage the links associated with the contextual record. It has
        several field groups:</p>
      <h4>Links with</h4>
      <p>This field group contains a radio group showing the record types with which the contextual record can be
        linked. When a record type is selected (the 'other record type'), the 'Search for ... to link' and
        'Existing ... links' sections refresh to show unlinked and already-linked records of the other record type.</p>
      <h4>Search for ... to link</h4>
      <p>This field group supports searching for as-yet unlinked records of the other record type. There is a filter
        section containing the standard Status, Search, Advanced, Record ID, Refresh and Clear controls as
        described{" "}<Link href="/doc/tables/#filter">here</Link> and additionally:</p>
      <dl>
        <dt>Fuzzy</dt>
        <dd>Performs a fuzzy search. Guesses which other records might need linking to the contextual record based on
          the value(s) in some field of the contextual record and a corresponding field in the other record. The value
          of this contextual record's fuzzy-match field is displayed in 'Fuzzy match on' to the right. Only such
          'fuzzily-matching' other records will be displayed. 'Authors' and 'Author' is one possible pairing of
          fuzzily-matchable fields.</dd>
      </dl>
      <p>Other records matching the filter criteria are displayed in a drop-down combobox, which is itself searchable.
        When such an 'other record' is selected, this enables the 'Link' command button to the right of the combobox,
        which allows you to create a new link between the contextual record and the other record.
        The 'other records' combobox is paginated, as described <a href="/doc/tables/#paginator">here</a>. This is done
        to avoid the long delays that would otherwise result when no filter is active, causing{" "}<b>all</b> the other
        records to be fetched from the database (which could run into thousands).</p>
      <h4>Existing ... links</h4>
      <p>The 'Existing ... links' field group contains a drop-down combobox listing all the other records of the
        selected type that are already linked with the contextual record. The combobox is itself searchable to make it
        easier to locate a link of interest. When such an existing link is selected, its field values are displayed in
        the 'Link Details' field group, and also enables the 'Edit' command button to the right of the combobox.
        The Edit command makes the two 'Location(s) in ... record' inputs editable&mdash;see below.
      </p>
      <h4>Fuzzy match on</h4>
      <p>This field group is labelled according to the fuzzy match field in the contextual record type, and shows the
        value of that field. If appropriate, a word can be double-clicked to select it, then drag-dropped into the
        'other records' combobox or even the filter's Search input.
      </p>
      <h4>Link Details</h4>
      <p>The 'Link Details' field group show the field values of the selected existing link. The fields are read-only
        unless creating a new record link or editing an existing one.</p>
      <p>The Link Details field group contains the following command buttons:</p>
      <dl>
        <dt>Save</dt>
        <dd>Saves pending changes to a new or existing record link</dd>
        <dt>Cancel</dt>
        <dd>Discards pending changes and reverts to read-only 'view' mode</dd>
        <dt>Relink</dt>
        <dd>Changes the record link to target a different other record selected in the 'other records' combobox. The
          'Location(s) in other record' field, if set, should be amended accordingly (the existing value would likely
          not make sense applied to the new target record).</dd>
        <dt>Unlink</dt>
        <dd>Deletes the record link. Internally this just sets the record link's status to 'Deleted' so that it is no
          longer visible to unauthenticated users.
        </dd>
      </dl>
    </article>
  )
}