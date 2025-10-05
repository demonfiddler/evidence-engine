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

import User from "@/app/model/User"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details"
import Country from "@/app/model/Country"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Group from "@/app/model/Group"
import { getRecordLabel } from "@/lib/utils"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { toast } from "sonner"
import { authorities } from "./authority-ui"
import { Dispatch, SetStateAction, useCallback } from "react"
import { useFormContext } from "react-hook-form"
import { UserFieldValues } from "../validators/user"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"
import TextareaEx from "../ext/textarea-ex"
import CheckboxEx from "../ext/checkbox-ex"
import ButtonEx from "../ext/button-ex"
import FieldsetEx from "../ext/fieldset-ex"
import { detail, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(detail, "[UserDetails] ")

const countries = rawCountries as unknown as Country[]

export default function UserDetails(
  {
    user,
    group,
    state,
    setMode,
    onFormAction
  }:
  {
    user?: User
    group?: Group
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<UserFieldValues>
  }) {
  logger.debug("render")

  const form = useFormContext<UserFieldValues>()
  const { updating } = state
  const userInGroup = (group?.members?.findIndex(m => m.id == user?.id) ?? -1) != -1

  // TODO: the Add button/function should not be enabled if the user is already a member of the group.
  const handleAddOrRemove = useCallback(() => {
    const userLabel = getRecordLabel("User", user)
    const groupLabel = getRecordLabel("Group", group)
    if (userInGroup) {
      if (confirm(`Remove user ${userLabel} from ${groupLabel}?`)) {
        toast.warning(`Removing user ${userLabel} to ${groupLabel} ...`)
        onFormAction("remove", form.getValues())
      }
    } else {
      if (confirm(`Add user ${userLabel} to ${groupLabel}?`)) {
        toast.info(`Adding user ${userLabel} to ${groupLabel} ...`)
        onFormAction("add", form.getValues())
      }
    }
  }, [user, group, userInGroup, onFormAction, form])

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;User Details&nbsp;</legend>
      <StandardDetails recordKind="User" record={user} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new User"
                : user
                  ? `Details for selected User #${user?.id}`
                  : "-Select a User in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="username"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <InputEx
                      id="username"
                      disabled={!user && !updating}
                      readOnly={state.mode != "create"}
                      placeholder="username"
                      {...field}
                      help="The user's username, consisting of lowercase letters only. This cannot be changed once it has been set."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="firstName"
              render={({field}) => (
                <FormItem>
                  <FormLabel>First name</FormLabel>
                  <FormControl>
                    <InputEx
                      id="firstName"
                      className="col-span-2"
                      disabled={!user && !updating}
                      readOnly={!updating}
                      placeholder="first name"
                      {...field}
                      help="The user's first name"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <ButtonEx
              type="button"
              outerClassName="col-start-3 place-self-center"
              className="w-20 bg-blue-500"
              disabled={!user || !group || updating}
              onClick={handleAddOrRemove}
              help={
                user && group
                ? userInGroup
                  ? `Remove selected user ${user.firstName} ${user.lastName} (${user.username}) from  ${getRecordLabel("Group", group)}`
                  : `Add selected User ${user.firstName} ${user.lastName} (${user.username}) to ${getRecordLabel("Group", group)}`
                : "User or Group not selected"
              }
            >
              {
                userInGroup
                ? "Remove"
                : "Add"
              }
            </ButtonEx>
            <FormField
              control={form.control}
              name="lastName"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Last name</FormLabel>
                  <FormControl>
                    <InputEx
                      id="lastName"
                      disabled={!user && !updating}
                      readOnly={!updating}
                      placeholder="last name"
                      {...field}
                      help="The user's last name"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            {/* TODO: Should the New/Edit/Delete buttons be disabled when showing group members? */}
            <DetailActions
              className="col-start-3 row-span-3"
              recordKind="User"
              record={user}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="email"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <InputEx
                      id="email"
                      type="email"
                      disabled={!user && !updating}
                      readOnly={!updating}
                      placeholder="email"
                      {...field}
                      help="The user's email address"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <InputEx
                      id="password"
                      // type="password"
                      disabled={!user && !updating}
                      readOnly={!updating}
                      placeholder="password"
                      {...field}
                      help="bcrypt hash of user's password"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="country"
              render={({ field }) => (
                <FormItem className="col-start-1">
                  <FormLabel>Country</FormLabel>
                  <Select
                    disabled={!user && !updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="country"
                        className="w-full"
                        disabled={!user && !updating}
                        help="The user's country of residence"
                      >
                        <SelectValue placeholder="Specify country" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        countries.map(country => (
                          <SelectItem
                            key={country.alpha_2}
                            value={country.alpha_2}>
                            {country.common_name}
                          </SelectItem>
                        ))
                      }
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="notes"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-2">
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="h-40 overflow-y-auto"
                      disabled={!user && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Added notes on the user"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FieldsetEx
              outerClassName="col-start-1 col-span-2"
              className="grid grid-cols-7 border rounded-md p-4 gap-4"
              disabled={!user && !updating}
              help="Authorities to grant to the user"
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
                        <FormLabel>{auth.label}</FormLabel>
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
              <p className="col-span-7 text-sm">N.B. Consider adding this user to a group with authorities rather than granting authorities here</p>
            </FieldsetEx>
          </div>
        </form>
      </Form>
    </fieldset>
  )
}

UserDetails.whyDidYouRender = true