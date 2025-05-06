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

import Person from "@/app/model/Person";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country"
import StandardDetails from "./standard-details";

export default function PersonDetails({record}: {record: Person | undefined}) {
  const countries = rawCountries as unknown as Country[]
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
      <legend>&nbsp;Person Details&nbsp;</legend>
      <StandardDetails record={record} readOnly={isEditing} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Person #${record?.id}` : "-Select a person in the list above to see his/her details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input id="title" className="" disabled={!record} readOnly={!isEditing} placeholder="title" value={record?.title ?? ''} />
        <Label htmlFor="firstName" className="">First name:</Label>
        <Input id="firstName" className="" disabled={!record} readOnly={!isEditing} value={record?.firstName ?? ''} />
        <Label htmlFor="nickname" className="">Nickname:</Label>
        <Input id="nickname" className="" disabled={!record} readOnly={!isEditing} value={record?.nickname ?? ''} />
        <Label htmlFor="prefix" className="col-start-1">Prefix:</Label>
        <Input id="prefix" className="" disabled={!record} readOnly={!isEditing} value={record?.prefix ?? ''} />
        <Label htmlFor="lastName" className="">Last name:</Label>
        <Input id="lastName" className="" disabled={!record} readOnly={!isEditing} value={record?.lastName ?? ''} />
        <Label htmlFor="suffix" className="">Suffix:</Label>
        <Input id="suffix" className="" disabled={!record} readOnly={!isEditing} value={record?.suffix ?? ''} />
        <Label htmlFor="alias" className="">Alias:</Label>
        <Input id="alias" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.alias ?? ''} />
        <Button className="col-start-6 w-20 place-self-center bg-blue-500" disabled={!record || isEditing}>New</Button>
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.notes ?? ''} />
        <Label htmlFor="qualifications" className="col-start-1">Qualifications:</Label>
        <Textarea id="qualifications" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.qualifications ?? ''} />
        <Label htmlFor="country" className="col-start-1">Country:</Label>
        <Select disabled={!isEditing} value={record?.country ?? ''}>
          <SelectTrigger id="country" className="w-[180px]" disabled={!record}>
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
        <Label htmlFor="rating" className="col-start-1">Rating:</Label>
        <Input id="rating" type="number" className="" disabled={!record} readOnly={!isEditing} placeholder="title" value={record?.rating ?? ''} />
        <Label htmlFor="checked" className="col-start-1">Checked:</Label>
        <Checkbox id="checked" className="" disabled={!record || !isEditing} checked={record?.checked} />
        <Label htmlFor="published" className="col-start-1">Published:</Label>
        <Checkbox id="published" className="" disabled={!record || !isEditing} checked={record?.published} />

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