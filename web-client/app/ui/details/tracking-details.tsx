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

import ITrackedEntity from "@/app/model/ITrackedEntity";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import LogDialog from "../log/log-dialog";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
import { getRecordLabel } from "@/lib/utils";

export default function TrackingDetails(
  {
    recordKind,
    record,
    state
  } : {
    recordKind: RecordKind
    record: ITrackedEntity | undefined
    state: DetailState
  }) {

  return (
    <div className="w-full grid grid-cols-5 mb-2 gap-2">
      <Label htmlFor="db-id">Database ID:</Label>
      <Input id="db-id" type="text" readOnly={true} disabled={!record} value={String(record?.id ?? '')} />
      <Label htmlFor="status">Status:</Label>
      <Input id="status" type="text" readOnly={true} disabled={!record} value={record?.status ?? ''} />
      <LogDialog
        recordId={record?.id ?? ''}
        recordLabel={getRecordLabel(recordKind, record) ?? ''}
        className="col-start-5 place-items-center"
        disabled={!record || !state.allowRead || state.mode == "create"}
        state={state}
      />
      <Label htmlFor="created-by" className="col-start-1">Created by:</Label>
      <Input id="created-by" type="text" readOnly={true} disabled={!record} value={record?.createdByUser?.username ?? ''} />
      <Label htmlFor="created">Created on:</Label>
      <Input id="created" type="text" readOnly={true} disabled={!record} value={record?.created?.toLocaleString() ?? ''} />
      <Label htmlFor="updated-by" className="col-start-1">Updated by:</Label>
      <Input id="updated-by" type="text" readOnly={true} disabled={!record} value={record?.updatedByUser?.username ?? ''} />
      <Label htmlFor="updated">Updated on:</Label>
      <Input id="updated" type="text" readOnly={true} disabled={!record} value={record?.updated?.toLocaleString() ?? ''} />
    </div>
  )
}