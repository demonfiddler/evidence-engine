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

import Quotation from "@/app/model/Quotation"
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
import { QuotationFieldValues } from "../validators/quotation"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import ButtonEx from "../ext/button-ex"
import TextareaEx from "../ext/textarea-ex"
import LinkEx from "../ext/link-ex"

export default function QuotationDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Quotation
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<QuotationFieldValues>
  }) {

  const form = useFormContext<QuotationFieldValues>()
  const [open, setOpen] = useState(false)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Quotation Details&nbsp;</legend>
      <StandardDetails recordKind="Quotation" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Quotation"
                : record
                  ? `Details for selected Quotation #${record?.id}`
                  : "-Select a Quotation in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-3 ml-2 mr-2 mt-4 mb-4 gap-4 items-center">
            <FormField
              control={form.control}
              name="quotee"
              render={({field}) => (
                <FormItem>
                  <FormLabel>Quotee</FormLabel>
                  <FormControl>
                    <InputEx
                      id="quotee"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="quotee"
                      {...field}
                      help="The full name of the person being quoted"
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
                          help="The date when the quotation was first spoken or written"
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
              className="col-start-3 row-span-5"
              recordKind="Quotation"
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
                <FormItem className="col-span-2">
                  <FormLabel>Quote</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="text"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The verbatim text of the quotation"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="source"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Source</FormLabel>
                  <FormControl>
                    <InputEx
                      className="col-span-4"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="source"
                      {...field}
                      help="Where the quotation was spoken or written"
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
                        type="url"
                        className="col-span-4"
                        placeholder="URL"
                        {...field}
                        help="The online web address of the quotation"
                      />
                      : <LinkEx
                        className="col-span-4"
                        href={record?.url ?? ''}
                        target="_blank"
                        help="The online web address of the quotation"
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
              name="notes"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Contributors' notes on the quotation"
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