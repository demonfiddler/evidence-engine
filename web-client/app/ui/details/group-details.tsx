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
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction } from "react"
import { useFormContext } from "react-hook-form"
import { authorities } from "./authority-ui"
import { GroupFieldValues } from "../validators/group"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import CheckboxEx from "../ext/checkbox-ex"
import FieldsetEx from "../ext/fieldset-ex"
import { detail, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(detail, "[GroupDetails] ")

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
  logger.debug("render")

  const form = useFormContext<GroupFieldValues>()
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
                  <FormLabel htmlFor="groupname">Group name</FormLabel>
                  <FormControl>
                    <InputEx
                      id="groupname"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="groupname"
                      {...field}
                      help="The name for the group, consisting of letters only"
                    />
                  </FormControl>
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
              onFormAction={onFormAction}
            />
            <FieldsetEx
              className="grid grid-cols-7 border rounded-md p-4 gap-4"
              disabled={!updating}
              help="Authorities to grant to the group"
            >
              <legend>&nbsp;Authorities&nbsp;</legend>
              {
                authorities.map(auth => (
                  <FormField
                    key={auth.key}
                    control={form.control}
                    name={auth.key}
                    render={({field}) => (
                      <FormItem>
                        <FormLabel htmlFor={auth.key}>{auth.label}</FormLabel>
                        <FormControl>
                          <CheckboxEx
                            id={auth.key}
                            checked={field.value}
                            onCheckedChange={field.onChange}
                            help={auth.description}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                ))
              }
            </FieldsetEx>
            <p>Click the &apos;Users&apos; tab to see group members</p>
          </div>
        </form>
      </Form>
    </fieldset>
  )
}

GroupDetails.whyDidYouRender = true