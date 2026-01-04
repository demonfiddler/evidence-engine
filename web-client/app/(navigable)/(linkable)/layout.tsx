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

import "@/app/globals.css"
import '@/app/ui/global.css'
import EntityLinkFilter from "@/app/ui/filter/entity-link-filter"
import { layout, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(layout, "[LinkableEntityLayout] ")

// export const metadata: Metadata = {
//   title: {
//     template: '%s | Evidence Engine',
//     default: 'Evidence Engine',
//   },
//   description: "Scientific evidence curated by campaign-resources.org",
//   // metadataBase: new URL(process.env.webClientUrl);
// };

export default function LinkableEntityLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  logger.debug("render")

  return (
    <div className="m-4">
      <EntityLinkFilter />
      {children}
    </div>
  )
}