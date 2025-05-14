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
import useDetailHandlers from "./detail-handlers";
import DetailActions from "./detail-actions";
import Link from "next/link";

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

export default function PublicationDetails({record}: {record: Publication | undefined}) {
  const state = useDetailHandlers<Publication>("Publication", record)
  const { updating } = state

  function setDate(e: any) {
    console.log(`selected ${JSON.stringify(e)}`)
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={record} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publication #${record?.id}` : "-Select a publication in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Text:</Label>
        <Input id="title" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.title ?? ''} />
        <DetailActions className="col-start-6 row-span-3" recordKind="Publication" record={record} state={state} />
        <Label htmlFor="authors" className="col-start-1">Authors:</Label>
        <Textarea id="authors" className="col-span-2 h-40 overflow-y-auto" disabled={!record} readOnly={!updating} value={record?.authors ?? ''} />
        <Label htmlFor="kind" className="col-start-1">Kind:</Label>
        <Select disabled={!updating} value={record?.kind ?? ''}>
          <SelectTrigger id="kind" className="w-full" disabled={!record}>
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
        <Select disabled={!updating} value={record?.journal?.id?.toString() ?? ''}>
          <SelectTrigger id="journal" className="col-span-1 w-full" disabled={!record}>
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
        <Label htmlFor="year" className="col-start-4">Year:</Label>
        <Input type="number" id="year" className="" disabled={!record} readOnly={!updating} value={record?.year ?? ''} />
        <Label htmlFor="abstract" className="col-start-1">Abstract:</Label>
        <Textarea id="abstract" className="col-span-4 h-40 overflow-y-auto" disabled={!record} readOnly={!updating} value={record?.abstract ?? ''} />
        <Label htmlFor="peerReviewed" className="col-start-1">Peer reviewed:</Label>
        <Checkbox id="peerReviewed" className="col-span-2" disabled={!record || !updating} checked={record?.peerReviewed} />
        <Label htmlFor="cached" className="">Cached:</Label>
        <Checkbox id="cached" className="" disabled={!record || !updating} checked={record?.cached} />
        <Label htmlFor="doi" className="col-start-1">DOI:</Label>
        {
          updating
          ? <Input id="doi" className="col-span-2" value={record?.doi ?? ''} />
          : <Link className="col-span-2" href={record?.doi ? `https://doi.org/${record?.doi ?? ''}` : ''} target="_blank">{record?.doi ?? ''}</Link>
        }
        <Label htmlFor="isbn">ISBN:</Label>
        <Input id="isbn" className="" disabled={!record} readOnly={!updating} value={record?.isbn ?? ''} />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input id="url" className="col-span-2" value={record?.url?.toString() ?? ''} />
          : <Link className="col-span-2" href={record?.url?.toString() ?? ''} target="_blank">{record?.url?.toString() ?? ''}</Link>
        }
        <Label htmlFor="accessed">Accessed:</Label>
        <Popover>
          <PopoverTrigger id="accessed" asChild>
            <Button
              variant={"outline"}
              disabled={!updating}
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
        <Textarea id="notes" className="col-span-4 h-40 overflow-y-auto" disabled={!record} readOnly={!updating} value={record?.notes ?? ''} />
      </div>
    </fieldset>
  )
}