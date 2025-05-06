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

import Publication from "@/app/model/Publication";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar"
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Textarea } from "@/components/ui/textarea";
import { cn, formatDate } from "@/lib/utils";
import { CalendarIcon } from "@heroicons/react/24/outline";
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
import rawJournals from "@/data/journals.json" assert {type: 'json'}
import rawPublicationKinds from "@/data/publication-kinds.json" assert {type: 'json'}
import Journal from "@/app/model/Journal";
import StandardDetails from "./standard-details";

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

export default function PublicationDetails({record}: {record: Publication | undefined}) {
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
      <legend>&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails record={record} readOnly={isEditing} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publication #${record?.id}` : "-Select a publication in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Text:</Label>
        <Input id="title" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.title ?? ''} />
        <Label htmlFor="authors" className="col-start-1">Authors:</Label>
        <Textarea id="authors" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.authors ?? ''} />
        <Label htmlFor="kind" className="col-start-1">Kind:</Label>
        <Select disabled={!isEditing} value={record?.kind ?? ''}>
          <SelectTrigger id="kind" className="" disabled={!record}>
            <SelectValue placeholder="Specify kind" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Publication Kinds</SelectLabel> {
                publicationKinds.map(pk => <SelectItem key={pk.kind} value={pk.kind}>{pk.label}</SelectItem>)
              }
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="journal" className="">Journal:</Label>
        <Select disabled={!isEditing} value={record?.journal?.id?.toString() ?? ''}>
          <SelectTrigger id="journal" className="col-span-2" disabled={!record}>
            <SelectValue className="col-span-2 w-full" placeholder="Specify journal" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Journals</SelectLabel> {
                journals.map(journal => <SelectItem key={journal.id?.toString() ?? ''} value={journal.id?.toString() ?? ''}>{journal.title}</SelectItem>)
              }
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="date" className="col-start-1">Date published:</Label>
        <Popover>
          <PopoverTrigger id="date" asChild>
            <Button
              variant={"outline"}
              disabled={!isEditing}
              className={cn("justify-start text-left font-normal",
                (!record || !record.date) && "text-muted-foreground")}>
              <CalendarIcon />
              {formatDate(record?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={record?.date}
              onSelect={setDate}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="year">Year:</Label>
        <Input type="number" id="year" className="" disabled={!record} readOnly={!isEditing} value={record?.year ?? ''} />
        <Button className="col-start-6 w-20 place-self-center bg-blue-500" disabled={!record || isEditing}>New</Button>
        <Label htmlFor="abstract" className="col-start-1">Abstract:</Label>
        <Textarea id="abstract" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.abstract ?? ''} />
        <Label htmlFor="peerReviewed" className="col-start-1">Peer reviewed:</Label>
        <Checkbox id="peerReviewed" className="col-span-2" disabled={!record || !isEditing} checked={record?.peerReviewed} />
        <Label htmlFor="cached" className="">Cached:</Label>
        <Checkbox id="cached" className="" disabled={!record || !isEditing} checked={record?.cached} />
        <Label htmlFor="doi" className="col-start-1">DOI:</Label>
        <Input id="doi" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.doi ?? ''} />
        <Label htmlFor="isbn">ISBN:</Label>
        <Input id="isbn" className="" disabled={!record} readOnly={!isEditing} value={record?.isbn ?? ''} />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        <Input id="url" className="col-span-2" disabled={!record} readOnly={!isEditing} value={record?.url?.toString() ?? ''} />
        <Label htmlFor="accessed">Accessed:</Label>
        <Popover>
          <PopoverTrigger id="accessed" asChild>
            <Button
              variant={"outline"}
              disabled={!isEditing}
              className={cn("justify-start text-left font-normal",
                (!record || !record.date) && "text-muted-foreground")}>
              <CalendarIcon />
              {formatDate(record?.accessed, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={record?.accessed}
              onSelect={setDate}
              initialFocus
            />
          </PopoverContent>
        </Popover>
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