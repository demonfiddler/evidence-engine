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

import { useContext, useEffect, useMemo, useState } from "react";
import { ListBulletIcon } from '@heroicons/react/24/outline';

import LogDetails from "@/app/ui/details/log-details";
import DataTable from "@/app/ui/data-table/data-table";

import { columns, columnVisibility } from "@/app/ui/tables/log-columns"
import IPage from "@/app/model/IPage";
import Log from "@/app/model/Log";
import { SelectedRecordsContext } from "@/lib/context";
import { SearchSettings } from "@/lib/utils";
import { useQuery } from "@apollo/client";
import { READ_LOGS } from "@/lib/graphql-queries";
import { toast } from "sonner";
import { LogQueryFilter } from "@/app/model/schema";
import { SortingState } from "@tanstack/react-table";
// import usePageLogic from "@/hooks/use-page-logic";

export default function Logs() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Log?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 });
  const [sorting, setSorting] = /*manualSorting ? */useState<SortingState>([]) // : []
  // const filter = useMemo(() => {
  //   const filter: LogQueryFilter = {
  //     // status: search.status ? [search.status] : undefined,
  //     // text: search.text,
  //   }
  //   // if (search.text)
  //   //   filter.advancedSearch = search.advancedSearch
  //   console.log(`Logs effect: filter = ${JSON.stringify(filter)}`)
  //   return filter
  // }, [search])
  // const pageSort = useMemo(() => {
  //   const pageSort = {
  //     // pageNumber: pagination.pageIndex,
  //     // pageSize: pagination.pageSize
  //   }
  //   console.log(`Logs effect: pageSort = ${JSON.stringify(pageSort)}`)
  //   return pageSort
  // }, [/*pagination*/])

  const result = useQuery(
    READ_LOGS/*,
    {
      variables: {
        filter,
        pageSort
      },
    }*/
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  /*useEffect(() => {
    console.log(`Log effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);*/

  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}, result.data = ${JSON.stringify(result.data)}`)
  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}`)
  const page = result.data?.log as IPage<Log>

  const selectedRecord = page?.content.find(r => r.id == selectedRecordId)

  if (result.error) {
    toast.error(`Fetch error:\n\n${JSON.stringify(result.error)}`)
    console.error(result.error)
  }
  // const {
  //   search,
  //   setSearch,
  //   pagination,
  //   setPagination,
  //   loading,
  //   page,
  //   selectedRecord,
  //   handleRowSelectionChange,
  // } = usePageLogic<Log, LogQueryFilter, LogFieldValues>({
  //   recordKind: "Log",
  //   manualPagination: true,
  //   listQuery: QUERY_LOG,
  // })

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ListBulletIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Logs</h1>
      </div>
      <DataTable<Log, unknown>
        recordKind="Log"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        loading={result.loading}
        manualPagination={false}
        pagination={pagination}
        onPaginationChange={setPagination}
        manualSorting={false}
        sorting={sorting}
        onSortingChange={setSorting}
        search={search}
        onSearchChange={setSearch}
        onRowSelectionChange={setSelectedRecordId/*handleRowSelectionChange*/}
      />
      <LogDetails record={selectedRecord} />
    </main>
  );
}
