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
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function TrackingDetails({record}: {record: ITrackedEntity | undefined}) {
  function showLog() {
    console.log("Show log...")
  }

  return (
    <div className="w-full grid grid-cols-6 ml-2 mr-2 mb-2 gap-2">
      <Label htmlFor="db-id">Database ID:</Label>
      <Input id="db-id" type="text" readOnly={true} disabled={!record} value={String(record?.id ?? '')} />
      <Label htmlFor="status">Status:</Label>
      <Input id="status" type="text" className="" readOnly={true} disabled={!record} value={record?.status ?? ''} />
      <Button onClick={showLog} className="col-start-6 w-20 place-self-center bg-blue-500" disabled={!record}>Show log</Button>
      <Label htmlFor="created" className="col-start-1">Created on:</Label>
      <Input id="created" type="text" readOnly={true} disabled={!record} value={String(record?.created ?? '')}/>
      <Label htmlFor="created-by">Created by:</Label>
      <Input id="created-by" type="text" className="" readOnly={true} disabled={!record} value={record?.createdByUser?.username ?? ''} />
      <Label htmlFor="updated" className="col-start-1">Updated on:</Label>
      <Input id="updated" type="text" readOnly={true} disabled={!record} value={String(record?.updated ?? '')} />
      <Label htmlFor="updated-by">Updated by:</Label>
      <Input id="updated-by" type="text" className="" readOnly={true} disabled={!record} value={record?.updatedByUser?.username ?? ''} />
    </div>
  )
}