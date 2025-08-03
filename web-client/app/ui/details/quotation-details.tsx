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
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Input } from "@/components/ui/input"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Textarea } from "@/components/ui/textarea"
import { cn, formatDate } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import Link from "next/link"
import { Dispatch, SetStateAction, useState } from "react"
import { useFormContext } from "react-hook-form"
import { QuotationFieldValues } from "../validators/quotation"
import { FormActionHandler } from "@/hooks/use-page-logic"

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

  const form = useFormContext()
  const [showFieldHelp, setShowFieldHelp] = useState<boolean>(false)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Quotation Details&nbsp;</legend>
      <StandardDetails recordKind="Quotation" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="pt-2 pb-4">
              &nbsp;&nbsp;{record
              ? state.mode == "create"
                ? "Details for new Quotation"
                : `Details for selected Quotation #${record?.id}`
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
                    <Input
                      id="quotee"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="quotee"
                      {...field}
                    />
                  </FormControl>
                    {
                      showFieldHelp
                      ? <FormDescription>
                          The name of the person being quoted
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
                        The date the quotation was first spoken or written
                      </FormDescription>
                    : null
                  }
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
              showFieldHelp={showFieldHelp}
              setShowFieldHelp={setShowFieldHelp}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="text"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel>Quote</FormLabel>
                  <FormControl>
                    <Textarea
                      id="text"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The text of the quotation
                      </FormDescription>
                    : null
                  }
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
                    <Input
                      className="col-span-4"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      placeholder="source"
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Where the quotation was spoken or written
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
                        className="col-span-4"
                        placeholder="URL"
                        {...field}
                      />
                      : <Link className="col-span-4" href={record?.url ?? ''} target="_blank">{record?.url ?? ''}</Link>
                    }
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        The online web address of the quotation
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
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                    />
                  </FormControl>
                  {
                    showFieldHelp
                    ? <FormDescription>
                        Contributors' notes on the quotation
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