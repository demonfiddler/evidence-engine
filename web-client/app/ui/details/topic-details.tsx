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
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useMemo } from "react"
import { useFormContext } from "react-hook-form"
import { TopicFieldValues } from "../validators/topic"
import { FormActionHandler } from "@/hooks/use-page-logic"
import { flatten } from "@/lib/utils"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"

export default function TopicDetails(
  {
    record,
    topics,
    state,
    setMode,
    onFormAction
  } : {
    record?: Topic
    topics: Topic[]
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<TopicFieldValues>
  }) {

  const form = useFormContext()
  const { updating } = state

  const flatTopics = useMemo(() => {
    const flattened = flatten(topics, /*"", */[])
    // FIXME: A topic can't be its own ancestor
    const topicIdx = flattened.findIndex(t => t.id == record?.id)
    flattened.splice(topicIdx, 1)
    return flattened
  }, [topics, record])

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Topic Details&nbsp;</legend>
      <StandardDetails recordKind="Topic" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Topic"
                : record
                  ? `Details for selected Topic #${record?.id}`
                  : "-Select a Topic in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-5 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="path"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Path</FormLabel>
                  <FormControl>
                    <InputEx
                      id="path"
                      className="col-span-4"
                      readOnly={true}
                      placeholder="path"
                      {...field}
                      help="The full path to the topic, including ancestor labels. Non-editable."
                    />
                  </FormControl>
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
                      <SelectTriggerEx
                        outerClassName="col-span-4"
                        className="grow"
                        id="parentId"
                        help="The parent topic, if any"
                      >
                        <SelectValue className="col-span-4 w-full" placeholder="Specify parent" />
                      </SelectTriggerEx>
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
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="label"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Label</FormLabel>
                  <FormControl>
                    <InputEx
                      id="label"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="label"
                      {...field}
                      help="The short topic label"
                    />
                  </FormControl>
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
                    <InputEx
                      outerClassName="w-full"
                      className="grow"
                      id="description"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="description"
                      {...field}
                      help="The long topic description"
                    />
                  </FormControl>
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