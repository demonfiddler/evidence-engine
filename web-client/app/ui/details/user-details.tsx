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
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
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
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details"
import { Checkbox } from "@/components/ui/checkbox"
import Country from "@/app/model/Country"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Group from "@/app/model/Group"
import { getRecordLabel, SecurityFormAction } from "@/lib/utils"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { toast } from "sonner"
import { authorities } from "./authority-ui"
import { useCallback, useContext, useMemo, useState } from "react"
import useAuth from "@/hooks/use-auth"
import { useFormContext } from "react-hook-form"
import { UserFormFields } from "../validators/user"

const countries = rawCountries as unknown as Country[]

export default function UserDetails(
  {
    user,
    group,
    showUsersOrMembers,
    onFormAction
  }:
  {
    user?: User,
    group?: Group,
    showUsersOrMembers: string,
    onFormAction: (command: SecurityFormAction, formValue: UserFormFields) => void
  }) {

  const {hasAuthority} = useAuth()
  const form = useFormContext<UserFormFields>()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  const showingUsers = showUsersOrMembers == "users"
  // if (!showingUsers)
  //   state.disableNewCancelButton = state.disableEditSaveButton = state.disableDeleteButton = true

  // TODO: the Add button/function should not be enabled if the user is already a member of the group.
  const handleAddOrRemove = useCallback(() => {
    const userLabel = getRecordLabel("User", user)
    const groupLabel = getRecordLabel("Group", group)
    if (showingUsers) {
      if (confirm(`Add user ${userLabel} to ${groupLabel}?`)) {
        toast.info(`Adding user ${userLabel} to ${groupLabel} ...`)
        onFormAction("add", form.getValues())
      }
    } else {
      if (confirm(`Remove user ${userLabel} from ${groupLabel}?`)) {
        toast.warning(`Removing user ${userLabel} to ${groupLabel} ...`)
        onFormAction("remove", form.getValues())
      }
    }
  }, [user, group, onFormAction, form])

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend>&nbsp;User Details&nbsp;</legend>
      <StandardDetails recordKind="User" record={user} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{user ? `Details for selected User #${user?.id}` : "-Select a user in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="username"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input
                      id="username"
                      disabled={!user}
                      readOnly={!updating}
                      placeholder="username"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The user's username
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button
              type="button"
              className="col-start-3 w-20 place-self-center bg-blue-500"
              disabled={!user || !group || updating}
              title={
                showingUsers
                  ? `Add selected User to ${getRecordLabel("Group", group)}`
                  : `Remove selected user from  ${getRecordLabel("Group", group)}`
              }
              onClick={handleAddOrRemove}
            >
              {
                showingUsers
                  ? "Add"
                  : "Remove"
              }
            </Button>
            <FormField
              control={form.control}
              name="firstName"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>First name</FormLabel>
                  <FormControl>
                    <Input
                      id="firstName"
                      className="col-span-2"
                      disabled={!user}
                      readOnly={!updating}
                      placeholder="first name"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The user's first name
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="lastName"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Last name</FormLabel>
                  <FormControl>
                    <Input
                      id="lastName"
                      className=""
                      disabled={!user}
                      readOnly={!updating}
                      placeholder="last name"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The user's last name
                      </FormDescription>
                    : null
                  }
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
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="email"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input
                      id="email"
                      type="email"
                      disabled={!user}
                      readOnly={!updating}
                      placeholder="email"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The user's email address
                      </FormDescription>
                    : null
                  }
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
                    <Input
                      id="password"
                      // type="password"
                      disabled={!user}
                      readOnly={!updating}
                      placeholder="password"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        bcrypt hash of user's password
                      </FormDescription>
                    : null
                  }
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
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTrigger id="country" className="w-full" disabled={!user}>
                        <SelectValue placeholder="Specify country" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Countries</SelectLabel>
                        {
                          countries.map(country => (
                            <SelectItem
                              key={country.alpha_2}
                              value={country.alpha_2}>
                              {country.common_name}
                            </SelectItem>
                          ))
                        }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The user's country of residence
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <fieldset className="col-start-1 col-span-2 grid grid-cols-7 border rounded-md p-4 gap-4 w-full" disabled={!user}>
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
              {
                showFieldHelp
                ? <FormDescription className="col-span-3">
                    The authorities granted to the user
                  </FormDescription>
                : null
              }
            </fieldset>
          </div>
        </form>
      </Form>
    </fieldset>
  )
}