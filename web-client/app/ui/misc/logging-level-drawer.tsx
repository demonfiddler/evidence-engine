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
import { Logger } from "pino"
import { useCallback, useReducer, useState } from "react"

const logger = new LoggerEx(dialog, "[LoggingLevelDrawer] ")

const labelToLevel : {[k: string]: number} = {
  silent: 0,
  fatal: 1,
  error: 2,
  warn: 3,
  info: 4,
  debug: 5,
  trace: 6,
}
const levelToLabel = [
  "silent",
  "fatal",
  "error",
  "warn",
  "info",
  "debug",
  "trace",
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

export default function LoggingLevelDrawer(
  {open, onOpenChange} : {open: boolean, onOpenChange: (open: boolean) => void}
) {
  logger.debug("render")

  const [allLevel, setAllLevel] = useState("silent")
  // The logger states are non-reactive, so we use this dummy state to force a re-render when logging level changes.
  const [, forceUpdate] = useReducer(x => x + 1, 0)

  const handleValueChange = useCallback((label: string, _logger: Logger, value: number[]): void => {
    if (value.length == 1) {
      const level = levelToLabel[value[0]]
      if (_logger.level !== level) {
        _logger.level = level
        forceUpdate()
        logger.info("'%s' logging level set to '%s'", label, level)
      }
    }
  }, [])

  const handleSetAll = useCallback(() => {
    let modified = false
    for (let desc of loggers) {
      if (desc.logger.level !== allLevel) {
        desc.logger.level = allLevel
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
                      <span className="justify-self-start self-end text-xs text-gray-400"><MoveLeftIcon className="inline size-6"/>increasing severity</span>
                      <span className="justify-self-center text-lg">Level</span>
                      <span className="justify-self-end self-end text-xs text-gray-400">increasing verbosity<MoveRightIcon className="inline size-6"/></span>
                    </div>
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow>
                  <TableCell></TableCell>
                  <TableCell>
                    <div className="grid grid-cols-7 items-center justify-items-center w-full">
                      <span>silent</span>
                      <span>fatal</span>
                      <span>error</span>
                      <span>warn</span>
                      <span>info</span>
                      <span>debug</span>
                      <span>trace</span>
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
                          max={6}
                          step={1}
                          value={[labelToLevel[row.logger.level]]}
                          onValueChange={value => handleValueChange(row.label, row.logger, value)}
                        />
                      </TableCell>
                    </TableRow>
                  ))
                }
              </TableBody>
            </Table>
          </div>
          <DrawerFooter className="flex-row justify-center">
            <Button className="w-20 self-center" variant="outline" onClick={handleSetAll}>Set all to:</Button>
            <Select
              value={allLevel}
              onValueChange={setAllLevel}
            >
              <SelectTrigger>
                <SelectValue placeholder="Level" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="silent">silent</SelectItem>
                <SelectItem value="fatal">fatal</SelectItem>
                <SelectItem value="error">error</SelectItem>
                <SelectItem value="warn">warn</SelectItem>
                <SelectItem value="info">info</SelectItem>
                <SelectItem value="debug">debug</SelectItem>
                <SelectItem value="trace">trace</SelectItem>
              </SelectContent>
            </Select>
            <DrawerClose asChild>
              <Button className="w-20 self-center" variant="outline">Close</Button>
            </DrawerClose>
          </DrawerFooter>
        </div>
      </DrawerContent>
    </Drawer>
  )
}