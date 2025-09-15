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
import rawPublishers from "@/data/publishers.json" assert {type: 'json'}
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

const publishers = rawPublishers.content as unknown as Publisher[]

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

  const form = useFormContext<JournalFieldValues>()
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Journal Details&nbsp;</legend>
      <StandardDetails recordKind="Journal" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Journal"
                : record
                  ? `Details for selected Journal #${record?.id}`
                  : "-Select a Journal in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Title</FormLabel>
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
              name="abbreviation"
              render={({field}) => (
                <FormItem className="col-span-1">
                  <FormLabel>Abbreviation</FormLabel>
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
                  <FormLabel>ISSN</FormLabel>
                  <FormControl>
                    <InputEx
                      id="issn"
                      className="col-span-1"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The International Standard Serial Number (ISSN)"
                    />
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
                  <FormLabel>Publisher</FormLabel>
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
                  <FormLabel>URL</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="url"
                          {...field}
                          help="The journals's online web address"
                        />
                      : <LinkEx
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
                  <FormLabel>Notes</FormLabel>
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