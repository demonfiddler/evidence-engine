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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { getRecordLabel } from "@/lib/utils";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
// import { useImmer } from 'use-immer';

type RecordLink = {
  entityLinkId: string
  thisRecordIsToEntity: boolean
  thisLocations: string
  otherLocations: string
  otherRecordLabel: string
}

function getRecordLinks(record?: ILinkableEntity, toLinks?: boolean, entityLinks?: EntityLink[], results?: RecordLink[]) : RecordLink[] {
  if (entityLinks) {
    if (!record || !results)
      throw new Error("record, entityLinks and results must all be passed")
    for (let entityLink of entityLinks) {
      if (!entityLink.id)
        continue
      const otherRecord = toLinks ? entityLink.fromEntity : entityLink.toEntity
      const displayableLink = {
        entityLinkId: entityLink.id,
        thisRecordIsToEntity: toLinks ?? false,
        thisLocations: (toLinks ? entityLink.toEntityLocations : entityLink.fromEntityLocations) ?? '',
        otherLocations: (toLinks ? entityLink.fromEntityLocations : entityLink.toEntityLocations) ?? '',
        otherRecordLabel: getRecordLabel(otherRecord?.entityKind as RecordKind, otherRecord) ?? '',
      }
      if (!displayableLink.otherRecordLabel)
        console.warn(displayableLink)
      results.push(displayableLink)
    }
  } else {
    results = []
    if (record) {
      getRecordLinks(record, false, record.fromEntityLinks?.content, results)
      getRecordLinks(record, true, record.toEntityLinks?.content, results)
    }
  }
  return results
}

export default function LinkingDetails(
    {record, state}:
    {record: ILinkableEntity | undefined, state: DetailState}
  ) {
  const [ selectedLinkId, setSelectedLinkId ] = useState<string>('')
  const [ isEditing, setIsEditing ] = useState<boolean>(false)
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>()
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>()
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
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
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
  }

  function getSelectedLink(linkId : string | undefined) : RecordLink | undefined  {
    linkId ??= selectedLinkId
    if (!linkId)
      return undefined;

    return recordLinks.find(rl => rl.entityLinkId == linkId)
  }

  useEffect(() => handleSelectedLinkChange(''), [record])

  return (
    <div className="w-full grid grid-cols-6 gap-2">
      <Label htmlFor="master">{`Inbound links (${recordLinks.length}):`}</Label>
      <Select disabled={!record || isEditing} value={selectedLinkId} onValueChange={handleSelectedLinkChange}>
        <SelectTrigger className="col-span-4 w-full">
          <SelectValue placeholder={
            recordLinks.length ?? 0 > 0
            ? "-Select an inbound link-"
            : "-No inbound links-"}
          />
        </SelectTrigger>
        <SelectContent>
          {
            recordLinks.map(link => {
              return link
                ? <SelectItem key={link.entityLinkId} value={link.entityLinkId}>{link.otherRecordLabel}</SelectItem>
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
      <Label htmlFor="this-locations" className="col-start-1">Location(s) in this record:</Label>
      <Input
        id="this-locations"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!allowLinking || !isEditing}
        value={thisRecordLocations}
        onChange={e => setThisRecordLocations(e.target.value)}
      />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId}
        onClick={handleSaveOrEdit}
      >
        {isEditing ? 'Save' : 'Edit'}
      </Button>
      <Label htmlFor="other-locations">Location(s) in other record:</Label>
      <Input
        id="other-locations"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!allowLinking || !isEditing}
        value={otherRecordLocations}
        onChange={e => setOtherRecordLocations(e.target.value)}
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
