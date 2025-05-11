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

import Authority from "@/app/model/Authority";
import { Button } from "@/components/ui/button"
import { ChangeEvent, useContext, useState } from "react";
import { cn } from "@/lib/utils";
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
import { Label } from "@/components/ui/label"
import { SecurityContext } from "@/lib/context";
import { ShieldExclamationIcon, UserIcon } from '@heroicons/react/24/outline';
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
 
export default function SignIn({className} : {className: string}) {
  const securityContext = useContext(SecurityContext)
  const [signInOpen, setSignInOpen] = useState(false)
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  function authenticate() {
    if (!["root", "admin", "creator", "editor", "linker", "user"].includes(username) || password != "password") {
      setError("Invalid credentials - please try again.")
      return
    }
    let a : Authority[]
    switch (username) {
      case "root":
      case "admin":
        a = ["ADM", "CRE", "DEL", "LNK", "REA", "UPD", "UPL"]
        break;
      case "creator":
        a = ["CRE", "REA"]
        break;
      case "editor":
        a = ["LNK", "REA", "UPD", "UPL"]
        break;
      case "linker":
        a = ["LNK", "REA"]
        break;
      case "user":
        a = ["REA"]
        break;
      default:
        throw new Error(`Unknown username '${username}'`)
    }
    const user = {
      username: username,
      authorities: a
    }
    securityContext.setSecurityContext(securityContext, user)
    setUsername("")
    setPassword("")
    setError("")
    setSignInOpen(false)
  }

  function handleUsernameChange(e: ChangeEvent<HTMLInputElement>): void {
    setUsername(e.target.value)
  }

  function handlePasswordChange(e: ChangeEvent<HTMLInputElement>): void {
    setPassword(e.target.value)
  }

  function handleProfile(event: Event): void {
    alert("handleProfile() not implemented.");
  }

  function handleSettings(event: Event): void {
    alert("handleSettings() not implemented.");
  }

  function handleLogout(event: Event): void {
    securityContext.setSecurityContext(securityContext)
  }

  return (
    <div className={cn("flex flex-row items-center", className)}>
      <Dialog open={signInOpen} onOpenChange={setSignInOpen}>
        <DialogTrigger asChild>
          <Button variant="ghost" className="text-md" disabled={!!securityContext.username}>{securityContext.username ?? "Sign in"}</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Sign in</DialogTitle>
            <DialogDescription>
              <ShieldExclamationIcon className="w-6 h-6 inline" />
              &nbsp;Provide your credentials then click 'Sign in'.
            </DialogDescription>
          </DialogHeader>
          <div className="grid grid-cols-1 gap-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="username" className="text-right">Username</Label>
              <Input id="username" className="col-span-3" value={username} onChange={handleUsernameChange} />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="password" className="text-right">Password</Label>
              <Input id="password" type="password" className="col-span-3" value={password} onChange={handlePasswordChange} />
            </div>
            <p className="col-span-1 text-red-500">{error}</p>
          </div>
          <DialogFooter>
            <Button type="submit" disabled={!username || !password} onClick={authenticate}>Sign in</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="w-8 h-8 flex items-center justify-center" disabled={!securityContext.username}>
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