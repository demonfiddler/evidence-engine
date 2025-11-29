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

import { ComponentProps } from "react"
import Help, { HelpProps } from "../misc/help"
import * as CheckboxPrimitive from "@radix-ui/react-checkbox"
import { DividerHorizontalIcon, CheckIcon } from "@radix-ui/react-icons"
import { cn } from "@/lib/utils"

type CheckboxExProps = ComponentProps<typeof CheckboxPrimitive.Root> & HelpProps

export default function CheckboxEx({help, outerClassName, className, checked, ...props} : CheckboxExProps) {
  return (
    <div className={cn("flex flex-row items-center gap-1", outerClassName)}>
      <CheckboxPrimitive.Root
        data-slot="checkbox"
        className={cn(
          "peer border-input dark:bg-input/30 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground dark:data-[state=checked]:bg-primary data-[state=checked]:border-primary focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive size-4 shrink-0 rounded-[4px] border shadow-xs transition-shadow outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50",
          className
        )}
        checked={checked}
        title={help}
        {...props}
      >
        <CheckboxPrimitive.Indicator
          data-slot="checkbox-indicator"
          className="flex items-center justify-center text-current transition-none"
        >
					{checked === "indeterminate" && <DividerHorizontalIcon className="size-3.5" />}
					{checked === true && <CheckIcon className="size-3.5" />}
        </CheckboxPrimitive.Indicator>
      </CheckboxPrimitive.Root>
      <Help text={help} />
    </div>
  )
}