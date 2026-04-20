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
import PageNavigator from "@/app/ui/navigator/page-navigator"
import TableOfContents from "@/app/ui/navigator/table-of-contents"
import useAuth from "@/hooks/use-auth"
import { layout, LoggerEx } from "@/lib/logger"
import { CircleQuestionMarkIcon } from "lucide-react"
import { usePathname } from "next/navigation"
import { useMemo } from "react"

const logger = new LoggerEx(layout, "[DocLayout] ")

export default function DocLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  logger.debug("render")

  const pathname = usePathname()
  const {hasAuthority} = useAuth()
  const hrefs = useMemo(() => {
    return categories.filter(cat => !cat.authority || hasAuthority(cat.authority))
      .map(cat => cat.items.filter(item => !item.authority || hasAuthority(item.authority))
        .map(item => item.href)).flat()
  }, [categories, hasAuthority])
  const idx = hrefs.findIndex(href => href === pathname)
  const hrefPrevious = idx > 0 ? hrefs[idx - 1] : "#"
  const hrefNext = (idx < hrefs.length - 1) ? hrefs[idx + 1] : "#"

  return (
    <div className="fixed top-16 bottom-12 flex flex-row w-full">
      <div
        className="flex flex-col items-start border-r bg-gray-50"
        data-slot="sidebar-container"
      >
        <h3 className="ml-3">Documentation</h3>
        <TableOfContents className="w-50 mx-3" categories={categories} categoryClassName="justify-start mt-3" itemClassName="" />
      </div>
      <main className="relative flex flex-col m-8 h-full w-full">
        <div className="fixed top-16 bottom-12 left-100 right-0 flex flex-col items-center overflow-auto">
          <div className="flex items-center"><CircleQuestionMarkIcon className="inline" />&nbsp;<h1>Documentation</h1></div>
          <PageNavigator className="w-5/12 mx-0" hrefPrevious={hrefPrevious} hrefNext={hrefNext} />
          <div className="w-1/2">
            {children}
          </div>
          <PageNavigator className="w-5/12 mx-0" hrefPrevious={hrefPrevious} hrefNext={hrefNext} />
        </div>
      </main>
    </div>
  )
}

DocLayout.whyDidYouRender = true