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

import ITrackedEntity from "@/app/model/ITrackedEntity"
import { Label } from "@/components/ui/label"
import LogDialog from "../log/log-dialog"
import RecordKind from "@/app/model/RecordKind"
import { DetailState } from "./detail-actions"
import { formatDateTime, getRecordLabel } from "@/lib/utils"
import InputEx from "../ext/input-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"
import CommentsDialog from "../dialog/comments-dialog"

const logger = new LoggerEx(detail, "[TrackingDetails] ")

export default function TrackingDetails(
  {
    recordKind,
    record,
    state
  } : {
    recordKind: RecordKind
    record: ITrackedEntity | undefined
    state: DetailState
  }) {
  logger.debug("render")

  return (
    <div className="w-full grid grid-cols-5 mb-2 gap-2">
      <Label htmlFor="db-id">Database ID:</Label>
      <InputEx
        id="db-id"
        type="text"
        readOnly={true}
        disabled={!record}
        value={String(record?.id ?? '')}
        help="The database identifier for the selected record"
      />
      <LogDialog
        className="col-start-5 place-items-center"
        disabled={!record || !state.allowRead || state.mode == "create"}
        recordKind={recordKind}
        recordId={record?.id ?? ''}
        recordLabel={getRecordLabel(recordKind, record) ?? ''}
        state={state}
      />
      <Label htmlFor="status" className="col-start-1">Status:</Label>
      <InputEx
        id="status"
        type="text"
        readOnly={true}
        disabled={!record}
        value={record?.status ?? ''}
        help="The record status"
      />
      <Label htmlFor="rating">Rating:</Label>
      <StarRatingBasicEx
        readOnly={true}
        value={record?.rating ?? 0}
        maxStars={5}
        iconSize={18}
        className="ml-2 w-full"
        help="A five-star rating for the entity, interpretation depends on entity kind."
      />
      <CommentsDialog
        className="col-start-5 place-items-center"
        disabled={!record || !state.allowRead || state.mode == "create" || recordKind === "Comment"}
        targetKind={recordKind}
        targetId={record?.id ?? ''}
        targetLabel={getRecordLabel(recordKind, record) ?? ''}
      />
      <Label htmlFor="created-by" className="col-start-1">Created by:</Label>
      <InputEx
        id="created-by"
        type="text"
        readOnly={true}
        disabled={!record}
        value={record?.createdByUser?.username ?? ''}
        help="The username of the user who created the record"
      />
      <Label htmlFor="created">Created on:</Label>
      <InputEx
        id="created"
        type="text"
        readOnly={true}
        disabled={!record}
        value={formatDateTime(record?.created)}
        help="The date and time at which the record was created"
      />
      <Label htmlFor="updated-by" className="col-start-1">Updated by:</Label>
      <InputEx
        id="updated-by"
        type="text"
        readOnly={true}
        disabled={!record}
        value={record?.updatedByUser?.username ?? ''}
        help="The username of the user who last updated the record"
      />
      <Label htmlFor="updated">Updated on:</Label>
      <InputEx
        id="updated"
        type="text"
        readOnly={true}
        disabled={!record}
        value={formatDateTime(record?.updated)}
        help="The date and time at which the record was last updated"
      />
    </div>
  )
}

TrackingDetails.whyDidYouRender = true