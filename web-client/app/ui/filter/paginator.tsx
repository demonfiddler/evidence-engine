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

import {
  ChevronLeftIcon,
  ChevronRightIcon,
  ChevronsLeftIcon,
  ChevronsRightIcon,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { ChangeEvent, useCallback } from "react"
import InputEx from "../ext/input-ex"
import { Label } from "@/components/ui/label"
import IPage from "@/app/model/IPage"
import { Updater } from "@tanstack/react-table"
import { component, LoggerEx } from "@/lib/logger"
import { PageableInput } from "@/app/model/schema"

const logger = new LoggerEx(component, "[Paginator] ")

interface PaginatorProps<TData> {
  page?: IPage<TData>
  pageSort: PageableInput
  setPageSort: (pagination: Updater<PageableInput>) => void
}

export function Paginator<TData>({
  page,
  pageSort,
  setPageSort
}: PaginatorProps<TData>) {
  logger.trace("render, pageSort: %o", pageSort)

  const handleChangePageSize = useCallback((value: string) => {
    if (value)
      setPageSort({pageNumber: 0, pageSize: Number.parseInt(value)})
  }, [setPageSort])

  const handleGotoPage = useCallback((pageNumber: number) => {
    if (page && pageNumber != page.number && pageNumber >= 0 && pageNumber < page.totalPages)
      setPageSort({pageNumber, pageSize: pageSort.pageSize})
  }, [page, pageSort, setPageSort])

  const handlePageNumberChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    const newPageNumber = Number.parseInt(value) ?? 1
    handleGotoPage(newPageNumber - 1)
  }, [handleGotoPage])

  const handleGotoFirstPage = useCallback(() => {
    handleGotoPage(0)
  }, [handleGotoPage])

  const handleGotoPreviousPage = useCallback(() => {
    if (page)
      handleGotoPage(page.number - 1)
  }, [page, handleGotoPage])

  const handleGotoNextPage = useCallback(() => {
    if (page)
      handleGotoPage(page.number + 1)
  }, [page, handleGotoPage])

  const handleGotoLastPage = useCallback(() => {
    if (page)
      handleGotoPage(page.totalPages - 1)
  }, [page, handleGotoPage])

  return (
    <div className="flex items-center justify-end">
      <div className="flex items-center space-x-6 lg:space-x-8">
        <div className="flex items-center space-x-2">
          <p className="text-sm font-medium">Records per page</p>
          <Select
            value={`${pageSort.pageSize}`}
            onValueChange={handleChangePageSize}
          >
            <SelectTrigger className="h-8 w-fit" title="Click to change page size">
              <SelectValue placeholder={page?.size ?? 0}/>
            </SelectTrigger>
            <SelectContent side="top">
              {[5, 10, 20, 50, 100, 0].map((pageSize) => (
                <SelectItem key={pageSize} value={`${pageSize}`}>
                  {pageSize || '(all)'}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="flex items-center justify-center text-sm font-medium">
          <Label htmlFor="page-number">Page&nbsp;</Label>
          <InputEx
            id="page-number"
            outerClassName="w-14 inline"
            className="inline text-right"
            type="number"
            min={1}
            max={page ? page.totalPages : 0}
            value={page ? page.number + 1 : 0}
            onChange={handlePageNumberChange}
            delay={500}
            title="Page number"
          />
          &nbsp;of&nbsp;{page ? page.totalPages : 0}
          <span>
            &nbsp;(showing {page ? page.numberOfElements.toLocaleString() : 0}
            &nbsp;of&nbsp;{page ? page.totalElements.toLocaleString() : 0} items)
          </span>
        </div>
        <div className="flex items-center space-x-2">
          <Button
            type="button"
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            title="Go to first page"
            onClick={() => handleGotoFirstPage()}
            disabled={!page?.hasPrevious}
          >
            <span className="sr-only">Go to first page</span>
            <ChevronsLeftIcon />
          </Button>
          <Button
            type="button"
            variant="outline"
            className="h-8 w-8 p-0"
            title="Go to previous page"
            onClick={() => handleGotoPreviousPage()}
            disabled={!page?.hasPrevious}
          >
            <span className="sr-only">Go to previous page</span>
            <ChevronLeftIcon />
          </Button>
          <Button
            type="button"
            variant="outline"
            className="h-8 w-8 p-0"
            title="Go to next page"
            onClick={() => handleGotoNextPage()}
            disabled={!page?.hasNext}
          >
            <span className="sr-only">Go to next page</span>
            <ChevronRightIcon />
          </Button>
          <Button
            type="button"
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            title="Go to last page"
            onClick={() => handleGotoLastPage()}
            disabled={!page?.hasNext}
          >
            <span className="sr-only">Go to last page</span>
            <ChevronsRightIcon />
          </Button>
        </div>
      </div>
    </div>
  )
}

Paginator.whyDidYouRender = true