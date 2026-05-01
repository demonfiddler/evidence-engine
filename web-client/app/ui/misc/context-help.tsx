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
import { cn } from "@/lib/utils"
import { CircleQuestionMarkIcon } from "lucide-react"
import { useCallback } from "react"

let helpWindow : Window | null = null

export default function ContextHelp({className, href} : {className?: string, href: string}) {
  const handleClick = useCallback(() => {
    // Try to reuse the existing tab if still open
    if (helpWindow && !helpWindow.closed) {
      helpWindow.location.href = href
      helpWindow.focus()
      return
    }

    // Otherwise open a new one
    helpWindow = window.open(href, 'ee-context-help');
  }, [href])

  return (
    <Button
      type="button"
      variant="ghost"
      className={cn("p-0", className)}
      size="icon"
      onClick={handleClick}
      title="Display context-sensitive help in another browser tab"
    >
      <CircleQuestionMarkIcon className="w-6! h-6! text-green-600" />
    </Button>
  )
}