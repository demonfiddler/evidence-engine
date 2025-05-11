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
import { cn, getRecordLabel } from "@/lib/utils";
import { DetailState } from "./detail-handlers";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import RecordKind from "@/app/model/RecordKind";

export default function DetailActions(
  { className, recordKind, record, state, }:
  { className?: string, recordKind: RecordKind, record?: ITrackedEntity, state: DetailState } ) {

    const recordLabel = getRecordLabel(recordKind, record)

  return (
    <div className={cn("self-start flex flex-col gap-2", className)}>
      <Button
        onClick={state.handleNewOrCancel}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={state.disableNewCancelButton}
        title={
          state.updating
          ? `Discard changes to ${recordLabel}`
          : `Create a new ${recordKind}`
        }
      >
        {state.updating ? 'Cancel' : 'New'}
      </Button>
      <Button
        onClick={state.handleEditOrSave}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={state.disableEditSaveButton}
        title={
          state.updating
          ? `Save changes to ${recordLabel}`
          : `Edit the selected ${recordLabel}`
        }
      >
        {state.updating ? 'Save' : 'Edit'}
      </Button>
      <Button
        onClick={state.handleDelete}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={state.disableDeleteButton}
        title={`Delete the selected ${recordLabel}`}
      >
        Delete
      </Button>
    </div>
  )
}