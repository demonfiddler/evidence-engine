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

import EntityLink from "@/app/model/EntityLink"
import ILinkableEntity from "@/app/model/ILinkableEntity"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { toast } from "sonner"
import { getRecordLinkProperties, getReadQuery, getRecordLabel, TO_ENTITY_ID, setTopicFields, flatten, formatDateTime } from "@/lib/utils"
import RecordKind from "@/app/model/RecordKind"
import { DetailState } from "./detail-actions"
import { CREATE_ENTITY_LINK, DELETE_ENTITY_LINK, READ_ENTITY_LINKS, READ_TOPIC_HIERARCHY, UPDATE_ENTITY_LINK } from "@/lib/graphql-queries"
import { useMutation, useQuery } from "@apollo/client"
import { GlobalContext } from "@/lib/context"
import Topic from "@/app/model/Topic"
import LogDialog from "../log/log-dialog"
import SelectTriggerEx from "../ext/select-ex"
import InputEx from "../ext/input-ex"
import ButtonEx from "../ext/button-ex"

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
  return link
    ? `RecordLink #${link?.id} (${recordKind} #${link?.thisRecordId} <—> ${link?.otherRecordKind} #${link?.otherRecordId})`
    : "Selected Record Link"
}

function getRecordLinks(record?: ILinkableEntity, toLinks?: boolean, entityLinks?: EntityLink[], results?: RecordLink[]) : RecordLink[] {
  if (entityLinks) {
    if (!record || !results)
      throw new Error("record, entityLinks and results must all be passed")
    for (const entityLink of entityLinks) {
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
    if (record?.fromEntityLinks?.content)
      getRecordLinks(record, false, record.fromEntityLinks.content, results)
    if (record?.toEntityLinks?.content)
      getRecordLinks(record, true, record.toEntityLinks.content, results)
  }
  return results
}

const EMPTY = [] as Topic[]

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
  const { masterRecordKind, masterRecordId, masterRecordLabel } = useContext(GlobalContext)
  const [ selectedLinkId, setSelectedLinkId ] = useState('')
  const [ mode, setMode ] = useState("view")
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>('')
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>('')
  const [ topicId, setTopicId ] = useState<string>('')
  const topicsResult = useQuery(READ_TOPIC_HIERARCHY, {variables: {filter: {parentId: "-1"}}})
  const readRecordsQuery = getReadQuery(recordKind) ?? READ_ENTITY_LINKS
  const [createLinkOp/*, createResult*/] = useMutation(CREATE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const [updateLinkOp/*, updateResult*/] = useMutation(UPDATE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const [deleteLinkOp/*, deleteResult*/] = useMutation(DELETE_ENTITY_LINK, {refetchQueries: [readRecordsQuery]})
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
  const allowLinking = record && state.allowLink && !state.updating

  const rawTopics = (topicsResult.data?.topics.content ?? EMPTY) as Topic[]
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
        const target = mode === "edit"
          ? `link with record '${selectedLink?.otherRecordLabel}'`
          : "new record link"
        if (confirm(`Confirm discard changes to ${target}?`)) {
          toast.info(`Cancelling ${mode} ...`)
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
  }, [mode, selectedLinkId, selectedLink, isModified, refreshEditableFields, masterRecordLabel])

  const handleSaveOrEdit = useCallback(() => {
    if (mode === "create") {
      const thisRecordId = record?.id
      const otherRecordId = masterRecordId
      const otherRecordLabel = masterRecordLabel
      if (thisRecordId && otherRecordId && otherRecordLabel) {
        const [,, thisRecordIdProperty] = getRecordLinkProperties(recordKind, masterRecordKind)
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
          onCompleted: (/*data, clientOptions*/) => {
            setMode("view")
          },
          onError: (error/*, clientOptions*/) => {
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
    otherRecordLocations,
    thisRecordLocations,
    selectedLink,
    createLinkOp,
    createInput,
    updateLinkOp,
  ])

  const handleUnlink = useCallback(() => {
    if (confirm(`Confirm delete link with record '${selectedLink?.otherRecordLabel}'?`)) {
      toast.info(`Unlinking '${selectedLink?.otherRecordLabel}'...`)
      deleteLinkOp({
        variables: {entityLinkId: selectedLinkId},
        onError: (error/*, clientOptions*/) => {
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

  const prevRecord = useRef<ILinkableEntity>(undefined)
  useEffect(() => {
    if (record !== prevRecord.current) {
      prevRecord.current = record
      handleSelectedLinkChange('')
    }
  }, [handleSelectedLinkChange, record])

  const alreadyLinked = recordLinks.findIndex(r => r.otherRecordId == masterRecordId) != -1

  return (
    <div className="w-full grid grid-cols-5 gap-2">
      <span className="col-span-5 text-lg">Linking</span>
      <Label htmlFor="links">{`Record links (${recordLinks.length}):`}</Label>
      <Select disabled={!record || mode !== "view"} value={selectedLinkId ?? ''} onValueChange={handleSelectedLinkChange}>
        <SelectTriggerEx
          id="links"
          outerClassName="col-span-3"
          className="w-full"
          help="A list of all the records which are linked with the selected record. Select one to see/edit its settings."
        >
          <SelectValue placeholder={
            recordLinks.length ?? 0 > 0
            ? "-Select a record link-"
            : "-No record links-"}
          />
        </SelectTriggerEx>
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
        recordKind="RecordLink"
        recordId={selectedLinkId ?? ''}
        recordLabel={getLinkLabel(recordKind, selectedLink)}
        className="col-start-5 place-items-center"
        disabled={!selectedLink || mode != "view" || !state.allowRead}
        state={state}
      />
      <Label className="col-start-1" htmlFor="link-id">Database ID:</Label>
      <InputEx
        id="link-id"
        type="text"
        readOnly={true}
        disabled={!record}
        value={selectedLinkId}
        help="The database identifier of the selected record link"
      />
      <Label htmlFor="link-status">Status:</Label>
      <InputEx
        id="link-status"
        type="text"
        readOnly={true}
        disabled={!record}
        value={selectedLink?.status ?? ''}
        help="The status of the selected record link"
      />
      <ButtonEx
        outerClassName="justify-center w-full"
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || (mode == "view" && !selectedLinkId) || (mode == "edit" && !isModified())}
        onClick={handleSaveOrEdit}
        help={
          mode === "view"
          ? selectedLink
            ? `Edit selected link with record '${selectedLink?.otherRecordLabel}'. Requires authentication and 'Link' authority.`
            : "No record link selected"
          : mode === "create"
            ? "Save changes to new record link"
            : `Save changes to link with record '${selectedLink?.otherRecordLabel}'`
        }
      >
        {mode !== "view" ? 'Save' : 'Edit'}
      </ButtonEx>
      <Label htmlFor="link-created-by" className="col-start-1">Created by:</Label>
      <InputEx
        id="link-created-by"
        type="text"
        readOnly={true}
        disabled={!selectedLink}
        value={selectedLink?.createdByUser ?? ''}
        help="The username of the user who created the record link"
      />
      <Label htmlFor="link-created">Created on:</Label>
      <InputEx
        id="link-created"
        type="text"
        readOnly={true}
        disabled={!record}
        value={formatDateTime(selectedLink?.created)}
        help="The date and time at which the record link was created"
      />
      <ButtonEx
        outerClassName="justify-center w-full"
        className="w-20 place-self-center bg-blue-500"
        disabled={
          !allowLinking ||
          mode === "view" && (
            !!selectedLinkId ||
            masterRecordKind === "None" ||
            masterRecordKind === recordKind ||
            !masterRecordId ||
            alreadyLinked
          )
        }
        onClick={handleLinkorCancel}
        help={
          mode === "view"
          ? recordKind != masterRecordKind && masterRecordId && !alreadyLinked
             ? `Create link with record '${masterRecordLabel}'. Requires authentication and 'Link' authority.`
             : "Record link creation not possible"
          : mode === "create"
            ? "Discard changes to new record link"
            : `Discard changes to link with record '${selectedLink?.otherRecordLabel}'`
        }
      >
        {mode !== "view" ? "Cancel" : "Link"}
      </ButtonEx>
      <Label htmlFor="link-updated-by" className="col-start-1">Updated by:</Label>
      <InputEx
        id="link-updated-by"
        type="text"
        readOnly={true}
        disabled={!record}
        value={selectedLink?.updatedByUser ?? ''}
        help="The username of the user who last updated the record link"
      />
      <Label htmlFor="link-updated">Updated on:</Label>
      <InputEx
        id="link-updated"
        type="text"
        readOnly={true}
        disabled={!record}
        value={formatDateTime(selectedLink?.updated)}
        help="The date and time at which the record link was last updated"
      />
      <ButtonEx
        outerClassName="justify-center w-full"
        className="w-20 place-self-center bg-blue-500"
        disabled={!allowLinking || !selectedLinkId || mode !== "view"}
        onClick={handleUnlink}
        help={
          selectedLink
            ? `Delete link with record '${selectedLink?.otherRecordLabel}'. Requires authentication and 'Link' authority.`
            : "No record link selected"
        }
      >
        Unlink
      </ButtonEx>
      <Label htmlFor="link-this-locations" className="col-start-1">Location(s) in this record:</Label>
      <InputEx
        id="link-this-locations"
        type="text"
        outerClassName="col-span-3"
        disabled={!record && !mode}
        readOnly={!allowLinking || mode === "view"}
        value={thisRecordLocations}
        onChange={e => setThisRecordLocations(e.target.value)}
        help="Location(s) within this record that pertain to the record link"
      />
      <Label htmlFor="link-other-locations" className="col-start-1">Location(s) in other record:</Label>
      <InputEx
        id="link-other-locations"
        type="text"
        outerClassName="col-span-3"
        disabled={!record || selectedLink?.otherRecordKind == "Topic"}
        readOnly={!allowLinking || mode === "view"}
        value={otherRecordLocations}
        onChange={e => setOtherRecordLocations(e.target.value)}
        help="Location(s) within the other record that pertain to the record link"
      />
      <Label htmlFor="link-topicId" className="col-start-1">If other record is a Topic:</Label>
      <Select
        value={topicId}
        onValueChange={setTopicId}
      >
        <SelectTriggerEx
          id="link-topicId"
          className="col-span-3 w-full"
          disabled={mode != "edit" || selectedLink?.otherRecordKind != "Topic"}
          help="When applicable, the topic to which the record is linked"
        >
          <SelectValue className="col-span-3 w-full" placeholder="-Other record is not a topic-" />
        </SelectTriggerEx>
        <SelectContent>
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
        </SelectContent>
      </Select>
    </div>
  )
}

LinkingDetails.whyDidYouRender = true