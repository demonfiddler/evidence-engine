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

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import clsx from 'clsx'
import useAuth from "@/hooks/use-auth"
import { ForwardRefExoticComponent, RefAttributes } from 'react'
import { LucideProps } from 'lucide-react'
import { AuthorityKind } from '@/app/model/schema'
import { cn } from '@/lib/utils'

type Icon = ForwardRefExoticComponent<Omit<LucideProps, "ref"> & RefAttributes<SVGSVGElement>>

export type SideBarItem = {
  label: string
  href: string
  icon?: Icon
  authority?: AuthorityKind
}

export type SideBarCategory = {
  label: string
  icon?: Icon
  authority?: AuthorityKind
  items: SideBarItem[]
}

type SidebarProps = {
  categories: SideBarCategory[]
  className?: string
  categoryClassName?: string
  itemClassName?: string
}

export default function Sidebar({categories, className, categoryClassName, itemClassName} : SidebarProps) {
  const {hasAuthority} = useAuth()
  const pathname = usePathname();

  return (
    <div className={cn("grow shrink overflow-y-auto", className)}>
    {
      categories.filter(category => !category.authority || hasAuthority(category.authority)).map((category, categoryIdx) =>
      {
        return (
          <div key={categoryIdx}>
            <div className={cn("flex", categoryClassName)}>
              {
                (() => {
                  const CategoryIcon = category.icon
                  return CategoryIcon ? <CategoryIcon className="inline size-6" /> : null
                })()
              }
              <p className="text-lg">{category.label}</p>
            </div>
            {
              category.items.filter(item => !item.authority || hasAuthority(item.authority)).map((item, itemIdx) => {
                const LinkIcon = item.icon
                return (
                  <Link
                    key={itemIdx}
                    href={item.href}
                    className={clsx(
                      'flex h-9 grow items-center justify-center gap-2 rounded-md p-3 text-sm font-medium hover:bg-sky-50 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                      {
                        'bg-sky-100 text-blue-600': item.href !== '/' && pathname.startsWith(item.href),
                      },
                      itemClassName,
                    )}
                  >
                    {
                      LinkIcon
                      ? <LinkIcon className="w-6" />
                      : null
                    }
                    <p className="hidden md:block">{item.label}</p>
                  </Link>
                )
              })
            }
          </div>
        )
      })
    }
    </div>
  )
}

Sidebar.whyDidYouRender = true