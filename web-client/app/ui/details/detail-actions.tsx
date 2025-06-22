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

import { Dispatch, SetStateAction, useCallback } from "react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { cn, getRecordLabel, FormAction } from "@/lib/utils";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import RecordKind from "@/app/model/RecordKind";
import { SecurityContextType } from "@/lib/context"
import { UseFormReturn } from "react-hook-form";

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
}

export function createDetailState(
  securityContext: SecurityContextType,
  mode: DetailMode
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
  }
}

export default function DetailActions<T extends ITrackedEntity, S>(
  {
    className,
    recordKind,
    record,
    form,
    state,
    setMode,
    showFieldHelp,
    setShowFieldHelp,
    onFormAction
  } :
  {
    className?: string
    recordKind: RecordKind
    record?: T
    form: UseFormReturn<any>
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    showFieldHelp: boolean
    setShowFieldHelp: any
    onFormAction: (command: FormAction, value: S) => void
  }) {

  // console.log("DetailActions: render")
  const recordLabel = getRecordLabel(recordKind, record)

  const handleNewOrCancel = useCallback(() => {
    console.log(`ENTER handleNewOrCancel(), state = ${JSON.stringify(state)}, isDirty = ${JSON.stringify(form.formState.isDirty)}, isValid = ${JSON.stringify(form.formState.isValid)}`)
    if (state.updating) {
      if (form.formState.isDirty) {
        if (!confirm(`Discard changes to ${recordLabel}?`))
          return
        toast(`Cancelling ${state.mode}...`)
      }
      onFormAction("reset", form.getValues())
      setMode("view")
    } else {
      toast(`Creating new ${recordKind}...`)
      setMode("create")
      onFormAction("reset", {} as S)
      // form.reset({}, {keepValues: false, keepDefaultValues:false})
    }
  }, [state, form, recordLabel, onFormAction, setMode])

  const handleEditOrSave = useCallback(() => {
    console.log(`ENTER handleEditOrSave(), state = ${JSON.stringify(state)}, isDirty = ${JSON.stringify(form.formState.isDirty)}, isValid = ${JSON.stringify(form.formState.isValid)}`)
    if (state.editing) {
      if (form.formState.isDirty) {
        if (form.formState.isValid) {
          toast(`Saving updated ${recordKind}...`)
          onFormAction("update", form.getValues())
          setMode("view")
        }
      } else {
        setMode("view")
      }
    } else if (state.creating) {
      if (form.formState.isDirty) {
        if (form.formState.isValid) {
          toast(`Saving new ${recordKind}...`)
          onFormAction("create", form.getValues())
          setMode("view")
        }
      } else {
        setMode("view")
      }
    } else {
      toast(`Editing ${recordKind} details...`)
      form.trigger()
      setMode("edit")
    }
  }, [state, form, onFormAction, setMode])

  const handleDelete = useCallback(() => {
    if (confirm(`Delete ${recordLabel}?`)) {
      toast(`Deleting ${recordLabel}...`)
      onFormAction("delete", form.getValues())
      setMode("view")
    }
  }, [recordLabel, onFormAction, form, setMode])

  return (
    <div className={cn("self-start flex flex-col gap-2 items-center", className)}>
      <Button
        type="button"
        onClick={handleNewOrCancel}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={!state.updating && !state.allowCreate}
        title={
          state.updating
          ? `Discard changes to ${recordLabel}`
          : `Create a new ${recordKind}`
        }
      >
        {state.updating ? 'Cancel' : 'New'}
      </Button>
      <Button
        type="button"
        onClick={handleEditOrSave}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={!record || !state.updating && !state.allowEdit || state.updating && !form.formState.isValid}
        title={
          state.updating
          ? `Save changes to ${recordLabel}`
          : `Edit the selected ${recordLabel}`
        }
      >
        {state.updating ? 'Save' : 'Edit'}
      </Button>
      <Button
        type="button"
        onClick={handleDelete}
        className="col-start-6 w-20 place-self-center bg-blue-500"
        disabled={!record || !state.allowDelete || state.updating}
        title={`Delete the selected ${recordLabel}`}
      >
        Delete
      </Button>
      <Label htmlFor="field-help">Field Help</Label>
      <Switch
        id="field-help"
        checked={showFieldHelp}
        onCheckedChange={setShowFieldHelp}
      />
    </div>
  )
}

// DetailActions.whyDidYouRender = true;