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

import Publication from "@/app/model/Publication";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar"
import { Checkbox } from "@/components/ui/checkbox";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Textarea } from "@/components/ui/textarea";
import { cn, formatDate, FormAction } from "@/lib/utils";
import { CalendarIcon } from "@heroicons/react/24/outline";
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
import rawJournals from "@/data/journals.json" assert {type: 'json'}
import rawPublicationKinds from "@/data/publication-kinds.json" assert {type: 'json'}
import Journal from "@/app/model/Journal";
import StandardDetails from "./standard-details";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import Link from "next/link";
import { useContext, useMemo, useState } from "react";
import useAuth from "@/hooks/use-auth"
import { useFormContext } from "react-hook-form"
import { PublicationFieldValues } from "../validators/publication";

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

export default function PublicationDetails(
  { record, onFormAction }:
  { record?: Publication; onFormAction: (command: FormAction, fieldValues: PublicationFieldValues) => void }) {

  // console.log("PublicationDetails: render")
  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  // console.log(`PublicationDetails: valid=${form.formState.isValid}, dirtyFields==${JSON.stringify(form.formState.dirtyFields)}, fieldState==${JSON.stringify(form.getFieldState("kind"))}, errors=${JSON.stringify(form.formState.errors)}`)

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publication #${record?.id}` : "-Select a publication in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-4 ml-2 mr-2 mt-4 mb-4 gap-4 items-start">
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-3">
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <Input
                      id="title"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication name/title
                      </FormDescription>
                    : null
                  }
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
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
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
                      <SelectTrigger id="kind" className="w-full" disabled={!record}>
                        <SelectValue placeholder="Specify kind" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Publication Kind</SelectLabel> {
                          publicationKinds.map(kind =>
                            <SelectItem
                              key={kind.kind?.toString() ?? ''}
                              value={kind.kind?.toString() ?? ''}>
                              {kind.label}
                            </SelectItem>)
                        }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The kind of declaration
                      </FormDescription>
                    : null
                  }
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
                      <SelectTrigger id="journal" className="w-full" disabled={!record}>
                        <SelectValue className="w-full" placeholder="Specify journal" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Journals</SelectLabel> {
                          journals.map(journal => (
                            <SelectItem
                              key={journal.id?.toString() ?? ''}
                              value={journal.id?.toString() ?? ''}>
                              {journal.title}
                            </SelectItem>
                          ))
                        }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The journal or series containing the publication
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="authors"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Authors</FormLabel>
                  <FormControl>
                    <Textarea
                      id="authors"
                      className=" h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The author(s) of the publication, one per line
                      </FormDescription>
                    : null
                  }
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
                  <Popover>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <Button
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}>
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value, "PPP")
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        initialFocus
                      />
                    </PopoverContent>
                  </Popover>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication date
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="" />
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
                    <Input
                      id="year"
                      type="number"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication year
                      </FormDescription>
                    : null
                  }
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
                      ? <Input
                          id="doi"
                          className="col-span-2"
                          {...field}
                        />
                      : <Link className="col-span-2" href={record?.doi ? `https://doi.org/${record?.doi ?? ''}` : ''} target="_blank">{record?.doi ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication's digital object identifier
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="isbn"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>ISBN</FormLabel>
                  <FormControl>
                    <Input
                      id="isbn"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication's international standard book number
                      </FormDescription>
                    : null
                  }
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
                      ? <Input
                          id="url"
                          type="url"
                          className=""
                          {...field}
                        />
                      : <Link className="" href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publication's online web address
                      </FormDescription>
                    : null
                  }
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
                  <Popover>
                    <PopoverTrigger id="accessed" asChild>
                      <FormControl>
                        <Button
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}>
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value, "PPP")
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        initialFocus
                      />
                    </PopoverContent>
                  </Popover>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The date the publication was last accessed by contributor
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="" />
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
                    <Checkbox
                      id="peerReviewed"
                      className="col-span-1"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Whether the publication was peer-reviewed
                      </FormDescription>
                    : null
                  }
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
                    <Checkbox
                      id="cached"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Whether the publication content is cached on this server
                      </FormDescription>
                    : null
                  }
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
                    <Textarea
                      id="abstract"
                      className="h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Concise summary of the publication
                      </FormDescription>
                    : null
                  }
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
                    <Textarea
                      id="notes"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Contributor notes about the publication
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

// PublicationDetails.whyDidYouRender = true;