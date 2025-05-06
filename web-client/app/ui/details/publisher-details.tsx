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

import Publisher from "@/app/model/Publisher";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details";
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country";

const countries = rawCountries as unknown as Country[]

export default function PublisherDetails({record}: {record: Publisher | undefined}) {
  const [ isEditing, setIsEditing ] = useState<boolean>(false)

  function handleClickSaveOrEdit() {
    if (isEditing)
      console.log("saving details...")
    else
      console.log("editing details...")
    setIsEditing(!isEditing)
  }

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Publisher Details&nbsp;</legend>
      <StandardDetails record={record} readOnly={isEditing} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publisher #${record?.id}` : "-Select a publisher in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="name" className="col-start-1">Name:</Label>
        <Input id="name" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.name ?? ''} />
        <Label htmlFor="location" className="col-start-1">Location:</Label>
        <Input id="location" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.location ?? ''} />
        <Label htmlFor="country">Country:</Label>
        <Select disabled={!isEditing} value={record?.country ?? ''}>
          <SelectTrigger id="country" className="" disabled={!record}>
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
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        <Input id="url" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.url?.toString() ?? ''} />
        <Button className="col-start-6 w-20 place-self-center bg-blue-500" disabled={!record || isEditing}>New</Button>
        <Label htmlFor="journalCount" className="col-start-1">Journal count:</Label>
        <Input type="number" id="journalCount" className="col-span-1" disabled={!record} readOnly={!isEditing} value={record?.journalCount ?? ''} />
        <Button
          onClick={handleClickSaveOrEdit}
          className="col-start-6 w-20 place-self-center bg-blue-500"
          disabled={!record}
        >
          {isEditing ? 'Save' : 'Edit'}
        </Button>
      </div>
    </fieldset>
  )
}