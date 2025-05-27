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

import Group from "@/app/model/Group"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import StandardDetails from "./standard-details"
import { Checkbox } from "@/components/ui/checkbox"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { Action } from "@/lib/utils"
import { Dispatch, useContext, useState } from "react"
import { SecurityContext } from "@/lib/context"
import { useImmerReducer } from "use-immer"
import Authority from "@/app/model/Authority"

function groupReducer(draft?: Group, action?: Action) {
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
      case "setDescription":
        draft.description = action.value
        break
      case "setGroupname":
        draft.groupname = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
    }
  }
}

function setAuthority(draft: Group, authority: Authority, grant: boolean) {
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

export default function GroupDetails(
  {record, pageDispatch}:
  {record?: Group; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(groupReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const group = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: group?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend>&nbsp;Group Details&nbsp;</legend>
      <StandardDetails recordKind="Group" record={group} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{group ? `Details for selected Group #${group?.id}` : "-Select a group in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="groupname" className="">Group name:</Label>
        <Input
          id="groupname"
          disabled={!group}
          readOnly={!updating}
          placeholder="groupname"
          value={group?.groupname ?? ''}
          onChange={e => dispatch("setGroupname", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-3"
          recordKind="Group"
          record={group}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <fieldset className="flex flex-row col-span-5 border rounded-md p-4 gap-4 w-full" disabled={!group}>
          <legend>&nbsp;Authorities&nbsp;</legend>
          <Checkbox
            id="adm"
            checked={group?.authorities?.includes("ADM")}
            onCheckedChange={checked => dispatch("setAdmAuthority", checked)}
          />
          <Label htmlFor="adm">Adminster</Label>
          <Checkbox
            id="cre"
            checked={group?.authorities?.includes("CRE")}
            onCheckedChange={checked => dispatch("setCreAuthority", checked)}
          />
          <Label htmlFor="cre">Create records</Label>
          <Checkbox
            id="del"
            checked={group?.authorities?.includes("DEL")}
            onCheckedChange={checked => dispatch("setDelAuthority", checked)}
          />
          <Label htmlFor="del">Delete records</Label>
          <Checkbox
            id="lnk"
            checked={group?.authorities?.includes("LNK")}
            onCheckedChange={checked => dispatch("setLnkAuthority", checked)}
          />
          <Label htmlFor="lnk">Link records</Label>
          <Checkbox
            id="rea"
            checked={group?.authorities?.includes("REA")}
            onCheckedChange={checked => dispatch("setReaAuthority", checked)}
          />
          <Label htmlFor="rea">Read records</Label>
          <Checkbox
            id="upd"
            checked={group?.authorities?.includes("UPD")}
            onCheckedChange={checked => dispatch("setUpdAuthority", checked)}
          />
          <Label htmlFor="upd">Update records</Label>
          <Checkbox
            id="upl"
            checked={group?.authorities?.includes("UPL")}
            onCheckedChange={checked => dispatch("setUplAuthority", checked)}
          />
          <Label htmlFor="upl">Upload files</Label>
        </fieldset>
      </div>
    </fieldset>
  )
}