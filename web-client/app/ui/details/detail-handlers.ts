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

import { getRecordLabel } from "@/lib/utils"
import { useContext, useState } from "react"
import { toast } from "sonner"
import ITrackedEntity from "@/app/model/ITrackedEntity"
import { SecurityContext, SecurityContextType } from "@/lib/context"
import RecordKind from "@/app/model/RecordKind"

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
  handleNewOrCancel: ClickHandler
  handleEditOrSave: ClickHandler
  handleDelete: ClickHandler
}

function createDetailState(
  securityContext: SecurityContextType,
  mode: DetailMode,
  handleNewOrCancel: ClickHandler,
  handleEditOrSave: ClickHandler,
  handleDelete: ClickHandler,
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
    disableNewCancelButton: !record || !updating && !allowCreate,
    disableEditSaveButton: !record || !updating && !allowEdit,
    disableDeleteButton: !record || !allowDelete || updating,
    handleNewOrCancel: handleNewOrCancel,
    handleEditOrSave: handleEditOrSave,
    handleDelete: handleDelete,
    }
}

export default function useDetailHandlers<T extends ITrackedEntity>(recordKind: RecordKind, record?: T) {
  const securityContext = useContext(SecurityContext)
  const [mode, setMode] = useState<DetailMode>("view")
  const [oldRecord, setOldRecord] = useState<T>()

  const state = createDetailState(securityContext, mode, handleNewOrCancel, handleEditOrSave, handleDelete, record)

  function handleNewOrCancel() {
    if (state.updating) {
      if (confirm(`Discard changes to ${getRecordLabel(recordKind, record)}?`)) {
        toast(`Cancelling ${mode}...`)
        // TODO: restore old record
        setOldRecord(undefined)
        setMode("view")
      }
    } else {
      toast(`Creating new ${recordKind}...`)
      setOldRecord(record)
      setMode("create")
    }
  }

  function handleEditOrSave() {
    if (state.editing) {
      toast(`Saving updated ${recordKind}...`)
      // TODO: save updated record
      setOldRecord(undefined)
      setMode("view")
    } else if (state.creating) {
      toast(`Saving new ${recordKind}...`)
      // TODO: create new record
      setOldRecord(undefined)
      setMode("view")
    } else {
      toast(`Editing ${recordKind} details...`)
      setOldRecord(record)
      setMode("edit")
    }
  }

  function handleDelete() {
    if (confirm(`Delete ${getRecordLabel(recordKind, record)}?`)) {
      toast(`Deleting ${getRecordLabel(recordKind, record)}...`)
      // TODO: delete record
    }
  }

  return state
}