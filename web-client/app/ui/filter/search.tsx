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

import { SearchIcon } from "@/app/ui/icons"
import { XMarkIcon } from "@heroicons/react/24/outline"
import { useCallback, useEffect, useRef, useState } from "react"
import InputEx from "../ext/input-ex"
import { component, LoggerEx } from "@/lib/logger"
import { cn } from "@/lib/utils"
import { ClassValue } from "clsx"

const logger = new LoggerEx(component, "[Search] ")

export default function Search(
  {id, value, onChangeValue, className} :
  {id?: string, value: string, onChangeValue: (value: string) => void, className?: ClassValue}
) {
  logger.debug("render")

  const [text, setText] = useState(value)

  // If the supplied value changes externally, update text state to match and notify supplied listener.
  const prevValue = useRef<string | number | readonly string[] | undefined>('')
  useEffect(() => {
    logger.trace("effect (1)")
    if (value !== prevValue.current) {
      logger.trace("effect (2) value changed from '%s' to '%s'", prevValue.current, value)
      prevValue.current = value
      if (value !== text) {
        logger.trace("effect (3): value = '%s', text='%s'", value, text)
        setText(value)
      }
    }
  }, [value, text, onChangeValue])

  // If the input component value changes, update text state to match and notify supplied listener.
  const onChangeText = useCallback((s: string) => {
    logger.trace("onChangeText: s = '%s', text='%s'", s, text)
    setText(s)
    onChangeValue(s)
  }, [onChangeValue])

  return (
    <div className={cn("flex flex-row items-center gap-2 px-2 pr-2 border rounded-md", className)}>
      <SearchIcon className="w-6 h-6" />
      <InputEx
        id={id ?? "search"}
        outerClassName="grow"
        className="border-0 border-transparent"
        placeholder="Search..."
        value={text ?? ''}
        delay={500}
        onChange={(e) => onChangeText(e.target.value ?? '')}
        clearOnEscape={true}
        help="Filter the table to show only records containing the specified text. This performs a case-insensitive match against all text fields, matching whole words unless 'Advanced' is checked."
      />
      <XMarkIcon className="w-5 h-5" onClick={() => onChangeText('')} />
    </div>
  )
}

Search.whyDidYouRender = true