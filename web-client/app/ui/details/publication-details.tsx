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
import { Action, cn, formatDate } from "@/lib/utils";
import { CalendarIcon } from "@heroicons/react/24/outline";
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
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import Link from "next/link";
import { Dispatch, useContext, useState } from "react";
import { SecurityContext } from "@/lib/context";
import { useImmerReducer } from "use-immer";

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

function publicationReducer(draft?: Publication, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setAbstract":
        draft.abstract = action.value
        break
      case "setAccessed":
        draft.accessed = action.value
        break
      case "setAuthors":
        draft.authors = action.value
        break
      case "setCached":
        draft.cached = action.value
        break
      case "setDate":
        draft.date = action.value
        break
      case "setDoi":
        draft.doi = action.value
        break
      case "setIsbn":
        draft.isbn = action.value
        break
      case "setJournal":
        draft.journal = action.value
        break
      case "setKind":
        draft.kind = action.value
        break
      case "setNotes":
        draft.notes = action.value
        break
      case "setPeerReviewed":
        draft.peerReviewed = action.value
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
      case "setYear":
        draft.year = action.value
        break
    }
  }
}

export default function PublicationDetails(
  { record, pageDispatch }:
  { record?: Publication; pageDispatch: Dispatch<Action> }) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(publicationReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const publication = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: publication?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={publication} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publication #${publication?.id}` : "-Select a publication in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Text:</Label>
        <Input
          id="title"
          className="col-span-4"
          disabled={!publication}
          readOnly={!updating}
          value={publication?.title ?? ''}
          onChange={e => dispatch("setTitle", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-3"
          recordKind="Publication"
          record={publication}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="authors" className="col-start-1">Authors:</Label>
        <Textarea
          id="authors"
          className="col-span-2 h-40 overflow-y-auto"
          disabled={!publication}
          readOnly={!updating}
          value={publication?.authors ?? ''}
          onChange={e => dispatch("setAuthors", e.target.value)}
        />
        <Label htmlFor="kind" className="col-start-1">Kind:</Label>
        <Select
          disabled={!updating}
          value={publication?.kind ?? ''}
          onValueChange={value => dispatch("setKind", value)}
        >
          <SelectTrigger id="kind" className="w-full" disabled={!publication}>
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
        <Label htmlFor="journal" className="col-start-4">Journal:</Label>
        <Select
          disabled={!updating}
          value={publication?.journal?.id?.toString() ?? ''}
          onValueChange={value => dispatch("setJournal", value)}
        >
          <SelectTrigger id="journal" className="col-span-1 w-full" disabled={!publication}>
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
              disabled={!updating}
              className={cn("justify-start text-left font-normal",
                (!publication || !publication.date) && "text-muted-foreground")}>
              <CalendarIcon />
              {formatDate(publication?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={publication?.date}
              onSelect={(day, selectedDay, activeModifiers) => dispatch("setDate", selectedDay)}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="year" className="col-start-4">Year:</Label>
        <Input
          type="number"
          id="year"
          className=""
          disabled={!publication}
          readOnly={!updating}
          value={publication?.year ?? ''}
          onChange={e => dispatch("setYear", e.target.value)}
        />
        <Label htmlFor="abstract" className="col-start-1">Abstract:</Label>
        <Textarea
          id="abstract"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!publication}
          readOnly={!updating}
          value={publication?.abstract ?? ''}
          onChange={e => dispatch("setAbstract", e.target.value)}
        />
        <Label htmlFor="peerReviewed" className="col-start-1">Peer reviewed:</Label>
        <Checkbox
          id="peerReviewed"
          className="col-span-2"
          disabled={!updating}
          checked={publication?.peerReviewed}
          onCheckedChange={checked => dispatch("setPeerReviewed", checked)}
        />
        <Label htmlFor="cached" className="">Cached:</Label>
        <Checkbox
          id="cached"
          disabled={!updating}
          checked={publication?.cached}
          onCheckedChange={checked => dispatch("setCached", checked)}
        />
        <Label htmlFor="doi" className="col-start-1">DOI:</Label>
        {
          updating
          ? <Input
              id="doi"
              className="col-span-2"
              value={publication?.doi ?? ''}
              onChange={e => dispatch("setDoi", e.target.value)}
            />
          : <Link className="col-span-2" href={publication?.doi ? `https://doi.org/${publication?.doi ?? ''}` : ''} target="_blank">{publication?.doi ?? ''}</Link>
        }
        <Label htmlFor="isbn">ISBN:</Label>
        <Input
          id="isbn"
          disabled={!publication}
          readOnly={!updating}
          value={publication?.isbn ?? ''}
          onChange={e => dispatch("setIsbn", e.target.value)}
        />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input
              id="url"
              className="col-span-2"
              value={publication?.url ?? ''}
              onChange={e => dispatch("setUrl", e.target.value)}
            />
          : <Link className="col-span-2" href={publication?.url ?? ''} target="_blank">{publication?.url ?? ''}</Link>
        }
        <Label htmlFor="accessed">Accessed:</Label>
        <Popover>
          <PopoverTrigger id="accessed" asChild>
            <Button
              variant={"outline"}
              disabled={!updating}
              className={cn("justify-start text-left font-normal",
                (!publication || !publication.date) && "text-muted-foreground")}>
              <CalendarIcon />
              {formatDate(publication?.accessed, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={publication?.accessed}
              onSelect={(day, selectedDay, activeModifiers) => dispatch("setAccessed", selectedDay)}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea
          id="notes"
          className="col-span-4 h-40 overflow-y-auto"
          disabled={!publication}
          readOnly={!updating}
          value={publication?.notes ?? ''}
          onChange={e => dispatch("setNotes", e.target.value)}
        />
      </div>
    </fieldset>
  )
}