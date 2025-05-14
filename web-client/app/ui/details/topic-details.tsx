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

import Topic from "@/app/model/Topic"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import rawTopics from "@/data/topics.json" assert {type: 'json'}
import StandardDetails from "./standard-details"
import { setTopicFields } from "@/lib/utils"
import useDetailHandlers from "./detail-handlers"
import DetailActions from "./detail-actions"

const topics = rawTopics.content as unknown as Topic[]
setTopicFields("", undefined, topics)
function flatten(topics: Topic[], result: Topic[]) : Topic[] {
  for (let topic of topics) {
    result.push({
      id: topic.id,
      parentId: topic.parentId,
      path: topic.path
    })
    if (topic.children)
      flatten(topic.children, result)
  }
  return result
}
const flatTopics = flatten(topics, [])

export default function TopicDetails({record}: {record: Topic | undefined}) {
  const state = useDetailHandlers<Topic>("Topic", record)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Topic Details&nbsp;</legend>
      <StandardDetails recordKind="Topic" record={record} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Topic #${record?.id}` : "-Select a topic in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="path" className="col-start-1">Path:</Label>
        <Input id="path" className="col-span-4" readOnly={true} value={record?.path} />
        <DetailActions className="col-start-6 row-span-5" recordKind="Topic" record={record} state={state} />
        <Label htmlFor="parent" className="col-start-1">Parent:</Label>
        <Select disabled={!updating} value={record?.parentId?.toString() ?? ''}>
          <SelectTrigger id="parent" className="col-span-4" disabled={!record}>
            <SelectValue className="col-span-4 w-full" placeholder="Specify parent" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Topics</SelectLabel> {
                flatTopics.map(topic => <SelectItem key={topic.id?.toString() ?? ''} value={topic.id?.toString() ?? ''}>
                  {`Topic #${topic.id}: ${topic.path}`}
                </SelectItem>)
              }
            </SelectGroup>
          </SelectContent>
        </Select>
        <Label htmlFor="label" className="col-start-1">Label:</Label>
        <Input
          id="label"
          className="col-span-4"
          disabled={!record}
          readOnly={!updating}
          placeholder="label"
          value={record?.label ?? ''}
        />
        <Label htmlFor="description" className="">Description:</Label>
        <Input
          id="description"
          className="col-span-4"
          disabled={!record}
          readOnly={!updating}
          placeholder="description"
          value={record?.description ?? ''}
        />
      </div>
    </fieldset>
  )
}