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

import "@/app/globals.css";
import '@/app/ui/global.css';
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/app/ui//navigator/app-sidebar"
import SignIn from "../ui/security/sign-in";
import { GlobalContext } from "@/lib/context";
import { useContext } from "react";

export default function NavigableLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const {sidebarOpen, setSidebarOpen} = useContext(GlobalContext)

  return (
    <SidebarProvider open={sidebarOpen} onOpenChange={setSidebarOpen}>
      <AppSidebar />
      <main className="flex flex-col">
        <header className="grid grid-cols-3 items-center w-screen h-12 rounded-md text-white bg-blue-500">
          <SidebarTrigger title="Toggle sidebar (Ctrl+B)" />
          <p className="justify-self-center">Evidence Engine</p>
          <SignIn className="justify-self-end" />
        </header>
        {children}
        <div className="grow"></div>
        <footer className="grid grid-cols-3 items-center w-screen h-12 text-xs rounded-md text-white bg-blue-500">
          <p>&nbsp;Copyright &copy; 2024-25 Adrian Price. All rights reserved.</p>
          <p className="justify-self-center">A <a href="https://campaign-resources.org" target="_blank" className="text-white"><i>Campaign Resources</i></a> application</p>
        </footer>
      </main>
    </SidebarProvider>
  );
}