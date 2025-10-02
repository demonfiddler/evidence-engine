
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

'use client'

import { cn } from "@/lib/utils"

/*
 * Loosely based on https://tailwindflex.com/@mohit/spinning-loading.
 * See also https://tailwindflex.com/tag/loading.
 */
export default function Spinner({
    loading,
    className,
    label = "Loading...",
    strokeWidth = 24
  } : {
    loading: boolean
    className?: string
    label?: string
    strokeWidth?: number
  }) {
  return (
    loading ? (
      <div aria-label={label} role="status" className={cn("flex justify-center items-center space-x-2", className)}>
        <svg className="h-20 w-20 animate-spin stroke-gray-500" viewBox="0 0 256 256">
          <line x1="128" y1="32" x2="128" y2="64" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}></line>
          <line x1="195.9" y1="60.1" x2="173.3" y2="82.7" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}></line>
          <line x1="224" y1="128" x2="192" y2="128" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}>
          </line>
          <line x1="195.9" y1="195.9" x2="173.3" y2="173.3" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}></line>
          <line x1="128" y1="224" x2="128" y2="192" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}>
          </line>
          <line x1="60.1" y1="195.9" x2="82.7" y2="173.3" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}></line>
          <line x1="32" y1="128" x2="64" y2="128" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}></line>
          <line x1="60.1" y1="60.1" x2="82.7" y2="82.7" strokeLinecap="round" strokeLinejoin="round"
            strokeWidth={strokeWidth}>
          </line>
        </svg>
        <span className="text-4xl font-medium text-gray-500">{label}</span>
    </div>
    )
    : null
  )
}

Spinner.whyDidYouRender = true