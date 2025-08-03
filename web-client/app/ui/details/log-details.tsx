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

import Log from "@/app/model/Log";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { formatDate } from "@/lib/utils";

export default function LogDetails({record}: {record: Log | undefined}) {
  return (
    <fieldset className="border shadow-lg rounded-md w-2/3">
      <legend>&nbsp;Log Details&nbsp;</legend>
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Log #${record?.id}` : "-Select a log entry in the list above to see its details-"}</p>
      <div className="grid grid-cols-4 ml-2 mr-2 mb-2 gap-2">
        <Label htmlFor="timestamp" className="col-start-1">Timestamp:</Label>
        <Input id="timestamp" className="col-span-1" disabled={!record} readOnly={true} value={formatDate(record?.timestamp) ?? ''} />
        <Label htmlFor="transactionKind" className="col-start-1">Transaction kind:</Label>
        <Input id="transactionKind" disabled={!record} readOnly={true} value={record?.transactionKind ?? ''} />
        <Label htmlFor="username">Username:</Label>
        <Input id="username" disabled={!record} readOnly={true} value={record?.user?.username ?? ''} />
        <Label htmlFor="entityKind" className="col-start-1">Record kind:</Label>
        <Input id="entityKind" disabled={!record} readOnly={true} value={record?.entityKind ?? ''} />
        <Label htmlFor="linkedEntityKind">Linked record kind:</Label>
        <Input id="linkedEntityKind" disabled={!record} readOnly={true} value={record?.linkedEntityKind ?? ''} />
        <Label htmlFor="entityId">Record ID:</Label>
        <Input id="entityId" disabled={!record} readOnly={true} value={record?.entityId?.toString() ?? ''} />
        <Label htmlFor="linkedEntityId">Linked record ID:</Label>
        <Input id="linkedEntityId" disabled={!record} readOnly={true} value={record?.linkedEntityId?.toString() ?? ''} />
      </div>
    </fieldset>
  )
}