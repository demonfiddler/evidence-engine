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

import { toDate } from "@/lib/utils"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import ListBulletIcon from "@heroicons/react/24/outline/ListBulletIcon"
import { useState } from "react"
import Log from "@/app/model/Log"
import { ownColumns as columns } from "@/app/ui/tables/log-columns"
import DataTable from "../data-table/data-table"
import { DetailState } from "../details/detail-actions"
import { BaseEntityInput, LogQueryFilter } from "@/app/model/schema"
import usePageLogic from "@/hooks/use-page-logic"
import { READ_LOGS } from "@/lib/graphql-queries"
import { LogFieldValues } from "../validators/log"
import LogDialogFilter from "../filter/log-dialog-filter"
import ButtonEx from "../ext/button-ex"
import RecordKind from "@/app/model/RecordKind"

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

export default function LogDialog({
  className,
  disabled,
  recordKind,
  recordId,
  recordLabel,
  state,
} : {
  className?: string
  disabled: boolean
  recordKind: RecordKind
  recordId: string
  recordLabel: string
  state: DetailState
}) {
  const [open, setOpen] = useState(false)

  const {
    loading,
    page,
    refetch,
    loadingPathWithSearchParams,
  } = usePageLogic<Log, LogFieldValues, BaseEntityInput, LogQueryFilter>({
    recordKind: "Log",
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_LOGS,
    createFieldValues,
  })

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger className={className} asChild>
        <ButtonEx
          outerClassName="place-self-center"
          className="w-20 bg-blue-500 text-md"
          disabled={disabled}
          help={
            recordId
            ? `Show log for ${recordLabel}`
            : `No ${recordKind} selected`
          }
        >Show log</ButtonEx>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record Log</DialogTitle>
          <DialogDescription>
            <ListBulletIcon className="w-6 h-6 inline" />
            &nbsp;{recordLabel}
          </DialogDescription>
        </DialogHeader>
        <DataTable<Log, unknown>
          recordKind="Log"
          defaultColumns={columns}
          page={page}
          state={state}
          loading={loading}
          filterComponent={LogDialogFilter}
          manualPagination={true}
          manualSorting={true}
          auxRecordId={recordId}
          refetch={refetch}
          loadingPathWithSearchParams={loadingPathWithSearchParams}
        />
        <DialogFooter>
          <Button onClick={() => setOpen(false)}>Close</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}