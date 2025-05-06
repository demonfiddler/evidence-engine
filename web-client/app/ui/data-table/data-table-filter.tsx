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

import { Checkbox } from "@/components/ui/checkbox";
import Search from "../filter/search";
import { DataTableViewOptions, DataTableViewOptionsProps } from "./data-table-view-options";

export default function DataTableFilter<TData>({
  table,
  isLinkableEntity
}: DataTableViewOptionsProps<TData>) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center gap-2">
        <Search />
        <Checkbox id="advanced" title="Use advanced text search syntax" />
        <label htmlFor="advanced">Advanced</label>
        {
          isLinkableEntity
          ? <>
              <Checkbox id="linkedOnly" title="Only show records linked to the current master record" />
              <label htmlFor="linkedOnly" className="flex-none">Show only linked records</label>
            </>
          : <></>
        }
        <DataTableViewOptions table={table} isLinkableEntity={isLinkableEntity} />
      </div>
    </div>
  )
}