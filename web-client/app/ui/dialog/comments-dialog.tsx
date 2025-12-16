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
import { RecordKind } from "@/app/model/RecordKinds"
import { Button } from "@/components/ui/button"
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet"
import ButtonEx from "../ext/button-ex"
import { cn, formatDate, toDate } from "@/lib/utils"
import { dialog, LoggerEx } from "@/lib/logger"
import { CREATE_COMMENT, DELETE_COMMENT, READ_OWNED_COMMENTS, UPDATE_COMMENT } from "@/lib/graphql-queries"
import { useMutation, useQuery } from "@apollo/client/react"
import { CommentQueryFilter } from "@/app/model/schema"
import { KeyboardEvent, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react"
import { toast } from "sonner"
import IPage from "@/app/model/IPage"
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card"
import useAuth from "@/hooks/use-auth"
import { Textarea } from "@/components/ui/textarea"
import {
  CircleChevronDownIcon,
  SearchIcon,
  SendHorizontalIcon,
  PencilIcon,
  ReplyIcon,
  Trash2Icon,
  XIcon,
  MessagesSquareIcon
} from "lucide-react"
import User from "@/app/model/User"
import Spinner from "../misc/spinner"
import { format } from "date-fns"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import CommentDialogFilter from "../filter/comment-dialog-filter"
import { GlobalContext, QueryState } from "@/lib/context"
import { Toggle } from "@/components/ui/toggle"
import { QueryResult } from "@/lib/graphql-utils"

const logger = new LoggerEx(dialog, "[CommentsDialog] ")

const EMPTY = [] as Comment[]
const pageSort = {
  sort: {
    orders: [{
      property: "created",
      direction: "ASC"
    }]
  }
}

// Comments remain editable for 48 hours.
const EDITABLE_INTERVAL = 48 * 60 * 60 * 1000

function formatName(user: User | null | undefined, includeUsername: boolean) {
  let result
  if (user) {
    result = `${user.firstName} ${user.lastName}`
    if (includeUsername)
      result += ` (${user.username})`
  } else {
    result = ''
  }
  return result
}

function formatTime(date: string | Date | null | undefined) {
  const dateObj = toDate(date) ?? new Date("1970-01-01T00:00:00.000UTC")
  return format(dateObj, "HH:mm")
}

export default function CommentsDialog({
  className,
  disabled,
  targetKind,
  targetId,
  targetLabel,
}: {
  className?: string
  disabled: boolean
  targetKind: RecordKind
  targetId: string
  targetLabel: string
}) {
  logger.debug("render: targetKind='%s', targetId='%s', targetLabel='%s'", targetKind, targetId, targetLabel)

  const { user, hasAuthority } = useAuth()
  const [isOpen, setIsOpen] = useState(false)
  const [commentFilterOpen, setCommentFilterOpen] = useState(false)
  const [comment, setComment] = useState<Comment>()
  const [parent, setParent] = useState<Comment>()
  const [text, setText] = useState('')
  const [showScroller, setShowScroller] = useState(false)
  const [renderComplete, setRenderComplete] = useState(false)
  const lastCommentRef = useRef<HTMLDivElement>(null)
  const commentsRef = useRef<HTMLDivElement>(null)
  const editorRef = useRef<HTMLTextAreaElement>(null)

  const { queries, setFilter } = useContext(GlobalContext)
  const query = queries.Comment as QueryState<CommentQueryFilter>
  const { filter } = query

  const readResult = useQuery(
    READ_OWNED_COMMENTS,
    {
      variables: {
        filter,
        pageSort,
      },
    }
  )
  const [createOp, createResult] = useMutation(CREATE_COMMENT, { refetchQueries: [READ_OWNED_COMMENTS] })
  const [updateOp, updateResult] = useMutation(UPDATE_COMMENT, { refetchQueries: [READ_OWNED_COMMENTS] })
  const [deleteOp, deleteResult] = useMutation(DELETE_COMMENT, { refetchQueries: [READ_OWNED_COMMENTS] })
  const error = readResult.error || createResult.error || updateResult.error || deleteResult.error
  const loading = readResult.loading || createResult.loading || updateResult.loading || deleteResult.loading
  if (error) {
    // TODO: display user-friendly error notification
    toast.error(`Operation failed:\n\n${error.message}`)
    logger.error("Operation failed: %o", error)
  }
  const refetch = useCallback(() => {
    readResult.refetch({
      filter,
      pageSort,
    })
  }, [readResult, filter, pageSort])
  const prevTargetId = useRef<string>(undefined)
  useEffect(() => {
    if (isOpen && targetKind === "Comment") {
      logger.debug("effect1: closing Comments dialog as Comments page is now displayed")
      setIsOpen(false)
    }
  }, [targetKind])
  useEffect(() => {
    if (targetId !== prevTargetId.current) {
      logger.trace("effect2: targetId has changed from '%s' to '%s'", prevTargetId.current, targetId)
      prevTargetId.current = targetId
      if (targetKind !== "Comment") {
        const newFilter = {
          ...filter,
          targetId
        } as CommentQueryFilter
        setFilter("Comment", newFilter)
      }
    }
  }, [prevTargetId, targetId])
  useEffect(() => {
    logger.trace("effect3: filter has changed to %o", filter)
    if (targetKind !== "Comment")
      refetch()
  }, [filter, refetch])

  const comments = targetId && (readResult.data as QueryResult<IPage<Comment>>)?.comments //
    ? ((readResult.data as QueryResult<IPage<Comment>>)?.comments).content //
    : EMPTY

  const commentCreatorRef = useRef('')
  commentCreatorRef.current = ''
  const needsUser = useCallback((comment: Comment) => {
    const commentUsername = comment.createdByUser?.username ?? ''
    if (commentUsername !== commentCreatorRef.current || !commentCreatorRef.current) {
      commentCreatorRef.current = commentUsername
      return commentUsername !== user?.username
    }
    return false
  }, [commentCreatorRef, user])
  // NOTE: this memoized current year value never changes, so will be incorrect if the session ever spans a year-end.
  const currentYear = useMemo(() => new Date().getFullYear(), [])
  const dateRef = useRef('')
  dateRef.current = ''
  const needsDate = useCallback((comment: Comment) => {
    const commentDate = formatDate(comment.created)
    if (dateRef.current !== commentDate) {
      dateRef.current = commentDate
      commentCreatorRef.current = ''
      return true
    }
    return false
  }, [dateRef, commentCreatorRef])
  const getDate = useCallback((comment: Comment) => {
    const date = toDate(comment.created) || new Date(0)
    dateRef.current = formatDate(date)
    const pattern = date.getFullYear() == currentYear ? "dd MMMM" : "dd MMMM yyyy"
    const dateStr = format(date, pattern)
    return dateStr
  }, [currentYear, dateRef])
  // For some unfathomable reason, the expression doesn't work if used inline, as in disabled={!!(comment || parent)}
  const isEditing = useCallback(() => !!(comment || parent), [comment, parent])
  const needsScroller = useCallback(() => {
    // Show the scroller when the comments element is scrolled upwards by a quarter or more of the element height.
    const commentsElt = commentsRef.current
    return (commentsElt && commentsElt.scrollTop + 1.25 * commentsElt.offsetHeight < commentsElt.scrollHeight) ?? false
  }, [commentsRef])
  const scrollToBottom = useRef(true)
  const requestScrollToBottom = useCallback(() => {
    scrollToBottom.current = true
  }, [scrollToBottom])
  const scroll = useCallback((commentId?: string) => {
    // If a comment ID is specified, smoothly scroll that comment into view. Otherwise, scroll straight to the bottom.
    if (commentId) {
      const commentElt = document.getElementById(`comment-${commentId}`)
      if (commentElt) {
        commentElt.scrollIntoView({ behavior: 'smooth' })
        function animationListener() {
          commentElt?.classList.remove("animation-pulse-bg")
          commentElt?.removeEventListener("animationend", animationListener)
        }
        commentElt.addEventListener("animationend", animationListener)
        commentElt.classList.add("animation-pulse-bg")
      }
    } else {
      const commentsElt = commentsRef.current
      if (commentsElt) {
        commentsElt.scrollTop = commentsElt.scrollHeight
        scrollToBottom.current = false
      }
    }
  }, [commentsRef, scrollToBottom])
  const canComment = useCallback(() => {
    return hasAuthority("COM")
  }, [user, hasAuthority])
  const canEdit = useCallback((comment: Comment) => {
    return !!(hasAuthority("COM") && comment && comment.status !== "Deleted" &&
      comment.createdByUser?.username === user?.username) &&
      Date.now() < (toDate(comment.created)?.getTime() ?? 0) + EDITABLE_INTERVAL
  }, [user, hasAuthority])
  const canReply = useCallback((parent: Comment) => {
    return !!(hasAuthority("COM") && parent && parent.status !== "Deleted")
  }, [hasAuthority])
  const canSave = useCallback(() => {
    return text.length != 0 && text !== (comment?.text ?? '')
  }, [text, comment])
  // const handleShowLog = useCallback((comment: Comment) => {
  //   // TODO: implement Show Comment Log
  // }, [])
  const handleDelete = useCallback((comment: Comment) => {
    const label = `Comment#${comment.id}`
    if (confirm(`Confirm delete ${label}?`)) {
      toast.info("Deleting " + label)
      logger.trace("handleDelete comment: %o", comment)
      deleteOp({
        variables: {
          id: comment.id
        }
      })
    }
  }, [deleteOp])
  const handleEdit = useCallback((comment: Comment) => {
    if (parent == null) {
      logger.trace("handleEdit comment: %o", comment)
      setComment(comment)
      setText(comment?.text ?? '')
      editorRef.current?.focus()
    }
  }, [parent])
  const handleReply = useCallback((parent: Comment) => {
    if (comment == null) {
      logger.trace("handleReply parent: %o", parent)
      setParent(parent)
      setText('')
      editorRef.current?.focus()
    }
  }, [comment])
  const handleCancel = useCallback(() => {
    logger.trace("handleCancel comment: %o, parent: %o", comment, parent)
    setComment(undefined)
    setParent(undefined)
    setText('')
  }, [comment, parent])
  const filtered = useMemo(() => {
    return !!(filter.text || filter.status || filter.userId || filter.from || filter.to)
  }, [filter])
  const handleSave = useCallback(() => {
    if (canSave()) {
      if (comment) {
        // Editing an existing comment.
        logger.trace("handleSave update comment #%s with text: '%s'", comment.id, text)
        updateOp({
          variables: {
            input: {
              id: comment.id,
              targetId: targetId,
              parentId: comment.parent?.id,
              text
            }
          },
          onCompleted: () => {
            handleCancel()
            scroll(comment.id)
          }
        })
      } else if (parent) {
        // Creating a reply to an existing comment.
        logger.trace("handleSave create reply to comment #%s with text: '%s'", parent.id, text)
        createOp({
          variables: {
            input: {
              targetId: targetId,
              parentId: parent.id,
              text
            }
          },
          onCompleted: () => {
            handleCancel()
            requestScrollToBottom()
          }
        })
      } else {
        // Creating a new comment.
        logger.trace("handleSave create comment with text: '%s'", text)
        createOp({
          variables: {
            input: {
              targetId: targetId,
              text
            }
          },
          onCompleted: () => {
            handleCancel()
            requestScrollToBottom()
          }
        })
      }
    }
  }, [targetId, comment, parent, text, canSave, handleCancel, scroll])
  const handleKeyDown = useCallback((e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.ctrlKey && e.key === "Enter")
      handleSave()
  }, [handleSave])
  const handleTextChange = useCallback((value: string) => {
    setText(value)
  }, [])
  const handleScroll = useCallback(() => {
    logger.trace("handleScroll")
    setShowScroller(needsScroller())
  }, [needsScroller])

  const setLastCommentRef = useCallback((elt: HTMLDivElement) => {
    if (elt) {
      lastCommentRef.current = elt
      setRenderComplete(true);
    }
  }, [lastCommentRef])

  useEffect(() => {
    if (renderComplete && lastCommentRef.current && scrollToBottom.current) {
      logger.trace("Effect2: all comments rendered - scrolling to bottom");
      scroll()
    }
  }, [comments, renderComplete, scroll])

  return (
    <Sheet modal={false} open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>
        <ButtonEx
          outerClassName={cn("place-self-center", className)}
          className="w-35 bg-blue-500 text-md"
          disabled={disabled}
          onClick={() => setIsOpen(true)}
          help={
            targetId
              ? `Show comments for ${targetLabel}`
              : `No ${targetKind} selected`
          }
        >
          Comments...
        </ButtonEx>
      </SheetTrigger>
      <SheetContent className="w-100" onInteractOutside={(e) => e.preventDefault()}>
        <Spinner loading={loading} className="absolute inset-0 bg-black/20 z-50" />
        <SheetHeader className="border-b">
          <SheetTitle><MessagesSquareIcon className="inline" />&nbsp;Comments</SheetTitle>
          {/* <SheetDescription></SheetDescription> */}
          <Collapsible
            open={commentFilterOpen}
            onOpenChange={setCommentFilterOpen}
          >
            <div className="flex flex-row max-w-full overflow-hidden items-center space-x-4">
              <span className="grow text-nowrap truncate" title={targetLabel}>
                {
                  targetId
                    ? targetLabel
                    : `-No ${targetKind} selected-`
                }
              </span>
              <CollapsibleTrigger className="justify-self-end" asChild>
                {/* N.B. Wrapped in a <div> to prevent styling clash between Collapsible Trigger and Toggle. */}
                <div>
                  <Toggle
                    variant="outline"
                    pressed={commentFilterOpen}
                    onPressedChange={setCommentFilterOpen}
                    title="Toggle filter controls"
                  >
                    <SearchIcon />
                  </Toggle>
                </div>
              </CollapsibleTrigger>
            </div>
            <CollapsibleContent className="max-w-full">
              <CommentDialogFilter
                targetId={targetId}
                refetch={refetch}
              />
            </CollapsibleContent>
          </Collapsible>
        </SheetHeader>
        <div ref={commentsRef} className="grid auto-rows-min overflow-y-auto gap-4 px-4" onScroll={handleScroll}>
          <p className="sticky top-0 text-center bg-white/90">
            {`${comments.length} comment${comments.length != 1 ? "s" : ""}${filtered ? " (filtered)" : ""}`}
          </p>
          {
            comments.map((comment, index) => (
              <div key={comment.id} className="grid gap-2">
                {
                  needsDate(comment)
                    ? <span className="bg-lime-700 text-white px-2 justify-self-center rounded-full">
                      {getDate(comment)}
                    </span>
                    : null
                }
                <Card
                  id={`comment-${comment.id}`}
                  ref={index === comments.length - 1 ? setLastCommentRef : null}
                  className={cn(comment.createdByUser?.username === user?.username ? "justify-self-end bg-lime-100" : "justify-self-start bg-cyan-100", "p-2 gap-2 w-15/16")}
                >
                  <CardHeader className="px-2">
                    {
                      needsUser(comment)
                        ? <span className="font-bold text-blue-600 text-nowrap truncate">
                          {formatName(comment.createdByUser, false)}
                        </span>
                        : null
                    }
                    {
                      comment.parent
                        ? <div
                          className="flex flex-col p-2 cursor-pointer max-w-full overflow-hidden border-blue-400 border-l-4 rounded-lg bg-blue-100"
                          title="Scroll to the parent comment"
                          onClick={() => scroll(comment.parent?.id)}
                        >
                          <span className="font-bold text-blue-600">{formatName(comment.parent.createdByUser, false)}</span>
                          <span className="font-normal text-nowrap truncate">{comment.parent.text}</span>
                        </div>
                        : null
                    }
                    {/* <CardTitle></CardTitle> */}
                  </CardHeader>
                  <CardContent className="px-2">
                    <p className={cn(comment.status === "Deleted" ? "text-center text-red-400" : '')}>{comment.text}</p>
                  </CardContent>
                  <CardFooter className="px-2 justify-end gap-1">
                    <span className="text-xs text-gray-400">#{comment.id}</span>
                    <span
                      className="grow text-end text-xs text-gray-400"
                    >
                      {formatTime(comment.created)} {comment.updated ? "(edited)" : ''}
                    </span>
                    {/* <Button
                      className="cursor-pointer w-auto h-auto shrink has-[>svg]:px-0"
                      type="button"
                      variant="ghost"
                      title="Show comment log"
                      disabled={isEditing()}
                      onClick={() => handleShowLog(comment)}
                    >
                      <FileClock className="stroke-blue-400 stroke-2 size-4" />
                    </Button> */}
                    {
                      canEdit(comment)
                        ? <Button
                          className="cursor-pointer w-auto h-auto shrink has-[>svg]:px-0"
                          type="button"
                          variant="ghost"
                          title="Delete this comment"
                          disabled={isEditing()}
                          onClick={() => handleDelete(comment)}
                        >
                          <Trash2Icon className="stroke-blue-400 stroke-2 size-4" />
                        </Button>
                        : null
                    }
                    {
                      canEdit(comment)
                        ? <Button
                          className="cursor-pointer w-auto h-auto shrink has-[>svg]:px-0"
                          type="button"
                          variant="ghost"
                          title="Edit this comment"
                          disabled={isEditing()}
                          onClick={() => handleEdit(comment)}
                        >
                          <PencilIcon className="stroke-blue-400 stroke-2 size-4" />
                        </Button>
                        : null
                    }
                    {
                      canReply(comment)
                        ? <Button
                          className="cursor-pointer w-auto h-auto shrink has-[>svg]:px-0"
                          type="button"
                          variant="ghost"
                          title="Reply to this comment"
                          disabled={isEditing()}
                          onClick={() => handleReply(comment)}
                        >
                          <ReplyIcon className="stroke-blue-400 stroke-3 size-4" />
                        </Button>
                        : null
                    }
                  </CardFooter>
                </Card>
              </div>
            ))
          }
          {
            showScroller
              ? <Button
                className="shrink absolute bottom-40 right-3 has-[>svg]:px-0"
                type="button"
                variant="ghost"
                title="Scroll to the bottom"
                onClick={() => scroll()}
              >
                <CircleChevronDownIcon className="size-12 fill-white stroke-1 stroke-gray-500" />
              </Button>
              : null
          }
        </div>
        <SheetFooter>
          <div className="flex gap-2">
            {
              canComment()
                ? (
                  <div className="flex flex-col w-full">
                    {
                      comment && !parent
                        ? <div className="flex gap-4 border-t w-full">
                          <PencilIcon className="stroke-blue-400 stroke-3 size-8" />
                          <div className="flex flex-col grow">
                            <span className="font-bold text-blue-400">Edit comment</span>
                            <span>{comment.text}</span>
                          </div>
                          <Button className="" variant="ghost" title="Cancel" onClick={handleCancel}>
                            <XIcon className="size-6" />
                          </Button>
                        </div>
                        : null
                    }
                    {
                      parent && !comment
                        ? <div className="flex gap-4 border-t w-full">
                          <ReplyIcon className="stroke-blue-400 stroke-4 size-8" />
                          <div className="flex flex-col grow">
                            <span className="font-bold text-blue-400">Reply to {formatName(parent.createdByUser, false)}</span>
                            <span className="text-nowrap overflow-hidden">{parent.text}</span>
                          </div>
                          <Button className="" variant="ghost" title="Cancel" onClick={handleCancel}>
                            <XIcon className="size-6" />
                          </Button>
                        </div>
                        : null
                    }
                    <div className="flex">
                      <Textarea
                        ref={editorRef}
                        placeholder="Enter a comment"
                        value={text}
                        onKeyDown={handleKeyDown}
                        onChange={e => handleTextChange(e.target.value)}
                      />
                      <Button
                        className="w-14 h-14"
                        variant="ghost"
                        title="Send (Ctrl+Enter)"
                        disabled={!canSave()}
                        onClick={handleSave}
                      >
                        <SendHorizontalIcon className="text-blue-400 fill-blue-100 size-8" />
                      </Button>
                    </div>
                  </div>
                )
                : null
            }
          </div>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}