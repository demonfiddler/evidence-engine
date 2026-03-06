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

import { ChangeEvent, ComponentProps, KeyboardEvent, useCallback, useEffect, useRef, useState } from "react"
import { useDebounceValue } from "usehooks-ts"
import Help, { HelpProps } from "../misc/help"
import { component, LoggerEx } from "@/lib/logger"
import { InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput } from "@/components/ui/input-group"
import { SearchIcon, XIcon } from "lucide-react"

const logger = new LoggerEx(component, "[InputEx] ")

type InputExProps = ComponentProps<"input"> & HelpProps & {
  clear?: boolean
  delay?: number
  search?: boolean
}

function createChangeEvent(
  input: HTMLInputElement
): React.ChangeEvent<HTMLInputElement> {
  return {
    target: input,
    currentTarget: input,
    bubbles: true,
    cancelable: false,
    defaultPrevented: false,
    eventPhase: 3,
    isTrusted: false,
    preventDefault() {},
    isDefaultPrevented() { return false; },
    stopPropagation() {},
    isPropagationStopped() { return false; },
    persist() {},
    timeStamp: Date.now(),
    type: "change",
    nativeEvent: new Event("change", { bubbles: true })
  } as React.ChangeEvent<HTMLInputElement>;
}

export default function InputEx(
  {help, outerClassName, value, onChange, clear, delay, search, disabled, ...props} : InputExProps
) {
  logger.debug("render")

  const [text, setText] = useState(value)
  const [event, setEvent] = useDebounceValue<ChangeEvent<HTMLInputElement>|undefined>(undefined, delay || 0)
  const inputRef = useRef<HTMLInputElement>(null)

  // If the supplied value changes externally, update text to match.
  const prevValue = useRef<string | number | readonly string[] | undefined>('')
  useEffect(() => {
    logger.trace("effect1 (1): value='%s' prevValue='%s'", value, prevValue.current)
    if (value !== prevValue.current) {
      prevValue.current = value
      if (value !== text) {
        logger.trace("effect1 (2): value='%s' text='%s'", value, text)
        setText(value)
        if (event && onChange) {
          event.target.value = value?.toString() ?? ''
          onChange(event)
        }
      }
    }
  }, [value, text, event, onChange])

  // When the debounced event changes, invoke the supplied listener function.
  const prevEvent = useRef(event)
  useEffect(() => {
    logger.trace("effect2 (1): event.target.value='%s'", event?.target.value)
    if (event && event !== prevEvent.current) {
      prevEvent.current = event
      logger.trace("effect2 (2)")
      onChange?.(event)
    }
  }, [event, onChange])

  // To clear, empty text value and invoke supplied change handler immediately.
  const handleClear = useCallback(() => {
    setText('')
    if (onChange && inputRef.current) {
      inputRef.current.value = ''
      onChange(createChangeEvent(inputRef.current))
    }
  }, [event, onChange])

  // On typing, sync the controlled text value and trigger event debouncing.
  const handleChangeText = useCallback((e:  ChangeEvent<HTMLInputElement>) => {
    logger.trace("onChangeText: e.target.value='%s'", e?.target.value)
    setText(e.target.value ?? '')
    setEvent(e)
  }, [setEvent])

  const handleKeyDown = useCallback((e: KeyboardEvent<HTMLInputElement>) => {
    if (e.code == "Escape") {
      // TODO: prevent dialog closure by setting onEscapeKeyDown={(e) => e.preventDefault()} on Dialog or DialogContent
      e.preventDefault()
      e.stopPropagation()
      handleClear()
    }
  }, [handleClear])

  return (
    <InputGroup className={outerClassName}>
      <InputGroupInput
        value={text}
        disabled={disabled}
        onKeyDown={(e) => clear && handleKeyDown(e)}
        onChange={(e) => handleChangeText(e)}
        ref={inputRef}
        {...props}
      />
      {
        search
        ? <InputGroupAddon>
          <SearchIcon />
        </InputGroupAddon>
        : null
      }
      {
        clear
        ? <InputGroupAddon align="inline-end">
          <InputGroupButton
            disabled={disabled}
            onClick={handleClear}
          >
            <XIcon />
          </InputGroupButton>
        </InputGroupAddon>
        : null
      }
      {
        help
        ? <InputGroupAddon align="inline-end">
            <Help text={help} />
          </InputGroupAddon>
        : null
      }
    </InputGroup>
  )
}