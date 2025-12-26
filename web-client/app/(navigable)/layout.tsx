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

import "@/app/globals.css"
import '@/app/ui/global.css'
import MyAccount from "../ui/security/my-account"
import { GlobalContext } from "@/lib/context"
import { useContext } from "react"
import { layout, LoggerEx } from "@/lib/logger"
import Sidebar from "../ui/navigator/sidebar"
import { Button } from "@/components/ui/button"
import { GithubIcon, PanelRightCloseIcon, PanelRightOpenIcon } from "lucide-react"

const logger = new LoggerEx(layout, "[NavigableLayout] ")

export default function NavigableLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  logger.debug("render")

  const {sidebarOpen, setSidebarOpen} = useContext(GlobalContext)

  return (
    <div className="w-full">
      <header className="fixed top-0 left-0 right-0 z-10 grid grid-cols-3 items-center w-full h-16 text-white bg-blue-500">
        <Button
          type="button"
          variant="ghost"
          className="w-12"
          title="Toggle sidebar (Ctrl+B)"
          onClick={() => setSidebarOpen(!sidebarOpen)}
        >
          {
            sidebarOpen
            ? <PanelRightOpenIcon />
            : <PanelRightCloseIcon />
          }
        </Button>
        <p className="justify-self-center"><b>The Evidence Engine</b></p>
        <div className="justify-self-end flex items-center mr-2">
          <MyAccount className="justify-self-end" />
          <a href="https://github.com/demonfiddler/evidence-engine" target="_blank" title="Source code on GitHub"><GithubIcon className="justify-self-end inline text-white"/></a>
        </div>
      </header>
      <footer className="fixed bottom-0 left-0 right-0 z-10 grid grid-cols-3 items-center w-full h-12 text-xs text-white bg-blue-500">
        <p>&nbsp;Copyright &copy; 2024-25 Adrian Price. All rights reserved.</p>
        <p className="justify-self-center">A <a href="https://campaign-resources.org" target="_blank" className="text-white"><i>Campaign Resources</i></a> application</p>
      </footer>
      <div className="fixed top-16 bottom-12 flex flex-row w-screen">
        <Sidebar open={sidebarOpen} />
        <div className="flex flex-col w-full overflow-y-auto">
          {children}
        </div>
      </div>
    </div>
  )
}

NavigableLayout.whyDidYouRender = true