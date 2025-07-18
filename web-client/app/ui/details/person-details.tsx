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

import Person from "@/app/model/Person";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country"
import StandardDetails from "./standard-details";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import { useContext, useMemo, useState } from "react";
import { FormAction } from "@/lib/utils";
import { useFormContext } from "react-hook-form"
import useAuth from "@/hooks/use-auth"
import { PersonFormFields } from "../validators/person";

const countries = rawCountries as unknown as Country[]

export default function PersonDetails(
  { record, onFormAction }:
  { record?: Person; onFormAction: (command: FormAction, formValue: PersonFormFields) => void }) {

  const form = useFormContext()
  const {hasAuthority} = useAuth()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Person Details&nbsp;</legend>
      <StandardDetails recordKind="Person" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Person #${record?.id}` : "-Select a person in the list above to see his/her details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-5 ml-2 mr-2 mt-4 mb-4 gap-2">
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <Input
                      id="title"
                      disabled={!record}
                      readOnly={!updating}
                      placeholder="title"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The person's formal honorific title (Dr, Prof, Sir, etc.)
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="firstName"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>First name(s)</FormLabel>
                  <FormControl>
                    <Input
                      id="firstName"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The person's forename(s) or initials
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="nickname"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Nickname</FormLabel>
                  <FormControl>
                    <Input
                      id="nickname"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The person's informal nickname
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-5 row-span-8"
              recordKind="Person"
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
              name="prefix"
              render={({field}) => (
                <FormItem className="col-start-1">
                  <FormLabel>Prefix</FormLabel>
                  <FormControl>
                    <Input
                      id="prefix"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The prefix to the person's surname (de, van, zu, etc.)
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="lastName"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Last name</FormLabel>
                  <FormControl>
                    <Input
                      id="lastName"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The person's surname/family name, excluding any prefix
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="suffix"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Suffix</FormLabel>
                  <FormControl>
                    <Input
                      id="suffix"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The suffix to the person's surname (Jnr, Snr, III, etc.)
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="alias"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Alias</FormLabel>
                  <FormControl>
                    <Input
                      id="alias"
                      className="col-span-2"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        An alternative name by which the person is also known
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
                <FormItem className="col-start-1 col-span-4">
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
                        Biographical and other notes about the person
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="qualifications"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-4">
                  <FormLabel>Qualifications</FormLabel>
                  <FormControl>
                    <Textarea
                      id="qualifications"
                      className="h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The person's formal academic qualifications
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
                <FormItem className="col-start-1">
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
                        The country with which the person is primarily associated
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="rating"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Rating</FormLabel>
                  <FormControl>
                    <Input
                      id="rating"
                      type="number"
                      disabled={!record}
                      readOnly={!updating}
                      placeholder="rating"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        A star-rating to reflect the person's eminence and credibility
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="checked"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Checked</FormLabel>
                  <FormControl>
                    <Checkbox
                      id="checked"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Whether the person's details and credentials have been checked
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="published"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Published</FormLabel>
                  <FormControl>
                    <Checkbox
                      id="published"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Whether the person has authored peer-reviewed publications
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