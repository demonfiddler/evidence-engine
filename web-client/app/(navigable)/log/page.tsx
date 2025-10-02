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

import { ListBulletIcon } from '@heroicons/react/24/outline'
import LogDetails from "@/app/ui/details/log-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/log-columns"
import Log from "@/app/model/Log"
import { toDate } from "@/lib/utils"
import { READ_LOGS } from "@/lib/graphql-queries"
import LogTableFilter from "@/app/ui/filter/log-table-filter"
import { BaseEntityInput, LogQueryFilter } from "@/app/model/schema"
import usePageLogic from "@/hooks/use-page-logic"
import { LogFieldValues } from '@/app/ui/validators/log'
import useLogQueryFilter from '@/hooks/use-log-query-filter'

function createFieldValues(record?: Log) : LogFieldValues {
  return {
    timestamp: toDate(record?.timestamp),
    transactionKind: record?.transactionKind ?? '',
    username: record?.user?.username ?? '',
    entityKind: record?.entityKind ?? '',
    linkedEntityKind: record?.linkedEntityKind ?? '',
    entityId: record?.entityId ?? '',
    linkedEntityId: record?.linkedEntityId ?? '',
  }
}

export default function Logs() {
  const filterLogic = useLogQueryFilter()
  const {
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    refetch,
    loadingPathWithSearchParams,
  } = usePageLogic<Log, LogFieldValues, BaseEntityInput, LogQueryFilter>({
    recordKind: "Log",
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_LOGS,
    createFieldValues,
    filterLogic,
  })

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
        page={page}
        loading={loading}
        filterComponent={LogTableFilter}
        manualPagination={true}
        manualSorting={true}
        onRowSelectionChange={handleRowSelectionChange}
        refetch={refetch}
        loadingPathWithSearchParams={loadingPathWithSearchParams}
      />
      <LogDetails record={selectedRecord} />
    </main>
  )
}

Logs.whyDidYouRender = true