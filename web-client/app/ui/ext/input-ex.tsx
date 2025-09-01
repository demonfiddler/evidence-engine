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

import { Input } from "@/components/ui/input"
import { ChangeEvent, ComponentProps, useCallback, useEffect, useState } from "react"
import { useDebounceValue } from "usehooks-ts"
import Help, { HelpProps } from "../misc/help"
import { cn } from "@/lib/utils"

type InputExProps = ComponentProps<"input"> & HelpProps & {
    delay?: number
  }

export default function InputEx(
  {help, outerClassName, value, onChange, delay, ...props} : InputExProps
) {
  const [text, setText] = useState(value)
  const [event, setEvent] = useDebounceValue<ChangeEvent<HTMLInputElement>|undefined>(undefined, delay || 0)

  useEffect(() => {
    if (value !== text) {
      setText(value)
      if (event && onChange) {
        event.target.value = value?.toString() ?? ''
        onChange(event)
      }
    }
  }, [value])
  useEffect(() => {
    if (event)
      onChange?.(event)
  }, [event])

  const onChangeText = useCallback((e:  ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value ?? '')
    setEvent(e)
  }, [])

  const clear = useCallback(() => {
    setText('')
    if (event && onChange) {
      event.target.value = ''
      onChange(event)
    }
  }, [event])

  return (
    <div className={cn("flex flex-row items-center gap-1", outerClassName)}>
      <Input
        value={text}
        onKeyDown={(e) => e.code == "Escape" && clear()}
        onChange={(e) => onChangeText(e)}
        {...props}
      />
      <Help text={help} />
    </div>
  )
}