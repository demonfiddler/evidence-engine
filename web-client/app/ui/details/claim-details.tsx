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
import StandardDetails from "./standard-details";
import { toast } from "sonner";
import useDetailHandlers from "./detail-handlers";
import DetailActions from "./detail-actions";

export default function ClaimDetails({ record }: { record: Claim | undefined }) {
  const state = useDetailHandlers<Claim>("Claim", record)
  const { updating } = state

  function setDate(e: any) {
    toast(`selected ${JSON.stringify(e)}`)
  }

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Claim Details&nbsp;</legend>
      <StandardDetails recordKind="Claim" record={record} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Claim #${record?.id}` : "-Select a claim in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="date">Date:</Label>
        <Popover>
          <PopoverTrigger id="date" asChild>
            <Button
              variant={"outline"}
              disabled={!updating}
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
        <DetailActions className="col-start-6 row-span-3" recordKind="Claim" record={record} state={state} />
        <Label htmlFor="text" className="col-start-1">Text:</Label>
        <Textarea id="text" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.text ?? ''} />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.notes ?? ''} />
      </div>
    </fieldset>
  )
}