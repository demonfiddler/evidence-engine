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

import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "@/components/ui/combobox"
import { InputGroupAddon } from "@/components/ui/input-group"
import Help from "../misc/help"
import { QueryResult } from "@/lib/graphql-utils"
import IPage from "@/app/model/IPage"
import User from "@/app/model/User"
import { useQuery } from "@apollo/client/react"
import { READ_USERS } from "@/lib/graphql-queries"
import { toast } from "sonner"
import { component, LoggerEx } from "@/lib/logger"
import { useMemo } from "react"
import { Button } from "@/components/ui/button"
import { RotateCwIcon } from "lucide-react"

const logger = new LoggerEx(component, "[UserCombobox] ")

type UserComboboxProps = {
  id?: string
  className?: string
  help?: string
  value?: string
  onValueChange?: (value: string) => void
}

const READ_USER_OPTIONS = {
  variables: {
    pageSort: {
      sort: {
        orders: [
          { property: "username" }
        ]
      }
    }
  },
}

const EMPTY_USERS = [] as User[]

export default function UserCombobox({ id, className, value, onValueChange, help } : UserComboboxProps) {

  const {loading, error, data, previousData, refetch} = useQuery(READ_USERS, READ_USER_OPTIONS)
  if (error) {
    // TODO: display user-friendly error notification
    toast.error(`READ_USERS operation failed:\n\n${error.message}`)
    logger.error("READ_USERS operation failed: %o", error)
  }
  const users = ((loading ? previousData : data) as QueryResult<IPage<User>>)?.users?.content ?? EMPTY_USERS
  const usersById = useMemo(() => Object.fromEntries(users.map(u => [u.id, u])), [users])
  const selectedUser = value
    ? usersById[value] ?? null
    : null

  return (
    <Combobox
      items={users}
      itemToStringValue={u => u.id ?? ''}
      itemToStringLabel={u => u.username ?? ''}
      value={selectedUser}
      onValueChange={u => onValueChange?.(u?.id ?? '')}
    >
      <ComboboxInput className={className} id={id} placeholder="Username" showClear>
        <InputGroupAddon align="inline-end">
          <Button
            type="button"
            variant="ghost"
            className="w-6 h-6"
            onClick={() => refetch()}
            title="Refresh the list of users"
          >
            <RotateCwIcon className="w-6 h-6" />
          </Button>
          <Help text={help} />
        </InputGroupAddon>
      </ComboboxInput>
      <ComboboxContent className="">
        <ComboboxEmpty>-No users found-</ComboboxEmpty>
        <ComboboxList>
          {u => (
            <ComboboxItem key={u.id} value={u}>
              {u.username}
            </ComboboxItem>
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  )
}
