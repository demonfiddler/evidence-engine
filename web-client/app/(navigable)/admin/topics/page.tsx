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

// import type { Metadata } from "next";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useImmerReducer } from "use-immer";
import { Bars3BottomRightIcon } from '@heroicons/react/24/outline';

import TopicDetails from "@/app/ui/details/topic-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/topic-columns"
import IPage from "@/app/model/IPage";
import Topic from "@/app/model/Topic";
import { SelectedRecordsContext } from "@/lib/context";
import { FormAction, MutationAction, SearchSettings, setTopicFields, } from "@/lib/utils";
import { useForm, FormProvider } from "react-hook-form"
import { TopicFormFields, TopicSchema } from "@/app/ui/validators/topic";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import { QUERY_TOPIC_HIERARCHY } from "@/lib/graphql-queries";
import { useQuery } from "@apollo/client";
import { TopicQueryFilter } from "@/app/model/schema";

// export const metadata: Metadata = {
//   title: "Topics",
//   description: "Public topics, open letters & petitions",
// };

function copyToForm(topic?: Topic) {
  return {
    path: topic?.path ?? '',
    label: topic?.label ?? '',
    description: topic?.description ?? '',
    parentId: topic?.parentId ?? ''
  }
}

function copyFromForm(topic: Topic, formValue: TopicFormFields) {
  topic.path = formValue.path ?? null
  topic.label = formValue.label
  topic.description = formValue.description ?? null
  topic.parentId = formValue.parentId ?? null
}

function findTopic(topics?: Topic[], topicId?: string | null) : Topic | undefined {
  if (topics) {
    let topic: Topic | undefined
    for (topic of topics) {
      if (topic?.id == topicId)
        return topic
      if (topic?.children) {
        topic = findTopic(topic.children, topicId)
        if (topic)
          return topic
      }
    }
  }
  return undefined
}

export default function Topics() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [search, setSearch] = useState<SearchSettings>({/*advancedSearch: false, */showOnlyLinkedRecords: false} as SearchSettings)
  const [selectedRecordId, setSelectedRecordId] = useState<string|undefined>(selectedRecordsContext.Topic?.id)
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 0 });
/*  const pageReducer = useCallback((draft: IPage<Topic>, action: MutationAction<FormAction, TopicFormFields>) => {
    if (action.value.parentId == "0")
      action.value.parentId = ""
    let topic = findTopic(draft.content, selectedRecordId)
    switch (action.command) {
      case "create":
        topic = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedRecordId(topic.id)
        copyFromForm(topic, action.value)
        draft.content.push(topic)
        break
      case "update":
        if (topic) {
          if (action.value.parentId != topic.parentId) {
            const oldParent = findTopic(draft.content, topic.parentId)
            const newParent = findTopic(draft.content, action.value.parentId)
            if (oldParent != newParent) {
              const oldSiblings = oldParent?.children ?? draft.content
              const newSiblings = newParent?.children ?? draft.content
              const oldChildIdx = oldSiblings.findIndex(t => t.id == topic?.id)
              if (oldChildIdx != -1)
                oldSiblings.splice(oldChildIdx, 1)
              newSiblings.push(topic)
              const newParentPath = newParent ? newParent.path + " > " : ""
              action.value.path = `${newParentPath}${action.value.label}`
            }
          }
          copyFromForm(topic, action.value)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (topic) {
          // FIXME: dangling children should be promoted to the old parent or top level
          const parent = findTopic(draft.content, topic.parentId)
          const siblings = parent?.children ?? draft.content
          const idx = siblings.findIndex(t => t.id == topic?.id)
          if (idx != -1)
            siblings.splice(idx, 1)
        }
        break
    }
  }, [selectedRecordId, setSelectedRecordId])*/
  // const [page, pageDispatch] = useImmerReducer(pageReducer, rawPage as unknown as IPage<Topic>)
  const filter = useMemo(() => {
    const filter: TopicQueryFilter = {
      parentId: "-1",
      status: search.status ? [search.status] : undefined,
      text: search.text,
    }
    if (search.text)
      filter.advancedSearch = search.advancedSearch
    console.log(`Topics effect: filter = ${JSON.stringify(filter)}`)
    return filter
  }, [search])

  const pageSort = useMemo(() => {
    const pageSort = {
      pageNumber: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    console.log(`Topics effect: pageSort = ${JSON.stringify(pageSort)}`)
    return pageSort
  }, [pagination])

  const result = useQuery(
    QUERY_TOPIC_HIERARCHY,
    {
      variables: {
        filter,
        pageSort
      },
    }
  )

  // Whenever filter or pagination changes, ask Apollo to refetch
  useEffect(() => {
    console.log(`Topics effect: search = ${JSON.stringify(search)}`)
    result.refetch({
      filter,
      pageSort
    });
  }, [filter, pageSort]);

  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}, result.data = ${JSON.stringify(result.data)}`)
  // console.log(`result.loading = ${result.loading}, result.error = ${JSON.stringify(result.error)}`)
  const rawPage = result.data?.topics as IPage<Topic>
  const page = useMemo(() => {
    const result = rawPage ? {
      ...rawPage,
      content: []
    } : undefined
    setTopicFields("", undefined, rawPage?.content, result?.content)
    return result
  }, [rawPage])

  const getSelectedRecord = useCallback((id?: string) => {
    return findTopic(page?.content, id)
  }, [page])
  const selectedRecord = getSelectedRecord(selectedRecordId)
  const origFormValue = useMemo(() => copyToForm(selectedRecord), [selectedRecord])
  const form = useForm<TopicFormFields>({
    resolver: standardSchemaResolver(TopicSchema),
    mode: "onChange",
    values: origFormValue
  })

  const handleFormAction = useCallback((command: FormAction, formValue: TopicFormFields) => {
    // console.log(`handleFormAction: formValue = ${JSON.stringify(formValue)}`)
    switch (command) {
      case "create":
        // TODO: invoke mutation: createTopic
        break
      case "update":
        // TODO: invoke mutation: updateTopic
        break
      case "delete":
        // TODO: invoke mutation: deleteTopic
        break
        // OLD: pageDispatch({command: command, value: formValue})
      case "reset":
        form.reset(origFormValue)
        break
    }
  }, [form, /*pageDispatch, */selectedRecord])

  const handleRowSelectionChange = useCallback((recordId?: string) => {
    setSelectedRecordId(recordId)
    const topic = getSelectedRecord(recordId)
    form.reset(copyToForm(topic))
  }, [setSelectedRecordId, getSelectedRecord, form])

  // console.log(`selectedRecordId = ${selectedRecordId}, selectedRecord = ${JSON.stringify(selectedRecord)}`)
  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <Bars3BottomRightIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Topics</h1>
      </div>
      <DataTable<Topic, unknown>
        recordKind="Topic"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        loading={result.loading}
        pagination={pagination}
        onPaginationChange={setPagination}
        search={search}
        onSearchChange={setSearch}
        onRowSelectionChange={handleRowSelectionChange}
        getSubRows={(row) => row.children}
      />
      <FormProvider {...form}>
        <TopicDetails record={selectedRecord} topics={page?.content ?? []} onFormAction={handleFormAction} />
      </FormProvider>
    </main>
  )
}
