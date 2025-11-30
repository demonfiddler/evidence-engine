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

import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card"
import { CircleQuestionMarkIcon } from "lucide-react"

export type HelpProps = {
  outerClassName?: string
  help?: string
}

export default function Help({text} : {text?: string}) {
  if (!text)
    return null

  return (
    <HoverCard>
      <HoverCardTrigger asChild>
        <CircleQuestionMarkIcon className="w-6 h-6 text-sky-200"/>
      </HoverCardTrigger>
      <HoverCardContent className="w-80 bg-sky-50" side="left">
        <p className="text-sm">
          {text}
        </p>
      </HoverCardContent>
    </HoverCard>
  )
}

Help.whyDidYouRender = true