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
import { Drawer, DrawerClose, DrawerContent, DrawerDescription, DrawerFooter, DrawerHeader, DrawerTitle } from "@/components/ui/drawer"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Slider } from "@/components/ui/slider"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { layout, page, detail, dialog, filter, component, table, hook, utility, LoggerEx } from "@/lib/logger"
import { MoveLeftIcon, MoveRightIcon, SlidersHorizontalIcon } from "lucide-react"
import { useCallback, useReducer, useState } from "react"
import log, { LogLevelNames, LogLevelNumbers } from 'loglevel'

const logger = new LoggerEx(dialog, "[LoggingLevelDrawer] ")

const labelToLevel : {[k: string]: LogLevelNumbers} = {
  trace: 0,
  debug: 1,
  info: 2,
  warn: 3,
  error: 4,
  silent: 5,
}
const levelToLabel : (LogLevelNames | "silent")[] = [
  "trace",
  "debug",
  "info",
  "warn",
  "error",
  "silent",
]
const loggers = [
  {label: "Components", logger: component},
  {label: "Details", logger: detail},
  {label: "Dialogs", logger: dialog},
  {label: "Filters", logger: filter},
  {label: "Hooks", logger: hook},
  {label: "Layouts", logger: layout},
  {label: "Pages", logger: page},
  {label: "Tables", logger: table},
  {label: "Utilities", logger: utility},
]

export default function LoggingLevelDialog(
  {open, onOpenChange} : {open: boolean, onOpenChange: (open: boolean) => void}
) {
  logger.debug("render")

  const [allLevel, setAllLevel] = useState(levelToLabel[labelToLevel.silent])
  // The logger states are non-reactive, so we use this dummy state to force a re-render when logging level changes.
  const [, forceUpdate] = useReducer(x => x + 1, 0)

  const handleValueChange = useCallback((label: string, _logger: log.Logger, value: LogLevelNumbers[]): void => {
    if (value.length == 1) {
      const newLevel = value[0]
      if (_logger.getLevel() !== newLevel) {
        _logger.setLevel(newLevel)
        forceUpdate()
        logger.info("'%s' logging level set to '%s'", label, levelToLabel[newLevel])
      }
    }
  }, [])

  const handleSetAll = useCallback(() => {
    let modified = false
    const newLevel = labelToLevel[allLevel]
    for (let desc of loggers) {
      if (desc.logger.getLevel() !== newLevel) {
        desc.logger.setLevel(newLevel)
        logger.info("'%s' logging level set to '%s'", desc.label, allLevel)
        modified = true
      }
    }
    if (modified)
      forceUpdate()
  }, [allLevel])

  return (
    <Drawer open={open} onOpenChange={onOpenChange}>
      <DrawerContent className="w-1/2 bottom-0 translate-x-1/2 transform">
        <div className="mx-auto w-full">
          <DrawerHeader>
            <DrawerTitle><SlidersHorizontalIcon className="inline" />&nbsp;Logging Levels</DrawerTitle>
            <DrawerDescription>Set client-side logging levels. Open the browser console or developer tools to see logger output.</DrawerDescription>
          </DrawerHeader>
          <div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="text-lg">Logger</TableHead>
                  <TableHead className="relative">
                    <div className="grid grid-cols-3 absolute w-6/7 left-1/14 top-0">
                      <span className="justify-self-start self-end text-xs text-gray-400"><MoveLeftIcon className="inline size-6"/>increasing verbosity</span>
                      <span className="justify-self-center text-lg">Level</span>
                      <span className="justify-self-end self-end text-xs text-gray-400">increasing severity<MoveRightIcon className="inline size-6"/></span>
                    </div>
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow>
                  <TableCell></TableCell>
                  <TableCell>
                    <div className="grid grid-cols-6 items-center justify-items-center w-full">
                      {
                        levelToLabel.map(label => <span key={label}>{label}</span>)
                      }
                    </div>
                  </TableCell>
                </TableRow>
                {
                  loggers.map((row, index) => (
                    <TableRow key={index.toString()}>
                      <TableCell>{row.label}</TableCell>
                      <TableCell className="relative">
                        <Slider
                          className="w-6/7 left-1/14"
                          min={0}
                          max={5}
                          step={1}
                          value={[row.logger.getLevel()]}
                          onValueChange={value => handleValueChange(row.label, row.logger, value as LogLevelNumbers[])}
                        />
                      </TableCell>
                    </TableRow>
                  ))
                }
              </TableBody>
            </Table>
          </div>
          <DrawerFooter className="flex-row justify-center">
            <Button
              type="button"
              variant="outline"
              className="w-20 self-center"
              onClick={handleSetAll}
            >
              Set all to:
            </Button>
            <Select
              value={allLevel}
              onValueChange={value => setAllLevel(value as LogLevelNames)}
            >
              <SelectTrigger>
                <SelectValue placeholder="Level" />
              </SelectTrigger>
              <SelectContent>
                {
                  levelToLabel.map(label => <SelectItem key={label} value={label}>{label}</SelectItem>)
                }
              </SelectContent>
            </Select>
            <DrawerClose asChild>
              <Button
                type="button"
                variant="outline"
                className="w-20 self-center"
              >
                Close
              </Button>
            </DrawerClose>
          </DrawerFooter>
        </div>
      </DrawerContent>
    </Drawer>
  )
}