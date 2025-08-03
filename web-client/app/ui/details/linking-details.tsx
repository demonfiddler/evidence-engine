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

import EntityLink from "@/app/model/EntityLink";
import ILinkableEntity from "@/app/model/ILinkableEntity";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useContext, useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { FROM_ENTITY_ID, getLinkFilterIdProperty, getReadQuery, getRecordLabel } from "@/lib/utils";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
import { CREATE_ENTITY_LINK, DELETE_ENTITY_LINK, READ_ENTITY_LINKS, UPDATE_ENTITY_LINK } from "@/lib/graphql-queries";
import { useMutation } from "@apollo/client";
import { MasterLinkContext } from "@/lib/context";

type RecordLink = {
  entityLinkId?: string
  thisRecordIsToEntity: boolean
  thisRecordId: string
  thisLocations: string
  otherRecordId: string
  otherLocations: string
  otherRecordLabel: string
}

function getRecordLinks(record?: ILinkableEntity, toLinks?: boolean, entityLinks?: EntityLink[], results?: RecordLink[]) : RecordLink[] {
  if (entityLinks) {
    if (!record || !results)
      throw new Error("record, entityLinks and results must all be passed")
    for (let entityLink of entityLinks) {
      if (!entityLink.id)
        continue
      const otherRecord = toLinks ? entityLink.fromEntity : entityLink.toEntity
      const displayableLink = {
        entityLinkId: entityLink.id,
        thisRecordIsToEntity: toLinks ?? false,
        thisRecordId: record?.id ?? '',
        thisLocations: (toLinks ? entityLink.toEntityLocations : entityLink.fromEntityLocations) ?? '',
        otherLocations: (toLinks ? entityLink.fromEntityLocations : entityLink.toEntityLocations) ?? '',
        otherRecordId: otherRecord?.id ?? '',
        otherRecordLabel: getRecordLabel(otherRecord?.entityKind as RecordKind, otherRecord) ?? '',
      }
      if (!displayableLink.otherRecordLabel)
        console.warn(`No label for RecordLink ${JSON.stringify(displayableLink)}`)
      results.push(displayableLink)
    }
  } else {
    results = []
    if (record) {
      getRecordLinks(record, false, record.fromEntityLinks?.content, results)
      getRecordLinks(record, true, record.toEntityLinks?.content, results)
    }
  }
  return results
}

export default function LinkingDetails(
  {
    recordKind,
    record,
    state
  } : {
    recordKind: RecordKind
    record: ILinkableEntity | undefined
    state: DetailState
  }) {
  const masterLinkContext = useContext(MasterLinkContext)
  const [ selectedLinkId, setSelectedLinkId ] = useState('')
  const [ mode, setMode ] = useState("view")
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>('')
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>('')
  const readQuery = getReadQuery(recordKind) ?? READ_ENTITY_LINKS
  const [createOp, createResult] = useMutation(CREATE_ENTITY_LINK, {refetchQueries: [readQuery]})
  const [updateOp, updateResult] = useMutation(UPDATE_ENTITY_LINK, {refetchQueries: [readQuery]})
  const [deleteOp, deleteResult] = useMutation(DELETE_ENTITY_LINK, {refetchQueries: [readQuery]})
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
  const selectedLink = getSelectedLink(selectedLinkId)
  const allowLinking = record && state.allowLink && !state.updating
  const recordLabel = mode == "create" ? `new ${recordKind}` : getRecordLabel(recordKind, record)

  function createInput(recordLink: RecordLink) {
    return recordLink.thisRecordIsToEntity
    ? {
      id: recordLink.entityLinkId,
      fromEntityId: recordLink.otherRecordId,
      fromEntityLocations: otherRecordLocations,
      toEntityId: recordLink.thisRecordId,
      toEntityLocations: thisRecordLocations,
    }
    : {
      id: recordLink.entityLinkId,
      fromEntityId: recordLink.thisRecordId,
      fromEntityLocations: thisRecordLocations,
      toEntityId: recordLink.otherRecordId,
      toEntityLocations: otherRecordLocations,
    }
  }

  function handleLinkorCancel() {
    if (mode === "edit") {
      if (confirm(`Confirm discard changes to record link '${selectedLink?.otherRecordLabel}'?`)) {
        toast.info("Cancelling edit ...")
        refreshLocations(selectedLinkId)
        setMode("view")
      }
    } else {
      toast.info(`Linking '${masterLinkContext.masterRecordLabel}'. Enter link locations then Save.`)
      setMode("create")
    }
  }

  function handleUnlink() {
    if (confirm(`Confirm delete record link '${selectedLink?.otherRecordLabel}'?`)) {
      toast.info(`Unlinking '${selectedLink?.otherRecordLabel}'...`)
      deleteOp({
        variables: {entityLinkId: selectedLinkId},
        // onCompleted: (data, clientOptions) => {
        //   // TODO: what, if anything?
        // },
        onError: (error, clientOptions) => {
          toast.error(error.message)
        },
      })
    } else {
      toast.info(`Cancelling unlink '${selectedLink?.otherRecordLabel}'...`)
    }
  }

  function handleSaveOrEdit() {
    if (mode === "create") {
      const thisRecordId = record?.id
      const otherRecordId = masterLinkContext.masterRecordId
      const otherRecordLabel = masterLinkContext.masterRecordLabel
      if (thisRecordId && otherRecordId && otherRecordLabel) {
        const otherRecordIdProperty = getLinkFilterIdProperty(recordKind, masterLinkContext.masterRecordKind)
        if (otherRecordIdProperty) {
          const thisRecordIsToEntity = otherRecordIdProperty === FROM_ENTITY_ID
          createOp({
            variables: {
              input: createInput({
                entityLinkId: "0",
                thisRecordId,
                thisLocations: thisRecordLocations,
                otherRecordId,
                otherLocations: otherRecordLocations,
                otherRecordLabel,
                thisRecordIsToEntity,
              }
            )},
            onCompleted: (data, clientOptions) => {
              setMode("view")
            },
            onError: (error, clientOptions) => {
              toast.error(error.message)
            },
          })
        }
      }
    } else if (mode === "edit") {
      toast.info("Saving link locations...")
      if (selectedLink)
        updateOp({
          variables: {
            input: createInput(selectedLink)
          },
          onCompleted: (data, clientOptions) => {
            setMode("view")
          },
          onError: (error, clientOptions) => {
            toast.error(error.message)
          },
        })
    } else {
      toast.info("Edit link locations then Save")
      setMode("edit")
    }
  }

  function refreshLocations(linkId : string) {
    const selectedLink = getSelectedLink(linkId)
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
  }

  function handleSelectedLinkChange(linkId : string) {
    if (linkId === "CLEAR")
      linkId = ''
    setSelectedLinkId(linkId)
    refreshLocations(linkId)
  }

  function getSelectedLink(linkId?: string) : RecordLink | undefined  {
    linkId ??= selectedLinkId
    if (!linkId)
      return undefined;

    return recordLinks.find(rl => rl.entityLinkId == linkId)
  }

  useEffect(() => handleSelectedLinkChange(''), [record])

  return (
    <div className="w-full grid grid-cols-5 gap-2">
      <Label htmlFor="master">{`Record links (${recordLinks.length}):`}</Label>
      <Select disabled={!record || mode !== "view"} value={selectedLinkId ?? ''} onValueChange={handleSelectedLinkChange}>
        <SelectTrigger className="col-span-3 w-full">
          <SelectValue placeholder={
            recordLinks.length ?? 0 > 0
            ? "-Select a record link-"
            : "-No record links-"}
          />
        </SelectTrigger>
        <SelectContent>
          {
            selectedLinkId
            ? <SelectItem key="0" value="CLEAR">-Clear-</SelectItem>
            : null
          }
          {
            recordLinks.map(link => {
              return link
                ? <SelectItem key={link.entityLinkId} value={link.entityLinkId ?? "0"}>{link.otherRecordLabel}</SelectItem>
                : null
            })
          }
        </SelectContent>
      </Select>
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={
          !allowLinking ||
          mode === "view" && (
            !!selectedLinkId ||
            masterLinkContext.masterRecordKind === "None" ||
            masterLinkContext.masterRecordKind === recordKind ||
            !masterLinkContext.masterRecordId ||
            recordLinks.findIndex(r => r.otherRecordId == masterLinkContext.masterRecordId) != -1
          )
        }
        onClick={handleLinkorCancel}
        title={
          mode === "view"
          ? `Create record link with '${masterLinkContext.masterRecordLabel}'`
          : mode === "create"
            ? "Save changes to new record link"
            : `Save changes to record link '${selectedLink?.otherRecordLabel}'`
        }
      >
        {mode !== "view" ? "Cancel" : "Link"}
      </Button>
      <Label htmlFor="this-locations" className="col-start-1">Location(s) in this record:</Label>
      <Input
        id="this-locations"
        type="text"
        className="col-span-3"
        disabled={!record && !mode}
        readOnly={!allowLinking || mode === "view"}
        value={thisRecordLocations}
        onChange={e => setThisRecordLocations(e.target.value)}
      />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || (mode == "view" && !selectedLinkId)}
        onClick={handleSaveOrEdit}
        title={
          mode === "view"
          ? `Edit selected record link '${selectedLink?.otherRecordLabel}'`
          : mode === "create"
            ? "Save changes to new record link"
            : `Save changes to record link '${selectedLink?.otherRecordLabel}'`
        }
      >
        {mode !== "view" ? 'Save' : 'Edit'}
      </Button>
      <Label htmlFor="other-locations">Location(s) in other record:</Label>
      <Input
        id="other-locations"
        type="text"
        className="col-span-3"
        disabled={!record}
        readOnly={!allowLinking || mode === "view"}
        value={otherRecordLocations}
        onChange={e => setOtherRecordLocations(e.target.value)}
      />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId || mode !== "view"}
        onClick={handleUnlink}
        title={`Delete record link '${selectedLink?.otherRecordLabel}'`}
      >
        Unlink
      </Button>
    </div>
  )
}
