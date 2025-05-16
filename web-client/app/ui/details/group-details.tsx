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
import useDetailHandlers from "./detail-handlers"
import DetailActions from "./detail-actions"

export default function GroupDetails({record}: {record: Group | undefined}) {
  const state = useDetailHandlers<Group>("Group", record)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend>&nbsp;Group Details&nbsp;</legend>
      <StandardDetails recordKind="Group" record={record} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Group #${record?.id}` : "-Select a group in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="groupname" className="">Group name:</Label>
        <Input id="groupname" disabled={!record} readOnly={!updating} placeholder="groupname" value={record?.groupname ?? ''} />
        <DetailActions className="col-start-6 row-span-3" recordKind="Group" record={record} state={state} />
        <fieldset className="flex flex-row col-span-5 border rounded-md p-4 gap-4 w-full" disabled={!record}>
          <legend>&nbsp;Authorities&nbsp;</legend>
          <Checkbox id="adm" checked={record?.authorities?.includes("ADM")}/>
          <Label htmlFor="adm">Adminster</Label>
          <Checkbox id="cre" checked={record?.authorities?.includes("CRE")}/>
          <Label htmlFor="cre">Create records</Label>
          <Checkbox id="del" checked={record?.authorities?.includes("DEL")}/>
          <Label htmlFor="del">Delete records</Label>
          <Checkbox id="lnk" checked={record?.authorities?.includes("LNK")}/>
          <Label htmlFor="lnk">Link records</Label>
          <Checkbox id="rea" checked={record?.authorities?.includes("REA")}/>
          <Label htmlFor="rea">Read records</Label>
          <Checkbox id="upd" checked={record?.authorities?.includes("UPD")}/>
          <Label htmlFor="upd">Update records</Label>
          <Checkbox id="upl" checked={record?.authorities?.includes("UPL")}/>
          <Label htmlFor="upl">Upload files</Label>
        </fieldset>
      </div>
    </fieldset>
  )
}