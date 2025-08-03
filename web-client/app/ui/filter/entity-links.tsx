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
import { useEffect, useState } from "react"
import { useContext } from 'react'
import DropdownTreeSelect, { TreeNode, TreeNodeProps } from 'react-dropdown-tree-select'
import 'react-dropdown-tree-select/dist/styles.css'
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible"
import { ChevronDownIcon, ChevronUpIcon } from '@heroicons/react/24/outline';
import Topic from "@/app/model/Topic"
import RecordKind from "@/app/model/RecordKind"
import { MasterLinkContext, SelectedRecordsContext } from '@/lib/context'
import { setTopicFields } from "@/lib/utils"
import { useQuery } from "@apollo/client"
import { READ_TOPIC_HIERARCHY } from "@/lib/graphql-queries"

interface TopicTreeNode extends TreeNodeProps {
  topic: Topic
  description: string
}

export default function EntityLinks() {
  const [isOpen, setIsOpen] = useState(false)
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [topicPlaceholder, setTopicPlaceholder] = useState(masterLinkContext.masterTopicId ? String.fromCharCode(160) : "-Choose topic-")
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
  const topics: Topic[] = []
  useEffect(() => {
    // console.log(`EntityLinks effect: result.data = ${result.data}`)
    if (result.data) {
      const inTopics = result.data?.topics.content ?? []
      // console.log(`EntityLinks effect: inTopics = ${JSON.stringify(inTopics)}`)
      setTopicFields("", undefined, inTopics, topics)
      setTreeData(getTreeData())
    }}, [result.data, masterLinkContext.masterTopicId]
  )

  function getTreeData(): TopicTreeNode[] {
    function getTreeDataRecursive(topics: Topic[]): TopicTreeNode[] {
      return topics.map(topic => {
        return {
          topic: topic,
          value: topic.id?.toString() ?? '(no value)',
          label: topic.label ?? '(no label)',
          description: topic.description ?? '(no description)',
          children: topic.children ? getTreeDataRecursive(topic.children) : undefined,
          checked: topic?.id == masterLinkContext.masterTopicId
        }
      })
    }
    function expandParents(nodes: TopicTreeNode[]): boolean {
      for (let node of nodes) {
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
    // console.log(`EntityLinks treeData = ${JSON.stringify(treeData)}`)
    return treeData
  }

  function getMasterRecordLabel(): string {
    if (masterLinkContext.masterRecordKind == "None")
      return '- None -'
    if (!masterLinkContext.masterRecordLabel)
      return `- Select a ${masterLinkContext.masterRecordKind} in the ${masterLinkContext.masterRecordKind}s page -`
    return masterLinkContext.masterRecordLabel
  }

  function getMasterRecordUri(): string {
    let uri = ""
    if (masterLinkContext.masterRecordKind != "None")
      uri = `/${masterLinkContext.masterRecordKind?.toLowerCase()}s`
    if (masterLinkContext.masterRecordId)
      uri += `?id=${masterLinkContext.masterRecordId}`
    return uri
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

  function handleTopicChange(currentNode: TreeNode, selectedNodes: TreeNode[]) {
    let newData = getTreeData()
    setChecked(newData, currentNode)
    setTreeData(newData)

    const description = currentNode.description.trim()
    const hasTopic = currentNode.checked
    setTopicPlaceholder(hasTopic ? String.fromCharCode(160) : "-Choose topic-")
    const topic = hasTopic ? currentNode.topic : undefined
    console.log(`EntityLinks.handleTopicChange(): topicId = ${topic?.id} masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
    masterLinkContext.setMasterTopic(masterLinkContext, topic)

    const topicId = currentNode.topic.id;
    const label = currentNode.topic.label;
    const verb = currentNode.checked ? "selected" : "deselected"
    toast.success(`You ${verb} topic #${topicId} (${label} - ${description})`);
  }

  function handleMasterRecordKindChange(masterRecordKind: RecordKind) {
    // console.log(`EntityLink.handleMasterRecordKindChange(${masterRecordKind}): masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
    masterLinkContext.setMasterRecordKind(masterLinkContext, selectedRecordsContext, masterRecordKind)
  }

  // console.log(`EntityLinks(): masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
  // console.log(`EntityLinks(): selectedRecordsContext = ${JSON.stringify(selectedRecordsContext)}`)

  return (
    <Collapsible
      className="border shadow-lg rounded-md w-fit"
      open={isOpen}
      onOpenChange={setIsOpen}
    >
      <div>
        <div className="flex flex-row items-center space-x-4 px-4">
          <h4>
            Filter by Links
          </h4>
          <CollapsibleTrigger className="justify-self-end" asChild>
            <Button variant="ghost" size="default">
              {
                isOpen
                ? <ChevronUpIcon className="h-4 w-4" />
                : <ChevronDownIcon className="h-4 w-4" />
              }
              <span className="sr-only">Toggle</span>
            </Button>
          </CollapsibleTrigger>
        </div>
        <CollapsibleContent className="flex items-stretch m-4 gap-4">
          <fieldset className="border rounded-md">
            <legend className="text-sm">&nbsp;Topic Link&nbsp;</legend>
            <div className="flex flex-col m-2 gap-2">
              <DropdownTreeSelect
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
              <Textarea placeholder="-Topic description here-" disabled={true} value={masterLinkContext.masterTopicDescription ?? ''} />
              <p className="text-xs text-gray-500">{`Path: ${masterLinkContext.masterTopicPath ?? ""}`}</p>
            </div>
          </fieldset>
          <fieldset className="border rounded-md">
            <legend className="text-sm">&nbsp;Master Record Link&nbsp;</legend>
            <div className="flex flex-col m-2 gap-2">
              <RadioGroup
                className="flex flex-row"
                value={masterLinkContext.masterRecordKind}
                onValueChange={handleMasterRecordKindChange}>
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
              </RadioGroup>
              <div className="flex flex-row m-2 gap-4">
                <Label htmlFor="masterRecord">Master record:</Label>
                <Input id="masterRecord" readOnly={true} placeholder="-Master record description here-" value={getMasterRecordLabel()} />
                <Link href={getMasterRecordUri()}>
                  <Button className="bg-blue-500" disabled={masterLinkContext.masterRecordKind == "None"}>Go to</Button>
                </Link>
              </div>
              <Label className="text-xs text-gray-500">{
                masterLinkContext.masterRecordKind == "None"
                  ? "None selected: all lists behave independently"
                  : `Other lists show only records linked with the selected master ${masterLinkContext.masterRecordKind}`
              }</Label>
            </div>
          </fieldset>
        </CollapsibleContent>
      </div>
    </Collapsible>
  )
}
