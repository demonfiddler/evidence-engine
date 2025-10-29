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
  SelectItem,
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
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(detail, "[TopicDetails] ")

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
  logger.debug("render")

  // I thought this nonsense might be necessary because according to WhyDidYouRender, successive calls to useFormContext
  // apparently return different objects that are equal by value.
  // const rawForm = useFormContext<TopicFieldValues>()
  // const prevForm = useRef(rawForm)
  // let form
  // if (isEqual(rawForm, prevForm.current)) {
  //   form = prevForm.current
  // } else {
  //   logger.debug("render: form has changed")
  //   form = prevForm.current = rawForm
  // }
  const form = useFormContext<TopicFieldValues>()

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
              name="rating"
              render={({field}) => (
                <FormItem>
                  <FormLabel id="rating-label">Rating</FormLabel>
                  <FormControl>
                    <StarRatingBasicEx
                      id="rating"
                      ariaLabelledby="rating-label"
                      disabled={!updating}
                      maxStars={5}
                      iconSize={18}
                      className="ml-2 w-full"
                      value={field.value ?? 0}
                      onChange={field.onChange}
                      help="A five-star rating for the topic, indicative of significance, evidence base, etc."
                    />
                  </FormControl>
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
              name="path"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="path">Path</FormLabel>
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
                  <FormLabel htmlFor="parentId">Parent</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="parentId"
                        outerClassName="col-span-4"
                        className="grow"
                        help="The parent topic, if any"
                      >
                        <SelectValue className="col-span-4 w-full" placeholder="Specify parent" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
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
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="label"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="label">Label</FormLabel>
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
                  <FormLabel htmlFor="description">Description</FormLabel>
                  <FormControl>
                    <InputEx
                      id="description"
                      outerClassName="w-full"
                      className="grow"
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

TopicDetails.whyDidYouRender = true