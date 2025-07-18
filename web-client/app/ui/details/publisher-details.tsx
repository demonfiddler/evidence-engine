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

import Publisher from "@/app/model/Publisher";
import { Input } from "@/components/ui/input";
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
import StandardDetails from "./standard-details";
import rawCountries from "@/data/countries.json" assert {type: 'json'}
import Country from "@/app/model/Country";
import DetailActions, { createDetailState, DetailMode } from "./detail-actions";
import Link from "next/link";
import { FormAction } from "@/lib/utils";
import { useContext, useMemo, useState } from "react";
import useAuth from "@/hooks/use-auth"
import { useFormContext } from "react-hook-form"
import { PublisherFormFields } from "../validators/publisher";

const countries = rawCountries as unknown as Country[]

export default function PublisherDetails(
  { record, onFormAction }:
  { record?: Publisher; onFormAction: (command: FormAction, formValue: PublisherFormFields) => void }) {

  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Publisher Details&nbsp;</legend>
      <StandardDetails recordKind="Publisher" record={record} state={state} showLinkingDetails={false} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Publisher #${record?.id}` : "-Select a publisher in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
            <FormField
              control={form.control}
              name="name"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input
                      id="name"
                      className=""
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publisher's company name
                      </FormDescription>
                    : null
                  }
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
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="location"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Location</FormLabel>
                  <FormControl>
                    <Input
                      id="location"
                      className="col-span-2"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publisher's location (city/region)
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
                          {
                            countries.map(country => (
                              <SelectItem
                                key={country.alpha_2}
                                value={country.alpha_2}>
                                {country.common_name}
                              </SelectItem>
                            ))
                          }
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publisher's country
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
                <FormItem className="col-start-1 col-span-2">
                  <FormLabel>URL</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <Input
                          id="url"
                          className=""
                          {...field}
                        />
                      : <Link className="" href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The publishers's online web address
                      </FormDescription>
                    : null
                  }
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="journalCount"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Journal count</FormLabel>
                  <FormControl>
                    <Input
                      id="journalCount"
                      type="number"
                      className="col-span-1"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The number of journals published by this organisation
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