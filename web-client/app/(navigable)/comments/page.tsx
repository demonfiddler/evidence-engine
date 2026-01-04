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

import CommentDetails from "@/app/ui/details/comment-details"
import DataTable from "@/app/ui/data-table/data-table"
import { columns } from "@/app/ui/tables/comment-columns"
import Comment from "@/app/model/Comment"
import { READ_COMMENTS } from "@/lib/graphql-queries"
import CommentTableFilter from "@/app/ui/filter/comment-table-filter"
import { BaseEntityInput, CommentQueryFilter } from "@/app/model/schema"
import usePageLogic from "@/hooks/use-page-logic"
import { CommentFieldValues } from '@/app/ui/validators/comment'
import useCommentQueryFilter from '@/hooks/use-comment-query-filter'
import { LoggerEx, page } from '@/lib/logger'
import { MessagesSquareIcon } from 'lucide-react'

const logger = new LoggerEx(page, "[Comments] ")

function createFieldValues(record?: Comment) : CommentFieldValues {
  return {
    rating: record?.rating ?? 0,
    targetId: record?.target?.id ?? '',
    parentId: record?.parent?.id ?? '',
    text: record?.text ?? '',
  }
}

export default function Comments() {
  logger.debug("render")

  const filterLogic = useCommentQueryFilter()
  const {
    loading,
    page,
    selectedRecord,
    handleRowSelectionChange,
    state,
    // setMode,
    // form,
    // handleFormAction,
    refetch,
    loadingPathWithSearchParams,
  } = usePageLogic<Comment, CommentFieldValues, BaseEntityInput, CommentQueryFilter>({
    recordKind: "Comment",
    manualPagination: true,
    manualSorting: true,
    readQuery: READ_COMMENTS,
    createFieldValues,
    filterLogic,
  })

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <MessagesSquareIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Comments</h1>
      </div>
      <DataTable<Comment, unknown>
        recordKind="Comment"
        defaultColumns={columns}
        page={page}
        loading={loading}
        filterComponent={CommentTableFilter}
        manualPagination={true}
        manualSorting={true}
        onRowSelectionChange={handleRowSelectionChange}
        refetch={refetch}
        loadingPathWithSearchParams={loadingPathWithSearchParams}
      />
      <CommentDetails
        record={selectedRecord}
        state={state}
        // setMode={setMode}
        // onFormAction={handleFormAction}
      />
    </main>
  )
}

Comments.whyDidYouRender = true