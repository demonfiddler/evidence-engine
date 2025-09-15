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

import { Table } from "@tanstack/react-table"
import {
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { ChangeEvent, useCallback, useEffect, useState } from "react"
import InputEx from "../ext/input-ex"

interface DataTablePaginationProps<TData> {
  table: Table<TData>
}

export function DataTablePaginator<TData>({
  table,
}: DataTablePaginationProps<TData>) {
  const pn = (table.getState().pagination?.pageIndex ?? 0) + 1
  const [pageNumber, setPageNumber] = useState(pn)
  useEffect(() => {
    setPageNumber(pn)
  }, [pn])
  const handlePageNumberChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    const newpn = typeof value ==="number" ? value : Number.parseInt(value?.toString() ?? "0")
    if (newpn !== pageNumber && newpn > 0 && newpn <= table.getPageCount()) {
      const pageIndex = newpn - 1
      setPageNumber(newpn)
      table.setPageIndex(pageIndex)
    }
  }, [pageNumber, table, setPageNumber])

  return (
    <div className="flex items-center justify-between">
      {
        // FIXME: there doesn't seem to be a Table API to tell whether row selection is enabled.
        table.getSelectedRowModel()
        ? <div className="flex-1 text-sm text-muted-foreground">
          {table.getFilteredSelectedRowModel().rows.length} of{" "}
          {table.getFilteredRowModel().rows.length} row(s) selected
        </div>
        : <div></div>
      }
      <div className="flex items-center space-x-6 lg:space-x-8">
        <div className="flex items-center space-x-2">
          <p className="text-sm font-medium">Rows per page</p>
          <Select
            value={`${table.getState().pagination?.pageSize}`}
            onValueChange={(value) => {
              table.setPageSize(Number.parseInt(value))
            }}
          >
            <SelectTrigger className="h-8 w-fit">
              <SelectValue placeholder={table.getState().pagination?.pageSize} />
            </SelectTrigger>
            <SelectContent side="top">
              {[5, 10, 20, 50, 100].map((pageSize) => (
                <SelectItem key={pageSize} value={`${pageSize}`}>
                  {pageSize}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="flex items-center justify-center text-sm font-medium">
          Page&nbsp;
          <InputEx
            outerClassName="inline"
            className="w-14 inline text-right"
            type="number"
            min={1}
            max={table.getPageCount()}
            value={pageNumber}
            onChange={handlePageNumberChange}
            delay={500}
          />
          &nbsp;of&nbsp;{table.getPageCount()}
          <span>
            &nbsp;(showing {table.getRowModel().rows.length.toLocaleString()}
            &nbsp;of&nbsp;{table.getRowCount().toLocaleString()} items)
          </span>
        </div>
        <div className="flex items-center space-x-2">
          <Button
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            title="Go to first page"
            onClick={() => table.firstPage()}
            disabled={!table.getCanPreviousPage()}
          >
            <span className="sr-only">Go to first page</span>
            <ChevronsLeft />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            title="Go to previous page"
            onClick={() => table.previousPage()}
            disabled={!table.getCanPreviousPage()}
          >
            <span className="sr-only">Go to previous page</span>
            <ChevronLeft />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            title="Go to next page"
            onClick={() => table.nextPage()}
            disabled={!table.getCanNextPage()}
          >
            <span className="sr-only">Go to next page</span>
            <ChevronRight />
          </Button>
          <Button
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            title="Go to last page"
            onClick={() => table.lastPage()}
            disabled={!table.getCanNextPage()}
          >
            <span className="sr-only">Go to last page</span>
            <ChevronsRight />
          </Button>
        </div>
      </div>
    </div>
  )
}
