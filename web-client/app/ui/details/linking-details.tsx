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
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { getRecordLinkProperties, getReadQuery, getRecordLabel, TO_ENTITY_ID, setTopicFields, flatten } from "@/lib/utils";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
import { CREATE_ENTITY_LINK, DELETE_ENTITY_LINK, READ_ENTITY_LINKS, READ_TOPIC_HIERARCHY, UPDATE_ENTITY_LINK } from "@/lib/graphql-queries";
import { useMutation, useQuery } from "@apollo/client";
import { GlobalContext } from "@/lib/context";
import Topic from "@/app/model/Topic";
import LogDialog from "../log/log-dialog";
import { Span } from "next/dist/trace";

type RecordLink = {
  id: string
  status: string,
  created: string,
  createdByUser: string,
  updated?: string,
  updatedByUser?: string,
  thisRecordIsToEntity: boolean
  thisRecordId: string
  thisLocations: string
  otherRecordKind: RecordKind
  otherRecordId: string
  otherLocations: string
  otherRecordLabel: string
}

function getLinkLabel(recordKind: RecordKind, link?: RecordLink) {
  return `RecordLink #${link?.id} (${recordKind} #${link?.thisRecordId} <—> ${link?.otherRecordKind} #${link?.otherRecordId})`
}

function getRecordLinks(record?: ILinkableEntity, toLinks?: boolean, entityLinks?: EntityLink[], results?: RecordLink[]) : RecordLink[] {
  if (entityLinks) {
    if (!record || !results)
      throw new Error("record, entityLinks and results must all be passed")
    for (let entityLink of entityLinks) {
      if (!entityLink.id)
        continue
      const otherRecord = toLinks ? entityLink.fromEntity : entityLink.toEntity
      const recordLink = {
        id: entityLink.id,
        status: entityLink.status || '',
        created: entityLink.created?.toLocaleString() || '',
        createdByUser: entityLink.createdByUser?.username || '',
        updated: entityLink.updated?.toLocaleString() || '',
        updatedByUser: entityLink.updatedByUser?.username || '',
        thisRecordIsToEntity: toLinks ?? false,
        thisRecordId: record?.id ?? '',
        thisLocations: (toLinks ? entityLink.toEntityLocations : entityLink.fromEntityLocations) ?? '',
        otherLocations: (toLinks ? entityLink.fromEntityLocations : entityLink.toEntityLocations) ?? '',
        otherRecordKind: (otherRecord?.entityKind ?? '') as RecordKind,
        otherRecordId: otherRecord?.id ?? '',
        otherRecordLabel: getRecordLabel(otherRecord?.entityKind as RecordKind, otherRecord) ?? '',
      }
      if (!recordLink.otherRecordLabel)
        console.warn(`No label for RecordLink ${JSON.stringify(recordLink)}`)
      results.push(recordLink)
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
  const { masterRecordKind, masterRecordId, masterRecordLabel, /*queries, setSelectedLinkId*/ } = useContext(GlobalContext)
  const [ selectedLinkId, setSelectedLinkId ] = useState('')
  const [ mode, setMode ] = useState("view")
  const [ linkStatus, setLinkStatus ] = useState<string>('')
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>('')
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>('')
  const [ topicId, setTopicId ] = useState<string>('')
  const topicsResult = useQuery(READ_TOPIC_HIERARCHY, {variables: {filter: {parentId: "-1"}}})
  const readRecordsQuery = getReadQuery(recordKind) ?? READ_ENTITY_LINKS
  const [createLinkOp, createResult] = useMutation(CREATE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const [updateLinkOp, updateResult] = useMutation(UPDATE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const [deleteLinkOp, deleteResult] = useMutation(DELETE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
  const allowLinking = record && state.allowLink && !state.updating

  const rawTopics = (topicsResult.data?.topics.content ?? []) as Topic[]
  const topics = useMemo(() => {
    const tmpTopics = [] as Topic[]
    setTopicFields("", undefined, rawTopics, tmpTopics)
    const flatTopics = flatten(tmpTopics, [])
    return flatTopics
  }, [rawTopics])
  if (topicsResult.error)
    toast.error(topicsResult.error.message)

  const getSelectedLink = useCallback((linkId?: string) : RecordLink | undefined => {
    linkId ??= selectedLinkId
    if (!linkId)
      return undefined;

    return recordLinks.find(rl => rl.id == linkId)
  }, [selectedLinkId, recordLinks])
  const selectedLink = getSelectedLink(selectedLinkId)

  const isModified = useCallback(() => {
    return thisRecordLocations != selectedLink?.thisLocations ||
      otherRecordLocations != selectedLink?.otherLocations ||
      selectedLink?.otherRecordKind == "Topic" && topicId != selectedLink?.otherRecordId
  }, [thisRecordLocations, otherRecordLocations, topicId, selectedLink])

  const createInput = useCallback((recordLink: Partial<RecordLink>) => {
    return recordLink.thisRecordIsToEntity
    ? {
      id: recordLink.id,
      fromEntityId: topicId || recordLink.otherRecordId,
      fromEntityLocations: otherRecordLocations,
      toEntityId: recordLink.thisRecordId,
      toEntityLocations: thisRecordLocations,
    }
    : {
      id: recordLink.id,
      fromEntityId: recordLink.thisRecordId,
      fromEntityLocations: thisRecordLocations,
      toEntityId: recordLink.otherRecordId,
      toEntityLocations: otherRecordLocations,
    }
  }, [topicId, thisRecordLocations, otherRecordLocations])

  const refreshEditableFields = useCallback((linkId : string) => {
    const selectedLink = getSelectedLink(linkId)
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
    setTopicId(selectedLink?.otherRecordKind == "Topic" ? selectedLink?.otherRecordId : '')
  }, [getSelectedLink])

  const handleLinkorCancel = useCallback(() => {
    if (mode === "edit" || mode === "create") {
      if (isModified()) {
        if (confirm(`Confirm discard changes to record link '${selectedLink?.otherRecordLabel}'?`)) {
          toast.info("Cancelling edit ...")
          refreshEditableFields(selectedLinkId)
          setMode("view")
        }
      } else {
        setMode("view")
      }
    } else {
      toast.info(`Linking '${masterRecordLabel}'. Enter link locations then Save.`)
      setMode("create")
    }
  }, [mode, selectedLinkId, selectedLink, isModified, refreshEditableFields])

  const handleSaveOrEdit = useCallback(() => {
    if (mode === "create") {
      const thisRecordId = record?.id
      const otherRecordId = masterRecordId
      const otherRecordLabel = masterRecordLabel
      if (thisRecordId && otherRecordId && otherRecordLabel) {
        const [thisRecordIdProperty] = getRecordLinkProperties(recordKind, masterRecordKind)
        if (thisRecordIdProperty) {
          const thisRecordIsToEntity = thisRecordIdProperty === TO_ENTITY_ID
          createLinkOp({
            variables: {
              input: createInput({
                id: "0",
                thisRecordId,
                thisLocations: thisRecordLocations,
                otherRecordKind: masterRecordKind,
                otherRecordId,
                otherLocations: otherRecordLocations,
                otherRecordLabel,
                thisRecordIsToEntity,
              }
            )},
            onCompleted: () => {
              setMode("view")
            },
            onError: (error) => {
              toast.error(error.message)
            },
          })
        }
      }
    } else if (mode === "edit") {
      toast.info("Saving link locations...")
      if (selectedLink)
        updateLinkOp({
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
  }, [
    mode,
    record,
    recordKind,
    masterRecordKind,
    masterRecordId,
    masterRecordLabel,
    selectedLink,
    createLinkOp,
    createInput,
    updateLinkOp,
  ])

  const handleUnlink = useCallback(() => {
    if (confirm(`Confirm delete record link '${selectedLink?.otherRecordLabel}'?`)) {
      toast.info(`Unlinking '${selectedLink?.otherRecordLabel}'...`)
      deleteLinkOp({
        variables: {entityLinkId: selectedLinkId},
        onError: (error, clientOptions) => {
          toast.error(error.message)
        },
      })
    } else {
      toast.info(`Cancelling unlink '${selectedLink?.otherRecordLabel}'...`)
    }
  }, [selectedLinkId, selectedLink, deleteLinkOp])

  const handleSelectedLinkChange = useCallback((linkId : string) => {
    if (linkId === "CLEAR")
      linkId = ''
    setSelectedLinkId(linkId)
    refreshEditableFields(linkId)
  }, [refreshEditableFields])

  useEffect(() => handleSelectedLinkChange(''), [record])

  return (
    <div className="w-full grid grid-cols-5 gap-2">
      <span className="col-span-5 text-lg">Linking</span>
      <Label htmlFor="links">{`Record links (${recordLinks.length}):`}</Label>
      <Select disabled={!record || mode !== "view"} value={selectedLinkId ?? ''} onValueChange={handleSelectedLinkChange}>
        <SelectTrigger id="links" className="col-span-3 w-full">
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
                ? <SelectItem key={link.id} value={link.id ?? "0"}>{link.otherRecordLabel}</SelectItem>
                : null
            })
          }
        </SelectContent>
      </Select>
      <LogDialog
        recordId={selectedLinkId ?? ''}
        recordLabel={getLinkLabel(recordKind, selectedLink)}
        className="col-start-5 place-items-center"
        disabled={!selectedLink || mode != "view" || !state.allowRead}
        state={state}
      />
      <Label htmlFor="db-id">Database ID:</Label>
      <Input id="db-id" type="text" readOnly={true} disabled={!record} value={selectedLinkId} />
      <Label htmlFor="status">Status:</Label>
      <Input id="status" type="text" readOnly={true} disabled={!record} value={selectedLink?.status ?? ''} />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || (mode == "view" && !selectedLinkId) || (mode == "edit" && !isModified())}
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
      <Label htmlFor="created-by" className="col-start-1">Created by:</Label>
      <Input id="created-by" type="text" readOnly={true} disabled={!selectedLink} value={selectedLink?.createdByUser ?? ''} />
      <Label htmlFor="created">Created on:</Label>
      <Input id="created" type="text" readOnly={true} disabled={!record} value={selectedLink?.created ?? ''} />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={
          !allowLinking ||
          mode === "view" && (
            !!selectedLinkId ||
            masterRecordKind === "None" ||
            masterRecordKind === recordKind ||
            !masterRecordId ||
            recordLinks.findIndex(r => r.otherRecordId == masterRecordId) != -1
          )
        }
        onClick={handleLinkorCancel}
        title={
          mode === "view"
          ? `Create record link with '${masterRecordLabel}'`
          : mode === "create"
            ? "Save changes to new record link"
            : `Save changes to record link '${selectedLink?.otherRecordLabel}'`
        }
      >
        {mode !== "view" ? "Cancel" : "Link"}
      </Button>
      <Label htmlFor="updated-by" className="col-start-1">Updated by:</Label>
      <Input id="updated-by" type="text" readOnly={true} disabled={!record} value={selectedLink?.updatedByUser ?? ''} />
      <Label htmlFor="updated">Updated on:</Label>
      <Input id="updated" type="text" readOnly={true} disabled={!record} value={selectedLink?.updated ?? ''} />
      <Button
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId || mode !== "view"}
        onClick={handleUnlink}
        title={`Delete record link '${selectedLink?.otherRecordLabel}'`}
      >
        Unlink
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
      <Label htmlFor="other-locations" className="col-start-1">Location(s) in other record:</Label>
      <Input
        id="other-locations"
        type="text"
        className="col-span-3"
        disabled={!record || selectedLink?.otherRecordKind == "Topic"}
        readOnly={!allowLinking || mode === "view"}
        value={otherRecordLocations}
        onChange={e => setOtherRecordLocations(e.target.value)}
      />
      <Label htmlFor="topicId" className="col-start-1">If other record is a Topic:</Label>
      <Select
        value={topicId}
        onValueChange={setTopicId}
      >
        <SelectTrigger
          id="topicId"
          className="col-span-3 w-full"
          disabled={mode != "edit" || selectedLink?.otherRecordKind != "Topic"}>
          <SelectValue className="col-span-3 w-full" placeholder="-Other record is not a topic-" />
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel>Topics</SelectLabel>
            {
              topics.map(topic => (
                <SelectItem
                  key={topic.id ?? ''}
                  value={topic.id ?? ''}
                >
                  {`Topic #${topic.id}: ${topic.path}`}
                </SelectItem>
              ))
            }
          </SelectGroup>
        </SelectContent>
      </Select>
    </div>
  )
}
