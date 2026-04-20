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

import { categories } from "@/app/ui/navigator/doc-items"
import useAuth from "@/hooks/use-auth";
import Link from "next/link";

export default function TableOfContentsPage() {
  const {hasAuthority} = useAuth()

  return (
    <article className="flex flex-col prose">
      <h2>Table of Contents</h2>
      {
        categories.filter(cat => !cat.authority || hasAuthority(cat.authority)).map((cat, catIdx) => (
          <div key={catIdx} className="flex flex-col">
            <h3 className="mt-2">{cat.label}</h3>
            {
              cat.items.filter(item => !item.authority || hasAuthority(item.authority)).map((item, itemIdx) => (
                <Link key={itemIdx} href={item.href}>{item.label}</Link>
              ))
            }
          </div>
        ))
      }
    </article>
  )
}
