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
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details"
import { FormAction } from "@/lib/utils"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import { useContext, useMemo, useState } from "react"
import useAuth from "@/hooks/use-auth"
import { useFormContext } from "react-hook-form"
import { TopicFieldValues } from "../validators/topic"

function flatten(topics: Topic[], result: Topic[]) : Topic[] {
  for (let topic of topics) {
    result.push(topic)
    if (topic.children)
      flatten(topic.children, result)
  }
  return result
}

export default function TopicDetails(
  { record, topics, onFormAction }:
  { record?: Topic; topics: Topic[], onFormAction: (command: FormAction, fieldValues: TopicFieldValues) => void }) {

  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  const flatTopics = useMemo(() => {
    const flattened = flatten(topics, /*"", */[])
    // FIXME: A topic can't be its own ancestor
    const topicIdx = flattened.findIndex(t => t.id == record?.id)
    flattened.splice(topicIdx, 1)
    return flattened
  }, [topics, record])

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Topic Details&nbsp;</legend>
      <StandardDetails recordKind="Topic" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Topic #${record?.id}` : "-Select a topic in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-5 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="path"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Path</FormLabel>
                  <FormControl>
                    <Input
                      id="path"
                      className="col-span-4"
                      readOnly={true}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The full path to the topic
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="parentId"
              render={({ field }) => (
                <FormItem className="col-span-2">
                  <FormLabel>Parent</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTrigger id="parentId" className="col-span-4" disabled={!record}>
                        <SelectValue className="col-span-4 w-full" placeholder="Specify parent" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Topics</SelectLabel>
                        {
                          record?.parentId
                          ? (
                            <SelectItem
                              key="0"
                              value="0"
                            >
                              <span>-Clear Selection-</span>
                            </SelectItem>
                          )
                          : null
                        }
                        {
                          flatTopics.map(topic => (
                            <SelectItem
                              key={topic.id ?? ''}
                              value={topic.id ?? ''}
                            >
                              {`Topic #${topic.id}: ${topic.path}`}
                            </SelectItem>
                          ))
                        }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The parent topic
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-5 row-span-5"
              recordKind="Topic"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="label"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Label</FormLabel>
                  <FormControl>
                    <Input
                      id="label"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      placeholder="label"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The short topic label
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="description"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Input
                      id="description"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      placeholder="description"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The long topic description
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        </form>
      </Form>
    </fieldset>
  )
}