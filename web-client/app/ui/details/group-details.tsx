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

import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import Group from "@/app/model/Group"
import { Input } from "@/components/ui/input"
import StandardDetails from "./standard-details"
import { Checkbox } from "@/components/ui/checkbox"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useState } from "react"
import { useFormContext } from "react-hook-form"
import { authorities } from "./authority-ui"
import { GroupFieldValues } from "../validators/group"
import { FormActionHandler } from "@/hooks/use-page-logic"

export default function GroupDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Group
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<GroupFieldValues>
  }) {

  // const {hasAuthority} = useAuth()
  const form = useFormContext<GroupFieldValues>()
  // const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  // const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Group Details&nbsp;</legend>
      <StandardDetails recordKind="Group" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Group"
                : record
                  ? `Details for selected Group #${record?.id}`
                  : "-Select a Group in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-2 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="groupname"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Group name</FormLabel>
                  <FormControl>
                    <Input
                      id="groupname"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="groupname"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The group name
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-2 row-span-3"
              recordKind="Group"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <fieldset className="grid grid-cols-7 border rounded-md p-4 gap-4 w-full" disabled={!record && !updating}>
              <legend>&nbsp;Authorities&nbsp;</legend>
              {
                authorities.map(auth => (
                  <FormField
                    key={auth.key}
                    control={form.control}
                    name={auth.key}
                    render={({field}) => (
                      <FormItem>
                        <FormLabel>{auth.label}</FormLabel>
                        <FormControl>
                          <Checkbox
                            id={auth.key}
                            checked={field.value}
                            onCheckedChange={field.onChange}
                          />
                        </FormControl>
                        {
                          showFieldHelp
                          ? <FormDescription>
                              {auth.description}
                            </FormDescription>
                          : null
                        }
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                ))
              }
            </fieldset>
            <p>Click the 'Users' tab to see group members</p>
          </div>
        </form>
      </Form>
    </fieldset>
  )
}