/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { formatDateTime, getRecordLinks, RecordLink, getLinkLabel } from "@/lib/utils"
import { LinkableEntityKind, RecordKind } from "@/app/model/RecordKinds"
import { DetailState } from "./detail-actions"
import LogDialog from "../log/log-dialog"
import InputEx from "../ext/input-ex"
import { detail, LoggerEx } from "@/lib/logger"
import StatusDialog from "../dialog/status-dialog"
import { ArrowRight, LinkIcon } from "lucide-react"
import ButtonEx from "../ext/button-ex"
import useLinkableEntityQueryFilter from "@/hooks/use-linkable-entity-query-filter"
import { LinkableEntityQueryFilter } from "@/app/model/schema"
import { GlobalContext } from "@/lib/context"
import Link from "next/link"
import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "@/components/ui/combobox"
import { InputGroupAddon } from "@/components/ui/input-group"
import Help from "../misc/help"
import { AddOnLink } from "../ext/addon-link"
import { Badge } from "@/components/ui/badge"

const logger = new LoggerEx(detail, "[LinkingDetails] ")

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

  const {createSearchParams} = useLinkableEntityQueryFilter()
  const {queries} = useContext(GlobalContext)
  const [ selectedLinkId, setSelectedLinkId ] = useState('')
  const [ thisRecordLocations, setThisRecordLocations ] = useState<string>('')
  const [ otherRecordLocations, setOtherRecordLocations ] = useState<string>('')
  const recordLinks = useMemo(() => getRecordLinks(record), [record])

  const recordLinksById = useMemo(() => {
    return Object.fromEntries(
      recordLinks.map(l => [l.id, l])
    )
  }, [recordLinks])
  const getSelectedLink = useCallback((linkId?: string) : RecordLink | null => {
    linkId ??= selectedLinkId
    return linkId ? recordLinksById[linkId] ?? null : null
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

  const getOtherRecordUri = useCallback(() => {
    let uri = ""
    if (selectedLink?.otherRecordKind && selectedLink?.otherRecordId) {
      uri = `/${selectedLink.otherRecordKind?.toLowerCase()}s/`
      const newFilter = {...queries[selectedLink.otherRecordKind]?.filter} as LinkableEntityQueryFilter
      newFilter.recordId = selectedLink?.otherRecordId
      const searchParams = createSearchParams(newFilter)
      uri += `?${searchParams.toString()}`
    }
    return uri
  }, [selectedLink, queries, createSearchParams])

  return (
    <div className="w-full grid grid-cols-5 gap-2">
      <span className="text-lg"><LinkIcon className="inline" />&nbsp;Linking</span>
      <Label className="col-start-1" htmlFor="links">Record links</Label>
      <Combobox
        disabled={!record}
        items={recordLinks}
        itemToStringValue={p => p.id}
        itemToStringLabel={p => p.otherRecordLabel}
        value={selectedLink}
        onValueChange={l => handleSelectedLinkChange(l?.id ?? '')}
      >
        <ComboboxInput
          id="links"
          className="col-span-3"
          placeholder={
            recordLinks.length ?? 0 > 0
            ? "-Select a record link-"
            : "-No record links-"
          }
          readOnly={!record}
          showClear
        >
          <InputGroupAddon className="gap-1" align="inline-end">
            <AddOnLink
              href={getOtherRecordUri()}
              disabled={!selectedLinkId}
              title={`Go to the linked ${selectedLink?.otherRecordKind ?? "record"}`}
            >
              <ArrowRight className="w-6 h-6 text-gray-400" />
            </AddOnLink>
            <Badge variant="outline" title="The number of record links">{recordLinks.length.toLocaleString()}</Badge>
            <Help text="A list of all the records which are linked with the selected record. Select one to see its settings." />
          </InputGroupAddon>
        </ComboboxInput>
        <ComboboxContent>
          <ComboboxEmpty>-No record links found-</ComboboxEmpty>
          <ComboboxList>
            {l => (
              <ComboboxItem key={l.id} value={l}>
                {l.otherRecordLabel}
              </ComboboxItem>
            )}
          </ComboboxList>
        </ComboboxContent>
      </Combobox>
      <StatusDialog
        recordKind={recordKind as LinkableEntityKind}
        record={record}
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
      <LogDialog
        className="col-start-5 place-items-center"
        recordKind="RecordLink"
        recordId={selectedLinkId ?? ''}
        recordLabel={getLinkLabel(recordKind as LinkableEntityKind, selectedLink)}
        disabled={!selectedLink || !state.allowRead}
        state={state}
      />
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