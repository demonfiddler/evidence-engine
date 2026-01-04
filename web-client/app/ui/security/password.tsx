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

import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from "react"
import Spinner from "../misc/spinner"
import { FormProvider, useForm } from "react-hook-form"
import { FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import InputEx from "../ext/input-ex"
import { Button } from "@/components/ui/button"
import useAuth from "@/hooks/use-auth"
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { useMutation } from "@apollo/client/react"
import { UPDATE_USER_PASSWORD } from "@/lib/graphql-queries"
import z from "zod/v4"
import { genSaltSync, hashSync } from "bcrypt-ts"
import { dialog, LoggerEx } from "@/lib/logger"
import { ShieldAlertIcon } from "lucide-react"

const logger = new LoggerEx(dialog, "[PasswordDialog] ")

const STRONG_PASSWORD = /^\S*(?=\S{6,})(?=\S*\d)(?=\S*[A-Z])(?=\S*[a-z])(?=\S*[!@#$%^&*? ])\S*$/
const PASSWORD_ERROR = {error: "Password must be at least 6 characters, including at least 1 uppercase and 1 lowercase letter, 1 other letter and 1 special character."}
const PasswordSchema = z.object({
  password1: z.string().min(6).max(50).regex(STRONG_PASSWORD, PASSWORD_ERROR),
  password2: z.string().min(6).max(50).regex(STRONG_PASSWORD, PASSWORD_ERROR),
})

type PasswordFields = z.infer<typeof PasswordSchema>

export default function PasswordDialog(
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
  const form = useForm<PasswordFields>({
    resolver: standardSchemaResolver(PasswordSchema),
    mode: "onChange",
    values: {
      password1: '',
      password2: '',
    },
  })
  const [updateUserProfileOp, { loading }] = useMutation(UPDATE_USER_PASSWORD)

  const createInput = useCallback(() => {
    const salt = genSaltSync(10)
    const hash = hashSync(form.getValues().password1, salt)
    return {
      id: user?.id,
      password: `{bcrypt}${hash}`
    }
  }, [user, form])

  const handleCancel = useCallback(() => {
    if (!form.formState.isDirty || confirm("Confirm discard changes?")) {
      form.clearErrors()
      setOpen(false)
    }
  }, [form, setOpen])

  const handleSave = useCallback(() => {
    const values = form.getValues()
    if (values.password1 !== values.password2) {
      setError("Passwords do not match")
    } else {
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
      <DialogContent className="sm:max-w-[425px]">
        <Spinner loading={loading} label="Saving..." className="absolute inset-0 bg-black/20 z-50" />
        <FormProvider {...form}>
          <form>
            <DialogHeader>
              <DialogTitle>
                <ShieldAlertIcon className="inline" />
                &nbsp;Change Password
              </DialogTitle>
              <DialogDescription className="py-4">
                &nbsp;Enter your new password twice then click &apos;Save&apos;.
              </DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-1 gap-4">
              <FormField
                control={form.control}
                name="password1"
                render={({field}) => (
                  <FormItem className="col-start-1">
                    <FormLabel htmlFor="password1">New password</FormLabel>
                    <InputEx
                      id="password1"
                      type="password"
                      autoComplete="new-password"
                      {...field}
                      help={`Your new password. This is a required field. ${PASSWORD_ERROR.error}`}
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password2"
                render={({field}) => (
                  <FormItem className="col-start-1">
                    <FormLabel htmlFor="password2">Re-enter password</FormLabel>
                    <InputEx
                      id="password2"
                      type="password"
                      autoComplete="new-password"
                      {...field}
                      help={`Re-enter your new password. This is a required field. ${PASSWORD_ERROR.error}`}
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
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

PasswordDialog.whyDidYouRender = true