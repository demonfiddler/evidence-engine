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

import { LinkableEntityKind, LinkableEntityKindKeys, RecordIcons } from "@/app/model/RecordKinds"
import { ForwardRefExoticComponent, RefAttributes, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { Button } from "@/components/ui/button"
import ButtonEx from "../ext/button-ex"
import {
  Carousel,
  CarouselApi,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { toast } from "sonner"
import { dialog, LoggerEx } from "@/lib/logger"
import { CheckIcon, CircleAlertIcon, CircleXIcon, InfoIcon, LinkIcon, LucideProps, RectangleEllipsisIcon, RotateCwIcon, SearchIcon, XIcon } from "lucide-react"
import Spinner from "../misc/spinner"
import ILinkableEntity from "@/app/model/ILinkableEntity"
import { cn, formatDateTime, getReadQuery, getRecordLabel, getRecordLinkProperties, getRecordLinks, isEqual, RecordLink, TO_ENTITY_ID } from "@/lib/utils"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import Search from "../filter/search"
import InputEx from "../ext/input-ex"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import SelectTriggerEx from "../ext/select-ex"
import { LinkableEntityQueryFilter, SeverityKind } from "@/app/model/schema"
import { Checkbox } from "@/components/ui/checkbox"
import LabelEx from "../ext/label-ex"
import {
  CREATE_ENTITY_LINK,
  DELETE_ENTITY_LINK,
  READ_ENTITY_AUDIT,
  READ_ENTITY_LINKS,
  UPDATE_ENTITY_LINK,
  UPDATE_ENTITY_STATUS
} from "@/lib/graphql-queries"
import { useMutation, useQuery } from "@apollo/client/react"
import { introspect, QueryResult } from "@/lib/graphql-utils"
import { OperationTypeNode } from "graphql"
import IPage from "@/app/model/IPage"
import EntityLink from "@/app/model/EntityLink"
import EntityAudit from "@/app/model/EntityAudit"
import useAuth from "@/hooks/use-auth"
import ITrackedEntity from "@/app/model/ITrackedEntity"
import { GlobalContext } from "@/lib/context"

const logger = new LoggerEx(dialog, "[StatusDialog] ")

const SeverityIcons: { [K in SeverityKind]: ForwardRefExoticComponent<Omit<LucideProps, "ref"> & RefAttributes<SVGSVGElement>> } = {
  ERROR: CircleXIcon, //
  WARNING: CircleAlertIcon, //
  INFO: InfoIcon, //
}

const SeverityColours: { [K in SeverityKind]: string } = {
  ERROR: "text-red-600", //
  WARNING: "text-orange-600", //
  INFO: "text-blue-600", //
}

function isTextEditing(el: Element | null) {
  const e = el as HTMLElement | null;
  if (!e) return false;
  return (
    e.tagName === "INPUT" ||
    e.tagName === "TEXTAREA" ||
    e.isContentEditable ||
    e.getAttribute("role") === "textbox"
  )
}

const EMPTY_DATA = {content: []}
const EMPTY_RESULT = {data: EMPTY_DATA, previousData: EMPTY_DATA, loading: false, refetch: () => {}}

export default function StatusDialog({recordKind, record} : {recordKind?: LinkableEntityKind, record?: ILinkableEntity}) {
  const {hasAuthority} = useAuth()
  const {statusDialogOpen, setStatusDialogOpen, statusDialogItem, setStatusDialogItem} = useContext(GlobalContext)
  const [error, setError] = useState("")
  const [api, setApi] = useState<CarouselApi>()
  const [otherRecordKind, setOtherRecordKind] = useState<LinkableEntityKind>()
  const [otherRecordId, setOtherRecordId] = useState('')
  const recordLabel = useMemo(() => getRecordLabel(recordKind, record), [recordKind, record])
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
  const filteredRecordLinks = useMemo(() => {
    return recordLinks.filter(link => link.otherRecordKind === otherRecordKind)
  }, [recordLinks, otherRecordKind])
  const [filter, setFilter] = useState<LinkableEntityQueryFilter>({})
  const [filterStatus, setFilterStatus] = useState(filter.status?.[0] ?? '')
  const [filterText, setFilterText] = useState('')
  const [filterAdvanced, setFilterAdvanced] = useState<boolean | "indeterminate">(false)
  const [filterRecordId, setFilterRecordId] = useState('')
  const [selectedLinkId, setSelectedLinkId] = useState('')
  const [mode, setMode] = useState("view")
  const [thisRecordLocations, setThisRecordLocations] = useState<string>('')
  const [otherRecordLocations, setOtherRecordLocations] = useState<string>('')
  const [topicId, setTopicId] = useState<string>('')
  const auditResult = useQuery(READ_ENTITY_AUDIT, {variables: {id: record?.id ?? "0"}})
  const otherRecordsQuery = (otherRecordKind && getReadQuery(otherRecordKind)) ?? READ_ENTITY_LINKS // A dummy query.
  const otherRecordsResult = useQuery(otherRecordsQuery, {variables: {filter}, skip: !otherRecordKind})
  const [otherRecordsFieldName] = useMemo(() => otherRecordsQuery ? introspect(otherRecordsQuery, OperationTypeNode.QUERY) : '', [otherRecordsQuery])
  const [createLinkOp, createLinkResult] = useMutation(CREATE_ENTITY_LINK, {refetchQueries: [READ_ENTITY_AUDIT/*otherRecordsQuery*/]})
  const [updateLinkOp, updateLinkResult] = useMutation(UPDATE_ENTITY_LINK, {refetchQueries: [/*otherRecordsQuery*/]})
  const [deleteLinkOp, deleteLinkResult] = useMutation(DELETE_ENTITY_LINK, {refetchQueries: [/*otherRecordsQuery*/]})
  const [updateStatusOp, updateStatusResult] = useMutation(UPDATE_ENTITY_STATUS, {refetchQueries: [/*otherRecordsQuery*/]})
  const allowLinking = !!record && hasAuthority("LNK")
  const thisLocationsRef = useRef<HTMLInputElement>(null)

  const loading = auditResult.loading || otherRecordsResult?.loading || createLinkResult.loading ||
    updateLinkResult.loading || deleteLinkResult.loading || updateStatusResult.loading

  const entityAudit = useMemo(() => {
    const data = (auditResult.loading
      ? auditResult.previousData
      : auditResult.data) as QueryResult<EntityAudit>
    return data?.audit ?? {pass:true}
  }, [auditResult])
  const fieldAudit = useMemo(() => {
    return entityAudit.fieldAudit ?? {fields: [], groups: [], pass: true}
  }, [entityAudit])
  const linkAudit = useMemo(() => {
    return entityAudit.linkAudit ?? {links: [], groups: [], pass: true}
  }, [entityAudit])

  const otherRecords = useMemo(() => {
    const data = (otherRecordsResult?.loading
      ? otherRecordsResult.previousData
      : otherRecordsResult.data) as QueryResult<IPage<ILinkableEntity>>
    let otherRecordsPage = data && otherRecordsFieldName
      ? data[otherRecordsFieldName]
      : undefined
    return otherRecordsPage?.content.filter(
      r => filteredRecordLinks.findIndex(
        l => l.otherRecordId == r.id) == -1) ?? []
  }, [otherRecordsResult, otherRecordsFieldName, filteredRecordLinks])
  const otherRecord = useMemo(() => otherRecords.find(r => r.id === otherRecordId), [otherRecords, otherRecordId])
  const otherRecordLabel = useMemo(() => getRecordLabel(otherRecordKind, otherRecord) ?? '', [otherRecordKind, otherRecord])

  // Ghastly AI hack to prevent Carousel from scrolling when pressing LeftArrow or RightArrow inside a TextInput.
  useEffect(() => {
    const handler = (ev: KeyboardEvent) => {
      if (ev.key !== "ArrowLeft" && ev.key !== "ArrowRight")
        return

      const active = document.activeElement
      if (isTextEditing(active)) {
        // Let caret move, but stop carousel
        ev.stopPropagation()
        ev.stopImmediatePropagation?.()
      }
      // Otherwise, let carousel handle left/right
    }

    document.addEventListener("keydown", handler, { capture: true })
    return () => document.removeEventListener("keydown", handler, { capture: true })
  }, [])

  // Bidirectionally sync carousel scroll state with item state
  useEffect(() => {
    if (!api)
      return
    const onSelect = () => setStatusDialogItem(api.selectedScrollSnap())
    api.on("select", onSelect)
    return () => {
      api.off("select", onSelect)
    }
  }, [api])
  useEffect(() => {
    api?.scrollTo(statusDialogItem)
  }, [api, statusDialogItem])

  const handleNew = useCallback((otherRecordKind: LinkableEntityKind): void => {
    logger.trace("handleNew: otherRecordKind='%s'", otherRecordKind)
    setOtherRecordKind(otherRecordKind)
    setOtherRecordId('')
    setSelectedLinkId('')
    api?.scrollTo(2)
    toast.info(`Use the search to find a link target ${otherRecordKind}`)
  }, [api])

  const updateFilter = useCallback((status: string, text: string, advanced: boolean | "indeterminate", recordId: string) => {
    logger.trace("updateFilter: status='%s', text='%s', advanced=%s, recordId='%s'", status, text, advanced, recordId)
    const newFilter = {
      status: status ? [status] : undefined,
      text: text || undefined,
      advancedSearch: text && advanced || undefined,
      recordId: recordId || undefined,
    } as LinkableEntityQueryFilter
    if (!isEqual(newFilter as LinkableEntityQueryFilter, filter)) {
      logger.trace("updateFilter from %o to %o", filter, newFilter)
      setFilter(newFilter)
    }
  }, [filter, setFilter])

  const getSelectedLink = useCallback((linkId?: string) : RecordLink | undefined => {
    linkId ??= selectedLinkId
    if (!linkId)
      return undefined;

    return filteredRecordLinks.find(link => link.id == linkId)
  }, [selectedLinkId, filteredRecordLinks])
  const selectedLink = getSelectedLink(selectedLinkId)

  const isModified = useCallback(() => {
    // NOTE: when editing a new record, selectedLink is undefined.
    return thisRecordLocations != (selectedLink?.thisLocations ?? '') ||
      otherRecordLocations != (selectedLink?.otherLocations ?? '')/* ||
      selectedLink?.otherRecordKind == "Topic" && topicId != selectedLink?.otherRecordId*/
  }, [thisRecordLocations, otherRecordLocations, /*topicId, */selectedLink])

  const createInput = useCallback((recordLink: Partial<RecordLink>) => {
    logger.trace("createInput: recordLink=%o", recordLink)
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
    logger.trace("refreshEditableFields: linkId='%s'", linkId)
    const selectedLink = getSelectedLink(linkId)
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
    setTopicId(selectedLink?.otherRecordKind == "Topic" ? selectedLink?.otherRecordId : '')
  }, [getSelectedLink])

  const handleOtherRecordKindChange = useCallback((otherRecordKind: LinkableEntityKind) => {
    logger.trace("handleOtherRecordKindChange: otherRecordKind='%s'", otherRecordKind)
    setOtherRecordKind(otherRecordKind)
    setOtherRecordId('')
    setSelectedLinkId('')
  }, [])

  const handleStatusChange = useCallback((status: string) => {
    logger.trace("handleStatusChange: status='%s'", status)
    status = status === "ALL" ? '' : status
    setFilterStatus(status)
    updateFilter(status, filterText, filterAdvanced, otherRecordId)
  }, [updateFilter, filterText, filterAdvanced, otherRecordId])

  const handleTextChange = useCallback((text: string) => {
    logger.trace("handleTextChange: text='%s'", text)
    setFilterText(text)
    updateFilter(filterStatus, text, filterAdvanced, otherRecordId)
  }, [updateFilter, filterStatus, filterAdvanced, otherRecordId])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    logger.trace("handleAdvancedSearchChange: advanced=%s", advanced)
    setFilterAdvanced(advanced)
    updateFilter(filterStatus, filterText, advanced, otherRecordId)
  }, [updateFilter, filterStatus, filterText, otherRecordId])

  const handleRecordIdChange = useCallback((recordId: string) => {
    logger.trace("handleRecordIdChange: recordId='%s'", recordId)
    setFilterRecordId(recordId)
    updateFilter(filterStatus, filterText, filterAdvanced, recordId)
  }, [updateFilter, filterStatus, filterText, filterAdvanced])

  const handleReset = useCallback(() => {
    logger.trace("handleReset")
    setFilterStatus('')
    setFilterText('')
    setFilterAdvanced(false)
    setFilterRecordId('')
    updateFilter('', '', false, '')
  }, [updateFilter])

  const handleLink = useCallback(() => {
    logger.trace("handleLink")
    if (mode === "view") {
      toast.info(`Linking '${otherRecordLabel}'. Enter link locations then Save.`)
      setSelectedLinkId('')
      setThisRecordLocations('')
      setOtherRecordLocations('')
      setMode("create")
      requestAnimationFrame(() => requestAnimationFrame(() => thisLocationsRef.current?.focus()))
    }
  }, [mode, otherRecordLabel])

  const handleEdit = useCallback(() => {
    logger.trace("handleEdit")
    if (mode === "view") {
      toast.info("Edit link locations then Save.")
      setMode("edit")
      requestAnimationFrame(() => requestAnimationFrame(() => thisLocationsRef.current?.focus()))
    }
  }, [])

  const handleSave = useCallback(() => {
    if (mode === "create") {
      const thisRecordId = record?.id
      if (recordKind && otherRecordKind && thisRecordId && otherRecordId) {
        const [,, thisRecordIdProperty] = getRecordLinkProperties(recordKind, otherRecordKind)
        if (thisRecordIdProperty) {
          const thisRecordIsToEntity = thisRecordIdProperty === TO_ENTITY_ID
          createLinkOp({
            variables: {
              input: createInput({
                id: "0",
                thisRecordId,
                thisLocations: thisRecordLocations,
                otherRecordKind: otherRecordKind as LinkableEntityKind,
                otherRecordId,
                otherLocations: otherRecordLocations,
                otherRecordLabel,
                thisRecordIsToEntity,
              }
            )},
            onCompleted: (data) => {
              const entityLink = (data as QueryResult<EntityLink>).createEntityLink
              logger.trace("handleSave(create).completed: new EntityLink: %o", entityLink)
              setMode("view")
              setOtherRecordId('')
              setSelectedLinkId(entityLink.id ?? '')
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
            logger.trace("handleSave(edit).completed")
            setMode("view")
          },
          onError: (error/*, clientOptions*/) => {
            toast.error(error.message)
          },
        })
    }
  }, [
    mode,
    record,
    recordKind,
    otherRecordKind,
    otherRecordId,
    otherRecordLabel,
    otherRecordLocations,
    thisRecordLocations,
    selectedLink,
    createLinkOp,
    createInput,
    updateLinkOp,
  ])

  const handleCancel = useCallback(() => {
    if (mode === "create" || mode === "edit") {
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
    }
  }, [mode, selectedLinkId, selectedLink, isModified, refreshEditableFields])

  const handleRelink = useCallback(() => {
    if (confirm(`Change target of link from ${selectedLink?.otherRecordLabel} to ${otherRecordLabel}?${otherRecordLocations ? "\n\nN.B. The 'Location(s) in other record' value will be retained but may not be appropriate to the new target record. Change it if necessary." : ""}`)) {
      toast.info(`Relinking to ${otherRecordLabel}`)
      const newTargetLabel = otherRecordLabel
      updateLinkOp({
        variables: {
          input: {
            id: selectedLink?.id,
            // rating: selectedLink?.rating,
            fromEntityId: selectedLink?.thisRecordIsToEntity ? otherRecordId : record?.id,
            fromEntityLocations: selectedLink?.thisRecordIsToEntity ? thisRecordLocations : otherRecordLocations,
            toEntityId: selectedLink?.thisRecordIsToEntity ? record?.id : otherRecordId,
            toEntityLocations: selectedLink?.thisRecordIsToEntity ? otherRecordLocations : thisRecordLocations,
          }
        },
        onCompleted: (data) => {
          setOtherRecordId('')
          toast.info(`Changed link target to ${newTargetLabel}`)
        },
        onError: (error) => {
          toast.error(error.message)
        }
      })
    }
  }, [selectedLink, record, otherRecordId, otherRecordLabel, thisRecordLocations, otherRecordLocations, updateLinkOp])

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
      if (record?.id !== prevRecord.current?.id)
        handleSelectedLinkChange('')
      prevRecord.current = record
    }
  }, [handleSelectedLinkChange, record])

  const handleClose = useCallback(() => {
    setStatusDialogOpen(false)
  }, [])

  const handlePublish = useCallback(() => {
    toast.info(`Publishing ${recordLabel}...`)
    updateStatusOp({
      variables: {
        entityId: record?.id,
        status: "PUB"
      },
      onCompleted: (data) => {
        const entity = (data as QueryResult<ITrackedEntity>).setEntityStatus
        logger.trace("handlePublish().completed: ITrackedEntity: %o", entity)
        // setMode("view")
        // setOtherRecordId('')
        // setSelectedLinkId(entity.id ?? '')
        toast.info(`Published ${recordLabel}`)
      },
      onError: (error) => {
        toast.error(error.message)
      },
    })
  }, [recordLabel])

  logger.debug(`item: ${statusDialogItem}, canScrollPrev: ${api?.canScrollPrev()}, canScrollNext: ${api?.canScrollNext()} `)

  return (
    <Dialog open={statusDialogOpen} onOpenChange={setStatusDialogOpen}>
      <DialogTrigger asChild>
        <ButtonEx
          outerClassName={cn("place-self-center", "")}
          className="w-35 bg-blue-500 text-md"
          type="button"
          variant="default"
          disabled={!record}
          onClick={() => setStatusDialogItem(2)}
          help={`Manage links for ${recordLabel}`}>
          Links...
        </ButtonEx>
      </DialogTrigger>
      <DialogContent className="flex flex-col items-center w-5/6 min-w-0 max-w-none! h-5/6 min-h-0 max-h-none! overflow-hidden">
        <DialogHeader>
          <DialogTitle className="text-center">{
            recordKind
            ? (() => {
              const Icon = RecordIcons[recordKind]
              return <Icon className="inline" />
            })()
            : null
          }&nbsp;{recordKind} Status</DialogTitle>
          <DialogDescription>
            Manage status for {recordLabel}
          </DialogDescription>
          <fieldset className="grow-0 w-90 self-center p-2 border rounded-md">
            <legend>Page</legend>
            <RadioGroup
              className="flex justify-center"
              value={statusDialogItem.toString()}
              onValueChange={(value) => setStatusDialogItem(Number.parseInt(value))}>
              <RadioGroupItem id="field-audit" value="0" />
              <Label htmlFor="field-audit">Field Audit</Label>
              <RadioGroupItem id="link-audit" value="1" />
              <Label htmlFor="link-audit">Link Audit</Label>
              <RadioGroupItem id="link-manager" value="2" />
              <Label htmlFor="link-manager">Link Manager</Label>
            </RadioGroup>
          </fieldset>
          <p className="text-red-600">{error}</p>
        </DialogHeader>
        <Spinner className="absolute inset-0 bg-black/20 z-50" loading={loading} />
        <Carousel
          setApi={setApi} 
          className="flex flex-col w-7/8 h-full min-h-0 [&>div[data-slot=carousel-content]]:grow"
        >
          <CarouselContent className="w-full h-full max-h-full">
            <CarouselItem id="field-audit" className="w-full h-full max-h-full overflow-auto">
                <h2><RectangleEllipsisIcon className="inline"/><SearchIcon className="inline"/>&nbsp;Field Audit</h2>
                <Table className="table-fixed w-full max-h-full border caption-top">
                  <colgroup>
                    <col className="w-[15%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[55%]" />
                    <col className="w-[10%]" />
                  </colgroup>
                  <TableCaption>Field Rules</TableCaption>
                  <TableHeader className="sticky top-0 z-10 bg-gray-50 border">
                    <TableRow className="border">
                      <TableHead className="border">Field name</TableHead>
                      <TableHead className="border">Rule</TableHead>
                      <TableHead className="border">Severity</TableHead>
                      <TableHead className="border">Message</TableHead>
                      <TableHead className="text-center border">Check</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody className="max-h-full border">
                    {
                      fieldAudit.fields.map((fieldAuditEntry, idx) => {
                        return (
                          <TableRow key={`field-audit-${idx}`} className="border">
                            <TableCell className="border">{fieldAuditEntry.fieldName}</TableCell>
                            <TableCell className="border">{fieldAuditEntry.severity == "ERROR" ? "required" : "suggested"}</TableCell>
                            <TableCell className="border">
                              {(() => {
                                const Icon = SeverityIcons[fieldAuditEntry.severity]
                                return <Icon className={cn("inline", !fieldAuditEntry.pass ? SeverityColours[fieldAuditEntry.severity] : "")} />
                              })()}
                              &nbsp;{fieldAuditEntry.severity.toLowerCase()}
                            </TableCell>
                            <TableCell className="border">{fieldAuditEntry.message}</TableCell>
                            <TableCell className="text-center border">
                              {
                                fieldAuditEntry.pass
                                ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                                : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                              }
                            </TableCell>
                          </TableRow>
                        )
                      })
                    }
                    {
                      fieldAudit.groups.map((fieldGroupAuditEntry, idx1) => {
                        return fieldGroupAuditEntry.fields.map((fieldAuditEntry, idx2) => {
                          return (
                            <TableRow key={`field-group-audit-${idx1}-${idx2}`} className="border">
                              <TableCell className="border">{fieldAuditEntry.fieldName}</TableCell>
                              {
                                idx2 == 0
                                ? <TableCell rowSpan={fieldGroupAuditEntry.fields.length}>any of</TableCell>
                                : null
                              }
                              <TableCell className="border">
                                {(() => {
                                  const Icon = SeverityIcons[fieldAuditEntry.severity]
                                  return <Icon className={cn("inline", !fieldAuditEntry.pass ? SeverityColours[fieldAuditEntry.severity] : "")} />
                                })()}
                                &nbsp;{fieldAuditEntry.severity.toLowerCase()}
                              </TableCell>
                              <TableCell className="border">{fieldAuditEntry.message}</TableCell>
                              <TableCell className="text-center border">
                                {
                                  fieldAuditEntry.pass
                                  ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                                  : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                                }
                              </TableCell>
                            </TableRow>
                          )
                        })
                      })
                    }
                  </TableBody>
                  <TableFooter className="sticky bottom-0 z-10">
                    <TableRow>
                      <TableCell colSpan={4} className="text-right border">Overall:</TableCell>
                      <TableCell className="text-center border">
                        <span className={cn(fieldAudit.pass ? "text-black" : "text-red-600")}>
                          {
                            fieldAudit.pass
                            ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                            : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                          }
                        </span>
                      </TableCell>
                      <TableCell></TableCell>
                    </TableRow>
                  </TableFooter>
                </Table>
            </CarouselItem>
            <CarouselItem id="link-audit" className="h-full max-h-full overflow-auto">
              <form>
                <h2><LinkIcon className="inline"/><SearchIcon className="inline"/>&nbsp;Link Audit</h2>
                <Table className="table-fixed w-2/3 max-h-full border caption-top">
                  <colgroup>
                    <col className="w-[20%]" />
                    <col className="w-[15%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                  </colgroup>
                  <TableCaption>Links by linked record kind</TableCaption>
                  <TableHeader className="sticky top-0 z-10 bg-gray-50 border">
                    <TableRow className="border">
                      <TableHead className="border">Linked Record Kind</TableHead>
                      <TableHead className="border">Rule</TableHead>
                      <TableHead className="text-right border">Minimum</TableHead>
                      <TableHead className="text-right border">Actual</TableHead>
                      <TableHead className="text-center border">Check</TableHead>
                      <TableHead className="text-center border">Action</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody className="max-h-full border">
                    {
                      linkAudit.links.map((linkAuditEntry, idx) => {
                        return (
                          <TableRow key={`link-audit-${idx}`}>
                            <TableCell className="border">
                              {(() => {
                                const Icon = RecordIcons[linkAuditEntry.linkedEntityKind]
                                return <Icon className="inline" />
                              })()}
                              &nbsp;{linkAuditEntry.linkedEntityKind}
                            </TableCell>
                            <TableCell className="border">required</TableCell>
                            <TableCell className="text-right border">{linkAuditEntry.min}</TableCell>
                            <TableCell className="text-right border">{linkAuditEntry.actual ?? 0}</TableCell>
                            <TableCell className="text-center border">
                              {
                                linkAuditEntry.pass
                                ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                                : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                              }
                            </TableCell>
                            <TableCell className="text-center border">
                              <Button
                                className="cursor-pointer"
                                type="button"
                                variant="outline"
                                onClick={() => handleNew(linkAuditEntry.linkedEntityKind)}
                              >
                                New...
                              </Button>
                            </TableCell>
                          </TableRow>
                        )
                      })
                    }
                    {
                      linkAudit.groups.map((linkGroupAuditEntry, idx1) => {
                        return linkGroupAuditEntry.links.map((linkAuditEntry, idx2) => {
                          return (
                            <TableRow key={`link-group-audit-${idx1}-${idx2}`}>
                              <TableCell className="border">
                                {(() => {
                                  const Icon = RecordIcons[linkAuditEntry.linkedEntityKind];
                                  return <Icon className="inline" />;
                                })()}
                                &nbsp;{linkAuditEntry.linkedEntityKind}
                              </TableCell>
                              {
                                idx2 == 0
                                ? <TableCell rowSpan={linkGroupAuditEntry.links.length} className="border">any of</TableCell>
                                : null
                              }
                              <TableCell className="text-right border">{linkAuditEntry.min}</TableCell>
                              <TableCell className="text-right border">{linkAuditEntry.actual ?? 0}</TableCell>
                              <TableCell className="text-center border">
                                {
                                  linkAuditEntry.pass
                                  ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                                  : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                                }
                              </TableCell>
                              <TableCell className="text-center border">
                                <Button
                                  className="cursor-pointer"
                                  type="button"
                                  variant="outline"
                                  onClick={() => handleNew(linkAuditEntry.linkedEntityKind)}
                                >
                                  New...
                                </Button>
                              </TableCell>
                            </TableRow>
                          )
                        })
                      })
                    }
                  </TableBody>
                  <TableFooter className="sticky bottom-0 z-10">
                    <TableRow>
                      <TableCell colSpan={4} className="text-right border">Overall:</TableCell>
                      <TableCell className="text-center border">
                        <span className={cn(linkAudit.pass ? "text-black" : "text-red-600")}>
                          {
                            linkAudit.pass
                            ? <><CheckIcon className="inline text-green-600" />&nbsp;Pass</>
                            : <><XIcon className="inline text-red-600" />&nbsp;Fail</>
                          }
                        </span>
                      </TableCell>
                      <TableCell></TableCell>
                    </TableRow>
                  </TableFooter>
                </Table>
                <br />
              </form>
            </CarouselItem>
            <CarouselItem id="record-links" className="w-full h-full max-h-full overflow-auto">
              <h2><LinkIcon className="inline"/>&nbsp;Link Manager</h2><br/>
              <form>
                <div className="flex">
                  <fieldset className="border rounded-md ml-1 p-2 gap-2">
                    <legend>{`Link ${recordKind} with:`}</legend>
                    <RadioGroup
                      value={otherRecordKind}
                      onValueChange={value => handleOtherRecordKindChange(value as LinkableEntityKind)}
                    >
                      {
                        LinkableEntityKindKeys.map(key => (
                          <div id={key} key={key} className="flex items-center gap-3">
                            <RadioGroupItem
                              value={key}
                              disabled={key === recordKind}
                            />
                            {
                              (() => {
                                const Icon = RecordIcons[key]
                                return <Icon className="size-4 inline" />
                              })()
                            }
                            <Label
                              className={cn(key === recordKind ? "text-gray-400" : "text-black")}
                              htmlFor={key}
                            >
                              {key}
                            </Label>
                          </div>
                        ), [recordKind, otherRecordKind])
                      }
                    </RadioGroup>
                  </fieldset>
                  <div className="flex flex-col">
                    <fieldset className="grow border rounded-md ml-1 p-2 gap-2">
                      <legend>{`Search for ${otherRecordKind ?? "record"}s to link`}</legend>
                      <div className="flex items-center gap-2 mb-2">
                        <Select
                          value={filterStatus ?? ''}
                          onValueChange={handleStatusChange}
                        >
                          <SelectTriggerEx
                            id="status"
                            help="Filter the list to show only records with this status"
                            title="Filter the list to show only records with this status"
                          >
                            <SelectValue placeholder="Status" />
                          </SelectTriggerEx>
                          <SelectContent>
                            {
                              filterStatus
                              ? <SelectItem value="ALL">-Clear-</SelectItem>
                              : null
                            }
                            <SelectItem value="DRA">Draft</SelectItem>
                            <SelectItem value="PUB">Published</SelectItem>
                            <SelectItem value="SUS">Suspended</SelectItem>
                            <SelectItem value="DEL">Deleted</SelectItem>
                          </SelectContent>
                        </Select>
                        <Search id="searchText" value={filterText} onChangeValue={handleTextChange} />
                        <Checkbox
                          id="advanced"
                          checked={filterAdvanced}
                          onCheckedChange={handleAdvancedSearchChange}
                          title="Use advanced text search syntax. See info hover tip to the right."
                        />
                        <LabelEx
                          htmlFor="advanced"
                          help="Use advanced text search syntax. See MariaDB documentation at https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode">Advanced</LabelEx>
                        <InputEx
                          id="recordId"
                          outerClassName="w-30"
                          className="text-right"
                          placeholder="Record ID"
                          value={filterRecordId}
                          onChange={e => handleRecordIdChange(e.target.value)}
                          delay={500}
                          clearOnEscape={true}
                          help="Filter the list to show only the record with the specified ID. Other filters are retained but ignored."
                        />
                        <ButtonEx
                          id="refresh"
                          variant="outline"
                          help="Refresh the list using the same filter and pagination settings."
                          onClick={() => otherRecordsResult.refetch()}
                        >
                          <RotateCwIcon />
                        </ButtonEx>
                        <ButtonEx
                          id="reset"
                          type="button"
                          variant="outline"
                          disabled={!filterStatus && !filterText && !filterAdvanced && !filterRecordId}
                          className="w-20"
                          onClick={handleReset}
                          help="Clear all filters"
                        >
                          Reset
                        </ButtonEx>
                      </div>
                      <div className="flex items-center gap-2 mb-2">
                        <Select
                          disabled={!record || mode !== "view"}
                          value={otherRecordId ?? ''}
                          onValueChange={recordId => setOtherRecordId(recordId === "CLEAR" ? '' : recordId)}
                        >
                          <SelectTriggerEx
                            id="other-record"
                            outerClassName="grow"
                            className="w-full"
                            help="A list of matching records"
                          >
                            <SelectValue placeholder={
                              otherRecords.length ?? 0 > 0
                              ? "-Select a record to link-"
                              : "-No matching records-"}
                            />
                          </SelectTriggerEx>
                          <SelectContent>
                            {
                              otherRecordId
                              ? <SelectItem key="0" value="CLEAR">-Clear-</SelectItem>
                              : null
                            }
                            {
                              otherRecords.map(otherRecord => {
                                return otherRecord
                                  ? <SelectItem key={otherRecord.id} value={otherRecord.id ?? "0"}>{getRecordLabel(otherRecordKind, otherRecord)}</SelectItem>
                                  : null
                              })
                            }
                          </SelectContent>
                        </Select>
                        <ButtonEx
                          type="button"
                          variant="outline"
                          className="w-20"
                          disabled={!allowLinking || !otherRecordId || mode !== "view"}
                          onClick={handleLink}
                          help={
                            otherRecordId
                            ? `Create a new link between ${recordLabel} and ${otherRecordLabel}`
                            : "No record selected"
                          }
                        >
                          Link
                        </ButtonEx>
                      </div>
                    </fieldset>
                    <fieldset className="flex border rounded-md ml-1 p-2 gap-2">
                      <legend>{`Existing ${otherRecordKind} links`}</legend>
                      <Select
                        disabled={!record || mode !== "view"}
                        value={selectedLinkId ?? ''}
                        onValueChange={handleSelectedLinkChange}
                      >
                        <SelectTriggerEx
                          id="links"
                          outerClassName="grow"
                          className="w-full"
                          help="A list of all the records which are linked with the selected record. Select one to see/edit its settings."
                        >
                          <SelectValue
                            placeholder=
                              {
                                filteredRecordLinks.length ?? 0 > 0
                                ? "-Select a record link-"
                                : "-No record links-"
                              }
                          />
                        </SelectTriggerEx>
                        <SelectContent>
                          {
                            selectedLinkId
                            ? <SelectItem key="0" value="CLEAR">-Clear-</SelectItem>
                            : null
                          }
                          {
                            filteredRecordLinks.map(link => {
                              return link
                                ? <SelectItem key={link.id} value={link.id ?? "0"}>{link.otherRecordLabel}</SelectItem>
                                : null
                            })
                          }
                        </SelectContent>
                      </Select>
                      <ButtonEx
                        type="button"
                        variant="outline"
                        outerClassName="justify-center"
                        className="w-20"
                        disabled={!allowLinking || (mode == "view" && !selectedLinkId) || (mode == "edit" && !isModified())}
                        onClick={handleEdit}
                        help={
                          selectedLinkId
                          ? "Edit the selected link"
                          : "No record link selected"
                        }
                      >
                        Edit
                      </ButtonEx>
                    </fieldset>
                  </div>
                </div>
                <fieldset className="grid grid-cols-5 border rounded-md ml-1 p-2 gap-2">
                  <legend>Link Details</legend>
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
                  <ButtonEx
                    type="button"
                    variant="outline"
                    outerClassName="w-full justify-end"
                    className="w-20 place-self-center"
                    disabled={!allowLinking || mode == "view" || (mode == "edit" && !isModified())}
                    onClick={handleSave}
                    help={
                      selectedLink
                        ? mode === "create"
                        ? "Save changes to new record link"
                        : `Save changes to link with record '${selectedLink?.otherRecordLabel}'`
                        : "No record link selected"
                    }
                  >
                    Save
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
                    type="button"
                    variant="outline"
                    outerClassName="w-full justify-end"
                    className="w-20 place-self-center"
                    disabled={!allowLinking || mode === "view"}
                    onClick={handleCancel}
                    help={
                      selectedLink
                      ? mode === "create"
                      ? "Discard changes to new record link"
                      : `Discard changes to link with record '${selectedLink?.otherRecordLabel}'`
                      : "No record link selected"
                    }
                  >
                    Cancel
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
                    type="button"
                    variant="outline"
                    outerClassName="justify-end w-full"
                    className="w-20 place-self-center"
                    disabled={!allowLinking || !selectedLinkId || !otherRecordId || mode !== "view"}
                    onClick={handleRelink}
                    help={
                      otherRecord
                        ? `Change link target to '${otherRecordLabel}'.`
                        : "No target record selected"
                    }
                  >
                    Relink
                  </ButtonEx>
                  <Label htmlFor="link-this-locations" className="col-start-1">Location(s) in this record:</Label>
                  <InputEx
                    id="link-this-locations"
                    ref={thisLocationsRef}
                    type="text"
                    outerClassName="col-span-3"
                    readOnly={!allowLinking || mode === "view"}
                    disabled={!record}
                    value={thisRecordLocations}
                    onChange={e => setThisRecordLocations(e.target.value)}
                    help="Location(s) within this record that pertain to the record link"
                  />
                  <ButtonEx
                    type="button"
                    variant="outline"
                    outerClassName="justify-end w-full"
                    className="w-20 place-self-center"
                    disabled={!allowLinking || !selectedLinkId || selectedLink?.status === "Deleted" || mode !== "view"}
                    onClick={handleUnlink}
                    help={
                      selectedLink
                        ? `Delete link with record '${selectedLink?.otherRecordLabel}'.`
                        : "No record link selected"
                    }
                  >
                    Unlink
                  </ButtonEx>
                  <Label htmlFor="link-other-locations" className="col-start-1">Location(s) in other record:</Label>
                  <InputEx
                    id="link-other-locations"
                    type="text"
                    outerClassName="col-span-3"
                    readOnly={!allowLinking || mode === "view"}
                    disabled={!record || selectedLink?.otherRecordKind == "Topic"}
                    value={otherRecordLocations}
                    onChange={e => setOtherRecordLocations(e.target.value)}
                    help="Location(s) within the other record that pertain to the record link"
                  />
                </fieldset>
              </form>
            </CarouselItem>
          </CarouselContent>
          <CarouselPrevious />
          <CarouselNext />
        </Carousel>
        <DialogFooter>
          <div className="flex flex-col gap-2">
            {
              record
              ? <p className="text-center">
                {
                  entityAudit.pass
                  ? <><CheckIcon className="inline text-green-600" />&nbsp;</>
                  : <><XIcon className="inline text-red-600" />&nbsp;</>
                }
                {
                record.status === "Published"
                ? entityAudit.pass
                  ? `${recordKind} #${record.id} is already published and still meets the minumum criteria for publication.`
                  : <>{`${recordKind} #${record.id} is already published but no longer meets the minumum criteria for publication.`}<br/>Please review the field/link audit pages and correct any failures.</>
                : entityAudit.pass
                  ? <>{`${recordKind} #${record.id} meets the minumum criteria for publication.`}<br/>You may publish it at will; any unpublished outbound links will also be published.</>
                  : <>{`${recordKind} #${record.id} does not yet meet the minumum criteria for publication.`}<br/>Please review the field/link audit pages and correct any failures.</>
                }
              </p>
              : null
            }
            <div className="flex justify-center gap-2">
              <DialogClose asChild>
                <Button
                  type="button"
                  variant="outline"
                  className="w-20"
                  title="Close this dialog"
                  onClick={handleClose}
                >
                  Close
                </Button>
              </DialogClose>
                <Button
                  type="button"
                  variant="outline"
                  disabled={!hasAuthority("UPD") || record?.status === "Published" || !entityAudit.pass}
                  title="Publish the selected record"
                  onClick={handlePublish}
                >
                  Publish
                </Button>
            </div>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}