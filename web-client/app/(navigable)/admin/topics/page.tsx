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

import { Bars3BottomRightIcon } from '@heroicons/react/24/outline';
import TopicDetails from "@/app/ui/details/topic-details";
import DataTable from "@/app/ui/data-table/data-table";
import { columns, columnVisibility } from "@/app/ui/tables/topic-columns"
import IPage from "@/app/model/IPage";
import Topic from "@/app/model/Topic";
import { setTopicFields, } from "@/lib/utils";
import { FormProvider } from "react-hook-form"
import { TopicFieldValues, TopicSchema } from "@/app/ui/validators/topic";
import { QUERY_TOPIC_HIERARCHY } from "@/lib/graphql-queries";
import usePageLogic from "@/hooks/use-page-logic";
import { TopicQueryFilter } from '@/app/model/schema';

function copyToForm(topic?: Topic) {
  return {
    path: topic?.path ?? '',
    label: topic?.label ?? '',
    description: topic?.description ?? '',
    parentId: topic?.parentId ?? ''
  }
}

function copyFromForm(topic: Topic, fieldValues: TopicFieldValues) {
  topic.path = fieldValues.path ?? null
  topic.label = fieldValues.label
  topic.description = fieldValues.description ?? null
  topic.parentId = fieldValues.parentId ?? null
}

function prepareFilter(filter: TopicQueryFilter) {
  filter.parentId = "-1"
  filter.recursive = false
}

function preparePage(rawPage?: IPage<Topic>) {
  const result = rawPage
  ? {
    ...rawPage,
    content: [] as Topic[]
  }
  : undefined
  setTopicFields("", undefined, rawPage?.content, result?.content)
  return result
}

function findRecord(topics?: Topic[], topicId?: string | null) : Topic | undefined {
  if (topics) {
    let topic: Topic | undefined
    for (topic of topics) {
      if (topic?.id == topicId)
        return topic
      if (topic?.children) {
        topic = findRecord(topic.children, topicId)
        if (topic)
          return topic
      }
    }
  }
  return undefined
}

export default function Topics() {
  const {
    search,
    setSearch,
    pagination,
    setPagination,
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    form,
    handleFormAction,
  } = usePageLogic<Topic, TopicQueryFilter, TopicFieldValues>({
    recordKind: "Topic",
    schema: TopicSchema,
    listQuery: QUERY_TOPIC_HIERARCHY,
    copyToForm,
    copyFromForm,
    prepareFilter,
    preparePage,
    findRecord,
  })

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
        loading={loading}
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
