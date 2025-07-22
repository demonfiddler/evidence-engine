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

import Claim from "@/app/model/Claim"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Textarea } from "@/components/ui/textarea"
import { cn, formatDate, FormAction } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import StandardDetails from "./standard-details"
import DetailActions, { createDetailState, DetailMode } from "./detail-actions"
import {/* Dispatch, SetStateAction,*/ useContext, useMemo, useState } from "react"
import { useFormContext } from "react-hook-form"
import { ClaimFieldValues } from "../validators/claim"
import useAuth from "@/hooks/use-auth"

export default function ClaimDetails(
  {
    record,
    // mode,
    // setMode,
    onFormAction
  }:
  {
    record?: Claim;
    // mode: DetailMode;
    // setMode: Dispatch<SetStateAction<DetailMode>>;
    onFormAction: (command: FormAction, fieldValues: ClaimFieldValues) => void
  }) {

  const {hasAuthority} = useAuth()
  const form = useFormContext()
  const [mode, setMode] = useState<DetailMode>("view")
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)

  const state = useMemo(() => createDetailState(hasAuthority, mode), [hasAuthority, mode])
  const { updating } = state

  // console.log(`record = ${JSON.stringify(record)}`)
  // console.log(`state = ${JSON.stringify(state)}`)
  // console.log(`form = ${JSON.stringify(form)}`)
  // console.log(`ClaimDetails: valid=${form.formState.isValid}, dirtyFields==${JSON.stringify(form.formState.dirtyFields)}  errors=${JSON.stringify(form.formState.errors)}`)

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Claim Details&nbsp;</legend>
      <StandardDetails recordKind="Claim" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Claim #${record?.id}` : "-Select a claim in the list above to see its details-"}</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4">
            <FormField
              control={form.control}
              name="date"
              render={({field}) => (
                <FormItem className="">
                  <FormLabel>Date</FormLabel>
                  <Popover>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <Button
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("w-full justify-start text-left font-normal",
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
                        The date the claim was first made
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="" />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-3 row-span-3"
              recordKind="Claim"
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
              name="text"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Text</FormLabel>
                  <FormControl>
                    <Textarea
                      id="text"
                      className="h-40 overflow-y-auto"
                      disabled={!record}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Succinct summary of the claim
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="" />
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
                        Contributors' notes on the claim
                      </FormDescription>
                    : null
                  }
                  <FormMessage className="" />
                </FormItem>
              )}
            />
          </div>
        </form>
      </Form>
    </fieldset>
  )
}