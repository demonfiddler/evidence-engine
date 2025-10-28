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

import Person from "@/app/model/Person"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction } from "react"
import { useFormContext } from "react-hook-form"
import { PersonFieldValues } from "../validators/person"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import TextareaEx from "../ext/textarea-ex"
import SelectTriggerEx from "../ext/select-ex"
import CheckboxEx from "../ext/checkbox-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(detail, "[PersonDetails] ")

const countries = rawCountries as unknown as Country[]

export default function PersonDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  }:
  {
    record?: Person
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<PersonFieldValues>
  }) {
  logger.debug("render")

  const form = useFormContext<PersonFieldValues>()
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Person Details&nbsp;</legend>
      <StandardDetails recordKind="Person" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Person"
                : record
                  ? `Details for selected Person #${record?.id}`
                  : "-Select a Person in the list above to see his/her details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-5 ml-2 mr-2 mt-4 mb-4 gap-2">
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
                      readOnly={!updating}
                      maxStars={5}
                      iconSize={18}
                      className="ml-2 w-full"
                      value={field.value ?? 0}
                      onChange={field.onChange}
                      help="A five-star rating for the person, intended to reflect eminence, credibility, experience, qualifications, publications, etc."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="title">Title</FormLabel>
                  <FormControl>
                    <InputEx
                      id="title"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="title"
                      {...field}
                      help="The person's formal honorific title (Dr, Prof, Sir, etc.)"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="firstName"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="firstName">First name(s)</FormLabel>
                  <FormControl>
                    <InputEx
                      id="firstName"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The person's forename(s) or initials"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="nickname"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="nickname">Nickname</FormLabel>
                  <FormControl>
                    <InputEx
                      id="nickname"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The person's informal nickname"
                    />
                  </FormControl>
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
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="prefix"
              render={({field}) => (
                <FormItem className="col-start-1">
                  <FormLabel htmlFor="prefix">Prefix</FormLabel>
                  <FormControl>
                    <InputEx
                      id="prefix"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The prefix to the person's surname (de, van, zu, etc.)"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="lastName"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="lastName">Last name</FormLabel>
                  <FormControl>
                    <InputEx
                      id="lastName"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The person's surname/family name, excluding any prefix"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="suffix"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="suffix">Suffix</FormLabel>
                  <FormControl>
                    <InputEx
                      id="suffix"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The suffix to the person's surname (Jnr, Snr, III, etc.)"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="alias"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="alias">Alias</FormLabel>
                  <FormControl>
                    <InputEx
                      id="alias"
                      className="col-span-2"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="An alternative name by which the person is also known"
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
                <FormItem className="col-start-1 col-span-4">
                  <FormLabel htmlFor="notes">Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Biographical and other notes about the person"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="qualifications"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-4">
                  <FormLabel htmlFor="qualifications">Qualifications</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="qualifications"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The person's formal academic qualifications, including degree, subject, graduation year and academic institution"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="country"
              render={({ field }) => (
                <FormItem className="col-start-1">
                  <FormLabel htmlFor="country">Country</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="country"
                        className="w-full"
                        help="The country with which the person is primarily associated"
                      >
                        <SelectValue placeholder="Specify country" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        countries.map(country =>
                          <SelectItem key={country.alpha_2} value={country.alpha_2}>{country.common_name}</SelectItem>)
                      }
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="rating"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="rating">Rating</FormLabel>
                  <FormControl>
                    <InputEx
                      id="rating"
                      type="number"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="rating"
                      {...field}
                      help="A star-rating to reflect the person's eminence and credibility"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="checked"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="checked">Checked</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="checked"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the person's details and credentials have been checked"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="published"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="published">Published</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="published"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the person has authored peer-reviewed publications"
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

PersonDetails.whyDidYouRender = true