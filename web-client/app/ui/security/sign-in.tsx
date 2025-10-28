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

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import Spinner from "../misc/spinner";
import { FormProvider, useForm } from "react-hook-form";
import { ShieldExclamationIcon } from "@heroicons/react/24/outline";
import { FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useCallback, useState } from "react";
import z from "zod/v4";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
import useAuth from "@/hooks/use-auth";
import { dialog, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(dialog, "[SignInDialog] ")

const SignInFormSchema = z.object({
  username: z.string().min(3).max(50),
  password: z.string().min(6).max(20),
})

type SignInFormFields = z.infer<typeof SignInFormSchema>

export default function SignInDialog() {
  logger.debug("render")

  const {loading, user, login} = useAuth()
  const [signInOpen, setSignInOpen] = useState(false)
  const [formValue] = useState({
    username: '',
    password: '',
  })
  const [error, setError] = useState('')
  const form = useForm<SignInFormFields>({
    resolver: standardSchemaResolver(SignInFormSchema),
    mode: "onChange",
    values: formValue,
  })

  const authenticate = useCallback(() => {
    const {username, password} = form.getValues()
    login(username, password)
      .then(
        () => {setError(''); setSignInOpen(false)},
        (reason) => setError(`Login failed: ${reason}`))
  }, [form, login])

  return (
    <Dialog open={signInOpen} onOpenChange={setSignInOpen}>
      <DialogTrigger asChild>
        <Button variant="ghost" className="text-md" disabled={!!user}>{user?.username ?? "Sign in"}</Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <Spinner loading={loading} label="Signing in..." className="absolute inset-0 bg-black/20 z-50" />
        <FormProvider {...form}>
          <form>
            <DialogHeader>
              <DialogTitle>Sign in</DialogTitle>
              <DialogDescription>
                <ShieldExclamationIcon className="w-6 h-6 inline" />
                &nbsp;Provide your credentials then click &apos;Sign in&apos;.
              </DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-1 gap-4">
              <div className="grid grid-cols-1 items-center gap-4">
                <FormField
                  control={form.control}
                  name="username"
                  render={({field}) => (
                    <FormItem>
                      <FormLabel htmlFor="username">Username</FormLabel>
                      <Input
                        id="username"
                        className="col-span-3"
                        autoComplete="username"
                        {...field}
                      />
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              <div className="grid grid-cols-1 items-center gap-4">
                <FormField
                  control={form.control}
                  name="password"
                  render={({field}) => (
                    <FormItem>
                      <FormLabel htmlFor="password">Password</FormLabel>
                      <Input
                        id="password"
                        type="password"
                        className="col-span-3"
                        autoComplete="current-password"
                        {...field}
                      />
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              <p className="col-span-1 text-red-500">{error}</p>
            </div>
            <DialogFooter>
              <Button
                type="button"
                disabled={!form.formState.isValid}
                onClick={authenticate}
              >
                Sign in
              </Button>
            </DialogFooter>
          </form>
        </FormProvider>
      </DialogContent>
    </Dialog>
  )
}

SignInDialog.whyDidYouRender = true