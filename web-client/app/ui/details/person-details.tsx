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
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country"
import StandardDetails from "./standard-details";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import { Dispatch, useContext, useState } from "react";
import { Action } from "@/lib/utils";
import { SecurityContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";

function personReducer(draft?: Person, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setAlias":
        draft.alias = action.value
        break
      case "setChecked":
        draft.checked = action.value
        break
      case "setCountry":
        draft.country = action.value
        break
      case "setFirstName":
        draft.firstName = action.value
        break
      case "setLastName":
        draft.lastName = action.value
        break
      case "setNickname":
        draft.nickname = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
      case "setPrefix":
        draft.prefix = action.value
        break
      case "setPublished":
        draft.published = action.value
        break
      case "setQualifications":
        draft.qualifications = action.value
        break
      case "setRating":
        draft.rating = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
      case "setSuffix":
        draft.suffix = action.value
        break
      case "setTitle":
        draft.title = action.value
        break
    }
  }
}

export default function PersonDetails(
  {record, pageDispatch}:
  {record?: Person; pageDispatch: Dispatch<Action>}) {

  const countries = rawCountries as unknown as Country[]
  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(personReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const person = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: person?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Person Details&nbsp;</legend>
      <StandardDetails recordKind="Person" record={person} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Person #${person?.id}` : "-Select a person in the list above to see his/her details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input
          id="title"
          disabled={!person}
          readOnly={!updating}
          placeholder="title"
          value={person?.title ?? ''}
          onChange={e => dispatch("setTitle", e.target.value)}
        />
        <Label htmlFor="nickname" className="">Nickname:</Label>
        <Input
          id="nickname"
          disabled={!person}
          readOnly={!updating}
          value={person?.nickname ?? ''}
          onChange={e => dispatch("setNickname", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-8"
          recordKind="Person"
          record={person}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="firstName" className="">First name:</Label>
        <Input
          id="firstName"
          disabled={!person}
          readOnly={!updating}
          value={person?.firstName ?? ''}
          onChange={e => dispatch("setFirstName", e.target.value)}
        />
        <Label htmlFor="prefix" className="">Prefix:</Label>
        <Input
          id="prefix"
          disabled={!person}
          readOnly={!updating}
          value={person?.prefix ?? ''}
          onChange={e => dispatch("setPrefix", e.target.value)}
        />
        <Label htmlFor="lastName" className="col-start-1">Last name:</Label>
        <Input
          id="lastName"
          disabled={!person}
          readOnly={!updating}
          value={person?.lastName ?? ''}
          onChange={e => dispatch("setLastName", e.target.value)}
        />
        <Label htmlFor="suffix" className="">Suffix:</Label>
        <Input
          id="suffix"
          disabled={!person}
          readOnly={!updating}
          value={person?.suffix ?? ''}
          onChange={e => dispatch("setSuffix", e.target.value)}
        />
        <Label htmlFor="alias" className="col-start-1">Alias:</Label>
        <Input
          id="alias"
          className="col-span-2"
          disabled={!person}
          readOnly={!updating}
          value={person?.alias ?? ''}
          onChange={e => dispatch("setAlias", e.target.value)}
        />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!person}
          readOnly={!updating}
          value={person?.notes ?? ''}
          onChange={e => dispatch("setNotes", e.target.value)}
        />
        <Label htmlFor="qualifications" className="col-start-1">Qualifications:</Label>
        <Textarea
          id="qualifications"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!person}
          readOnly={!updating}
          value={person?.qualifications ?? ''}
          onChange={e => dispatch("setQualifications", e.target.value)}
        />
        <Label htmlFor="country" className="col-start-1">Country:</Label>
        <Select
          disabled={!updating}
          value={person?.country ?? ''}
          onValueChange={value => dispatch("setCountry", value)}
        >
          <SelectTrigger id="country" className="w-[180px]" disabled={!person}>
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
        <Label htmlFor="rating" className="">Rating:</Label>
        <Input
          id="rating"
          type="number"
          disabled={!person}
          readOnly={!updating}
          placeholder="title"
          value={person?.rating ?? ''}
          onChange={e => dispatch("setRating", e.target.value)}
        />
        <Label htmlFor="checked" className="col-start-1">Checked:</Label>
        <Checkbox
          id="checked"
          disabled={!updating}
          checked={person?.checked}
          onCheckedChange={checked => dispatch("setChecked", checked)}
        />
        <Label htmlFor="published" className="">Published:</Label>
        <Checkbox
          id="published"
          disabled={!updating}
          checked={person?.published}
          onCheckedChange={checked => dispatch("setPublished", checked)}
        />
      </div>
    </fieldset>
  )
}