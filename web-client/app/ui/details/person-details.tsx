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
import useDetailHandlers from "./detail-handlers";
import DetailActions from "./detail-actions";

export default function PersonDetails({record}: {record: Person | undefined}) {
  const countries = rawCountries as unknown as Country[]
  const state = useDetailHandlers<Person>("Person", record)
  const { updating } = state

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Person Details&nbsp;</legend>
      <StandardDetails recordKind="Person" record={record} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Person #${record?.id}` : "-Select a person in the list above to see his/her details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input id="title" className="" disabled={!record} readOnly={!updating} placeholder="title" value={record?.title ?? ''} />
        <Label htmlFor="nickname" className="">Nickname:</Label>
        <Input id="nickname" className="" disabled={!record} readOnly={!updating} value={record?.nickname ?? ''} />
        <DetailActions className="col-start-6 row-span-8" recordKind="Person" record={record} state={state} />
        <Label htmlFor="firstName" className="">First name:</Label>
        <Input id="firstName" className="" disabled={!record} readOnly={!updating} value={record?.firstName ?? ''} />
        <Label htmlFor="prefix" className="">Prefix:</Label>
        <Input id="prefix" className="" disabled={!record} readOnly={!updating} value={record?.prefix ?? ''} />
        <Label htmlFor="lastName" className="col-start-1">Last name:</Label>
        <Input id="lastName" className="" disabled={!record} readOnly={!updating} value={record?.lastName ?? ''} />
        <Label htmlFor="suffix" className="">Suffix:</Label>
        <Input id="suffix" className="" disabled={!record} readOnly={!updating} value={record?.suffix ?? ''} />
        <Label htmlFor="alias" className="col-start-1">Alias:</Label>
        <Input id="alias" className="col-span-2" disabled={!record} readOnly={!updating} value={record?.alias ?? ''} />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.notes ?? ''} />
        <Label htmlFor="qualifications" className="col-start-1">Qualifications:</Label>
        <Textarea id="qualifications" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.qualifications ?? ''} />
        <Label htmlFor="country" className="col-start-1">Country:</Label>
        <Select disabled={!updating} value={record?.country ?? ''}>
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
        <Label htmlFor="rating" className="">Rating:</Label>
        <Input id="rating" type="number" className="" disabled={!record} readOnly={!updating} placeholder="title" value={record?.rating ?? ''} />
        <Label htmlFor="checked" className="col-start-1">Checked:</Label>
        <Checkbox id="checked" className="" disabled={!record || !updating} checked={record?.checked} />
        <Label htmlFor="published" className="">Published:</Label>
        <Checkbox id="published" className="" disabled={!record || !updating} checked={record?.published} />
      </div>
    </fieldset>
  )
}