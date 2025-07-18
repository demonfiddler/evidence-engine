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

import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { ArrowLeftIcon, ArrowRightIcon } from '@heroicons/react/24/outline';
import { ArrowLeftStartIcon, ArrowRightEndIcon } from "@/app/ui/icons"
import IPage from "@/app/model/IPage";

export default function Paginator() {
  const page : IPage<any> = {
    hasContent: true,
    isEmpty: false,
    number: 1,
    size: 5,
    numberOfElements: 5,
    totalPages: 3,
    totalElements: "13",
    isFirst: true,
    isLast: false,
    hasNext: true,
    hasPrevious: false,
    content: []
  }
  const start = (page.number - 1) * page.size + 1;
  const end = start + page.numberOfElements - 1;

  function firstPage() {
    // console.log("Go to first page")
  }

  function previousPage() {
    // console.log("Go to previous page")
  }

  function nextPage() {
    // console.log("Go to next page")
  }

  function lastPage() {
    // console.log("Go to last page")
  }

  return (
    <div className="flex flex-row items-center ml-2 mr-2 mb-2 gap-2 text-sm">
      <label htmlFor="">Items per page:</label>
      <Select>
        <SelectTrigger className="w-[180px]">
          <SelectValue title="Select page size">{page.size}</SelectValue>
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="5">5</SelectItem>
          <SelectItem value="10">10</SelectItem>
          <SelectItem value="20">20</SelectItem>
          <SelectItem value="50">50</SelectItem>
          <SelectItem value="100">100</SelectItem>
        </SelectContent>
      </Select>
      <p>{`Page ${page.number} of ${page.totalPages}: items ${start}–${end} of ${page.totalElements}`}</p>
      <Button variant="ghost" title="Go to first page" onClick={firstPage}><ArrowLeftStartIcon /></Button>
      <Button variant="ghost" title="Go to previous page" onClick={previousPage}><ArrowLeftIcon /></Button>
      <Button variant="ghost" title="Go to next page" onClick={nextPage}><ArrowRightIcon /></Button>
      <Button variant="ghost" title="Go to last page" onClick={lastPage}><ArrowRightEndIcon /></Button>
    </div>
  );
}