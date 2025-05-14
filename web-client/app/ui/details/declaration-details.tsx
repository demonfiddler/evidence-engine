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
import { cn, formatDate } from "@/lib/utils"
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
import useDetailHandlers from "./detail-handlers"
import DetailActions from "./detail-actions"
import Link from "next/link"
const countries = rawCountries as unknown as Country[]

export default function DeclarationDetails({record}: {record: Declaration | undefined}) {
  const state = useDetailHandlers<Declaration>("Declaration", record)
  const { updating } = state

  function setDate(e: any) {
    console.log(`selected ${JSON.stringify(e)}`)
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Declaration Details&nbsp;</legend>
      <StandardDetails recordKind="Declaration" record={record} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Declaration #${record?.id}` : "-Select a declaration in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="date" className="text-right">Date:</Label>
        <Popover>
          <PopoverTrigger asChild id="date" disabled={!record}>
            <Button
              variant={"outline"}
              disabled={!updating}
              className={cn("w-full justify-start text-left font-normal",
                (!record || !record.date) && "text-muted-foreground")}
            >
              <CalendarIcon />
              {formatDate(record?.date, "PPP")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="col-span-2 w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={record?.date}
              onSelect={setDate}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        <Label htmlFor="kind" className="col-start-4">Kind:</Label>
        <Select disabled={!record || !updating} value={record?.kind ?? ''}>
          <SelectTrigger id="kind" className="w-[180px]" disabled={!record}>
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
        <DetailActions className="col-start-6 row-span-6" recordKind="Declaration" record={record} state={state} />
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input id="title" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.title ?? ''} />
        <Label htmlFor="url" className="col-start-1">URL:</Label>
        {
          updating
          ? <Input type="url" className="col-span-4" placeholder="URL" value={record?.url?.toString() ?? ''} />
          : <Link className="col-span-4" href={record?.url?.toString() ?? ''} target="_blank">{record?.url?.toString() ?? ''}</Link>
        }
        <Label htmlFor="cached" className="col-start-1">Cached:</Label>
        <Checkbox id="cached" className="col-span-2" disabled={!record || !updating} checked={record?.cached} />
        <Label htmlFor="country">Country:</Label>
        <Select disabled={!record || !updating} value={record?.country ?? ''}>
          <SelectTrigger id="country" className="w-[180px]" disabled={!record}>
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
        <Textarea id="signatories" className="col-span-2 h-40 overflow-y-auto" disabled={!record} readOnly={!updating} value={record?.signatories ?? ''} />
        <Label htmlFor="signatoryCount" className="">Signatory count:</Label>
        <Input id="signatoryCount" type="signatoryCount" disabled={!record} readOnly={!updating} placeholder="count" value={record?.signatoryCount ?? ''} />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4 h-40 overflow-y-auto" disabled={!record} readOnly={!updating} value={record?.notes ?? ''} />
      </div>
    </fieldset>
  )
}