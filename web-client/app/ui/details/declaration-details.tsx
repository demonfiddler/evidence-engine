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

import Declaration from "@/app/model/Declaration"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Checkbox } from "@/components/ui/checkbox"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Textarea } from "@/components/ui/textarea"
import { cn, formatDate } from "@/lib/utils"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { useState } from "react"
import { CalendarIcon } from "@heroicons/react/24/outline"
import Country from "@/app/model/Country"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import StandardDetails from "./standard-details"
const countries = rawCountries as unknown as Country[]

export default function DeclarationDetails({record}: {record: Declaration | undefined}) {
  const [ isEditing, setIsEditing ] = useState<boolean>(false)

  function setDate(e: any) {
    console.log(`selected ${JSON.stringify(e)}`)
  }

  function handleClickSaveOrEdit() {
    if (isEditing)
      console.log("saving details...")
    else
      console.log("editing details...")
    setIsEditing(!isEditing)
  }

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Declaration Details&nbsp;</legend>
      <StandardDetails record={record} readOnly={isEditing} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Declaration #${record?.id}` : "-Select a declaration in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="date" className="text-right">Date:</Label>
        <Popover>
          <PopoverTrigger asChild id="date" disabled={!record}>
            <Button
              variant={"outline"}
              className={cn("w-[240px] justify-start text-left font-normal",
                (!record || !record.date) && "text-muted-foreground")}
              disabled={!isEditing}
            >
              <CalendarIcon />
              {formatDate(record?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="col-span-2 w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={record?.date}
              onSelect={setDate}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="kind" className="col-start-4">Kind:</Label>
        <Select disabled={!isEditing} value={record?.kind ?? ''}>
          <SelectTrigger id="kind" className="w-[180px]" disabled={!record}>
            <SelectValue placeholder="Specify kind" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Declaration Kinds</SelectLabel>
              <SelectItem value="DECL">Declaration</SelectItem>
              <SelectItem value="OPLE">Open Letter</SelectItem>
              <SelectItem value="PETN">Petition</SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Textarea id="title" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.title ?? ''} />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        <Input type="url" className="col-span-4" disabled={!record} readOnly={!isEditing} placeholder="URL" value={record?.url?.toString() ?? ''} />
        <Label htmlFor="cached" className="col-start-1">Cached:</Label>
        <Checkbox id="cached" className="col-span-2" disabled={!record || !isEditing} checked={record?.cached} />
        <Label htmlFor="country">Country:</Label>
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
        <Label htmlFor="signatories" className="col-start-1">Title:</Label>
        <Textarea id="signatories" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.signatories ?? ''} />
        <Label htmlFor="signatoryCount" className="">Signatory count:</Label>
        <Input id="signatoryCount" type="signatoryCount" disabled={!record} readOnly={!isEditing} placeholder="count" value={record?.signatoryCount ?? ''} />
        <Button className="col-start-6 w-20 place-self-center bg-blue-500" disabled={!record || isEditing}>New</Button>
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.notes ?? ''} />
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