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

import { Input } from "@/components/ui/input";
import { ComponentProps, useEffect, useState } from "react";
import { useDebounceValue } from "usehooks-ts";

export default function InputEx(
  {onChangeValue, value, ...props} :
  {onChangeValue: (value: string | number | readonly string[] | undefined) => void} & ComponentProps<"input">
) {
  const [text, setText] = useState(value)
  const [debouncedValue, setDebouncedValue] = useDebounceValue(value, 500)
  useEffect(() => setText(value), [value])
  useEffect(() => onChangeValue(debouncedValue), [debouncedValue])

  function onChangeText(s: string) {
    setText(s)
    setDebouncedValue(s)
  }

  return (
    <Input
      value={text}
      onKeyDown={(e) => e.code == "Escape" && onChangeText('')}
      onChange={(e) => onChangeText(e.target.value ?? '')}
      {...props}
    />
  )
}