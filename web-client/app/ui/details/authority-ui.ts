/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
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

export type AuthorityUI = {
  key: "adm" | "chg" | "com" | "cre" | "lnk" | "rea" | "upd" | "upl"
  label: string
  description: string
}

// AuthoritiesFormFields

export const authorities : AuthorityUI[] = [
  {key: "adm", label: "Administer", description: "Administer system"},
  {key: "chg", label: "Change", description: "Change record status"},
  {key: "com", label: "Comment", description: "Comment on records"},
  {key: "cre", label: "Create", description: "Create records"},
  {key: "lnk", label: "Link", description: "Link/unlink records"},
  {key: "rea", label: "Read", description: "Read records"},
  {key: "upd", label: "Update", description: "Update records"},
  {key: "upl", label: "Upload", description: "Upload files"},
]