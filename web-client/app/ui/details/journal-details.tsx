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

import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import Journal from "@/app/model/Journal"
import Publisher from "@/app/model/Publisher"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction } from "react"
import { useFormContext } from "react-hook-form"
import { JournalFieldValues } from "../validators/journal"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"
import TextareaEx from "../ext/textarea-ex"
import LinkEx from "../ext/link-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"
import { READ_PUBLISHERS } from "@/lib/graphql-queries"
import IPage from "@/app/model/IPage"
import { QueryResult } from "@/lib/graphql-utils"
import { useQuery } from "@apollo/client/react"
import CheckboxEx from "../ext/checkbox-ex"
import { NotebookTabsIcon } from "lucide-react"

const logger = new LoggerEx(detail, "[JournalDetails] ")

export default function JournalDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Journal
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<JournalFieldValues>
  }) {
  logger.debug("render")

  const form = useFormContext<JournalFieldValues>()
  const { updating } = state
  const publishersResult = useQuery(READ_PUBLISHERS)
  const publishersData = (publishersResult.loading
    ? publishersResult.previousData
    : publishersResult.data) as QueryResult<IPage<Publisher>>
  const rawPublishers = publishersData
    ? publishersData.publishers
    : undefined
  const publishers = rawPublishers?.content ?? []

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Journal Details&nbsp;</legend>
      <StandardDetails recordKind="Journal" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-black text-lg ml-2"><NotebookTabsIcon className="inline" />&nbsp;Details</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
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
                      help="A five-star rating for the journal, indicative of prestige, impact, circulation, etc."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="peerReviewed"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="peerReviewed">Peer Reviewed</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="peerReviewed"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the journal publishes peer-reviewed articles"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-3 row-span-5"
              recordKind="Journal"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="title">Title</FormLabel>
                  <FormControl>
                    <InputEx
                      id="title"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The journal's official name or title"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="abbreviation"
              render={({field}) => (
                <FormItem className="col-span-1">
                  <FormLabel htmlFor="abbreviation">Abbreviation</FormLabel>
                  <FormControl>
                    <InputEx
                      id="abbreviation"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The ISO 4 journal title abbreviation, with full stops for LTWA abbreviated words"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="issn"
              render={({field}) => (
                <FormItem className="col-span-1">
                  <FormLabel htmlFor="issn">ISSN</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                        id="issn"
                        className="col-span-1"
                        disabled={!record && !updating}
                        readOnly={!updating}
                        {...field}
                        help="The International Standard Serial Number (ISSN)"
                      />
                      : <LinkEx
                          id="issn"
                          href={record?.issn ? `https://portal.issn.org/resource/ISSN/${record.issn}` : ''}
                          target="_blank"
                          help="The International Standard Serial Number (ISSN)"
                      >
                        {record?.issn ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="publisherId"
              render={({ field }) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="publisherId">Publisher</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="publisherId"
                        className="w-full"
                        disabled={!updating}
                        help="The journal publisher"
                      >
                        <SelectValue className="w-full" placeholder="Specify publisher" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        publishers.map(publisher => (
                          <SelectItem
                            key={publisher.id?.toString() ?? ''}
                            value={publisher.id?.toString() ?? ''}>
                            {publisher.name}
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
              name="url"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="url">URL</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                        id="url"
                        {...field}
                        help="The journals's online web address"
                      />
                      : <LinkEx
                        id="url"
                        href={record?.url ?? ''}
                        target="_blank"
                        help="The journals's online web address"
                      >
                        {record?.url ?? ''}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="notes"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-2">
                  <FormLabel htmlFor="notes">Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Contributor notes about the journal"
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

JournalDetails.whyDidYouRender = true