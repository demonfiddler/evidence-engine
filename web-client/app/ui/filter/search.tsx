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
import { SearchIcon } from "@/app/ui/icons"
import { ChangeEvent, KeyboardEvent, useState } from "react";

export default function Search() {
  const [ value, setValue ] = useState("")

  function clear() {
    setValue("")
  }

  function onKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (e.code == "Escape")
      clear();
  }

  function onChange(e : ChangeEvent<HTMLInputElement>) {
    console.log(e.target.value)
    setValue(e.target.value)
  }

  return (
    <div className="flex flex-row items-center gap-2 px-2 pr-2 border rounded-md">
      <SearchIcon className="w-6 h-6" />
      <Input
        type="search"
        className="grow border-0 border-transparent"
        placeholder="Search..."
        title="Case-insensitive match against all text fields"
        value={value}
        onKeyDown={onKeyDown}
        onChange={onChange} />
    </div>
  )
}