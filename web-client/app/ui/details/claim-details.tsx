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
import { Action, cn, formatDate } from "@/lib/utils";
import { CalendarIcon } from "@heroicons/react/24/outline";
import StandardDetails from "./standard-details";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import { Dispatch, useContext, useState } from "react";
import { useImmerReducer } from "use-immer";
import { SecurityContext } from "@/lib/context";

function claimReducer(draft?: Claim, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setStatus":
        draft.status = action.value
        break
      case "setText":
        draft.text = action.value
        break
      case "setDate":
        draft.date = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
    }
  }
}

export default function ClaimDetails(
  { record, pageDispatch }:
  { record?: Claim; pageDispatch: Dispatch<Action> }) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(claimReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const claim = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: claim?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Claim Details&nbsp;</legend>
      <StandardDetails recordKind="Claim" record={claim} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Claim #${claim?.id}` : "-Select a claim in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="date">Date:</Label>
        <Popover>
          <PopoverTrigger id="date" asChild>
            <Button
              variant={"outline"}
              disabled={!updating}
              className={cn("w-[240px] justify-start text-left font-normal",
                (!claim || !claim.date) && "text-muted-foreground")}>
              <CalendarIcon />
              {formatDate(claim?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={claim?.date}
              onSelect={(day, selectedDay, activeModifiers) => dispatch("setDate", selectedDay)}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <DetailActions
          className="col-start-6 row-span-3"
          recordKind="Claim"
          record={claim}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="text" className="col-start-1">Text:</Label>
        <Textarea
          id="text"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!claim}
          readOnly={!updating}
          value={claim?.text ?? ''}
          onChange={e => dispatch("setText", e.target.value)}
        />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!claim}
          readOnly={!updating}
          value={claim?.notes ?? ''}
          onChange={e => dispatch("setNotes", e.target.value)}
        />
      </div>
    </fieldset>
  )
}