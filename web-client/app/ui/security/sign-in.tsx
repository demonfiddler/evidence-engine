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

import useAuth from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { useState } from "react"
import { cn } from "@/lib/utils"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { ShieldExclamationIcon, UserIcon } from '@heroicons/react/24/outline'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { FormProvider, useForm } from "react-hook-form"
import { FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import z from "zod/v4"
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { Checkbox } from "@/components/ui/checkbox"
import Spinner from "../misc/spinner"
import { toast } from "sonner"

const SignInFormSchema = z.object({
  username: z.string().min(3).max(50),
  password: z.string().min(6).max(20),
  rememberMe: z.boolean()
})

type SignInFormFields = z.infer<typeof SignInFormSchema>

const graphqlEndpointUrl = new URL(process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL ?? '')

function getUrl(path: string, query?: string, fragment?: string) {
  const url = new URL(path, graphqlEndpointUrl)
  if (query)
    url.search = query
  if (fragment)
    url.hash = fragment
  return url
}

export default function SignIn({className} : {className: string}) {
  const {loading, user, login, logout} = useAuth()
  const [signInOpen, setSignInOpen] = useState(false)
  const [formValue] = useState({
    username: '',
    password: '',
    rememberMe: true
  })
  const [error, setError] = useState('')
  const form = useForm<SignInFormFields>({
    resolver: standardSchemaResolver(SignInFormSchema),
    mode: "onChange",
    values: formValue,
  })

  function handleProfile(event: Event): void {
    toast.warning("handleProfile() not yet implemented.");
  }

  function handleSettings(event: Event): void {
    toast.warning("handleSettings() not yet implemented.");
  }

  function handleLogout(event: Event): void {
    logout()
  }

  function authenticate() {
    const {username, password, rememberMe} = form.getValues()
    login(username, password, rememberMe)
      .then(
        () => {setError(''); setSignInOpen(false)},
        (reason) => setError(`Login failed: ${reason}`))
  }

  return (
    <div className={cn("flex flex-row items-center", className)}>
      <Dialog open={signInOpen} onOpenChange={setSignInOpen}>
        <DialogTrigger asChild>
          <Button variant="ghost" className="text-md" disabled={!!user}>{user?.username ?? "Sign in"}</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <Spinner loading={loading} label="Signing in..." className="absolute inset-0 bg-black/20 z-50" />
          <FormProvider {...form}>
            <form method="post" action={getUrl("/login").toString()}>
              <DialogHeader>
                <DialogTitle>Sign in</DialogTitle>
                <DialogDescription>
                  <ShieldExclamationIcon className="w-6 h-6 inline" />
                  &nbsp;Provide your credentials then click 'Sign in'.
                </DialogDescription>
              </DialogHeader>
              <div className="grid grid-cols-1 gap-4">
                <div className="grid grid-cols-1 items-center gap-4">
                  <FormField
                    control={form.control}
                    name="username"
                    render={({field}) => (
                      <FormItem>
                        <FormLabel>Username</FormLabel>
                        <Input
                          id="username"
                          className="col-span-3"
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
                        <FormLabel>Password</FormLabel>
                        <Input
                          id="password"
                          type="password"
                          className="col-span-3"
                          {...field}
                        />
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <FormField
                  control={form.control}
                  name="rememberMe"
                  render={({field}) => (
                    <FormItem>
                      <FormLabel>Remember me on this computer</FormLabel>
                        <Checkbox
                          id="rememberMe"
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                      <FormMessage />
                    </FormItem>
                  )}
                />
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
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="w-8 h-8 flex items-center justify-center" disabled={!user}>
            <UserIcon className="size-6 stroke-2" />
          </Button>
        </DropdownMenuTrigger>
        &nbsp;
        <DropdownMenuContent className="w-56">
          <DropdownMenuLabel>My Account</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem onSelect={handleProfile}>
              Profile
              <DropdownMenuShortcut>⇧⌘P</DropdownMenuShortcut>
            </DropdownMenuItem>
            <DropdownMenuItem onSelect={handleSettings}>
              Settings
              <DropdownMenuShortcut>⌘S</DropdownMenuShortcut>
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem onSelect={handleLogout}>
            Log out
            <DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  )
}