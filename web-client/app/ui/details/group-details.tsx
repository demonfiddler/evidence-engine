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
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { FormAction } from "@/lib/utils"
import { useContext, useMemo, useState } from "react"
import { SecurityContext } from "@/lib/context"
import { useFormContext } from "react-hook-form"
import { authorities } from "./authority-ui"
import { GroupFormFields } from "../validators/group"

export default function GroupDetails(
  { record, onFormAction }:
  { record?: Group; onFormAction: (command: FormAction, formValue: GroupFormFields) => void }) {

  const securityContext = useContext(SecurityContext)
  const form = useFormContext<GroupFormFields>()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(securityContext, mode), [securityContext, mode])
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend>&nbsp;Group Details&nbsp;</legend>
      <StandardDetails recordKind="Group" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Group #${record?.id}` : "-Select a group in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-2 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="groupname"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Group name</FormLabel>
                  <FormControl>
                    <Input
                      id="groupname"
                      disabled={!record}
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
            <fieldset className="grid grid-cols-7 border rounded-md p-4 gap-4 w-full" disabled={!record}>
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