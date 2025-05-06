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

import Claim from "@/app/model/Claim";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar"
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Textarea } from "@/components/ui/textarea";
import { cn, formatDate } from "@/lib/utils";
import { CalendarIcon } from "@heroicons/react/24/outline";
import { useState } from "react";
import StandardDetails from "./standard-details";

export default function ClaimDetails({record}: {record: Claim | undefined}) {
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
      <legend>&nbsp;Claim Details&nbsp;</legend>
      <StandardDetails record={record} readOnly={isEditing} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Claim #${record?.id}` : "-Select a claim in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="date">Date:</Label>
        <Popover>
          <PopoverTrigger id="date" asChild>
            <Button
              variant={"outline"}
              disabled={!isEditing}
              className={cn("w-[240px] justify-start text-left font-normal",
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
        <Label htmlFor="text" className="col-start-1">Text:</Label>
        <Textarea id="text" className="col-span-4" disabled={!record} readOnly={!isEditing} value={record?.text ?? ''} />
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