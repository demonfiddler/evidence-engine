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

import { Dispatch, SetStateAction } from "react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button";
import { Action, cn, getRecordLabel } from "@/lib/utils";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import RecordKind from "@/app/model/RecordKind";
import { SecurityContextType } from "@/lib/context"

export type ClickHandler = () => void

export type DetailMode = "create" | "edit" | "view"

export type DetailState = {
  mode: DetailMode
  allowCreate: boolean
  allowEdit: boolean
  allowUpdate: boolean
  allowDelete: boolean
  allowLink: boolean
  allowRead: boolean
  editing: boolean
  updating: boolean
  creating: boolean
  disableNewCancelButton: boolean
  disableEditSaveButton: boolean
  disableDeleteButton: boolean
}

export function createDetailState(
  securityContext: SecurityContextType,
  mode: DetailMode,
  record?: ITrackedEntity,
): DetailState {

  const allowCreate = securityContext.authorities?.includes("CRE") ?? false
  const allowEdit = securityContext.authorities?.includes("UPD") ?? false
  const allowDelete = securityContext.authorities?.includes("DEL") ?? false
  const allowUpdate = allowCreate || allowEdit
  const allowLink = securityContext.authorities?.includes("LNK") ?? false
  const allowRead = securityContext.authorities?.includes("REA") ?? false
  const creating = mode == "create"
  const editing = mode == "edit"
  const updating = mode == "create" || mode == "edit"

  return {
    mode: mode,
    allowCreate: allowCreate,
    allowEdit: allowEdit,
    allowDelete: allowDelete,
    allowUpdate: allowUpdate,
    allowLink: allowLink,
    allowRead: allowRead,
    creating: creating,
    editing: editing,
    updating: updating,
    disableNewCancelButton: !updating && !allowCreate,
    disableEditSaveButton: !record || !updating && !allowEdit,
    disableDeleteButton: !record || !allowDelete || updating,
  }
}

export default function DetailActions<T extends ITrackedEntity>(
  { className, recordKind, record, state, setMode, pageDispatch, recordDispatch }:
  {
    className?: string
    recordKind: RecordKind
    record?: T
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    pageDispatch: Dispatch<Action>
    recordDispatch: Dispatch<Action | undefined>
  } ) {

  const recordLabel = getRecordLabel(recordKind, record)

  function handleNewOrCancel() {
    if (state.updating) {
      if (confirm(`Discard changes to ${recordLabel}?`)) {
        toast(`Cancelling ${state.mode}...`)
        setMode("view")
      }
    } else {
      toast(`Creating new ${recordKind}...`)
      recordDispatch({recordId: record?.id ?? "0", command: "new", value: record})
      setMode("create")
    }
  }

  function handleEditOrSave() {
    if (state.editing) {
      toast(`Saving updated ${recordKind}...`)
      pageDispatch({recordId: record?.id ?? "0", command: "update", value: record})
      setMode("view")
    } else if (state.creating) {
      toast(`Saving new ${recordKind}...`)
      pageDispatch({recordId: record?.id ?? "0", command: "add", value: record})
      setMode("view")
    } else {
      toast(`Editing ${recordKind} details...`)
      recordDispatch({recordId: record?.id ?? "0", command: "edit", value: record})
      setMode("edit")
    }
  }

  function handleDelete() {
    if (confirm(`Delete ${recordLabel}?`)) {
      toast(`Deleting ${recordLabel}...`)
      pageDispatch({recordId: record?.id ?? "0", command: "delete", value: undefined})
      setMode("view")
    }
  }

  return (
    <div className={cn("self-start flex flex-col gap-2", className)}>
      <Button
        onClick={handleNewOrCancel}
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
        onClick={handleEditOrSave}
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
        onClick={handleDelete}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={state.disableDeleteButton}
        title={`Delete the selected ${recordLabel}`}
      >
        Delete
      </Button>
    </div>
  )
}