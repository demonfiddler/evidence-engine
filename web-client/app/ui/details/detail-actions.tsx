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

import { Dispatch, SetStateAction, useCallback, useContext, useEffect, useState } from "react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { cn, getRecordLabel } from "@/lib/utils";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import RecordKind from "@/app/model/RecordKind";
import { FieldValues, UseFormReturn } from "react-hook-form";
import { FormActionHandler } from "@/hooks/use-page-logic";
import { Checkbox } from "@/components/ui/checkbox";
import { GlobalContext } from "@/lib/context";
import { Input } from "@/components/ui/input";

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
  updating: boolean
}

export default function DetailActions<T extends ITrackedEntity, V extends FieldValues>(
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
    onFormAction: FormActionHandler<V>
  }) {

  const {masterTopicId, masterTopicPath, masterRecordKind, masterRecordId, masterRecordLabel} = useContext(GlobalContext)

  // For now, we only support linking as a convenience during record creation.
  const allowTopicLink = !!masterTopicId && ["Claim", "Declaration", "Publication", "Person", "Quotation"].includes(recordKind)
  const allowMasterRecordLink = !!masterRecordId && masterRecordKind != recordKind
  const [linkMasterTopic, setLinkMasterTopic] = useState(allowTopicLink)
  const [linkMasterRecord, setLinkMasterRecord] = useState(allowMasterRecordLink)
  const [thisRecordLocationsForTopic, setThisRecordLocationsForTopic] = useState('')
  const [thisRecordLocationsForMaster, setThisRecordLocationsForMaster] = useState('')
  const [otherRecordLocationsForMaster, setOtherRecordLocationsForMaster] = useState('')
  useEffect(() => {
    if (linkMasterTopic != allowTopicLink)
      setLinkMasterTopic(allowTopicLink)
  }, [allowTopicLink])
  useEffect(() => {
    if (linkMasterRecord != allowMasterRecordLink)
      setLinkMasterRecord(allowMasterRecordLink)
    if (!linkMasterRecord) {
      if (thisRecordLocationsForMaster)
        setThisRecordLocationsForMaster('')
      if (otherRecordLocationsForMaster)
        setOtherRecordLocationsForMaster('')
    }
  }, [allowMasterRecordLink])
  useEffect(() => {
    if (!linkMasterTopic && thisRecordLocationsForTopic)
      setThisRecordLocationsForTopic('')
    if (!linkMasterRecord) {
      if (thisRecordLocationsForMaster)
        setThisRecordLocationsForMaster('')
      if (otherRecordLocationsForMaster)
        setOtherRecordLocationsForMaster('')
    }
  }, [linkMasterTopic, linkMasterRecord])
  useEffect(() => {
    if (state.mode == "view") {
      setThisRecordLocationsForTopic('')
      setThisRecordLocationsForMaster('')
      setOtherRecordLocationsForMaster('')
    }
  }, [state.mode])

  const recordLabel = getRecordLabel(recordKind, record)
  const isLinkableEntity = ["Claim", "Declaration", "Person", "Publication", "Quotation"].includes(recordKind)

  const handleNewOrCancel = useCallback(() => {
    // console.log(`ENTER handleNewOrCancel(), state = ${JSON.stringify(state)}, isDirty = ${JSON.stringify(form.formState.isDirty)}, isValid = ${JSON.stringify(form.formState.isValid)}`)
    if (state.updating) {
      // 'Cancel' logic
      if (form.formState.isDirty) {
        const itemLabel = state.mode == "create" ? `new ${recordKind}` : recordLabel
        if (!confirm(`Confirm discard changes to ${itemLabel}?`))
          return
        toast.info(`Cancelling ${state.mode}...`)
      }
      onFormAction("reset")
      setMode("view")
    } else {
      // 'New' logic
      toast.info(`Creating new ${recordKind}...`)
      onFormAction("new")
      setMode("create")
    }
  }, [state, form, recordLabel, onFormAction, setMode])

  const handleEditOrSave = useCallback(() => {
    // console.log(`ENTER handleEditOrSave(), state = ${JSON.stringify(state)}, isDirty = ${JSON.stringify(form.formState.isDirty)}, isValid = ${JSON.stringify(form.formState.isValid)}`)
    switch (state.mode) {
      case "create":
        // 'Create' logic
        if (form.formState.isDirty) {
          if (form.formState.isValid) {
            toast.info(`Saving new ${recordKind}...`)
            const options = linkMasterTopic || linkMasterRecord
              ? {
                linkMasterTopic,
                thisRecordLocationsForTopic,
                linkMasterRecord,
                thisRecordLocationsForMaster,
                otherRecordLocationsForMaster,
              }
              : undefined
            onFormAction("create", form.getValues(), options)
            // NOTE: allow completion callback to reset mode to "view"
          }
        } else {
          setMode("view")
        }
        break
      case "edit":
        // 'Save' logic
        if (form.formState.isDirty) {
          if (form.formState.isValid) {
            toast.info(`Saving updated ${recordKind}...`)
            onFormAction("update", form.getValues())
            // NOTE: allow completion callback to reset mode to "view"
          }
        } else {
          setMode("view")
        }
        break
      default:
        // 'Edit' logic
        toast.info(`Editing ${recordKind} details...`)
        form.trigger()
        setMode("edit")
        break
    }
  }, [
    state,
    form,
    linkMasterTopic,
    thisRecordLocationsForTopic,
    linkMasterRecord,
    thisRecordLocationsForMaster,
    otherRecordLocationsForMaster,
    onFormAction,
    setMode,
  ])

  const handleDelete = useCallback(() => {
    if (confirm(`Confirm delete ${recordLabel}?`)) {
      toast.warning(`Deleting ${recordLabel}...`)
      onFormAction("delete")
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
        disabled={
          !state.updating && (!state.allowEdit || !record) ||
          state.updating && (!form.formState.isDirty || !form.formState.isValid)
        }
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
      <fieldset className={cn("border rounded-md p-2 flex flex-col gap-2", isLinkableEntity && state.mode === "create"? "" : "hidden")}>
        <legend className="text-lg">&nbsp;Create Master Links&nbsp;</legend>
        <div className="flex gap-2" title={`Link new record to master topic '${masterTopicPath}'`}>
          <Checkbox
            id="link-topic"
            disabled={!allowTopicLink}
            checked={linkMasterTopic}
            onCheckedChange={(cs) => setLinkMasterTopic(!!cs)}/>
          <Label className="col-span-3" htmlFor="link-topic">To master topic</Label>
        </div>
        <Label htmlFor="this-locations" className="col-start-1">Location(s) in this record:</Label>
        <Input
          id="this-locations-topic"
          type="text"
          disabled={!linkMasterTopic}
          title={`Locations in the new new ${recordKind} that refer to Topic '${masterTopicPath}'`}
          value={thisRecordLocationsForTopic}
          onChange={e => setThisRecordLocationsForTopic(e.target.value)}
        />
        <div className="flex gap-2" title={`Link new record to master record '${masterRecordLabel}'`}>
          <Checkbox
            id="link-mr"
            disabled={!allowMasterRecordLink}
            checked={linkMasterRecord}
            onCheckedChange={(cs) => setLinkMasterRecord(!!cs)}/>
          <Label className="col-span-3" htmlFor="link-mr">To master record</Label>
        </div>
        <Label htmlFor="this-locations" className="col-start-1">Location(s) in this record:</Label>
        <Input
          id="this-locations-master"
          type="text"
          title={`Locations in the new ${recordKind} that refer to '${masterRecordLabel}'`}
          disabled={!linkMasterRecord}
          value={thisRecordLocationsForMaster}
          onChange={e => setThisRecordLocationsForMaster(e.target.value)}
        />
        <Label htmlFor="other-locations">Location(s) in other record:</Label>
        <Input
          id="other-locations-master"
          type="text"
          title={`Locations in '${masterRecordLabel}' that refer to the new ${recordKind}`}
          disabled={!linkMasterRecord}
          value={otherRecordLocationsForMaster}
          onChange={e => setOtherRecordLocationsForMaster(e.target.value)}
        />
      </fieldset>
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