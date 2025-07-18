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

import { Textarea } from "@/components/ui/textarea"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
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
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import Link from "next/link"
import { FormAction } from "@/lib/utils"
import { useMemo, useState } from "react"
import useAuth from "@/hooks/use-auth"
import { useFormContext } from "react-hook-form"
import { JournalFormFields } from "../validators/journal"

const publishers = rawPublishers.content as unknown as Publisher[]

export default function JournalDetails(
  { record, onFormAction }:
  { record?: Journal; onFormAction: (command: FormAction, formValue: JournalFormFields) => void } ) {

  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)
  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Journal Details&nbsp;</legend>
      <StandardDetails recordKind="Journal" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Journal #${record?.id}` : "-Select a journal in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-2">
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
                        The journal's official name/title
                      </FormDescription>
                    : null
                  }
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
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="abbreviation"
              render={({field}) => (
                <FormItem className="col-span-1">
                  <FormLabel>Abbreviation</FormLabel>
                  <FormControl>
                    <Input
                      id="abbreviation"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The ISO 4 journal title abbreviation
                      </FormDescription>
                    : null
                  }
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
                    <Input
                      id="issn"
                      className="col-span-1"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The international standard serial number
                      </FormDescription>
                    : null
                  }
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
                      <SelectTrigger id="publisherId" className="w-full" disabled={!record}>
                        <SelectValue className="w-full" placeholder="Specify publisher" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Publishers</SelectLabel>
                        {
                          publishers.map(publisher => (
                            <SelectItem
                              key={publisher.id?.toString() ?? ''}
                              value={publisher.id?.toString() ?? ''}>
                              {publisher.name}
                            </SelectItem>
                          ))
                        }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The journal publisher
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
                          // type="url"
                          className=""
                          {...field}
                        />
                      : <Link className="" href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The journals's online web address
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
                <FormItem className="col-start-1 col-span-2">
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
                        Contributor notes about the journal
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