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
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { cn, formatDate } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useState } from "react"
import { useFormContext } from "react-hook-form"
import { ClaimFieldValues } from "../validators/claim"
import { FormActionHandler } from "@/hooks/use-page-logic"
import TextareaEx from "../ext/textarea-ex"
import ButtonEx from "../ext/button-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"

export default function ClaimDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Claim
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<ClaimFieldValues>
  }) {

  const form = useFormContext<ClaimFieldValues>()
  const [open, setOpen] = useState(false)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Claim Details&nbsp;</legend>
      <StandardDetails recordKind="Claim" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
              ? "Details for new Claim"
              : record
                ? `Details for selected Claim #${record?.id}`
                : "-Select a Claim in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-5 ml-2 mr-2 mt-4 mb-4 gap-4">
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
                      help="A five-star rating for the claim, indicative of significance, credibility, evidence base, etc."
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
                  <FormLabel>Date</FormLabel>
                  <Popover open={open} onOpenChange={setOpen}>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <ButtonEx
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("grow justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                          help="The date the claim was first made"
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
                          setOpen(false)
                          field.onChange(e)
                        }}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-5 row-span-3"
              recordKind="Claim"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="text"
              render={({field}) => (
                <FormItem className="col-span-4">
                  <FormLabel>Text</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="text"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="A succinct summary of the claim"
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
                <FormItem className="col-span-4">
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Contributors' notes on the claim"
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

ClaimDetails.whyDidYouRender = true