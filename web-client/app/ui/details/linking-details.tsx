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

import ILinkableEntity from "@/app/model/ILinkableEntity"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { useCallback, useEffect, useMemo, useRef, useState } from "react"
import { formatDateTime, getRecordLinks, RecordLink, getLinkLabel } from "@/lib/utils"
import { LinkableEntityKind, RecordKind } from "@/app/model/RecordKinds"
import { DetailState } from "./detail-actions"
import Topic from "@/app/model/Topic"
import LogDialog from "../log/log-dialog"
import SelectTriggerEx from "../ext/select-ex"
import InputEx from "../ext/input-ex"
import { detail, LoggerEx } from "@/lib/logger"
import StatusDialog from "../dialog/status-dialog"
import { LinkIcon } from "lucide-react"

const logger = new LoggerEx(detail, "[LinkingDetails] ")

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
  logger.debug("render")

  const [ selectedLinkId, setSelectedLinkId ] = useState('')
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>('')
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>('')
  const recordLinks = useMemo(() => getRecordLinks(record), [record])

  const getSelectedLink = useCallback((linkId?: string) : RecordLink | undefined => {
    linkId ??= selectedLinkId
    if (!linkId)
      return undefined;

    return recordLinks.find(rl => rl.id == linkId)
  }, [selectedLinkId, recordLinks])
  const selectedLink = getSelectedLink(selectedLinkId)

  const refreshEditableFields = useCallback((linkId : string) => {
    const selectedLink = getSelectedLink(linkId)
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
  }, [getSelectedLink])

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

  return (
    <div className="w-full grid grid-cols-5 gap-2">
      <span className="text-lg"><LinkIcon className="inline" />&nbsp;Linking</span>
      <Label className="col-start-1" htmlFor="links">{`Record links (${recordLinks.length}):`}</Label>
      <Select disabled={!record} value={selectedLinkId ?? ''} onValueChange={handleSelectedLinkChange}>
        <SelectTriggerEx
          id="links"
          outerClassName="col-span-3"
          className="w-full"
          help="A list of all the records which are linked with the selected record. Select one to see its settings."
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
        recordLabel={getLinkLabel(recordKind as LinkableEntityKind, selectedLink)}
        className="col-start-5 place-items-center"
        disabled={!selectedLink || !state.allowRead}
        state={state}
      />
      <Label className="col-start-1" htmlFor="link-id">Link ID:</Label>
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
      <StatusDialog recordKind={recordKind as LinkableEntityKind} record={record} />
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
      <Label htmlFor="link-this-locations" className="col-start-1">Location(s) in this record:</Label>
      <InputEx
        id="link-this-locations"
        type="text"
        outerClassName="col-span-3"
        readOnly={true}
        value={thisRecordLocations}
        onChange={e => setThisRecordLocations(e.target.value)}
        help="Location(s) within this record that pertain to the record link"
      />
      <Label htmlFor="link-other-locations" className="col-start-1">Location(s) in other record:</Label>
      <InputEx
        id="link-other-locations"
        type="text"
        outerClassName="col-span-3"
        readOnly={true}
        value={otherRecordLocations}
        onChange={e => setOtherRecordLocations(e.target.value)}
        help="Location(s) within the other record that pertain to the record link"
      />
    </div>
  )
}

LinkingDetails.whyDidYouRender = true