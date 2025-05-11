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
import useDetailHandlers from "./detail-handlers";
import DetailActions from "./detail-actions";

const publishers = rawPublishers.content as unknown as Publisher[]

export default function JournalDetails({record}: {record: Journal | undefined}) {
  const state = useDetailHandlers<Journal>("Journal", record)
  const { updating } = state

  return (
    <fieldset className="border rounded-md w-2/3">
      <legend>&nbsp;Journal Details&nbsp;</legend>
      <StandardDetails recordKind="Journal" record={record} state={state} showLinkingDetails={false} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Journal #${record?.id}` : "-Select a journal in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="title" className="col-start-1">Title:</Label>
        <Input id="title" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.title ?? ''} />
        <DetailActions className="col-start-6 row-span-5" recordKind="Journal" record={record} state={state} />
        <Label htmlFor="abbreviation" className="col-start-1">Abbreviation:</Label>
        <Input id="abbreviation" className="col-span-2" disabled={!record} readOnly={!updating} value={record?.abbreviation ?? ''} />
        <Label htmlFor="issn" className="">ISSN:</Label>
        <Input id="issn" className="col-span-1" disabled={!record} readOnly={!updating} value={record?.issn ?? ''} />
        <Label htmlFor="publisher" className="col-start-1">Publisher:</Label>
        <Select disabled={!updating} value={record?.publisher?.id?.toString() ?? ''}>
          <SelectTrigger id="publisher" className="col-span-2" disabled={!record}>
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
        <Input id="url" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.url?.toString() ?? ''} />
        <Label htmlFor="notes" className="col-start-1">Notes:</Label>
        <Textarea id="notes" className="col-span-4" disabled={!record} readOnly={!updating} value={record?.notes ?? ''} />
      </div>
    </fieldset>
  )
}