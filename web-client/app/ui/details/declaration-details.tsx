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
import { Action, cn, formatDate } from "@/lib/utils"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { CalendarIcon } from "@heroicons/react/24/outline"
import Country from "@/app/model/Country"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import StandardDetails from "./standard-details"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import Link from "next/link"
import { Dispatch, useContext, useState } from "react"
import { useImmerReducer } from "use-immer"
import { SecurityContext } from "@/lib/context"
const countries = rawCountries as unknown as Country[]

function declarationReducer(draft?: Declaration, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setCached":
        draft.cached = action.value
        break
      case "setCountry":
        draft.country = action.value
        break
      case "setDate":
        draft.date = action.value
        break
      case "setKind":
        draft.kind = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
      case "setSignatories":
        draft.signatories = action.value
        break
      case "setSignatoryCount":
        draft.signatoryCount = action.value
        break
      case "setTitle":
        draft.title = action.value
        break
      case "setUrl":
        draft.url = action.value
        break
    }
  }
}

export default function DeclarationDetails(
  { record, pageDispatch}:
  { record?: Declaration; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(declarationReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const declaration = updating ? mutableRecord : record

  // console.log(`declaration = ${JSON.stringify(declaration)}`)
  // console.log(`mutableRecord = ${JSON.stringify(mutableRecord)}`)

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: declaration?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Declaration Details&nbsp;</legend>
      <StandardDetails recordKind="Declaration" record={declaration} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Declaration #${declaration?.id}` : "-Select a declaration in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="date" className="text-right">Date:</Label>
        <Popover>
          <PopoverTrigger asChild id="date" disabled={!declaration}>
            <Button
              variant={"outline"}
              disabled={!updating}
              className={cn("w-full justify-start text-left font-normal",
                (!declaration || !declaration.date) && "text-muted-foreground")}
            >
              <CalendarIcon />
              {formatDate(declaration?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="col-span-2 w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={declaration?.date}
              onSelect={(day, selectedDay, activeModifiers) => dispatch("setDate", selectedDay)}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="kind" className="col-start-4">Kind:</Label>
        <Select
          disabled={!updating}
          value={declaration?.kind ?? ''}
          onValueChange={value => dispatch("setKind", value)}
        >
          <SelectTrigger id="kind" className="w-[180px]" disabled={!declaration}>
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
        <DetailActions
          className="col-start-6 row-span-6"
          recordKind="Declaration"
          record={declaration}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input
          id="title"
          className="col-span-4"
          disabled={!declaration}
          readOnly={!updating}
          value={declaration?.title ?? ''}
          onChange={e => dispatch("setTitle", e.target.value)}
        />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input
              type="url"
              className="col-span-4"
              placeholder="URL"
              value={declaration?.url ?? ''}
              onChange={e => dispatch("setUrl", e.target.value)}
            />
          : <Link className="col-span-4" href={declaration?.url ?? ''} target="_blank">{declaration?.url ?? ''}</Link>
        }
        <Label htmlFor="cached" className="col-start-1">Cached:</Label>
        <Checkbox
          id="cached"
          className="col-span-2"
          disabled={!updating}
          checked={declaration?.cached}
          onCheckedChange={checked => dispatch("setCached", checked)}
        />
        <Label htmlFor="country">Country:</Label>
        <Select
          disabled={!updating}
          value={declaration?.country ?? ''}
          onValueChange={value => dispatch("setCountry", value)}
        >
          <SelectTrigger id="country" className="w-[180px]" disabled={!declaration}>
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
        <Label htmlFor="signatories" className="col-start-1">Signatories:</Label>
        <Textarea
          id="signatories"
          className="col-span-2 h-40 overflow-y-auto"
          disabled={!declaration}
          readOnly={!updating}
          value={declaration?.signatories ?? ''}
          onChange={e => dispatch("setSignatories", e.target.value)}
        />
        <Label htmlFor="signatoryCount" className="">Signatory count:</Label>
        <Input
          id="signatoryCount"
          type="signatoryCount"
          disabled={!declaration}
          readOnly={!updating}
          placeholder="count"
          value={declaration?.signatoryCount ?? ''}
          onChange={e => dispatch("setSignatoryCount", e.target.value)}
        />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!declaration}
          readOnly={!updating}
          value={declaration?.notes ?? ''}
          onChange={e => dispatch("setNotes", e.target.value)}
        />
      </div>
    </fieldset>
  )
}