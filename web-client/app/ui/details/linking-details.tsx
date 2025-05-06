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
// import { useImmer } from 'use-immer';

export default function LinkingDetails(
    {record, readOnly}:
    {record: ILinkableEntity | undefined, readOnly: boolean}
  ) {
  const [ selectedLinkId, setSelectedLinkId ] = useState<string>()
  const [ isEditing, setIsEditing ] = useState<boolean>(false)
  const [ fromEntityLocations, setFromEntityLocations ] = useState<string>()
  const [ toEntityLocations, setToEntityLocations ] = useState<string>()

  function handleClickLink() {
    console.log("Link...")
  }
  function handleClickUnlink() {
    console.log(`Unlink EntityLink#${selectedLinkId}...`)
  }
  function handleClickSaveOrEdit() {
    if (isEditing)
      console.log("saving locations...")
    else
      console.log("editing locations...")
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
    <div className="w-full grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
      <Label htmlFor="master" className="">Links from:</Label>
      <Select disabled={!record || isEditing} value={selectedLinkId} onValueChange={handleSelectedLinkChange}>
        <SelectTrigger className="col-span-3 w-full">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {
            record?.toEntityLinks?.content?.map(link => {
              const fromEntity = link.fromEntity;
              const linkId = link.id?.toString() ?? ''
              return fromEntity
                ? <SelectItem key={linkId} value={linkId}>{`${fromEntity?.entityKind}\u00a0#${fromEntity?.id}`}</SelectItem>
                : null
            })
          }
        </SelectContent>
      </Select>
      <Button onClick={handleClickLink} className="w-20 place-self-end bg-blue-500" disabled={!record}>Link</Button>
      <Button onClick={handleClickUnlink} className="w-20 place-self-center bg-blue-500" disabled={!record}>Unlink</Button>
      <Label htmlFor="from-loc" className="col-start-1">From location(s):</Label>
      <Input
        id="from-loc"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!isEditing}
        value={fromEntityLocations}
        onChange={e => setFromEntityLocations(e.target.value)}
      />
      <Button
        onClick={handleClickSaveOrEdit}
        className="row-span-2 w-20 place-self-center bg-blue-500"
        disabled={!record || readOnly}
      >
        {isEditing ? 'Save' : 'Edit'}
      </Button>
      <Label htmlFor="to-loc">To location(s):</Label>
      <Input
        id="to-loc"
        type="text"
        className="col-span-4"
        disabled={!record}
        readOnly={!isEditing}
        value={toEntityLocations}
        onChange={e => setToEntityLocations(e.target.value)}
      />
    </div>
  )
}

function EntityLinkLocations({entityLink, disabled}: {entityLink: EntityLink | null, disabled: boolean}) {
  const [ isEditing, setIsEditing ] = useState<boolean>(false)
  const [ fromEntityLocations, setFromEntityLocations ] = useState(entityLink?.fromEntityLocations)
  const [ toEntityLocations, setToEntityLocations ] = useState(entityLink?.toEntityLocations)

  function handleClickSaveOrEdit() {
    if (isEditing)
      console.log("saving locations...")
    else
      console.log("editing locations...")
    setIsEditing(!isEditing)
  }

  return (
    <>
      <Label htmlFor="from-loc" className="col-start-1">From location(s):</Label>
      <Input
        id="from-loc"
        type="text"
        className="col-span-4"
        disabled={disabled}
        readOnly={!isEditing}
        value={isEditing ? fromEntityLocations : entityLink?.fromEntityLocations}
        onChange={e => setFromEntityLocations(e.target.value)}
      />
      <Button
        onClick={handleClickSaveOrEdit}
        className="row-span-2 w-20 place-self-center bg-blue-500"
        disabled={disabled}>{isEditing ? 'Save' : 'Edit'}
      </Button>
      <Label htmlFor="to-loc">To location(s):</Label>
      <Input
        id="to-loc"
        type="text"
        className="col-span-4"
        disabled={disabled}
        readOnly={!isEditing}
        value={isEditing ? toEntityLocations : entityLink?.toEntityLocations}
        onChange={e => setToEntityLocations(e.target.value)}
      />
    </>
  )
}