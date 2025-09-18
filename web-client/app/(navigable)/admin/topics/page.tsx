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

import { Bars3BottomRightIcon } from '@heroicons/react/24/outline'
import TopicDetails from "@/app/ui/details/topic-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/topic-columns"
import IPage from "@/app/model/IPage"
import Topic from "@/app/model/Topic"
import { findTopic, setTopicFields, } from "@/lib/utils"
import { FormProvider } from "react-hook-form"
import { TopicFieldValues, TopicSchema } from "@/app/ui/validators/topic"
import { CREATE_TOPIC, DELETE_TOPIC, READ_TOPIC_HIERARCHY, READ_TOPICS, UPDATE_TOPIC } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { TopicInput, TopicQueryFilter } from '@/app/model/schema'
import TopicTableFilter from '@/app/ui/filter/topic-table-filter'
import useTopicQueryFilter from '@/hooks/use-topic-query-filter'
import { GlobalContext } from '@/lib/context'
import { useContext, useMemo } from 'react'
import { useQuery } from '@apollo/client'

function createFieldValues(topic?: Topic) : TopicFieldValues {
  return {
    path: topic?.path ?? '',
    label: topic?.label ?? '',
    description: topic?.description ?? '',
    parentId: topic?.parentId ?? ''
  }
}

function createInput(fieldValues: TopicFieldValues, id?: string) : TopicInput {
  return {
    id,
    label: fieldValues.label,
    description: fieldValues.description || null,
    parentId: fieldValues.parentId || null,
  }
}

function preparePage(rawPage: IPage<Topic> | undefined) {
  const result = rawPage
  ? {
    ...rawPage,
    content: [] as Topic[]
  }
  : undefined
  setTopicFields("", undefined, rawPage?.content, result?.content)
  return result
}

const EMPTY = [] as Topic[]

export default function Topics() {
  const { queries } = useContext(GlobalContext)
  const filter = queries["Topic"]?.filter as TopicQueryFilter
  const filterLogic = useTopicQueryFilter()
  const {
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    state,
    setMode,
    form,
    handleFormAction,
    refetch,
    loadingPathWithSearchParams,
  } = usePageLogic<Topic, TopicFieldValues, TopicInput, TopicQueryFilter>({
    recordKind: "Topic",
    schema: TopicSchema,
    manualPagination: false,
    manualSorting: false,
    readQuery: filter?.parentId === "-1" ? READ_TOPIC_HIERARCHY : READ_TOPICS,
    createMutation: CREATE_TOPIC,
    updateMutation: UPDATE_TOPIC,
    deleteMutation: DELETE_TOPIC,
    createFieldValues: createFieldValues,
    createInput,
    preparePage,
    findRecord: findTopic,
    filterLogic,
  })
  const allTopicsResult = useQuery(READ_TOPIC_HIERARCHY, {variables: {filter: {parentId: "-1"}}})
  const allTopicsPage = allTopicsResult.data?.topics
  const allTopics = useMemo(() => preparePage(allTopicsPage), [allTopicsPage])

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

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
        page={page}
        state={state}
        loading={loading}
        filterComponent={TopicTableFilter}
        manualPagination={false}
        manualSorting={false}
        onRowSelectionChange={handleRowSelectionChange}
        getSubRows={(row) => row.children}
        refetch={refetch}
        loadingPathWithSearchParams={loadingPathWithSearchParams}
      />
      <FormProvider {...form}>
        <TopicDetails
          record={selectedRecord}
          state={state}
          setMode={setMode}
          topics={allTopics?.content ?? EMPTY}
          onFormAction={handleFormAction}
        />
      </FormProvider>
    </main>
  )
}
