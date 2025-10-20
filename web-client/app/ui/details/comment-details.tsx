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

import Comment from "@/app/model/Comment"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction } from "react"
import { useFormContext } from "react-hook-form"
import { CommentFieldValues } from "../validators/comment"
import { FormActionHandler } from "@/hooks/use-page-logic"
import TextareaEx from "../ext/textarea-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"
import { Label } from "@/components/ui/label"
import InputEx from "../ext/input-ex"
import { getRecordLabel } from "@/lib/utils"
import RecordKind from "@/app/model/RecordKind"

const logger = new LoggerEx(detail, "[CommentDetails] ")

export default function CommentDetails(
  {
    record,
    state,
  } : {
    record?: Comment
    state: DetailState
  }) {
  logger.debug("render")

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Comment Details&nbsp;</legend>
      <p className="pt-2 pb-4">&nbsp;&nbsp;{record ? `Details for selected Comment #${record?.id}` : "-Select a comment in the list above to see its details-"}</p>
      <StandardDetails recordKind="Comment" record={record} state={state} showLinkingDetails={false} />
      <div className="grid grid-cols-5 m-2 gap-2">
        <Label htmlFor="target" className="col-start-1">Target:</Label>
        <InputEx
          id="target"
          outerClassName="col-span-3"
          value={getRecordLabel(record?.target?.entityKind as RecordKind, record?.target) ?? ''}
          help="The target record in respect of which the comment was made"
        />
        <Label htmlFor="parent" className="col-start-1">Parent:</Label>
        <InputEx
          id="parent"
          outerClassName="col-span-3"
          value={getRecordLabel("Comment", record?.parent) ?? ''}
          help="The parent comment to which this comment is a reply"
        />
        <Label htmlFor="text" className="col-start-1">Text:</Label>
        <TextareaEx
          id="text"
          outerClassName="col-span-3"
          className="h-40 overflow-y-auto"
          disabled={!record}
          readOnly={true}
          value={record?.text}
          help="The body of the comment"
        />
      </div>
    </fieldset>
  )
}

CommentDetails.whyDidYouRender = true