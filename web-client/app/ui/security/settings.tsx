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

import { Button } from "@/components/ui/button"
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { GlobalContext } from "@/lib/context"
import { Cog8ToothIcon } from "@heroicons/react/24/outline"
import { Dispatch, SetStateAction, useCallback, useContext, useEffect } from "react"
import { dialog, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(dialog, "[SettingsDialog] ")

export default function SettingsDialog(
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
  const settings = useContext(GlobalContext)

  const clearSettings = useCallback(() => {
    if (confirm("Confirm clear all settings for all pages?")) {
      settings.setDefaults()
      setOpen(false)
    }
  }, [settings, setOpen])

  return open ? (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            <Cog8ToothIcon className="w-6 h-6 inline" />
            &nbsp;Settings
          </DialogTitle>
          <DialogDescription className="py-4">
            You can clear all page, toggle, filter, sorting, pagination, etc. settings.
          </DialogDescription>
        </DialogHeader>
        <Button className="w-20 justify-self-center" onClick={clearSettings}>Clear</Button>
        <DialogFooter>
          <DialogClose asChild>
            <Button
              type="button"
              variant="outline"
            >
              Close
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
  : null
}

SettingsDialog.whyDidYouRender = true