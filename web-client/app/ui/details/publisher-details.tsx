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
import { Label } from "@/components/ui/label";
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
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import Link from "next/link";
import { Action } from "@/lib/utils";
import { Dispatch, useContext, useState } from "react";
import { SecurityContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";

const countries = rawCountries as unknown as Country[]

function publisherReducer(draft?: Publisher, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setCountry":
        draft.country = action.value
        break
      case "setJournalCount":
        draft.journalCount = action.value
        break
      case "setLocation":
        draft.location = action.value
        break
      case "setName":
        draft.name = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
      case "setUrl":
        draft.url = action.value
        break
    }
  }
}

export default function PublisherDetails(
  {record, pageDispatch}:
  {record?: Publisher; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(publisherReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const publisher = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: publisher?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Publisher Details&nbsp;</legend>
      <StandardDetails recordKind="Publisher" record={publisher} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{publisher ? `Details for selected Publisher #${publisher?.id}` : "-Select a publisher in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="name" className="col-start-1">Name:</Label>
        <Input
          id="name"
          className="col-span-4"
          disabled={!publisher}
          readOnly={!updating}
          value={publisher?.name ?? ''}
          onChange={e => dispatch("setName", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-5"
          recordKind="Publisher"
          record={publisher}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="location" className="col-start-1">Location:</Label>
        <Input
          id="location"
          className="col-span-2"
          disabled={!publisher}
          readOnly={!updating}
          value={publisher?.location ?? ''}
          onChange={e => dispatch("setLocation", e.target.value)}
        />
        <Label htmlFor="country">Country:</Label>
        <Select
          disabled={!updating}
          value={publisher?.country ?? ''}
          onValueChange={value => dispatch("setCountry", value)}
        >
          <SelectTrigger id="country" className="w-full" disabled={!publisher}>
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
        {
          updating
          ? <Input
              id="url"
              className="col-span-4"
              value={publisher?.url ?? ''}
              onChange={e => dispatch("setUrl", e.target.value)}
            />
          : <Link className="col-span-4" href={publisher?.url ?? ''} target="_blank">{publisher?.url ?? ''}</Link>
        }
        <Label htmlFor="journalCount" className="col-start-1">Journal count:</Label>
        <Input
          type="number"
          id="journalCount"
          className="col-span-1"
          disabled={!publisher}
          readOnly={!updating}
          value={publisher?.journalCount ?? ''}
          onChange={e => dispatch("setJournalCount", e.target.value)}
        />
      </div>
    </fieldset>
  )
}