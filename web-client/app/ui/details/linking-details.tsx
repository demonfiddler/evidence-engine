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

import EntityLink from "@/app/model/EntityLink";
import ILinkableEntity from "@/app/model/ILinkableEntity";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useState } from "react";
import { toast } from "sonner";
import { getRecordLabel } from "@/lib/utils";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
// import { useImmer } from 'use-immer';

export default function LinkingDetails(
    {record, state}:
    {record: ILinkableEntity | undefined, state: DetailState}
  ) {
  const [ selectedLinkId, setSelectedLinkId ] = useState<string>()
  const [ isEditing, setIsEditing ] = useState<boolean>(false)
  const [ fromEntityLocations, setFromEntityLocations ] = useState<string>()
  const [ toEntityLocations, setToEntityLocations ] = useState<string>()
  const allowLinking = record && state.allowLink && !state.updating

  function handleLinkorCancel() {
    if (isEditing) {
      toast.info("Cancelling ...")
      // TODO: handle cancel
    } else {
      toast.info("Linking...")
      // TODO: handle link
    }
    setIsEditing(false)
  }

  function handleUnlink() {
    if (confirm(`Delete record link #${selectedLinkId}?`)) {
      toast.info(`Unlinking EntityLink#${selectedLinkId}...`)
      // TODO: delete link
    } else {
      toast.info(`Cancelling delete EntityLink#${selectedLinkId}...`)
    }
  }

  function handleSaveOrEdit() {
    if (isEditing) {
      toast.info("Saving locations...")
      // TODO: save locations
    } else {
      toast.info("Editing locations...")
    }
    setIsEditing(!isEditing)
  }

  function handleSelectedLinkChange(linkId : string) {
    setSelectedLinkId(linkId)
    const selectedLink = getSelectedLink(linkId)
    setFromEntityLocations(selectedLink?.fromEntityLocations)
    setToEntityLocations(selectedLink?.toEntityLocations)
  }

  function getSelectedLink(linkId : string | undefined) : EntityLink | null  {
    linkId ??= selectedLinkId
    if (!linkId)
      return null;

    const links = record?.toEntityLinks?.content
    if (links) {
      for (let link of links) {
        if (link.id == linkId)
          return link
      }
    }
    return null
  }

  return (
    <div className="w-full grid grid-cols-6 gap-2">
      <Label htmlFor="master">Links from:</Label>
      <Select disabled={!record || isEditing} value={selectedLinkId} onValueChange={handleSelectedLinkChange}>
        <SelectTrigger className="col-span-4 w-full">
          <SelectValue placeholder={
            record?.toEntityLinks?.content?.length ?? 0 > 0
            ? "-Select an inbound link-"
            : "-No inbound links-"}
          />
        </SelectTrigger>
        <SelectContent>
          {
            record?.toEntityLinks?.content?.map(link => {
              const fromEntity = link.fromEntity;
              const linkId = link.id?.toString() ?? ''
              return fromEntity
                ? <SelectItem key={linkId} value={linkId}>{`${getRecordLabel(fromEntity?.entityKind as RecordKind, fromEntity)}`}</SelectItem>
                : null
            })
          }
        </SelectContent>
      </Select>
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking}
        onClick={handleLinkorCancel}
      >
        {isEditing ? "Cancel" : "Link"}
      </Button>
      <Label htmlFor="from-loc" className="col-start-1">From location(s):</Label>
      <Input
        id="from-loc"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!allowLinking || !isEditing}
        value={fromEntityLocations}
        onChange={e => setFromEntityLocations(e.target.value)}
      />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId}
        onClick={handleSaveOrEdit}
      >
        {isEditing ? 'Save' : 'Edit'}
      </Button>
      <Label htmlFor="to-loc">To location(s):</Label>
      <Input
        id="to-loc"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!allowLinking || !isEditing}
        value={toEntityLocations}
        onChange={e => setToEntityLocations(e.target.value)}
      />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId || isEditing}
        onClick={handleUnlink}
      >
        Unlink
      </Button>
    </div>
  )
}
