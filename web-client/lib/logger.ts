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

import { anything } from '@/types/types'
import type { Logger, LoggerOptions, WriteFn } from 'pino'

let pino: typeof import('pino')
let options: LoggerOptions
type Message = {msgPrefix: string, msg: string}
type WriteFns = {
  fatal?: WriteFn;
  error?: WriteFn;
  warn?: WriteFn;
  info?: WriteFn;
  debug?: WriteFn;
  trace?: WriteFn
}

if (typeof window === 'undefined') {
  // Server-side
  pino = require('pino') as typeof import('pino')
  options = { level: "trace" }
} else {
  // Client-side (we declared this in types/pino-browser.d.ts)
  // NOTE: in MS Edge, only console.debug/info() calls result in console output. trace/warn/error() do not, but
  // error() gets picked up by the Next.js dev runtime and flagged as a full-screen-displayable error.
  pino = require('pino/browser') as typeof import('pino')
  const writers = {
    silent: (o: Message) => {},
    trace: (o: Message) => console.debug((o.msgPrefix ?? '') + "[TRACE] " + o.msg),
    debug: (o: Message) => console.debug((o.msgPrefix ?? '') + "[DEBUG] " + o.msg),
    info: (o: Message) => console.info((o.msgPrefix ?? '') + "[INFO] " + o.msg),
    // TODO: finalise warn(), error() and fatal() handling
    // warn: (o: Message) => console.warn((o.msgPrefix ?? '') + "[WARN] " + o.msg),
    // error: (o: Message) => console.error((o.msgPrefix ?? '') + "[ERROR] " + o.msg),
    // fatal: (o: Message) => console.error((o.msgPrefix ?? '') + "[FATAL] " + o.msg),
    warn: (o: Message) => console.info((o.msgPrefix ?? '') + "[WARN] " + o.msg),
    error: (o: Message) => console.info((o.msgPrefix ?? '') + "[ERROR] " + o.msg),
    fatal: (o: Message) => console.info((o.msgPrefix ?? '') + "[FATAL] " + o.msg),
  }
  // Browser option asObjectBindingsOnly = true logs a single object, which (for now) is not what we want.
  options = { level: "info", browser: { asObject/*BindingsOnly*/: true, write: writers as WriteFns } }
}

// NOTE: pino/browser ignores the msgPrefix option, hence the need to implement it via bindings.
// ALSO: in pino/browser, child loggers ignore the asObjectBindingsOnly browser option and default to asObject,
// which leads to an inconsistent logging format between top-level and child loggers. For this reason, we don't
// use child loggers.
/** A logger for use by layouts. */
export const layout = pino({...options, name: "Layout", msgPrefix: "[Layout] "})
/** A logger for use by pages. */
export const page = pino({...options, name: "Page", msgPrefix: "[Page] "})
/** A logger for use by detail sections. */
export const detail = pino({...options, name: "Detail", msgPrefix: "[Detail] "})
/** A logger for use by dialogs/drawers/sheets. */
export const dialog = pino({...options, name: "Dialog", msgPrefix: "[Dialog] "})
/** A logger for use by filter components. */
export const filter = pino({...options, name: "Filter", msgPrefix: "[Filter] "})
/** A logger for use by other components. */
export const component = pino({...options, name: "Component", msgPrefix: "[Component] "})
/** A logger for use by tables. */
export const table = pino({...options, name: "Table", msgPrefix: "[Table] "})
/** A logger for use by hooks. */
export const hook = pino({...options, name: "Hook", msgPrefix: "[Hook] "})
/** A logger for use by utility functions. */
export const utility = pino({...options, name: "Utility", msgPrefix: "[Utility] "})

/**
 * Wraps an existing logger to give all messages a msgPrefix binding.
 */
export class LoggerEx {
  private logger: Logger
  private bindings: { msgPrefix: string }

  /**
   * Constructs a new ```LoggerEx```.
   * @param logger The logger to wrap.
   * @param msgPrefix The prefix to prepend to all messages.
   */
  constructor(logger: Logger, msgPrefix: string) {
    this.logger = logger
    this.bindings = { msgPrefix }
  }

  get level() { return this.logger.level}
  // set level(level: LevelWithSilentOrString) { this.logger.level = level}
  get msgPrefix() { return this.bindings.msgPrefix }

  silent(msg: string, ...args: anything[]) { /* no-op */ }
  trace(msg: string, ...args: anything[]) { this.logger.trace(this.bindings, msg, ...args) }
  debug(msg: string, ...args: anything[]) { this.logger.debug(this.bindings, msg, ...args) }
  info(msg: string, ...args: anything[]) { this.logger.info(this.bindings, msg, ...args) }
  warn(msg: string, ...args: anything[]) { this.logger.warn(this.bindings, msg, ...args) }
  error(msg: string, ...args: anything[]) { this.logger.error(this.bindings, msg, ...args) }
  fatal(msg: string, ...args: anything[]) { this.logger.fatal(this.bindings, msg, ...args) }
}