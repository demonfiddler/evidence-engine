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

import Publication from "@/app/model/Publication"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { cn, formatDate } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select"
import rawJournals from "@/data/journals.json" assert {type: 'json'}
import rawPublicationKinds from "@/data/publication-kinds.json" assert {type: 'json'}
import Journal from "@/app/model/Journal"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useState } from "react"
import { useFormContext } from "react-hook-form"
import { PublicationFieldValues } from "../validators/publication"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"
import TextareaEx from "../ext/textarea-ex"
import ButtonEx from "../ext/button-ex"
import LinkEx from "../ext/link-ex"
import CheckboxEx from "../ext/checkbox-ex"

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

export default function PublicationDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Publication
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<PublicationFieldValues>
  }) {

  const form = useFormContext<PublicationFieldValues>()
  const [dateOpen, setDateOpen] = useState(false)
  const [accessedOpen, setAccessedOpen] = useState(false)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Publication"
                : record
                  ? `Details for selected Publication #${record?.id}`
                  : "-Select a Publication in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-4 ml-2 mr-2 mt-4 mb-4 gap-4 items-start">
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-3">
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <InputEx
                      id="title"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The publication name/title"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-4 row-span-7"
              recordKind="Publication"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="kind"
              render={({ field }) => (
                <FormItem className="col-start-1">
                  <FormLabel>Kind</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="kind"
                        className="w-full"
                        help="The kind of publication (corresponds to the RIS 'TY' field)"
                      >
                        <SelectValue placeholder="Specify kind" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        publicationKinds.map(kind =>
                          <SelectItem
                            key={kind.kind?.toString() ?? ''}
                            value={kind.kind?.toString() ?? ''}>
                            {kind.label}
                          </SelectItem>)
                      }
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="journalId"
              render={({ field }) => (
                <FormItem className="col-span-2">
                  <FormLabel>Journal</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="journal"
                        className="w-full"
                        help="The journal or series containing the publication">
                        <SelectValue className="w-full" placeholder="Specify journal" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        journals.map(journal => (
                          <SelectItem
                            key={journal.id?.toString() ?? ''}
                            value={journal.id?.toString() ?? ''}>
                            {journal.title}
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
              name="authors"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Authors</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="authors"
                      className=" h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The author(s) of the publication, one per line"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="date"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Publication date</FormLabel>
                  <Popover open={dateOpen} onOpenChange={setDateOpen}>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <ButtonEx
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("grow justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                          help="The date on which the publication was first published"
                        >
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value)
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </ButtonEx>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        captionLayout="dropdown"
                        weekStartsOn={1}
                        selected={field.value}
                        onSelect={(e) => {
                          setDateOpen(false)
                          field.onChange(e)
                        }}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="year"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Publication year</FormLabel>
                  <FormControl>
                    <InputEx
                      id="year"
                      type="number"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The year in which the publication was first published"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="doi"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>DOI</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="doi"
                          className="col-span-2"
                          {...field}
                          help="The publication's Digital Object Identifier (DOI)"
                      />
                      : <LinkEx
                          className="col-span-2"
                          href={record?.doi ? `https://doi.org/${record?.doi ?? ''}` : ''}
                          target="_blank"
                          help="Link via the publication's Digital Object Identifier (DOI) to its current location online"
                      >
                        {record?.doi ?? ''}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="isbn"
              render={({field}) => (
                <FormItem>
                  <FormLabel>ISBN</FormLabel>
                  <FormControl>
                    <InputEx
                      id="isbn"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The publication's International Standard Book Number (ISBN)"
                    />
                  </FormControl>
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
                        type="url"
                        {...field}
                        help="The publication's online web address"
                      />
                      : <LinkEx
                        href={record?.url ?? ''}
                        target="_blank"
                        help="The publication's online web address"
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
              name="accessed"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Accessed</FormLabel>
                  <Popover open={accessedOpen} onOpenChange={setAccessedOpen}>
                    <PopoverTrigger id="accessed" asChild>
                      <FormControl>
                        <ButtonEx
                          className={cn("grow justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                          variant={"outline"}
                          disabled={!updating}
                          help="The date the publication was last accessed by contributor"
                        >
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value)
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </ButtonEx>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        captionLayout="dropdown"
                        weekStartsOn={1}
                        selected={field.value}
                        onSelect={(e) => {
                          setAccessedOpen(false)
                          field.onChange(e)
                        }}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="peerReviewed"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Peer reviewed</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="peerReviewed"
                      className="col-span-1"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the publication was peer-reviewed"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="cached"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Cached</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="cached"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the publication content is cached on this server"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="abstract"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-3">
                  <FormLabel>Abstract</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="abstract"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Concise summary of the publication"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="notes"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-3">
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Contributor notes about the publication"
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

// PublicationDetails.whyDidYouRender = true;