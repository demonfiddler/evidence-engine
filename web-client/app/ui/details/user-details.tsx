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
import { getRecordLabel } from "@/lib/utils"
import useDetailHandlers from "./detail-handlers"
import DetailActions from "./detail-actions"
import { toast } from "sonner"

const countries = rawCountries as unknown as Country[]

export default function UserDetails({ user, group, showUsersOrMembers }: { user?: User, group?: Group, showUsersOrMembers: string }) {
  const state = useDetailHandlers<User>("User", user)
  const { updating } = state
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
        <Input id="username" className="" disabled={!user} readOnly={!updating} placeholder="username" value={user?.username ?? ''} />
        <Label htmlFor="email" className="">Email:</Label>
        <Input id="email" className="col-span-2" disabled={!user} readOnly={!updating} placeholder="email" value={user?.email ?? ''} />
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
        <Input id="firstName" className="col-span-2" disabled={!user} readOnly={!updating} placeholder="first name" value={user?.firstName ?? ''} />
        {/* TODO: Should the New/Edit/Delete buttons be disabled when showing group members? */}
        <DetailActions className="col-start-6 row-span-3" recordKind="User" record={user} state={state} />
        <Label htmlFor="lastName" className="col-start-1">Last name:</Label>
        <Input id="lastName" className="col-span-2" disabled={!user} readOnly={!updating} placeholder="last name" value={user?.lastName ?? ''} />
        <Label htmlFor="country" className="col-start-1">Country:</Label>
        <Select disabled={!updating} value={user?.country ?? ''}>
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
        <Input id="password" className="col-span-2" disabled={!user} readOnly={!updating} placeholder="password" value={user?.password ?? ''} />
        <fieldset className="flex flex-row col-span-5 border rounded-md p-4 gap-4 w-full" disabled={!showingUsers}>
          <legend>&nbsp;Authorities&nbsp;</legend>
          <Checkbox id="adm" checked={user?.authorities?.includes("ADM")} />
          <Label htmlFor="adm">Adminster</Label>
          <Checkbox id="cre" checked={user?.authorities?.includes("CRE")} />
          <Label htmlFor="cre">Create records</Label>
          <Checkbox id="del" checked={user?.authorities?.includes("DEL")} />
          <Label htmlFor="del">Delete records</Label>
          <Checkbox id="lnk" checked={user?.authorities?.includes("LNK")} />
          <Label htmlFor="lnk">Link records</Label>
          <Checkbox id="rea" checked={user?.authorities?.includes("REA")} />
          <Label htmlFor="rea">Read records</Label>
          <Checkbox id="upd" checked={user?.authorities?.includes("UPD")} />
          <Label htmlFor="upd">Update records</Label>
          <Checkbox id="upl" checked={user?.authorities?.includes("UPL")} />
          <Label htmlFor="upl">Upload files</Label>
        </fieldset>
      </div>
    </fieldset>
  )
}