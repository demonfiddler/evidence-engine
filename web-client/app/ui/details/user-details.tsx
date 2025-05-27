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
import { Label } from "@/components/ui/label"
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
import { Action, getRecordLabel } from "@/lib/utils"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { toast } from "sonner"
import Authority from "@/app/model/Authority"
import { Dispatch, useContext, useState } from "react"
import { SecurityContext } from "@/lib/context"
import { useImmerReducer } from "use-immer"

const countries = rawCountries as unknown as Country[]

function userReducer(draft?: User, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setAdmAuthority":
        setAuthority(draft, "ADM", action.value)
        break
      case "setCreAuthority":
        setAuthority(draft, "CRE", action.value)
        break
      case "setDelAuthority":
        setAuthority(draft, "DEL", action.value)
        break
      case "setLnkAuthority":
        setAuthority(draft, "LNK", action.value)
        break
      case "setReaAuthority":
        setAuthority(draft, "REA", action.value)
        break
      case "setUpdAuthority":
        setAuthority(draft, "UPD", action.value)
        break
      case "setUplAuthority":
        setAuthority(draft, "UPL", action.value)
        break
      case "setCountry":
        draft.country = action.value
        break
      case "setEmail":
        draft.email = action.value
        break
      case "setFirstName":
        draft.firstName = action.value
        break
      case "setLastName":
        draft.lastName = action.value
        break
      case "setUsername":
        draft.username = action.value
        break
      case "setPassword":
        draft.password = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
    }
  }
}

function setAuthority(draft: User, authority: Authority, grant: boolean) {
  if (!draft.authorities)
    draft.authorities = []
  const index = draft.authorities?.findIndex(a => a == authority) as number
  if (grant) {
    if (index == -1)
      draft.authorities?.push(authority)
  } else {
    if (index != -1)
      draft.authorities?.splice(index, 1)
  }
}

export default function UserDetails(
  { user, group, showUsersOrMembers, pageDispatch }:
  { user?: User, group?: Group, showUsersOrMembers: string, pageDispatch: Dispatch<Action> }) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(userReducer, user ?? {})

  const state = createDetailState(securityContext, mode, user)
  const { updating } = state
  if (updating)
    user = mutableRecord

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: group?.id ?? "0", command: command, value: value})
  }

  const showingUsers = showUsersOrMembers == "users"
  if (!showingUsers)
    state.disableNewCancelButton = state.disableEditSaveButton = state.disableDeleteButton = true

  // TODO: the Add button/function should not be enabled if the user is already a member of the group.
  function handleAddOrRemove() {
    const userLabel = getRecordLabel("User", user)
    const groupLabel = getRecordLabel("Group", group)
    if (showingUsers) {
      if (confirm(`Add user ${userLabel} to ${groupLabel}?`)) {
        toast(`Adding user ${userLabel} to group ${groupLabel} ...`)
        // TODO: add user to group
      }
    } else {
      if (confirm(`Remove user ${userLabel} from ${groupLabel}?`)) {
        toast(`Removing user ${userLabel} to group ${groupLabel} ...`)
        // TODO: remove user from group
      }
    }
  }

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend>&nbsp;User Details&nbsp;</legend>
      <StandardDetails recordKind="User" record={user} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{user ? `Details for selected User #${user?.id}` : "-Select a user in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="username" className="">Username:</Label>
        <Input
          id="username"
          disabled={!user}
          readOnly={!updating}
          placeholder="username"
          value={user?.username ?? ''}
          onChange={e => dispatch("setUsername", e.target.value)}
        />
        <Label htmlFor="email" className="">Email:</Label>
        <Input
          id="email"
          className="col-span-2"
          disabled={!user}
          readOnly={!updating}
          placeholder="email"
          value={user?.email ?? ''}
          onChange={e => dispatch("setEmail", e.target.value)}
        />
        <Button
          className="col-start-6 w-20 place-self-center bg-blue-500"
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
        <Label htmlFor="firstName" className="col-start-1">First name:</Label>
        <Input
          id="firstName"
          className="col-span-2"
          disabled={!user}
          readOnly={!updating}
          placeholder="first name"
          value={user?.firstName ?? ''}
          onChange={e => dispatch("setFirstName", e.target.value)}
        />
        {/* TODO: Should the New/Edit/Delete buttons be disabled when showing group members? */}
        <DetailActions
          className="col-start-6 row-span-3"
          recordKind="User"
          record={user}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="lastName" className="col-start-1">Last name:</Label>
        <Input
          id="lastName"
          className="col-span-2"
          disabled={!user}
          readOnly={!updating}
          placeholder="last name"
          value={user?.lastName ?? ''}
          onChange={e => dispatch("setLastName", e.target.value)}
        />
        <Label htmlFor="country" className="col-start-1">Country:</Label>
        <Select
          disabled={!updating}
          value={user?.country ?? ''}
          onValueChange={value => dispatch("setCountry", value)}
        >
          <SelectTrigger id="country" className="w-[180px]" disabled={!user}>
            <SelectValue placeholder="Specify country" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Countries</SelectLabel>
              {countries.map(country =>
                <SelectItem key={country.alpha_2} value={country.alpha_2}>{country.common_name}</SelectItem>)}
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="password" className="">Password:</Label>
        <Input
          id="password"
          className="col-span-2"
          disabled={!user}
          readOnly={!updating}
          placeholder="password"
          value={user?.password ?? ''}
          onChange={e => dispatch("setPassword", e.target.value)}
        />
        <fieldset className="flex flex-row col-span-5 border rounded-md p-4 gap-4 w-full" disabled={!showingUsers}>
          <legend>&nbsp;Authorities&nbsp;</legend>
          <Checkbox
            id="adm"
            checked={user?.authorities?.includes("ADM")}
            onCheckedChange={checked => dispatch("setAdmAuthority", checked)}
          />
          <Label htmlFor="adm">Adminster</Label>
          <Checkbox
            id="cre"
            checked={user?.authorities?.includes("CRE")}
            onCheckedChange={checked => dispatch("setCreAuthority", checked)}
          />
          <Label htmlFor="cre">Create records</Label>
          <Checkbox
            id="del"
            checked={user?.authorities?.includes("DEL")}
            onCheckedChange={checked => dispatch("setDelAuthority", checked)}
          />
          <Label htmlFor="del">Delete records</Label>
          <Checkbox
            id="lnk"
            checked={user?.authorities?.includes("LNK")}
            onCheckedChange={checked => dispatch("setLnkAuthority", checked)}
          />
          <Label htmlFor="lnk">Link records</Label>
          <Checkbox
            id="rea"
            checked={user?.authorities?.includes("REA")}
            onCheckedChange={checked => dispatch("setReaAuthority", checked)}
          />
          <Label htmlFor="rea">Read records</Label>
          <Checkbox
            id="upd"
            checked={user?.authorities?.includes("UPD")}
            onCheckedChange={checked => dispatch("setUpdAuthority", checked)}
          />
          <Label htmlFor="upd">Update records</Label>
          <Checkbox
            id="upl"
            checked={user?.authorities?.includes("UPL")}
            onCheckedChange={checked => dispatch("setUplAuthority", checked)}
          />
          <Label htmlFor="upl">Upload files</Label>
        </fieldset>
      </div>
    </fieldset>
  )
}