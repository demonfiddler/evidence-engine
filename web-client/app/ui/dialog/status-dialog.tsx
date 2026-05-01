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

import { LinkableEntityKind, LinkableEntityKindKeys, RecordIcons } from "@/app/model/RecordKinds"
import { ForwardRefExoticComponent, RefAttributes, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { Button } from "@/components/ui/button"
import ButtonEx from "../ext/button-ex"
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
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger
} from "@/components/ui/tabs"
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
import {
  CheckIcon,
  CircleAlertIcon,
  CircleXIcon,
  InfoIcon,
  LinkIcon,
  LucideProps,
  RectangleEllipsisIcon,
  RotateCwIcon,
  SearchIcon,
  XIcon
} from "lucide-react"
import Spinner from "../misc/spinner"
import ILinkableEntity from "@/app/model/ILinkableEntity"
import {
  cn,
  firstToUpper,
  flatten,
  formatDateTime,
  getEntityKind,
  getReadQuery,
  getRecordLabel,
  getRecordLinkProperties,
  getRecordLinks,
  isEqual,
  RecordLink,
  TO_ENTITY_ID,
} from "@/lib/utils"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import InputEx from "../ext/input-ex"
import { Select, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import SelectTriggerEx from "../ext/select-ex"
import { LinkableEntityQueryFilter, PageableInput, SeverityKind } from "@/app/model/schema"
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
import { FIELD_AUDIT, GlobalContext, LINK_AUDIT, LINK_MANAGER, StatusDialogItemType } from "@/lib/context"
import Topic from "@/app/model/Topic"
import { Textarea } from "@/components/ui/textarea"
import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "@/components/ui/combobox"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import Link from "next/link"
import { Paginator } from "../filter/paginator"
import ContextHelp from "../misc/context-help"
import { InputGroupAddon } from "@/components/ui/input-group"
import { Badge } from "@/components/ui/badge"
import Help from "../misc/help"

const logger = new LoggerEx(dialog, "[StatusDialog] ")

const VIEW = "view"
const EDIT = "edit"
const CREATE = "create"

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

const FUZZY_SEARCH_SUPPORTED: { [K in LinkableEntityKind]?: LinkableEntityKind[] } = {
  Declaration: ["Person"], //
  Person: ["Declaration", "Publication", "Quotation"],
  Publication: ["Person"], //
  Quotation: ["Person", "Publication"], //
}

function isFuzzySearchSupported(contextualRecordKind?: LinkableEntityKind, otherRecordKind?: LinkableEntityKind) {
  return (contextualRecordKind && otherRecordKind && FUZZY_SEARCH_SUPPORTED[contextualRecordKind]?.includes(otherRecordKind)) ?? false
}

const FUZZY_SEARCH_FIELDS: { [K in LinkableEntityKind]?: string } = {
  Declaration: "signatories", //
  Person: "lastName", //
  Publication: "authors", //
  Quotation: "quotee", //
}

function getFuzzySearchField(contextualRecordKind?: LinkableEntityKind) {
  return contextualRecordKind ? FUZZY_SEARCH_FIELDS[contextualRecordKind] : undefined
}

function lineCount(s: string | undefined) {
  if (!s)
    return 0

  let lineCount = 1;
  let atLineEnd = false
  for (let i = 0, n = s.length; i < n; i++) {
    const c = s[i]
    if (c === '\r' || c === '\n') {
      if (!atLineEnd) {
        atLineEnd = true
        lineCount++
      }
    } else {
      atLineEnd = false
    }
  }
  return lineCount
}

const EMPTY_SET = new Set<string>()
const DEFAULT_PAGESORT = {pageNumber: 0, pageSize: 5};

/** Adds all ancestors of topicId to topicAxis. */
function addAncestors(topicAxis: Set<string>, topicTrees: Topic[], topicId: string): boolean {
  for (const topic of topicTrees) {
    if (!topic.id)
      continue
    if (topic.id === topicId ||
      topic.children && topic.children.length > 0 && addAncestors(topicAxis, topic.children, topicId)) {

      topicAxis.add(topic.id)
      return true
    }
  }
  return false
}

/** Adds all descendants of topicId to topicAxis. */
function addDescendants(topicAxis: Set<string>, topicTrees: Topic[], topicId: string, addAll: boolean) {
  for (const topic of topicTrees) {
    if (!topic.id)
      continue
    const add = addAll || topic.id === topicId
    if (add)
      topicAxis.add(topic.id)
    if (topic.children && topic.children.length > 0)
      addDescendants(topicAxis, topic.children, topicId, add)
  }
}

/** Returns a list containing all the topics referenced from filteredRecordLinks, their ancestors and descendants. */
function getTopicAxis(topicTree: Topic[], filteredRecordLinks: RecordLink[]): Set<string> {
  const topicAxis = new Set<string>()
  for (const frl of filteredRecordLinks) {
    const topicId = frl.otherRecordId
    addAncestors(topicAxis, topicTree, topicId)
    addDescendants(topicAxis, topicTree, topicId, false)
  }
  return topicAxis
}

export default function StatusDialog({ recordKind, record }: { recordKind?: LinkableEntityKind, record?: ILinkableEntity }) {
  const { hasAuthority } = useAuth()
  const { statusDialogOpen, setStatusDialogOpen, statusDialogItem, setStatusDialogItem } = useContext(GlobalContext)
  const [error, setError] = useState("")
  const [otherRecordKind, setOtherRecordKind] = useState<LinkableEntityKind>()
  const [otherRecord, setOtherRecord] = useState<ILinkableEntity | null>(null)
  const recordLabel = useMemo(() => getRecordLabel(recordKind, record), [recordKind, record])
  const recordLinks = useMemo(() => getRecordLinks(record), [record])
  const filteredRecordLinks = useMemo(() => {
    return recordLinks.filter(link => link.otherRecordKind === otherRecordKind)
  }, [recordLinks, otherRecordKind])
  const [filter, setFilter] = useState<LinkableEntityQueryFilter>({})
  const [pageSort, setPageSort] = useState<PageableInput>(DEFAULT_PAGESORT)
  const [filterStatus, setFilterStatus] = useState(filter.status?.[0] ?? '')
  const [filterText, setFilterText] = useState('')
  const [filterAdvanced, setFilterAdvanced] = useState<boolean | "indeterminate">(false)
  const [filterFuzzy, setFilterFuzzy] = useState<boolean | "indeterminate">(false)
  const [filterRecordId, setFilterRecordId] = useState('')
  const [selectedLink, setSelectedLink] = useState<RecordLink | null>(null)
  const [mode, setMode] = useState(VIEW)
  const [thisRecordLocations, setThisRecordLocations] = useState<string>('')
  const [otherRecordLocations, setOtherRecordLocations] = useState<string>('')
  const [topicId, setTopicId] = useState<string>('')
  const auditResult = useQuery(READ_ENTITY_AUDIT, { variables: { id: record?.id }, skip: !statusDialogOpen || !record })
  const otherRecordsQuery = (otherRecordKind && getReadQuery(otherRecordKind)) ?? READ_ENTITY_LINKS // A dummy query.
  const otherRecordsResult = useQuery(otherRecordsQuery, { variables: { filter, pageSort }, skip: !statusDialogOpen || !otherRecordKind })
  const [otherRecordsFieldName] = useMemo(() => otherRecordsQuery ? introspect(otherRecordsQuery, OperationTypeNode.QUERY) : '', [otherRecordsQuery])
  const [createLinkOp, createLinkResult] = useMutation(CREATE_ENTITY_LINK, { refetchQueries: [READ_ENTITY_AUDIT/*otherRecordsQuery*/] })
  const [updateLinkOp, updateLinkResult] = useMutation(UPDATE_ENTITY_LINK, { refetchQueries: [/*otherRecordsQuery*/] })
  const [deleteLinkOp, deleteLinkResult] = useMutation(DELETE_ENTITY_LINK, { refetchQueries: [/*otherRecordsQuery*/] })
  const [updateStatusOp, updateStatusResult] = useMutation(UPDATE_ENTITY_STATUS, { refetchQueries: [/*otherRecordsQuery*/] })
  const allowLinking = !!record && hasAuthority("LNK")
  const thisLocationsRef = useRef<HTMLInputElement>(null)

  const loading = auditResult.loading || otherRecordsResult?.loading || createLinkResult.loading ||
    updateLinkResult.loading || deleteLinkResult.loading || updateStatusResult.loading

  const entityAudit = useMemo(() => {
    const data = (auditResult.loading
      ? auditResult.previousData
      : auditResult.data) as QueryResult<EntityAudit>
    return data?.audit ?? { pass: true }
  }, [auditResult])
  const fieldAudit = useMemo(() => {
    return entityAudit.fieldAudit ?? { fields: [], groups: [], pass: true }
  }, [entityAudit])
  const linkAudit = useMemo(() => {
    return entityAudit.linkAudit ?? { links: [], groups: [], pass: true }
  }, [entityAudit])

  const fuzzySearchSupported = useMemo(() => {
    return isFuzzySearchSupported(recordKind, otherRecordKind)
  }, [recordKind, otherRecordKind])

  const fuzzySearchField = useMemo(() => {
    return getFuzzySearchField(recordKind)
  }, [recordKind])

  const fuzzyItemCount = useMemo(() => {
    let fuzzySearchValue = (fuzzySearchField ? record?.[fuzzySearchField as keyof ILinkableEntity] : '') as string
    return lineCount(fuzzySearchValue)
  }, [record, fuzzySearchField])

  const otherRecordsPage = useMemo(() => {
    const data = (otherRecordsResult?.loading
      ? otherRecordsResult.previousData
      : otherRecordsResult.data) as QueryResult<IPage<ILinkableEntity>>
    let otherRecordsPage = data && otherRecordsFieldName
      ? data[otherRecordsFieldName]
      : undefined
    return otherRecordsPage
  }, [otherRecordsResult, otherRecordsFieldName])

  const otherRecords = useMemo(() => otherRecordsPage?.content ?? [], [otherRecordsPage])
  const otherRecordsUnlinkedCount = useMemo(() => {
    let counter = 0
    otherRecords.map(otherRecord => {
      if (filteredRecordLinks.findIndex(link => link.otherRecordId == otherRecord.id) == -1)
        counter++
    })
    return counter
  }, [otherRecords, filteredRecordLinks])

  const otherRecordId = otherRecord?.id
  const otherRecordLabel = useMemo(() => getRecordLabel(otherRecordKind, otherRecord) ?? '', [otherRecordKind, otherRecord])

  // Must disable the 'Link' button if an ancestor or descendant topic is already linked.
  // filteredRecordLinks contains all existing links for the selected otherRecordKind.
  // otherRecords contains all as-yet unlinked records of the selected otherRecordKind.
  // When otherRecordKind === "Topic", gather a set containing IDs of linked topics, their ancestors and descendants.
  // If the set includes otherRecord.id, disable the 'Link' button.
  const topicAxis = useMemo(() => {
    return otherRecordKind === "Topic" ? getTopicAxis(otherRecords, filteredRecordLinks) : EMPTY_SET
  }, [otherRecordKind, otherRecords, filteredRecordLinks])

  const handlePrevious = useCallback(() => {
    switch (statusDialogItem) {
      case LINK_AUDIT:
        setStatusDialogItem(FIELD_AUDIT)
        break
      case LINK_MANAGER:
        setStatusDialogItem(LINK_AUDIT)
        break
    }
  }, [statusDialogItem, setStatusDialogItem])

  const handleNext = useCallback(() => {
    switch (statusDialogItem) {
      case FIELD_AUDIT:
        setStatusDialogItem(LINK_AUDIT)
        break
      case LINK_AUDIT:
        setStatusDialogItem(LINK_MANAGER)
        break
    }
  }, [statusDialogItem, setStatusDialogItem])

  const refreshEditableFields = useCallback((selectedLink: RecordLink | null) => {
    logger.trace("refreshEditableFields: selectedLink.id = '%s'", selectedLink?.id ?? '')
    setThisRecordLocations(selectedLink?.thisLocations ?? '')
    setOtherRecordLocations(selectedLink?.otherLocations ?? '')
    setTopicId(selectedLink?.otherRecordKind == "Topic" ? selectedLink?.otherRecordId : '')
  }, [setThisRecordLocations, setOtherRecordLocations, setTopicId])

  const handleNew = useCallback((otherRecordKind: LinkableEntityKind): void => {
    logger.trace("handleNew: otherRecordKind='%s'", otherRecordKind)
    setOtherRecordKind(otherRecordKind)
    setOtherRecord(null)
    setSelectedLink(null)
    setStatusDialogItem(LINK_MANAGER)
    toast.info(`Use the search to find a link target ${otherRecordKind}`)
  }, [setOtherRecordKind, setOtherRecord, setSelectedLink, setStatusDialogItem])

  const updateFilter = useCallback((newOtherRecordKind: LinkableEntityKind | undefined, newStatus: string, newText: string,
    newAdvanced: boolean | "indeterminate", newFuzzy: boolean | "indeterminate", newRecordId: string) => {

    logger.trace("updateFilter: status='%s', text='%s', advanced=%s, fuzzy=%s, recordId='%s'", newStatus, newText, newAdvanced, newFuzzy, newRecordId)
    const newFilter = {
      status: newStatus ? [newStatus] : undefined,
      text: newText || undefined,
      advancedSearch: newText && newAdvanced || undefined,
      recordId: newRecordId || undefined,
      parentId: newOtherRecordKind === "Topic" ? -1 : undefined,
    } as LinkableEntityQueryFilter
    if (newFuzzy && fuzzySearchSupported && record && recordKind && newOtherRecordKind) {
      const props = getRecordLinkProperties(recordKind, newOtherRecordKind)
      if (props) {
        newFilter[props.otherRecordFuzzyProperty] = true
        newFilter[props.thisRecordKindProperty] = getEntityKind(recordKind)
        newFilter[props.thisRecordIdProperty] = record.id
      }
    }
    if (!isEqual(newFilter as LinkableEntityQueryFilter, filter)) {
      logger.trace("updateFilter from %o to %o", filter, newFilter)
      setFilter(newFilter)

    }
    if (newOtherRecordKind !== otherRecordKind && pageSort.pageNumber != 0)
      setPageSort({pageNumber: 0, pageSize: pageSort.pageSize})
  }, [fuzzySearchSupported, record, recordKind, filter, setFilter, otherRecordKind, pageSort, setPageSort])

  logger.trace("selectedLink = %o", selectedLink)

  const isModified = useCallback(() => {
    // NOTE: when editing a new record, selectedLink is undefined.
    return thisRecordLocations != (selectedLink?.thisLocations ?? '') ||
      otherRecordLocations != (selectedLink?.otherLocations ?? '')
  }, [thisRecordLocations, otherRecordLocations, selectedLink])

  const createInput = useCallback((recordLink: Partial<RecordLink>) => {
    logger.trace("createInput: recordLink=%o", recordLink)
    return recordLink.thisRecordIsToEntity
      ? {
        id: recordLink.id,
        fromEntityId: recordLink.otherRecordId,
        fromEntityLocations: otherRecordLocations || null,
        toEntityId: recordLink.thisRecordId,
        toEntityLocations: thisRecordLocations || null,
      }
      : {
        id: recordLink.id,
        fromEntityId: recordLink.thisRecordId,
        fromEntityLocations: thisRecordLocations || null,
        toEntityId: recordLink.otherRecordId,
        toEntityLocations: otherRecordLocations || null,
      }
  }, [thisRecordLocations, otherRecordLocations])

  const handleOtherRecordKindChange = useCallback((otherRecordKind: LinkableEntityKind) => {
    logger.trace("handleOtherRecordKindChange: otherRecordKind='%s'", otherRecordKind)
    setOtherRecordKind(otherRecordKind)
    setOtherRecord(null)
    setSelectedLink(null)
    updateFilter(otherRecordKind, filterStatus, filterText, filterAdvanced, filterFuzzy, filterRecordId)
  }, [setOtherRecordKind, setOtherRecord, setSelectedLink, updateFilter, filterStatus, filterText, filterAdvanced, filterFuzzy, filterRecordId])

  const handleStatusChange = useCallback((status: string) => {
    logger.trace("handleStatusChange: status='%s'", status)
    status = status === "ALL" ? '' : status
    setFilterStatus(status)
    updateFilter(otherRecordKind, status, filterText, filterAdvanced, filterFuzzy, filterRecordId)
  }, [setFilterStatus, updateFilter, otherRecordKind, filterText, filterAdvanced, filterFuzzy, filterRecordId])

  const handleTextChange = useCallback((text: string) => {
    logger.trace("handleTextChange: text='%s'", text)
    setFilterText(text)
    updateFilter(otherRecordKind, filterStatus, text, filterAdvanced, filterFuzzy, filterRecordId)
  }, [setFilterText, updateFilter, otherRecordKind, filterStatus, filterAdvanced, filterFuzzy, filterRecordId])

  const handleAdvancedSearchChange = useCallback((advanced: boolean) => {
    logger.trace("handleAdvancedSearchChange: advanced=%s", advanced)
    setFilterAdvanced(advanced)
    updateFilter(otherRecordKind, filterStatus, filterText, advanced, filterFuzzy, filterRecordId)
  }, [setFilterAdvanced, updateFilter, otherRecordKind, filterStatus, filterText, filterFuzzy, filterRecordId])

  const handleFuzzySearchChange = useCallback((fuzzy: boolean) => {
    logger.trace("handleFuzzySearchChange: fuzzy=%s", fuzzy)
    setFilterFuzzy(fuzzy)
    updateFilter(otherRecordKind, filterStatus, filterText, filterAdvanced, fuzzy, filterRecordId)
  }, [setFilterFuzzy, updateFilter, otherRecordKind, filterStatus, filterText, filterAdvanced, filterRecordId])

  const handleRecordIdChange = useCallback((recordId: string) => {
    logger.trace("handleRecordIdChange: recordId='%s'", recordId)
    setFilterRecordId(recordId)
    updateFilter(otherRecordKind, filterStatus, filterText, filterAdvanced, filterFuzzy, recordId)
  }, [setFilterRecordId, updateFilter, otherRecordKind, filterStatus, filterText, filterAdvanced, filterFuzzy])

  useEffect(() => {
    updateFilter(otherRecordKind, filterStatus, filterText, filterAdvanced, filterFuzzy, filterRecordId)
  }, [updateFilter, recordKind, record, otherRecordKind, filterStatus, filterText, filterAdvanced, filterFuzzy, filterRecordId])

  const handleReset = useCallback(() => {
    logger.trace("handleReset")
    setFilterStatus('')
    setFilterText('')
    setFilterAdvanced(false)
    setFilterFuzzy(false)
    setFilterRecordId('')
    updateFilter(otherRecordKind, '', '', false, false, '')
  }, [setFilterStatus, setFilterText, setFilterAdvanced, setFilterFuzzy, setFilterRecordId, updateFilter, otherRecordKind])

  const handleLink = useCallback(() => {
    logger.trace("handleLink")
    if (mode === VIEW) {
      toast.info(`Linking '${otherRecordLabel}'. Enter link locations then Save.`)
      setSelectedLink(null)
      setThisRecordLocations('')
      setOtherRecordLocations('')
      setMode(CREATE)
      requestAnimationFrame(() => requestAnimationFrame(() => thisLocationsRef.current?.focus()))
    }
  }, [mode, otherRecordLabel, setSelectedLink, setThisRecordLocations, setOtherRecordLocations, setMode])

  const handleEdit = useCallback(() => {
    logger.trace("handleEdit")
    if (mode === VIEW) {
      toast.info("Edit link locations then Save.")
      setMode(EDIT)
      requestAnimationFrame(() => requestAnimationFrame(() => thisLocationsRef.current?.focus()))
    }
  }, [mode, setMode])

  const handleSave = useCallback(() => {
    if (mode === CREATE) {
      const thisRecordId = record?.id
      if (recordKind && otherRecordKind && thisRecordId && otherRecordId) {
        const props = getRecordLinkProperties(recordKind, otherRecordKind)
        if (props) {
          const thisRecordIsToEntity = props.thisRecordIdProperty === TO_ENTITY_ID
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
              })
            },
            onCompleted: (data) => {
              const newLink = (data as QueryResult<EntityLink>).createEntityLink
              logger.trace("handleSave(create).completed: new EntityLink: %o", newLink)
              toast.info(`New link with '${otherRecordLabel}' created.`)
              setMode(VIEW)
              setOtherRecord(null)
              setSelectedLink(recordLinks.find(link => link.id === newLink.id) ?? null)
            },
            onError: (error) => {
              toast.error(error.message)
            },
          })
        }
      }
    } else if (mode === EDIT) {
      toast.info("Saving link locations...")
      if (selectedLink)
        updateLinkOp({
          variables: {
            input: createInput(selectedLink)
          },
          onCompleted: (/*data, clientOptions*/) => {
            logger.trace("handleSave(edit).completed")
            toast.info(`Link to '${otherRecordLabel}' updated.`)
            setMode(VIEW)
          },
          onError: (error/*, clientOptions*/) => {
            toast.error(error.message)
          },
        })
    }
  }, [
    mode,
    setMode,
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
    setOtherRecord,
    setSelectedLink,
  ])

  const handleCancel = useCallback(() => {
    if (mode === CREATE || mode === EDIT) {
      if (isModified()) {
        const target = mode === EDIT
          ? `link with record '${selectedLink?.otherRecordLabel}'`
          : "new record link"
        if (confirm(`Confirm discard changes to ${target}?`)) {
          refreshEditableFields(selectedLink)
          setMode(VIEW)
          toast.info(`Cancelled ${mode}`)
        }
      } else {
        setMode(VIEW)
      }
    }
  }, [mode, setMode, selectedLink, isModified, refreshEditableFields])

  const handleRelink = useCallback(() => {
    if (confirm(`Change target of link from ${selectedLink?.otherRecordLabel} to ${otherRecordLabel}?${otherRecordLocations ? "\n\nN.B. The 'Location(s) in other record' value will be retained but may not be appropriate to the new target record. Change it if necessary." : ""}`)) {
      toast.info(`Relinking to ${otherRecordLabel}`)
      const newTargetLabel = otherRecordLabel
      updateLinkOp({
        variables: {
          input: {
            id: selectedLink?.id,
            fromEntityId: selectedLink?.thisRecordIsToEntity ? otherRecordId : record?.id,
            fromEntityLocations: selectedLink?.thisRecordIsToEntity ? thisRecordLocations : otherRecordLocations,
            toEntityId: selectedLink?.thisRecordIsToEntity ? record?.id : otherRecordId,
            toEntityLocations: selectedLink?.thisRecordIsToEntity ? otherRecordLocations : thisRecordLocations,
          }
        },
        onCompleted: (data) => {
          setOtherRecord(null)
          toast.info(`Changed link target to ${newTargetLabel}`)
        },
        onError: (error) => {
          toast.error(error.message)
        }
      })
    }
  }, [selectedLink, record, otherRecordId, otherRecordLabel, thisRecordLocations, otherRecordLocations, updateLinkOp])

  const handleUnlink = useCallback(() => {
    if (selectedLink) {
      if (confirm(`Confirm delete link with record '${selectedLink.otherRecordLabel}'?`)) {
        deleteLinkOp({
          variables: { entityLinkId: selectedLink.id },
          onCompleted: () => toast.info(`Unlinked '${selectedLink.otherRecordLabel}'`),
          onError: (error/*, clientOptions*/) => {
            toast.error(error.message)
          },
        })
      } else {
        toast.info(`Cancelled unlink '${selectedLink?.otherRecordLabel}'`)
      }
    }
  }, [selectedLink, deleteLinkOp])

  const prevRecord = useRef<ILinkableEntity>(undefined)
  useEffect(() => {
    if (record !== prevRecord.current) {
      if (record?.id !== prevRecord.current?.id)
        setSelectedLink(null)
      prevRecord.current = record
    }
  }, [record, setSelectedLink])

  const handleClose = useCallback(() => {
    setStatusDialogOpen(false)
  }, [setStatusDialogOpen])

  const handlePublish = useCallback(() => {
    updateStatusOp({
      variables: {
        entityId: record?.id,
        status: "PUB"
      },
      onCompleted: (data) => {
        const entity = (data as QueryResult<ITrackedEntity>).setEntityStatus
        logger.trace("handlePublish().completed: ITrackedEntity: %o", entity)
        toast.info(`Published ${recordLabel}`)
      },
      onError: (error) => {
        toast.error(error.message)
      },
    })
  }, [updateStatusOp, record, recordLabel])

  return (
    <Dialog open={statusDialogOpen} onOpenChange={setStatusDialogOpen}>
      <DialogTrigger asChild>
        <ButtonEx
          type="button"
          variant="default"
          outerClassName={cn("place-self-center", "")}
          className="w-40 bg-blue-500 text-md"
          disabled={!hasAuthority("UPD") || !record}
          onClick={() => setStatusDialogItem(LINK_MANAGER)}
          help={`Manage links for ${recordLabel}`}>
          Manage...
        </ButtonEx>
      </DialogTrigger>
      <DialogContent
        className="flex flex-col items-center w-5/6 min-w-0 max-w-none! h-5/6 min-h-0 max-h-none! overflow-hidden"
        onPointerDownOutside={e => e.preventDefault()}
      >
        <DialogHeader>
          <DialogTitle className="text-center">
            {
              recordKind
                ? (() => {
                  const Icon = RecordIcons[recordKind]
                  return <Icon className="inline" />
                })()
                : null
            }&nbsp;{recordKind} Status
            <ContextHelp href="/doc/status/" />
          </DialogTitle>
          <DialogDescription>
            Manage status for {recordLabel}
          </DialogDescription>
          <p className="text-red-600">{error}</p>
        </DialogHeader>
        <Spinner className="absolute inset-0 bg-black/20 z-50" loading={loading} />
        <Tabs
          className="flex flex-col w-7/8 h-full min-h-0"
          value={statusDialogItem}
          onValueChange={value => setStatusDialogItem(value as StatusDialogItemType)}
        >
          <TabsList>
            <TabsTrigger value={FIELD_AUDIT}>Field Audit</TabsTrigger>
            <TabsTrigger value={LINK_AUDIT}>Link Audit</TabsTrigger>
            <TabsTrigger value={LINK_MANAGER}>Link Manager</TabsTrigger>
          </TabsList>
          <TabsContent className="h-15/16" value={FIELD_AUDIT}>
            <Card className="h-15/16">
              <CardHeader>
                <CardTitle><RectangleEllipsisIcon className="inline" /><SearchIcon className="inline" />&nbsp;Field Audit</CardTitle>
                <CardDescription>
                  Checks the selected {recordKind ?? "record"} field values against minimum publication criteria.
                  Error-level failures must be corrected before publication is permitted but please also correct any warning-level failures, if possible.
                </CardDescription>
              </CardHeader>
              <CardContent className="w-full h-full max-h-full overflow-auto">
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
                      <TableHead className="border">Check</TableHead>
                      <TableHead className="text-center border">Result</TableHead>
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
              </CardContent>
            </Card>
          </TabsContent>
          <TabsContent className="h-15/16" value={LINK_AUDIT}>
            <Card className="h-15/16">
              <CardHeader>
                <CardTitle><LinkIcon className="inline" /><SearchIcon className="inline" />&nbsp;Link Audit</CardTitle>
                <CardDescription>
                  Checks the selected {recordKind ?? "record"} links against minimum publication criteria.
                  Error-level failures must be corrected before publication is permitted but please also correct any warning-level failures, if possible.
                </CardDescription>
              </CardHeader>
              <CardContent className="h-full max-h-full overflow-auto">
                <form>
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
                        <TableHead className="text-center border">Result</TableHead>
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
                                  type="button"
                                  variant="outline"
                                  className="cursor-pointer"
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
                                    type="button"
                                    variant="outline"
                                    className="cursor-pointer"
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
              </CardContent>
            </Card>
          </TabsContent>
          <TabsContent value={LINK_MANAGER}>
            <Card>
              <CardHeader>
                <CardTitle><LinkIcon className="inline" />&nbsp;Link Manager</CardTitle>
                <CardDescription>
                  Manage links between the selected {recordKind ?? "record"} and {otherRecordKind ?? "other"} records.
                  Please ensure that the {recordKind ?? "record"} is linked to all relevant records of other types.
                </CardDescription>
              </CardHeader>
              <CardContent className="w-full h-full max-h-full overflow-auto">
                <form>
                  <div className="flex">
                    <fieldset className="border rounded-md ml-1 p-2 gap-2">
                      <legend>&nbsp;Links with:&nbsp;</legend>
                      <RadioGroup
                        value={otherRecordKind}
                        onValueChange={value => handleOtherRecordKindChange(value as LinkableEntityKind)}
                      >
                        {
                          LinkableEntityKindKeys.map(key => (
                            <div id={key} key={key} className="flex items-center gap-3">
                              <RadioGroupItem
                                value={key}
                                disabled={mode != VIEW || key === recordKind}
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
                        <legend>&nbsp;{`Search for ${otherRecordKind ?? "record"}s to link (${otherRecordsUnlinkedCount.toLocaleString()} of ${(otherRecords?.length ?? 0).toLocaleString()} on this page)`}&nbsp;</legend>
                        <div className="flex items-center gap-2 mb-2">
                          <Select
                            value={filterStatus ?? ''}
                            onValueChange={handleStatusChange}
                          >
                            <SelectTriggerEx
                              id="status"
                              disabled={mode != VIEW || !otherRecordKind}
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
                              <SelectItem value="DEL">Deleted</SelectItem>
                              <SelectItem value="DRA">Draft</SelectItem>
                              <SelectItem value="PUB">Published</SelectItem>
                              <SelectItem value="SUS">Suspended</SelectItem>
                            </SelectContent>
                          </Select>
                          <InputEx
                            id="searchText"
                            disabled={mode != VIEW || !otherRecordKind}
                            outerClassName="w-56"
                            clear
                            delay={500}
                            search
                            help="Filter the list to show only records containing the specified text. This performs a case-insensitive match against all text fields, matching whole words unless 'Advanced' is checked."
                            title="Filter the list to show only records containing the specified text. This performs a case-insensitive match against all text fields, matching whole words unless 'Advanced' is checked."
                            placeholder="Search..."
                            value={filterText}
                            onChange={e => handleTextChange(e.target.value)}
                          />
                          <Checkbox
                            id="advanced"
                            disabled={mode != VIEW || !otherRecordKind}
                            checked={filterAdvanced}
                            onCheckedChange={handleAdvancedSearchChange}
                            title="Use advanced ('Boolean mode') text search syntax. See info hover tip to the right."
                          />
                          <Link
                            className="text-black"
                            href="https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-indexes/full-text-indexes/full-text-index-overview#in-boolean-mode"
                            target="_blank"
                          >
                            <LabelEx htmlFor="advanced" help="Use advanced ('Boolean mode') text search syntax. Click the ⓘ icon for details.">Advanced</LabelEx>
                          </Link>
                          <Checkbox
                            id="fuzzy"
                            disabled={mode != VIEW || !otherRecordKind || !fuzzySearchSupported}
                            checked={fuzzySearchSupported && filterFuzzy}
                            onCheckedChange={handleFuzzySearchChange}
                            title="Perform a fuzzy search. See info hover tip to the right."
                          />
                          <LabelEx
                            htmlFor="fuzzy"
                            help={`Perform a fuzzy search. Guesses which ${otherRecordKind} records might need linking to the contextual ${recordKind ?? "record"} based on its ${fuzzySearchField} field, displayed in 'Fuzzy match on' to the right.`}>Fuzzy</LabelEx>
                          <InputEx
                            id="recordId"
                            disabled={mode != VIEW || !otherRecordKind}
                            outerClassName="w-38"
                            className="text-right"
                            clear
                            placeholder="Record ID"
                            value={filterRecordId}
                            onChange={e => handleRecordIdChange(e.target.value)}
                            delay={500}
                            help="Filter the list to show only the record with the specified ID. Other filters are retained but ignored."
                          />
                          <ButtonEx
                            id="refresh"
                            type="button"
                            variant="outline"
                            disabled={mode != VIEW || !otherRecordKind}
                            help="Refresh the list using the same filter and pagination settings."
                            onClick={() => otherRecordsResult.refetch()}
                          >
                            <RotateCwIcon />
                          </ButtonEx>
                          <ButtonEx
                            id="reset"
                            type="button"
                            variant="outline"
                            disabled={mode != VIEW || !otherRecordKind || !filterStatus && !filterText && !filterAdvanced && !filterFuzzy && !filterRecordId}
                            className="w-20"
                            onClick={handleReset}
                            help="Clear all filters"
                          >
                            Reset
                          </ButtonEx>
                        </div>
                        <div className="flex items-center gap-2 mb-2">
                          <Combobox
                            items={otherRecords}
                            itemToStringValue={otherRecord => getRecordLabel(otherRecordKind, otherRecord as ITrackedEntity) ?? ''}
                            itemToStringLabel={otherRecord => getRecordLabel(otherRecordKind, otherRecord as ITrackedEntity) ?? ''}
                            value={otherRecord}
                            onValueChange={setOtherRecord}
                          >
                            <ComboboxInput
                              className="truncate w-full max-w-213"
                              disabled={!otherRecordKind || mode !== VIEW}
                              placeholder=
                              {
                                otherRecordKind
                                  ? otherRecords.length ?? 0 > 0
                                    ? "-Select a record to link-"
                                    : "-No matching records-"
                                  : "-Select a 'Links with' record kind-"
                              }
                              showClear
                            >
                              <InputGroupAddon className="gap-1" align="inline-end">
                                <Badge variant="outline" title="The number of other records on this page">{otherRecords.length.toLocaleString()}</Badge>
                                <Help text={`A filtered, paginated list of matching ${otherRecordKind ?? "record"}s. Already linked ones are shown disabled, unlinked ones are selectable. Select one in order to link it.`} />
                              </InputGroupAddon>
                            </ComboboxInput>
                            <ComboboxContent className="z-50 pointer-events-auto">
                              <ComboboxEmpty>-No records found-</ComboboxEmpty>
                              <ComboboxList>
                                {otherRecord => (
                                  <ComboboxItem key={otherRecord.id} value={otherRecord} disabled={filteredRecordLinks.findIndex(l => l.otherRecordId == otherRecord.id) != -1}>
                                    {getRecordLabel(otherRecordKind, otherRecord as ITrackedEntity)}
                                  </ComboboxItem>
                                )}
                              </ComboboxList>
                            </ComboboxContent>
                          </Combobox>
                          <ButtonEx
                            type="button"
                            variant="outline"
                            className="w-20"
                            disabled={!allowLinking || !otherRecordId || topicAxis.has(otherRecord?.id ?? '0') || mode !== VIEW}
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
                        <div className="flex justify-end">
                          <Paginator page={otherRecordsPage} pageSort={pageSort} setPageSort={setPageSort} />
                          <div className="w-29" />
                        </div>
                      </fieldset>
                      <fieldset className="flex border rounded-md ml-1 p-2 gap-2">
                        <legend>&nbsp;{`Existing ${otherRecordKind ?? "record"} links`}&nbsp;</legend>
                        <Combobox
                          items={filteredRecordLinks}
                          itemToStringValue={link => link.otherRecordLabel}
                          itemToStringLabel={link => link.otherRecordLabel}
                          value={selectedLink}
                          onValueChange={setSelectedLink}
                        >
                          <ComboboxInput
                            className="truncate w-full max-w-213"
                            disabled={!record || !otherRecordKind || mode !== VIEW}
                            placeholder=
                            {
                              otherRecordKind
                                ? filteredRecordLinks.length ?? 0 > 0
                                  ? "-Select a record link-"
                                  : "-No record links-"
                                : "-Select a 'Link with' record kind-"
                            }
                            showClear
                          >
                            <InputGroupAddon className="gap-1" align="inline-end">
                              <Badge variant="outline" title="The number of record links">{filteredRecordLinks.length.toLocaleString()}</Badge>
                              <Help text={`A list of all the links to ${otherRecordKind}s which are linked with the contextual ${recordKind}. Select one to see its settings.`} />
                            </InputGroupAddon>
                          </ComboboxInput>
                          <ComboboxContent className="z-50 pointer-events-auto">
                            <ComboboxEmpty>-No record links found-</ComboboxEmpty>
                            <ComboboxList>
                              {link => (
                                <ComboboxItem key={link.id} value={link}>
                                  {link.otherRecordLabel}
                                </ComboboxItem>
                              )}
                            </ComboboxList>
                          </ComboboxContent>
                        </Combobox>
                        <ButtonEx
                          type="button"
                          variant="outline"
                          outerClassName="justify-center"
                          className="w-20"
                          disabled={!allowLinking || mode == VIEW && !selectedLink || mode == EDIT || mode == CREATE}
                          onClick={handleEdit}
                          help={
                            selectedLink
                              ? "Edit the selected link"
                              : "No record link selected"
                          }
                        >
                          Edit
                        </ButtonEx>
                      </fieldset>
                    </div>
                    {
                      fuzzySearchField
                        ? <div className="flex grow">
                          <fieldset className="flex w-full h-full border rounded-md ml-1 p-2 gap-2">
                            <legend>&nbsp;{`${firstToUpper(fuzzySearchField)} (${fuzzyItemCount.toLocaleString()})`}&nbsp;</legend>
                            <Textarea
                              className="w-full h-42 overflow-auto"
                              readOnly
                              value={record && fuzzySearchField ? record[fuzzySearchField as keyof ILinkableEntity] as string : ''}
                              title={`The ${recordKind} ${fuzzySearchField}`}
                            />
                          </fieldset>
                        </div>
                        : null
                    }
                  </div>
                  <fieldset className="grid grid-cols-5 border rounded-md ml-1 p-2 gap-2">
                    <legend>&nbsp;Link Details&nbsp;</legend>
                    <Label className="col-start-1" htmlFor="link-id">Link ID:</Label>
                    <InputEx
                      id="link-id"
                      type="text"
                      readOnly={true}
                      disabled={!selectedLink && mode !== CREATE}
                      value={selectedLink?.id ?? ''}
                      help="The database identifier of the selected record link"
                    />
                    <Label htmlFor="link-status">Status:</Label>
                    <InputEx
                      id="link-status"
                      type="text"
                      readOnly={true}
                      disabled={!selectedLink && mode !== CREATE}
                      value={selectedLink?.status ?? ''}
                      help="The status of the selected record link"
                    />
                    <ButtonEx
                      type="button"
                      variant="outline"
                      outerClassName="w-full justify-end"
                      className="w-20 place-self-center"
                      disabled={!allowLinking || mode == VIEW || (mode == EDIT && !isModified())}
                      onClick={handleSave}
                      help={
                        selectedLink
                          ? mode === CREATE
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
                      disabled={!selectedLink}
                      value={formatDateTime(selectedLink?.created)}
                      help="The date and time at which the record link was created"
                    />
                    <ButtonEx
                      type="button"
                      variant="outline"
                      outerClassName="w-full justify-end"
                      className="w-20 place-self-center"
                      disabled={!allowLinking || mode === VIEW}
                      onClick={handleCancel}
                      help={
                        selectedLink
                          ? mode === CREATE
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
                      disabled={!selectedLink}
                      value={selectedLink?.updatedByUser ?? ''}
                      help="The username of the user who last updated the record link"
                    />
                    <Label htmlFor="link-updated">Updated on:</Label>
                    <InputEx
                      id="link-updated"
                      type="text"
                      readOnly={true}
                      disabled={!selectedLink}
                      value={formatDateTime(selectedLink?.updated)}
                      help="The date and time at which the record link was last updated"
                    />
                    <ButtonEx
                      type="button"
                      variant="outline"
                      outerClassName="justify-end w-full"
                      className="w-20 place-self-center"
                      disabled={!allowLinking || !selectedLink || !otherRecordId || mode !== VIEW}
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
                      readOnly={!allowLinking || mode === VIEW}
                      disabled={!selectedLink && mode !== CREATE}
                      value={thisRecordLocations}
                      onChange={e => setThisRecordLocations(e.target.value)}
                      help="Location(s) within this record that pertain to the record link"
                    />
                    <ButtonEx
                      type="button"
                      variant="outline"
                      outerClassName="justify-end w-full"
                      className="w-20 place-self-center"
                      disabled={!allowLinking || !selectedLink || selectedLink?.status === "Deleted" || mode !== VIEW}
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
                      readOnly={!allowLinking || mode === VIEW}
                      disabled={!selectedLink && mode !== CREATE}
                      value={otherRecordLocations}
                      onChange={e => setOtherRecordLocations(e.target.value)}
                      help="Location(s) within the other record that pertain to the record link"
                    />
                  </fieldset>
                </form>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
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
                        : <>{`${recordKind} #${record.id} is already published but no longer meets the minumum criteria for publication.`}<br />Please review the Field Audit and Link Audit pages and correct any failures.</>
                      : entityAudit.pass
                        ? <>{`${recordKind} #${record.id} meets the minumum criteria for publication.`}<br />You may publish it at will; any unpublished outbound links will also be published.</>
                        : <>{`${recordKind} #${record.id} does not yet meet the minumum criteria for publication.`}<br />Please review the Field Audit and Link Audit pages and correct any failures.</>
                  }
                </p>
                : null
            }
            <div className="flex justify-center gap-2">
              <Button
                type="button"
                variant="outline"
                className="w-20"
                disabled={statusDialogItem === FIELD_AUDIT}
                title="Show previous tab"
                onClick={handlePrevious}
              >
                Previous
              </Button>
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
              <Button
                type="button"
                variant="outline"
                className="w-20"
                disabled={statusDialogItem === LINK_MANAGER}
                title="Show next tab"
                onClick={handleNext}
              >
                Next
              </Button>
            </div>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}