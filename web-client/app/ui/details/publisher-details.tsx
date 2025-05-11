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
import useDetailHandlers from "./detail-handlers";
import DetailActions from "./detail-actions";

const countries = rawCountries as unknown as Country[]

export default function PublisherDetails({record}: {record: Publisher | undefined}) {
  const state = useDetailHandlers<Publisher>("Publisher", record)
  const { updating } = state

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Publisher Details&nbsp;</legend>
      <StandardDetails recordKind="Publisher" record={record} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publisher #${record?.id}` : "-Select a publisher in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="name" className="col-start-1">Name:</Label>
        <Input id="name" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.name ?? ''} />
        <DetailActions className="col-start-6 row-span-5" recordKind="Publisher" record={record} state={state} />
        <Label htmlFor="location" className="col-start-1">Location:</Label>
        <Input id="location" className="col-span-2" disabled={!record} readOnly={!updating} value={record?.location ?? ''} />
        <Label htmlFor="country">Country:</Label>
        <Select disabled={!updating} value={record?.country ?? ''}>
          <SelectTrigger id="country" className="" disabled={!record}>
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
        <Input id="url" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.url?.toString() ?? ''} />
        <Label htmlFor="journalCount" className="col-start-1">Journal count:</Label>
        <Input type="number" id="journalCount" className="col-span-1" disabled={!record} readOnly={!updating} value={record?.journalCount ?? ''} />
      </div>
    </fieldset>
  )
}