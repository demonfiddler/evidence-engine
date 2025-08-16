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

import { Column, Header, Table } from "@tanstack/react-table"
import { ArrowDown, ArrowUp, ChevronsUpDown, GripVertical } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { useSortable } from "@dnd-kit/sortable"
import { CSS } from '@dnd-kit/utilities'
import { CSSProperties } from "react"
import { TableHead } from "@/components/ui/table"

interface DataTableColumnHeaderProps<TData, TValue> extends React.HTMLAttributes<HTMLDivElement> {
  table: Table<TData>
  header: Header<TData, TValue>
  column: Column<TData, TValue>
  title: string
}

export default function DataTableColumnHeader<TData, TValue>({
  table,
  header,
  column,
  title,
  className,
}: DataTableColumnHeaderProps<TData, TValue>) {
  const { attributes, isDragging, listeners, setNodeRef, transform } = useSortable({id: column.id})

  const style: CSSProperties = {
    opacity: isDragging ? 0.8 : 1,
    position: 'relative',
    transform: CSS.Translate.toString(transform), // translate instead of transform to avoid squishing
    transition: 'width transform 0.2s ease-in-out',
    whiteSpace: 'nowrap',
    width: column.getSize(),
    zIndex: isDragging ? 1 : 0,
  }
  
  return (
    <TableHead key={header.id} className="relative border box-border" style={style} ref={setNodeRef}>
      {
        header.isPlaceholder
          ? null
          : <div className={cn("flex flex-0 items-center", className)}>
            {
              !column.getIsPinned()
              ? <Button variant="ghost" className={`w-2 h-full relative ${isDragging ? "cursor-grabbing" : "cursor-grab"}`}
                {...attributes} {...listeners}>
                <GripVertical className="absolute left-0 text-gray-400"/>
              </Button>
              : null
            }
            {
              column.getCanSort()
              ? <Button
                variant="ghost"
                size="sm"
                className="-ml-3 h-8"
                onClick={column.getToggleSortingHandler()}
              >
                <span>{title}</span>
                <div className="flex text-gray-400">
                  {
                    column.getIsSorted() === "asc"
                    ? <ArrowUp />
                    : column.getIsSorted() === "desc"
                      ? <ArrowDown />
                      : <ChevronsUpDown />
                  }
                  {column.getSortIndex() != -1
                    ? <Badge variant="outline">{column.getSortIndex() + 1}</Badge>
                    : <></>
                  }
                </div>
              </Button>
              : <span>{title}</span>
            }
          </div>
      }
      {
        header.column.columnDef.enableResizing ?? true
        ? <div
          {...{
            onDoubleClick: () => header.column.resetSize(),
            onMouseDown: header.getResizeHandler(),
            onTouchStart: header.getResizeHandler(),
            className: `resizer ${
              table.options.columnResizeDirection
            } ${
              header.column.getIsResizing() ? 'isResizing' : ''
            }`,
            style: {
              transform:
                table.options.columnResizeMode === 'onEnd' && header.column.getIsResizing()
                ? `translateX(${
                    (table.options.columnResizeDirection === 'rtl'
                      ? -1
                      : 1) *
                    (table.getState().columnSizingInfo.deltaOffset ?? 0)
                  }px)`
                : '',
            },
          }}
        />
        : null
      }
    </TableHead>
  )
}
