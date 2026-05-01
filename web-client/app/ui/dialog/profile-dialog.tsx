/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

import { Button } from "@/components/ui/button"
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import Spinner from "../misc/spinner"
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from "react"
import { FormProvider, useForm } from "react-hook-form"
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import z from "zod/v4"
import { useMutation } from "@apollo/client/react"
import { UPDATE_USER_PROFILE } from "@/lib/graphql-queries"
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import TextareaEx from "../ext/textarea-ex"
import InputEx from "../ext/input-ex"
import useAuth from "@/hooks/use-auth"
import { dialog, LoggerEx } from "@/lib/logger"
import { UserPenIcon } from "lucide-react"
import CountryCombobox from "../ext/country-combobox"
import ContextHelp from "../misc/context-help"
import FieldsetEx from "../ext/fieldset-ex"
import { authorities } from "../details/authority-ui"
import CheckboxEx from "../ext/checkbox-ex"
import { AuthorityKind } from "@/app/model/schema"
import { Label } from "@/components/ui/label"

const logger = new LoggerEx(dialog, "[ProfileDialog] ")

const NAME = /[A-Z].*/
const NAME_ERROR = {error: "Name must start with a capital letter"}
const ProfileSchema = z.object({
  firstName: z.string().min(1).max(50).regex(NAME, NAME_ERROR),
  lastName: z.string().min(1).max(50).regex(NAME, NAME_ERROR),
  email: z.email().min(1).max(100),
  country: z.string().uppercase().min(2).max(2),
  notes: z.string(),
})

/*
const state = {
  mode: "view" as DetailMode,
  allowCreate: false,
  allowEdit: false,
  allowUpdate: false,
  allowDelete: false,
  allowLink: false,
  allowRead: true,
  updating: true
}
*/

type ProfileFields = z.infer<typeof ProfileSchema>

export default function ProfileDialog(
  {open, setOpen} : {open: boolean, setOpen: Dispatch<SetStateAction<boolean>>}
) {
  logger.debug("render")

  // Workaround bug https://github.com/radix-ui/primitives/issues/3645
  // "Dialog leaves "pointer-events: none" on body after closing"
  useEffect(() => {
    if (!open) {
      // Let Radix attempt cleanup first
      requestAnimationFrame(() => {
        if (document.body.style.pointerEvents === 'none') {
          document.body.style.pointerEvents = '';
        }
      });
    }
  }, [open]);

  const {user} = useAuth()
  const [error, setError] = useState('')
  const form = useForm<ProfileFields>({
    resolver: standardSchemaResolver(ProfileSchema),
    mode: "onChange",
    values: {
      firstName: user?.firstName ?? '',
      lastName: user?.lastName ?? '',
      email: user?.email ?? '',
      country: user?.country ?? '',
      notes: user?.notes ?? '',
    },
  })
  const [updateUserProfileOp, { loading }] = useMutation(UPDATE_USER_PROFILE)

  const createInput = useCallback(() => {
    return {
      id: user?.id,
      ...form.getValues()
    }
  }, [user, form])

  const handleCancel = useCallback(() => {
    if (!form.formState.isDirty || confirm("Confirm discard changes?")) {
      form.clearErrors()
      setOpen(false)
    }
  }, [form, setOpen])

  const handleSave = useCallback(() => {
    if (form.formState.isValid) {
      updateUserProfileOp({
        variables: {
          input: createInput()
        },
        onCompleted: () => setOpen(false),
        onError: (e) => setError(e.message),
      })
    }
  }, [form, updateUserProfileOp, createInput, setOpen])

  return open ? (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent className="">
        <Spinner loading={loading} label="Saving..." className="absolute inset-0 bg-black/20 z-50" />
        <FormProvider {...form}>
          <form>
            <DialogHeader>
              <DialogTitle>
                <UserPenIcon className="inline" />&nbsp;User Profile
                <ContextHelp href="/doc/profile/" />
              </DialogTitle>
              <DialogDescription className="py-4">
                Update your details then click &apos;Save&apos;.
              </DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-4">
              {/* <div className="colspan-2 rowspan-5">
                <StandardDetails recordKind="User" record={user ?? undefined} state={state} showLinkingDetails={false} />
              </div> */}
              <div className="grid gap-2">
                <Label htmlFor="id">User ID</Label>
                <InputEx disabled={true} value={user?.id ?? ''} />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="id">Username</Label>
                <InputEx disabled={true} value={user?.username ?? ''} />
              </div>
              <FormField
                control={form.control}
                name="firstName"
                render={({field}) => (
                  <FormItem>
                    <FormLabel htmlFor="firstName">First name</FormLabel>
                    <InputEx
                      id="firstName"
                      {...field}
                      help="Your first/given name(s). This is a required field."
                    />
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
                    <InputEx
                      id="lastName"
                      {...field}
                      help="Your last/surname. This is a required field."
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="email"
                render={({field}) => (
                  <FormItem className="col-start-1 col-span-2">
                    <FormLabel htmlFor="email">Email</FormLabel>
                    <InputEx
                      id="email"
                      type="email"
                      {...field}
                      help="Your email address. This is a required field."
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="country"
                render={({ field }) => (
                  <FormItem className="col-start-1 col-span-2">
                    <FormLabel htmlFor="country">Country</FormLabel>
                    <CountryCombobox
                      field={field}
                      help="Your country of primary residence. This is a required field."
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="notes"
                render={({field}) => (
                  <FormItem className="col-start-1 col-span-2">
                    <FormLabel htmlFor="notes">Notes</FormLabel>
                    <FormControl>
                      <TextareaEx
                        id="notes"
                        className="h-40 overflow-y-auto"
                        {...field}
                        help="Notes about your background, interests, education and qualifications, accomplishments, activities, etc."
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="col-span-2 ">
                <FieldsetEx
                  className="w-full grid grid-cols-4 justify-items-center border rounded-md p-4 gap-4"
                  disabled={true}
                  help="The authorities granted to this user"
                >
                  <legend>&nbsp;Granted Authorities&nbsp;</legend>
                  {
                    authorities.map((auth, idx) => (
                      <div key={idx} className="flex flex-col items-center gap-1">
                        <Label htmlFor={auth.key}>{auth.label}</Label>
                        <CheckboxEx
                          id={auth.key}
                          checked={user?.authorities?.includes(auth.key.toUpperCase() as AuthorityKind)}
                          disabled={true}
                          help={auth.description}
                        />
                      </div>
                    ))
                  }
                </FieldsetEx>
              </div>
              <p className="col-span-2 text-red-500">{error}</p>
            </div>
            <DialogFooter>
              <DialogClose asChild>
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancel}
                >
                  Cancel
                </Button>
              </DialogClose>
              <Button
                type="button"
                disabled={!form.formState.isValid}
                onClick={handleSave}
              >
                Save
              </Button>
            </DialogFooter>
          </form>
        </FormProvider>
      </DialogContent>
    </Dialog>
  )
  : null
}

ProfileDialog.whyDidYouRender = true