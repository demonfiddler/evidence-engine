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

import { toast } from "sonner"
import { useCallback, useEffect, useMemo, useState } from "react"
import { useContext } from 'react'
import DropdownTreeSelect, { TreeNode, TreeNodeProps } from 'react-dropdown-tree-select'
import 'react-dropdown-tree-select/dist/styles.css'
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible"
import Topic from "@/app/model/Topic"
import { GlobalContext } from '@/lib/context'
import { findTopic, setTopicFields } from "@/lib/utils"
import { useQuery } from "@apollo/client/react"
import { READ_TOPIC_HIERARCHY } from "@/lib/graphql-queries"
import ButtonEx from "../ext/button-ex"
import Help from "../misc/help"
import InputEx from "../ext/input-ex"
import LabelEx from "../ext/label-ex"
import { Checkbox } from "@/components/ui/checkbox"
import { LinkableEntityQueryFilter } from "@/app/model/schema"
import useLinkableEntityQueryFilter from "@/hooks/use-linkable-entity-query-filter"
import { filter, LoggerEx } from "@/lib/logger"
import IPage from "@/app/model/IPage"
import { QueryResult } from "@/lib/graphql-utils"
import { ChevronDownIcon, ChevronUpIcon } from 'lucide-react'

const logger = new LoggerEx(filter, "[EntityLinkFilter] ")

interface TopicTreeNode extends TreeNodeProps {
  topic: Topic
  description: string
}

function getTreeData(topics: Topic[], masterTopicId?: string): TopicTreeNode[] {
  function getTreeDataRecursive(topics: Topic[]): TopicTreeNode[] {
    return topics.map(topic => {
      return {
        topic: topic,
        value: topic.id?.toString() ?? '(no value)',
        label: topic.label ?? '(no label)',
        description: topic.description ?? '(no description)',
        children: topic.children ? getTreeDataRecursive(topic.children) : undefined,
        checked: topic?.id == masterTopicId
      }
    })
  }
  function expandParents(nodes: TopicTreeNode[]): boolean {
    for (const node of nodes) {
      if (node.checked)
        return true
      if (node.children && expandParents(node.children as TopicTreeNode[])) {
        node.expanded = true
        return true
      }
    }
    return false
  }
  const treeData = getTreeDataRecursive(topics)
  expandParents(treeData)
  return treeData
}

function setChecked(data: TreeNode[], currentNode: TreeNode): boolean {
  for (let i = 0; i < data.length; i++) {
    if (data[i].value == currentNode.value) {
      data[i].checked = currentNode.checked
      return true
    }
    if (data[i].children && setChecked(data[i].children, currentNode))
      return true
  }
  return false
}

export default function EntityLinkFilter() {
  logger.debug("render")

  const {
    linkFilterOpen,
    masterTopicId,
    masterTopicRecursive,
    masterRecordKind,
    masterRecordId,
    masterRecordLabel,
    showOnlyLinkedRecords,
    queries,
    setLinkFilterOpen,
    setMasterTopicId,
    setMasterTopicRecursive,
    setMasterRecordKind,
    setShowOnlyLinkedRecords,
  } = useContext(GlobalContext)
  const {createSearchParams} = useLinkableEntityQueryFilter()
  const topicPlaceholder = masterTopicId ? String.fromCharCode(160) : "-Choose topic-"
  const [topicDescription, setTopicDescription] = useState('')
  const [topicPath, setTopicPath] = useState('')
  const result = useQuery(
    READ_TOPIC_HIERARCHY,
    {
      variables: {
        filter: {
          parentId: -1,
          recursive: false
        },
      },
    }
  )
  const [treeData, setTreeData] = useState<TopicTreeNode[]>([])
  const topics = useMemo(() => {
    logger.trace("memo")
    const outTopics: Topic[] = []
    if (result.data) {
      const inTopics = (result.data as QueryResult<IPage<Topic>>).topics.content ?? []
      setTopicFields("", undefined, inTopics, outTopics)
    }
    return outTopics
  }, [result.data])

  useEffect(() => {
    logger.trace("effect")
    setTreeData(getTreeData(topics, masterTopicId))
    const masterTopic = findTopic(topics, masterTopicId)
    setTopicDescription(masterTopic?.description ?? '')
    setTopicPath(masterTopic?.path ?? '')
  }, [topics, masterTopicId])

  const getMasterRecordLabel = useCallback(() => {
    if (masterRecordKind == "None")
      return '- None -'
    if (!masterRecordLabel)
      return `- Select a ${masterRecordKind} in the ${masterRecordKind}s page -`
    return masterRecordLabel
  }, [masterRecordKind, masterRecordLabel])

  const getMasterRecordUri = useCallback(() => {
    let uri = ""
    if (masterRecordKind != "None")
      uri = `/${masterRecordKind?.toLowerCase()}s`
    const newFilter = {...queries[masterRecordKind]?.filter} as LinkableEntityQueryFilter
    if (masterRecordId)
      newFilter.recordId = masterRecordId
    const searchParams = createSearchParams(newFilter)
    uri += `?${searchParams.toString()}`
    return uri
  }, [masterRecordKind, masterRecordId, queries, createSearchParams])

  const handleTopicChange = useCallback((currentNode: TreeNode, selectedNodes: TreeNode[]) => {
    const topic = selectedNodes.length != 0 ? selectedNodes[0].topic : undefined
    const newData = getTreeData(topics, topic?.id)
    setChecked(newData, currentNode)
    setTreeData(newData)

    const description = currentNode.description.trim()
    setMasterTopicId(topic?.id)
    setTopicDescription(topic?.description ?? '')
    setTopicPath(topic?.path ?? '')

    const topicId = currentNode.topic.id;
    const label = currentNode.topic.label;
    const verb = currentNode.checked ? "selected" : "deselected"
    toast.success(`You ${verb} Topic#${topicId} (${label} - ${description})`);
  }, [topics, setMasterTopicId])

  return (
    <Collapsible
      className="border shadow-lg rounded-md w-fit"
      open={linkFilterOpen}
      onOpenChange={setLinkFilterOpen}
    >
      <div>
        <div className="flex flex-row items-center space-x-4 px-4">
          <span className="text-lg">
            Filter by Links
          </span>
          <CollapsibleTrigger className="justify-self-end" asChild>
            <Button variant="ghost" size="default">
              {
                linkFilterOpen
                ? <ChevronUpIcon className="h-4 w-4" />
                : <ChevronDownIcon className="h-4 w-4" />
              }
              <span className="sr-only">Toggle</span>
            </Button>
          </CollapsibleTrigger>
        </div>
        <CollapsibleContent className="">
          <div className="flex items-stretch m-4 gap-4">
            <fieldset className="border rounded-md">
              <legend className="text-sm">&nbsp;Topic Link&nbsp;</legend>
              <div className="flex flex-col m-2 gap-2">
                <div className="flex flex-row items-center gap-2">
                  <DropdownTreeSelect
                    id="masterTopic"
                    className="ee"
                    clearSearchOnChange={false}
                    data={treeData}
                    onChange={handleTopicChange}
                    mode="radioSelect"
                    keepTreeOnSearch={true}
                    inlineSearchInput={true}
                    showPartiallySelected={true}
                    texts={{ placeholder: `${topicPlaceholder}` }}
                  />
                  <Help text="In the table below, show only records linked to this topic when 'Show only linked records' is checked" />
                </div>
                <Textarea id="masterTopicDescription" placeholder="-Topic description here-" disabled={true} value={topicDescription ?? ''} />
                <p className="text-xs text-gray-500">{`Path: ${topicPath ?? ""}`}</p>
                <div className="flex gap-2">
                  <Checkbox id="recursive" checked={masterTopicRecursive} onCheckedChange={setMasterTopicRecursive} />
                  <LabelEx
                    htmlFor="recursive"
                    help="If checked, the table below will include records linked to sub-topics of the selected topic"
                  >
                    Include records linked to sub-topics
                  </LabelEx>
                </div>
              </div>
            </fieldset>
            <fieldset className="border rounded-md">
              <legend className="text-sm">&nbsp;Master Record Link&nbsp;</legend>
              <div className="flex flex-col m-2 gap-2">
                <RadioGroup
                  id="masterRecordKind"
                  aria-label="Master record kind"
                  className="flex flex-row"
                  value={masterRecordKind}
                  onValueChange={setMasterRecordKind}>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="None" id="option-one" />
                    <Label htmlFor="option-one">None</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="Claim" id="option-two" />
                    <Label htmlFor="option-two">Claim</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="Declaration" id="option-three" />
                    <Label htmlFor="option-three">Declaration</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="Person" id="option-four" />
                    <Label htmlFor="option-four">Person</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="Publication" id="option-five" />
                    <Label htmlFor="option-five">Publication</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="Quotation" id="option-six" />
                    <Label htmlFor="option-six">Quotation</Label>
                  </div>
                  <Help text="In the table below, show only records linked with the instance of this record kind shown in the 'Master record' drop-down below, when 'Show only linked records' is checked" />
                </RadioGroup>
                <div className="flex flex-row items-center m-2 gap-2">
                  <Label htmlFor="masterRecord">Master record:</Label>
                  <InputEx
                    outerClassName="flex-grow"
                    id="masterRecord"
                    readOnly={true}
                    placeholder="-Master record description here-"
                    value={getMasterRecordLabel()}
                    help="In the table below, show only records linked with this master record instance when 'Show only linked records' is checked"
                  />
                  <Link href={getMasterRecordUri()}>
                    <ButtonEx
                      id="gotoMasterRecord"
                      className="bg-blue-500"
                      disabled={masterRecordKind == "None"}
                      help={
                        masterRecordKind != "None"
                        ? `Navigate to the ${masterRecordKind}s page`
                        : "No master record kind selected"
                      }
                    >
                      Go to
                    </ButtonEx>
                  </Link>
                </div>
                <Label className="text-xs text-gray-500">{
                  masterRecordKind == "None"
                    ? "None selected: all lists behave independently"
                    : `Other lists show only records linked with the selected master ${masterRecordKind}`
                }</Label>
              </div>
            </fieldset>
          </div>
          <div className="flex justify-center items-center m-4 gap-4">
            <Checkbox
              id="linkedOnly"
              disabled={!masterTopicId && !masterRecordId}
              checked={showOnlyLinkedRecords}
              onCheckedChange={setShowOnlyLinkedRecords}
            />
            <LabelEx
              htmlFor="linkedOnly"
              help="If checked, the table below will show only records linked to the master topic and/or master record selected above. Note that since records of the same kind cannot be linked, if the master record kind is the same kind as the table below, the master record filter will have no effect (though the topic link filter, if set, will still apply)."
            >
              Show only linked records
            </LabelEx>
          </div>
        </CollapsibleContent>
      </div>
    </Collapsible>
  )
}

EntityLinkFilter.whyDidYouRender = true