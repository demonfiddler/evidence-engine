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
import { Action, setTopicFields } from "@/lib/utils"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { Dispatch, useContext, useState } from "react"
import { SecurityContext } from "@/lib/context"
import { useImmerReducer } from "use-immer"

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

function topicReducer(draft?: Topic, action?: Action) {
  if (draft && action) {
    switch (action.command) {
      case "new":
        Object.keys(draft).forEach(key => (draft as any)[key] = undefined)
        break
      case "edit":
        Object.assign(draft, action.value);
        break
      case "setDescription":
        draft.description = action.value
        break
      case "setLabel":
        draft.label = action.value
        break
      case "setParentId":
        draft.parentId = action.value
        break
      case "setStatus":
        draft.status = action.value
        break
    }
  }
}

export default function TopicDetails(
  {record, pageDispatch}:
  {record?: Topic; pageDispatch: Dispatch<Action>}) {

  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [mutableRecord, recordDispatch] = useImmerReducer(topicReducer, record ?? {})

  const state = createDetailState(securityContext, mode, record)
  const { updating } = state
  const topic = updating ? mutableRecord : record

  function dispatch(command: string, value: any) {
    recordDispatch({recordId: topic?.id ?? "0", command: command, value: value})
  }

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Topic Details&nbsp;</legend>
      <StandardDetails recordKind="Topic" record={topic} state={state} showLinkingDetails={true} />
      <p className="pt-2 pb-4">&nbsp;&nbsp;{topic ? `Details for selected Topic #${topic?.id}` : "-Select a topic in the list above to see its details-"}</p>
      <div className="grid grid-cols-6 ml-2 mr-2 mb-2 gap-2 items-center">
        <Label htmlFor="path" className="col-start-1">Path:</Label>
        <Input
          id="path"
          className="col-span-4"
          readOnly={true}
          value={topic?.path}
          // onChange={e => dispatch("setPath", e.target.value)}
        />
        <DetailActions
          className="col-start-6 row-span-5"
          recordKind="Topic"
          record={topic}
          state={state}
          setMode={setMode}
          pageDispatch={pageDispatch}
          recordDispatch={recordDispatch}
        />
        <Label htmlFor="parent" className="col-start-1">Parent:</Label>
        <Select
          disabled={!updating}
          value={topic?.parentId?.toString() ?? ''}
          onValueChange={value => dispatch("setParentId", value)}
        >
          <SelectTrigger id="parent" className="col-span-4" disabled={!topic}>
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
          disabled={!topic}
          readOnly={!updating}
          placeholder="label"
          value={topic?.label ?? ''}
          onChange={e => dispatch("setLabel", e.target.value)}
        />
        <Label htmlFor="description" className="">Description:</Label>
        <Input
          id="description"
          className="col-span-4"
          disabled={!topic}
          readOnly={!updating}
          placeholder="description"
          value={topic?.description ?? ''}
          onChange={e => dispatch("setDescription", e.target.value)}
        />
      </div>
    </fieldset>
  )
}