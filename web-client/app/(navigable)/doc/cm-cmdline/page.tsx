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

import Link from "next/link"

export default function StatusDocPage() {
    return (
      <article className="prose">
        <h2>Command Line Interface (CLI)</h2>
        <p>Evidence Engine has a command line interface that supports various bulk update operations.</p>
        <a id="publish" /><h3>Publish Records</h3>
        <p>Publishes all records of the specified kinds, optionally filtered on status and Topic link.</p>
        <a id="normalize-links" /><h3>Normalize Record Links</h3>
        <p>Normalizes Publication and/or Quotation Topic links based on linked Claim Topics, and/or Person Claim and
          Topic links based on linked Publication Claims and Topics. So, if a Publication is linked to certain Claims,
          which in turn are linked to certain Topics, normalization consists of ensuring that the Publication has links
          to the Topics that are linked to the Publication's Claims, and similarly for Quotations. In the case of
          Persons, normalization consists of ensuring that the Person has links to the same Claims and Topics as are
          linked to the Publications that Person has authored. This ensures a consistent, coherent view of the links
          between Persons, Publications, Quotations, Claims and Topics.
        </p>
        <a id="normalize-publications" /><h3>Normalize Publication Records</h3>
        <p>Normalizes Publications to ensure that if a Journal is set for the Publication, that the Publication's
          publisher is set to the same Publisher as that of the Journal.
        </p>
        <a id="load-csv" /><h3>Load Records from CSV</h3>
        <p>Loads Declarations, Persons, Publications or Quotations from a CSV file.</p>
        <a id="syntax" /><h3>Syntax</h3>
        <code>java -jar ee-client.jar OPTIONS [FILE]</code>
        <a id="options" /><h3>Options</h3>
        <p>The following table describes the options that can be passed to the CLI. Some options which take arguments
          use <Link href="#abbreviations">abbreviations</Link>.
        </p>
        <table>
          <thead>
            <tr>
              <th>Short</th>
              <th>Long</th>
              <th>Arguments</th>
              <th>Compatible Options</th>
              <th>Description</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>-d</td>
              <td>--dry-run</td>
              <td>(no)</td>
              <td>-hklu</td>
              <td>Dry run&mdash;no database updates.</td>
            </tr>
            <tr>
              <td>-E</td>
              <td>--endpoint</td>
              <td>URL</td>
              <td>-hklu</td>
              <td>GraphQL API endpoint URL.</td>
            </tr>
            <tr>
              <td>-f</td>
              <td>--force</td>
              <td>(no)</td>
              <td>-u</td>
              <td>Force update (overwrite existing value).</td>
            </tr>
            <tr>
              <td>-h</td>
              <td>--publish</td>
              <td>CLA, COM, DEC, JOU, LNK, PER, PUB, PBR, QUO, TOP</td>
              <td>-dpst</td>
              <td>Publish records of the specified types, with optional status (-s) and topic (-t) filter. Pass a comma-separated list (no spaces). -t option only applies to CLA, DEC, PER, PUB, QUO.</td>
            </tr>
            <tr>
              <td>-k</td>
              <td>--link</td>
              <td>PUB, QUO, PER</td>
              <td>-dpst</td>
              <td>Link Publications (PUB) / Quotations (QUO) to Topics and author/quotee Persons (PER) to Claims & Topics. Pass a comma-separated list (no spaces).</td>
            </tr>
            <tr>
              <td>-l</td>
              <td>--load</td>
              <td>CLA | DEC | PUB | QUO</td>
              <td>-t</td>
              <td>Load data from FILE. -t option links imported records to the specified topic.</td>
            </tr>
            <tr>
              <td>-p</td>
              <td>--page-size</td>
              <td>PAGE_SIZE</td>
              <td>-hku</td>
              <td>Number of items to read per page (default 100).</td>
            </tr>
            <tr>
              <td>-P</td>
              <td>--password</td>
              <td>PASSWORD</td>
              <td>-hklu</td>
              <td>The password with which to login.</td>
            </tr>
            <tr>
              <td>-r</td>
              <td>--recursive</td>
              <td>(no)</td>
              <td>-hkrt</td>
              <td>Include records linked to sub-topics. Only applies if the -t / --topic-id option is specified.</td>
            </tr>
            <tr>
              <td>-s</td>
              <td>--status</td>
              <td>DEL, DRA, PUB, SUS</td>
              <td>-hku</td>
              <td>Filter on status. Pass a comma-separated list (no spaces).</td>
            </tr>
            <tr>
              <td>-t</td>
              <td>--topic-id</td>
              <td>ID</td>
              <td>-hkru</td>
              <td>Link imported records to specified topic OR filter on topic ID. Pass -r / --recursive to include
                sub-topics.</td>
            </tr>
            <tr>
              <td>-u</td>
              <td>--update-publications</td>
              <td>(no)</td>
              <td>-dfpst</td>
              <td>Normalize publications by setting publisher to journal.publisher.</td>
            </tr>
            <tr>
              <td>-U</td>
              <td>--username</td>
              <td>USERNAME</td>
              <td>-hklu</td>
              <td>The username with which to authenticate.</td>
            </tr>
            <tr>
              <td></td>
              <td>--spring.profiles.active</td>
              <td>PROFILE,...</td>
              <td></td>
              <td>Active Spring profiles. Pass a comma-separated list (no spaces).</td>
            </tr>
          </tbody>
        </table>
        <a id="abbreviations" /><h4>Abbreviations</h4>
        <p>In the Options table above, the following abbreviations are used. Pass the abbreviation(s) verbatim as option
          argument(s).
        </p>
        <table>
          <thead>
            <tr>
              <th>Context</th>
              <th>Abbreviation</th>
              <th>Meaning</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td rowSpan={10}>Record Kind</td>
              <td>CLA</td>
              <td>Claim</td>
            </tr>
            <tr>
              <td>COM</td>
              <td>Comment</td>
            </tr>
            <tr>
              <td>DEC</td>
              <td>Declaration</td>
            </tr>
            <tr>
              <td>JOU</td>
              <td>Journal</td>
            </tr>
            <tr>
              <td>LNK</td>
              <td>Record Link</td>
            </tr>
            <tr>
              <td>PER</td>
              <td>Person</td>
            </tr>
            <tr>
              <td>PUB</td>
              <td>Publication</td>
            </tr>
            <tr>
              <td>PBR</td>
              <td>Publisher</td>
            </tr>
            <tr>
              <td>QUO</td>
              <td>Quotation</td>
            </tr>
            <tr>
              <td>TOP</td>
              <td>Topic</td>
            </tr>
            <tr>
              <td rowSpan={4}>Record Status</td>
              <td>DEL</td>
              <td>Deleted</td>
            </tr>
            <tr>
              <td>DRA</td>
              <td>Draft</td>
            </tr>
            <tr>
              <td>PUB</td>
              <td>Published</td>
            </tr>
            <tr>
              <td>SUS</td>
              <td>Suspended</td>
            </tr>
          </tbody>
        </table>
      </article>
    )
}