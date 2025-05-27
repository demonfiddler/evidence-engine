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

import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
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
import Journal from "@/app/model/Journal";
import Publisher from "@/app/model/Publisher";
import rawPublishers from "@/data/publishers.json" assert {type: 'json'}
import StandardDetails from "./standard-details";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import Link from "next/link";
import { Action } from "@/lib/utils";
import { Dispatch, useContext, useState } from "react";
import { SecurityContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";

const publishers = rawPublishers.content as unknown as Publisher[]

function journalReducer(draft?: Journal, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setAbbreviation":
        draft.abbreviation = action.value
        break
      case "setIssn":
        draft.issn = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
      case "setPublisher":
        draft.publisher = action.value
        break
      case "setStatus":
        draft.status = action.value
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

export default function JournalDetails(
  {record, pageDispatch}:
  {record?: Journal; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(journalReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const journal = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: journal?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Journal Details&nbsp;</legend>
      <StandardDetails recordKind="Journal" record={journal} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{journal ? `Details for selected Journal #${journal?.id}` : "-Select a journal in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input
          id="title"
          className="col-span-4"
          disabled={!journal}
          readOnly={!updating}
          value={journal?.title ?? ''}
          onChange={e => dispatch("setTitle", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-5"
          recordKind="Journal"
          record={journal}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="abbreviation" className="col-start-1">Abbreviation:</Label>
        <Input
          id="abbreviation"
          className="col-span-2"
          disabled={!journal}
          readOnly={!updating}
          value={journal?.abbreviation ?? ''}
          onChange={e => dispatch("setAbbreviation", e.target.value)}
        />
        <Label htmlFor="issn" className="">ISSN:</Label>
        <Input
          id="issn"
          className="col-span-1"
          disabled={!journal}
          readOnly={!updating}
          value={journal?.issn ?? ''}
          onChange={e => dispatch("setIssn", e.target.value)}
        />
        <Label htmlFor="publisher" className="col-start-1">Publisher:</Label>
        <Select
          disabled={!updating}
          value={journal?.publisher?.id?.toString() ?? ''}
          onValueChange={value => dispatch("setPublisher", value)}
        >
          <SelectTrigger id="publisher" className="col-span-2 w-full" disabled={!journal}>
            <SelectValue className="col-span-2 w-full" placeholder="Specify publisher" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Publishers</SelectLabel> {
                publishers.map(publisher => <SelectItem key={publisher.id?.toString() ?? ''} value={publisher.id?.toString() ?? ''}>{publisher.name}</SelectItem>)
              }
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input
              id="url"
              className="col-span-4"
              value={journal?.url ?? ''}
              onChange={e => dispatch("setUrl", e.target.value)}
            />
          : <Link className="col-span-4" href={journal?.url ?? ''} target="_blank">{journal?.url ?? ''}</Link>
        }
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!journal}
          readOnly={!updating}
          value={journal?.notes ?? ''}
          onChange={e => dispatch("setNotes", e.target.value)}
        />
      </div>
    </fieldset>
  )
}