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

import Publisher from "@/app/model/Publisher"
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
  SelectItem,
  SelectValue,
} from "@/components/ui/select"
import StandardDetails from "./standard-details"
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction } from "react"
import { useFormContext } from "react-hook-form"
import { PublisherFieldValues } from "../validators/publisher"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"
import LinkEx from "../ext/link-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"

const countries = rawCountries as unknown as Country[]

export default function PublisherDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Publisher
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<PublisherFieldValues>
  }) {

  const form = useFormContext<PublisherFieldValues>()
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Publisher Details&nbsp;</legend>
      <StandardDetails recordKind="Publisher" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Publisher"
                : record
                  ? `Details for selected Publisher #${record?.id}`
                  : "-Select a Publisher in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
            <FormField
              control={form.control}
              name="rating"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Rating</FormLabel>
                  <FormControl>
                    <StarRatingBasicEx
                      readOnly={!updating}
                      maxStars={5}
                      iconSize={18}
                      className="ml-2 w-full"
                      value={field.value ?? 0}
                      onChange={field.onChange}
                      help="A five-star rating for the publisher, indicative of prestige, reputation, impact, quality, etc."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-3 row-span-4"
              recordKind="Publisher"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="name"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <InputEx
                      id="name"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The publisher's registered company or trading name"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="location"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Location</FormLabel>
                  <FormControl>
                    <InputEx
                      id="location"
                      className="col-span-2"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The publisher's location(s) (city/region)"
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
                <FormItem>
                  <FormLabel>Country</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="country"
                        className="w-full"
                        help="The publisher's country, if known and singular"
                      >
                        <SelectValue placeholder="Specify country" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        countries.map(country => (
                          <SelectItem
                            key={country.alpha_2}
                            value={country.alpha_2}>
                            {country.common_name}
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
                <FormItem className="col-start-1 col-span-2">
                  <FormLabel>URL</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                        id="url"
                        {...field}
                        help="The publisher's online web address"
                      />
                      : <LinkEx
                        href={record?.url ?? ''}
                        target="_blank"
                        help="The publisher's online web address"
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
              name="journalCount"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Journal count</FormLabel>
                  <FormControl>
                    <InputEx
                      id="journalCount"
                      type="number"
                      className="col-span-1"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The number of journals published by this organisation"
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