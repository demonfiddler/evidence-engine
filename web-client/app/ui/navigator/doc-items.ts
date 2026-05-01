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

import { SideBarCategory } from './sidebar'

export const categories = [
  {
    label: "Overview",
    items: [
      { label: "Table of Contents", href: "/doc/toc/" },
      { label: "Rationale", href: "/doc/rationale/" },
      { label: "Records", href: "/doc/records/" },
    ],
  }, {
    label: "User Interface",
    items: [
      { label: "Screen Layout", href: "/doc/layout/" },
      { label: "Master Filter", href: "/doc/filter/" },
      { label: "Tables", href: "/doc/tables/" },
      { label: "Details", href: "/doc/details/" },
    ],
  }, {
    label: "Pages",
    items: [
      { label: "Dashboard", href: "/doc/dashboard/" },
      { label: "Comments", href: "/doc/comments/" },
      { label: "Log", href: "/doc/log/" },
      { label: "Claims", href: "/doc/claims/" },
      { label: "Declarations", href: "/doc/declarations/" },
      { label: "Persons", href: "/doc/persons/" },
      { label: "Publications", href: "/doc/publications/" },
      { label: "Quotations", href: "/doc/quotations/" },
      { label: "Topics", href: "/doc/topics/" },
      { label: "Journals", href: "/doc/journals/" },
      { label: "Publishers", href: "/doc/publishers/" },
      { label: "Security", href: "/doc/security/", authority: "ADM" },
      { label: "Backup", href: "/doc/backup/", authority: "ADM" },
    ],
  }, {
    label: "Dialogs",
    items: [
      { label: "Export", href: "/doc/export/" },
      { label: "Import", href: "/doc/import/", authority: "UPL" },
      { label: "Status/Link Manager", href: "/doc/status/", authority: "CHG" },
      { label: "Sign-in", href: "/doc/sign-in/", authority: "REA" },
      { label: "Profile", href: "/doc/profile/", authority: "REA" },
      { label: "Password", href: "/doc/password/", authority: "REA" },
      { label: "Settings", href: "/doc/settings/", authority: "REA" },
    ],
  }, {
    label: "Content Management",
    authority: "REA",
    items: [
      { label: "Security", href: "/doc/cm-security/", authority: "REA" },
      { label: "Create & Editing", href: "/doc/cm-create-edit/", authority: "CRE" },
      { label: "Data Quality", href: "/doc/cm-data-quality/", authority: "CRE" },
      { label: "Record Status", href: "/doc/cm-status/", authority: "CHG" },
      { label: "Command Line", href: "/doc/cm-cmdline/", authority: "CRE" },
    ],
  },
] as SideBarCategory[]