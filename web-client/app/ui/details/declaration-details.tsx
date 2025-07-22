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

import Declaration from "@/app/model/Declaration"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Checkbox } from "@/components/ui/checkbox"
import { Input } from "@/components/ui/input"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Textarea } from "@/components/ui/textarea"
import { cn, formatDate, FormAction } from "@/lib/utils"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage
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
import { CalendarIcon } from "@heroicons/react/24/outline"
import Country from "@/app/model/Country"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import StandardDetails from "./standard-details"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import Link from "next/link"
import { useContext, useMemo, useState } from "react"
import { useFormContext } from "react-hook-form"
import { DeclarationFieldValues } from "../validators/declaration"
import useAuth from "@/hooks/use-auth"
const countries = rawCountries as unknown as Country[]

export default function DeclarationDetails(
  { record, onFormAction }:
  { record?: Declaration; onFormAction: (command: FormAction, fieldValues: DeclarationFieldValues) => void }) {

  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  // console.log(`DeclarationDetails: valid=${form.formState.isValid}, dirtyFields==${JSON.stringify(form.formState.dirtyFields)}, fieldState==${JSON.stringify(form.getFieldState("kind"))}, errors=${JSON.stringify(form.formState.errors)}`)

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Declaration Details&nbsp;</legend>
      <StandardDetails recordKind="Declaration" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Declaration #${record?.id}` : "-Select a declaration in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4 items-start">
            <FormField
              control={form.control}
              name="date"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Date</FormLabel>
                  <Popover>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <Button
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("w-full justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                        >
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value, "PPP")
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="col-span-2 w-auto p-0" align="start">
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
                        The date the declaration was last issued
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="col-start-2 col-span-4" />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="kind"
              render={({ field }) => (
                <FormItem>
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
                        <SelectLabel>Declaration Kinds</SelectLabel>
                        <SelectItem value="DECL">Declaration</SelectItem>
                        <SelectItem value="OPLE">Open Letter</SelectItem>
                        <SelectItem value="PETN">Petition</SelectItem>
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
            <DetailActions
              className="col-start-3 row-span-6"
              recordKind="Declaration"
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
              name="title"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <Input
                      id="title"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The declaration's official name/title
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
                          type="url"
                          placeholder="URL"
                          {...field}
                        />
                      : <Link href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The declaration's online web address
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
                        Whether the declaration is cached in this server
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="country"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Country</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTrigger id="country" className="w-full" disabled={!record}>
                        <SelectValue placeholder="Specify country" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Countries</SelectLabel>
                          {countries.map(country =>
                            <SelectItem key={country.alpha_2} value={country.alpha_2}>{country.common_name}</SelectItem>)}
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The country in which the declaration was issued
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="signatories"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Signatories</FormLabel>
                  <FormControl>
                    <Textarea
                      id="signatories"
                      className="h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Signatory names, verbatim, one per line
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="signatoryCount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Signatory count</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      id="signatoryCount"
                      disabled={!record}
                      readOnly={!updating}
                      placeholder="count"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The number of signatories
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
                <FormItem className="col-span-2">
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <Textarea
                      id="notes"
                      className="h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Contributor notes on the declaration
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