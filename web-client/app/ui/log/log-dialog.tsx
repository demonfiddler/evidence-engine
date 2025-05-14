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

import { getRecordLabel } from "@/lib/utils";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import ITrackedEntity from "@/app/model/ITrackedEntity";
import { Button } from "@/components/ui/button";
import RecordKind from "@/app/model/RecordKind";
import ListBulletIcon from "@heroicons/react/24/outline/ListBulletIcon";
import { useContext, useState } from "react";
import { SelectedRecordsContext } from "@/lib/context";
import Log from "@/app/model/Log";
import { ownColumns as columns, columnVisibility } from "@/app/ui/tables/log-columns"
import DataTable from "../data-table/data-table";

export default function LogDialog({
  className, disabled, recordKind, record
}: {
  className?: string, disabled: boolean, recordKind: RecordKind, record?: ITrackedEntity,
}
) {
  const [open, setOpen] = useState(false)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [/*selectedRow, */setSelectedRow] = useState<Log|undefined>(() => {
    const selectedRecordId = selectedRecordsContext.Log?.id
    return record?.log?.content.find(record => record.id == selectedRecordId)
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild className="col-start-6 place-items-center">
        <Button className="w-20 place-self-center bg-blue-500 text-md" disabled={disabled}>Show log</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record Log</DialogTitle>
          <DialogDescription>
            <ListBulletIcon className="w-6 h-6 inline" />
            &nbsp;{`${getRecordLabel(recordKind, record)}`}.
          </DialogDescription>
        </DialogHeader>
        <DataTable<Log, unknown>
          recordKind="Log"
          defaultColumns={columns}
          defaultColumnVisibility={columnVisibility}
          page={record?.log}
          onSelect={setSelectedRow}
        />
        <DialogFooter>
          <Button type="submit" onClick={() => setOpen(false)}>Close</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}