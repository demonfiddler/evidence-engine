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
import { cn } from '@/lib/utils'
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion'
import { SideBarCategory } from './sidebar'
import { useEffect, useMemo, useState } from 'react'

type TocProps = {
  categories: SideBarCategory[]
  className?: string
  categoryClassName?: string
  itemClassName?: string
}

export default function TableOfContents({categories, className, categoryClassName, itemClassName} : TocProps) {
  const {hasAuthority} = useAuth()
  const pathname = usePathname()
  const [values, setValues] = useState<string[]>([])
  const categoryIdx = useMemo(() => {
    return categories.findIndex(cat => cat.items.findIndex(item => item.href === pathname) != -1)
  }, [categories, pathname])

  // Ensure that the AccordionItem containing the active SideBarItem is always expanded.
  useEffect(() => {
    if (categoryIdx != -1) {
      const value = categories[categoryIdx].label
      if (!values.includes(value))
        setValues([...values, value])
    }
  }, [categories, categoryIdx])

  return (
    <div className={cn("grow shrink overflow-y-auto", className)}>
      <Accordion type="multiple" value={values} onValueChange={setValues}>
      {
        categories.filter(cat => !cat.authority || hasAuthority(cat.authority)).map((cat, catIdx) => (
        <AccordionItem key={catIdx} value={cat.label}>
          <AccordionTrigger className={categoryClassName}>
            {cat.label}
          </AccordionTrigger>
          <AccordionContent>
          {
            cat.items.filter(item => !item.authority || hasAuthority(item.authority)).map((item, itemIdx) =>
            {
              const LinkIcon = item.icon
              return (
                <Link
                  key={itemIdx}
                  href={item.href}
                  className={clsx(
                    'flex h-6 grow items-center justify-center rounded-md text-sm font-medium hover:bg-sky-50 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                    {
                      'bg-sky-100 text-blue-600': pathname === item.href,
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
          </AccordionContent>
        </AccordionItem>
      ))}
      </Accordion>
    </div>
  )
}

TableOfContents.whyDidYouRender = true