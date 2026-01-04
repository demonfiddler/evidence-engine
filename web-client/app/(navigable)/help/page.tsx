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

import { LoggerEx, page } from "@/lib/logger";
import { CircleQuestionMarkIcon } from "lucide-react";

const logger = new LoggerEx(page, "[Help] ")

export default function Help() {
  logger.debug("render")

  return (
    <main className="flex flex-col m-8 gap-8 w-1/2 self-center">
      <div className="flex flex-row items-center">
        <CircleQuestionMarkIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Help</h1>
      </div>
    </main>
  );
}
