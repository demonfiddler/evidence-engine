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

import Quotation from "@/app/model/Quotation"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Textarea } from "@/components/ui/textarea"
import { Action, cn, formatDate } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import StandardDetails from "./standard-details"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import Link from "next/link"
import { Dispatch, useContext, useState } from "react"
import { SecurityContext } from "@/lib/context"
import { useImmerReducer } from "use-immer"

function quotationReducer(draft?: Quotation, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setDate":
        draft.date = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
      case "setQuotee":
        draft.quotee = action.value
        break
      case "setSource":
        draft.source = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
      case "setText":
        draft.text = action.value
        break
    }
  }
}

export default function QuotationDetails(
  {record,  pageDispatch}:
  {record?: Quotation; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(quotationReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const quotation = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: quotation?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Quotation Details&nbsp;</legend>
      <StandardDetails recordKind="Quotation" record={quotation} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Quotation #${quotation?.id}` : "-Select a quotation in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="quotee">Quotee:</Label>
        <Input
          id="quotee"
          className="col-span-2"
          disabled={!quotation}
          readOnly={!updating}
          placeholder="count"
          value={quotation?.quotee ?? ''}
          onChange={e => dispatch("setQuotee", e.target.value)}
        />
        <Label htmlFor="date" className="text-right">Date:</Label>
        <Popover>
          <PopoverTrigger asChild id="date" disabled={!quotation}>
            <Button
              variant={"outline"}
              className={cn("w-full justify-start text-left font-normal",
                (!quotation || !quotation.date) && "text-muted-foreground")}
              disabled={!updating}
            >
              <CalendarIcon />
              {formatDate(quotation?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="col-span-2 w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={quotation?.date}
              onSelect={(day, selectedDay, activeModifiers) => dispatch("setDate", selectedDay)}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <DetailActions
          className="col-start-6 row-span-5"
          recordKind="Quotation"
          record={quotation}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="text" className="col-start-1">Quote:</Label>
        <Textarea
          id="text"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!quotation}
          readOnly={!updating}
          value={quotation?.text ?? ''}
          onChange={e => dispatch("setText", e.target.value)}
        />
        <Label htmlFor="source" className="col-start-1">Source:</Label>
        <Input
          type="source"
          className="col-span-4"
          disabled={!quotation}
          readOnly={!updating}
          placeholder="URL"
          value={quotation?.source ?? ''}
          onChange={e => dispatch("setSource", e.target.value)}
        />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input
            type="url"
            className="col-span-4"
            placeholder="URL"
            value={quotation?.url ?? ''}
            onChange={e => dispatch("setUrl", e.target.value)}
          />
          : <Link className="col-span-4" href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
        }
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!quotation}
          readOnly={!updating}
          value={quotation?.notes ?? ''}
            onChange={e => dispatch("setNotes", e.target.value)}
        />
      </div>
    </fieldset>
  )
}