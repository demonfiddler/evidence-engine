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

import { anything } from '@/types/types'
import log, { LogLevel, LogLevelNumbers } from 'loglevel'

/** A logger for use by layouts. */
export const layout = log.getLogger("Layout")
/** A logger for use by pages. */
export const page = log.getLogger("Page")
/** A logger for use by detail sections. */
export const detail = log.getLogger("Detail")
/** A logger for use by dialogs/drawers/sheets. */
export const dialog = log.getLogger("Dialog")
/** A logger for use by filter components. */
export const filter = log.getLogger("Filter")
/** A logger for use by other components. */
export const component = log.getLogger("Component")
/** A logger for use by tables. */
export const table = log.getLogger("Table")
/** A logger for use by hooks. */
export const hook = log.getLogger("Hook")
/** A logger for use by utility functions. */
export const utility = log.getLogger("Utility")

/**
 * Wraps an existing logger to give all messages a message prefix.
 */
export class LoggerEx {

  private log: log.Logger
  private prefix: string

  /**
   * Constructs a new ```LoggerEx```.
   * @param logger The logger to wrap.
   * @param prefix The prefix to prepend to all messages.
   */
  constructor(logger: log.Logger, prefix: string) {
    this.log = logger
    this.prefix = prefix
  }

  get level() : LogLevel { return this.log.levels}
  set level(level: LogLevelNumbers) { this.log.setLevel(level) }

  trace(msg: string, ...args: anything[]) {
    if (this.log.getLevel() <= log.levels.TRACE)
      this.log.trace("%s" + msg, this.prefix, ...args)
  }

  debug(msg: string, ...args: anything[]) {
    if (this.log.getLevel() <= log.levels.DEBUG)
      this.log.debug("%s" + msg, this.prefix, ...args)
  }

  info(msg: string, ...args: anything[]) {
    if (this.log.getLevel() <= log.levels.INFO)
      this.log.info("%s" + msg, this.prefix, ...args)
  }

  warn(msg: string, ...args: anything[]) {
    if (this.log.getLevel() <= log.levels.WARN)
      this.log.warn("%s" + msg, this.prefix, ...args)
  }

  error(msg: string, ...args: anything[]) {
    if (this.log.getLevel() <= log.levels.ERROR)
      this.log.error("%s" + msg, this.prefix, ...args)
  }

  silent(msg: string, ...args: anything[]) {
    // All logging disabled, so this is a no-op.
  }

}