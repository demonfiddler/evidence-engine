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

'use client';

import Link from 'next/link'
import Image from 'next/image'
import { usePathname } from 'next/navigation'
import clsx from 'clsx'
import { adminItems, appItems } from "./sidebar-items";
import useAuth from "@/hooks/use-auth";

type SidebarProps ={
  open: boolean
}

export default function Sidebar({open} : SidebarProps) {
  const {hasAuthority} = useAuth()
  const pathname = usePathname();

  return (
    <div
      className={open ? "flex flex-col w-50 border-r bg-gray-50" : "w-0 none overflow-clip"}
      data-slot="sidebar-container"
    >
      <div className="flex place-content-center border-b h-24" data-slot="sidebar-header">
        <Link href="/" title="Navigate to the Evidence Engine home page">
          <Image src="/logo.svg" alt="The Evidence Engine logo" width={120} height={120} className="" />
        </Link>
      </div>
      <div className="grow shrink overflow-y-auto">
        <h3 className="ml-3 mt-3">Pages</h3> {
        appItems.map((link) => {
          const LinkIcon = link.icon;
          return (
            <Link
              key={link.label}
              href={link.href}
              className={clsx(
                'flex h-10 grow items-center justify-center gap-2 rounded-md p-3 text-sm font-medium hover:bg-sky-50 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                {
                  'bg-sky-100 text-blue-600': pathname === link.href,
                },
              )}
            >
              <LinkIcon className="w-6" />
              <p className="hidden md:block">{link.label}</p>
            </Link>
          );
        })}
        {
          hasAuthority("ADM")
          ? <>
            <h3 className="ml-3 mt-3">Administration</h3> {
              adminItems.map((link) => {
                const LinkIcon = link.icon;
                return (
                  <Link
                    key={link.label}
                    href={link.href}
                    className={clsx(
                      'flex h-12 grow items-center justify-center gap-2 rounded-md p-3 text-sm font-medium hover:bg-sky-50 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                      {
                        'bg-sky-100 text-blue-600': pathname === link.href,
                      },
                    )}
                  >
                    <LinkIcon className="w-6" />
                    <p className="hidden md:block">{link.label}</p>
                  </Link>
                )
              })
            }
          </>
          : null
        }
      </div>
      <div className="flex place-content-center items-center border-t h-24" data-slot="sidebar-footer">
        <Link href="https://campaign-resources.org" target="_blank" title="Navigate to the Campaign Resources home page">
          <Image src="/cr-logo.svg" alt="The Campaign Resources logo" width={100} height={100} className="" />
        </Link>
      </div>
    </div>
  )
}

Sidebar.whyDidYouRender = true