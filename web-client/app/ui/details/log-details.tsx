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

import Log from "@/app/model/Log"
import { Label } from "@/components/ui/label"
import { formatDateTime } from "@/lib/utils"
import InputEx from "../ext/input-ex"
import { detail, LoggerEx } from "@/lib/logger"
import { NotebookTabsIcon } from "lucide-react"

const logger = new LoggerEx(detail, "[LogDetails] ")

export default function LogDetails({record}: {record: Log | undefined}) {
  logger.debug("render")

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Log Details&nbsp;</legend>
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Log #${record?.id}` : "-Select a log entry in the list above to see its details-"}</p>
      <span className="text-black text-lg ml-2"><NotebookTabsIcon className="inline" />&nbsp;Details</span>
      <div className="grid grid-cols-4 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="timestamp" className="col-start-1">Timestamp:</Label>
        <InputEx
          id="timestamp"
          className="col-span-1"
          disabled={!record}
          readOnly={true}
          value={formatDateTime(record?.timestamp)}
          help="The date and time at which the event occurred"
        />
        <Label htmlFor="transactionKind" className="col-start-1">Transaction kind:</Label>
        <InputEx
          id="transactionKind"
          disabled={!record}
          readOnly={true}
          value={record?.transactionKind ?? ''}
          help="The kind of transaction that was executed"
        />
        <Label htmlFor="username">Username:</Label>
        <InputEx
          id="username"
          disabled={!record}
          readOnly={true}
          value={record?.user?.username ?? ''}
          help="The username of the user who executed the transaction"
        />
        <Label htmlFor="entityKind" className="col-start-1">Record kind:</Label>
        <InputEx
          id="entityKind"
          disabled={!record}
          readOnly={true}
          value={record?.entityKind ?? ''}
          help="The kind of record affected by the transaction"
        />
        <Label htmlFor="linkedEntityKind">Linked record kind:</Label>
        <InputEx
          id="linkedEntityKind"
          disabled={!record}
          readOnly={true}
          value={record?.linkedEntityKind ?? ''}
          help="The kind of record that was linked with or unlinked from this record"
        />
        <Label htmlFor="entityId">Record ID:</Label>
        <InputEx
          id="entityId"
          disabled={!record}
          readOnly={true}
          value={record?.entityId?.toString() ?? ''}
          help="The database ID of the affected record"
        />
        <Label htmlFor="linkedEntityId">Linked record ID:</Label>
        <InputEx
          id="linkedEntityId"
          disabled={!record}
          readOnly={true}
          value={record?.linkedEntityId?.toString() ?? ''}
          help="The database ID of the other linked/unlinked record"
        />
      </div>
    </fieldset>
  )
}

LogDetails.whyDidYouRender = true