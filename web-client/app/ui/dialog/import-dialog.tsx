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

import RecordKind from "@/app/model/RecordKind";
import useAuth from "@/hooks/use-auth";
import { useCallback, useContext, useState } from "react";
import { Button } from "@/components/ui/button"
import ButtonEx from "../ext/button-ex";
import {
  Carousel,
  CarouselApi,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"
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
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Dropzone, DropzoneContent, DropzoneEmptyState } from '@/components/ui/shadcn-io/dropzone';
import { ImportAccept } from "../data-table/data-table-filter";
import { GlobalContext } from "@/lib/context";
import { toast } from "sonner";
import { dialog, LoggerEx } from "@/lib/logger";
import { CheckIcon, CircleAlertIcon, CircleXIcon, CopyMinusIcon, InfoIcon, UploadIcon, XIcon } from "lucide-react";

const logger = new LoggerEx(dialog, "[ImportDialog] ")

type ImportDialogProps = {
  recordKind : RecordKind,
  accept?: ImportAccept
}

type ResultEnum = "imported" | "duplicate" | "error"
type SeverityEnum = "info" | "warning" | "error"
type ImportMessage = {
  lineNum: number
  severity: SeverityEnum
  text: string
}
type ImportedRecord = {
  id?: number
  label?: string
  result: ResultEnum
  messages: ImportMessage[]
}

export default function ImportDialog({recordKind, accept} : ImportDialogProps) {
  const { jwtToken, hasAuthority } = useAuth()
  const [isOpen, setIsOpen] = useState(false)
  const [error, setError] = useState("")
  const [files, setFiles] = useState<File[] | undefined>()
  const [importedRecords, setImportedRecords] = useState<ImportedRecord[]>([])
  const globalContext = useContext(GlobalContext)
  const { queries, setFilter } = globalContext
  const [api, setApi] = useState<CarouselApi>()

  const getHref = useCallback(() => {
    const href = `${process.env.NEXT_PUBLIC_SERVER_URL}/rest/import/${recordKind.toLowerCase()}s`
    console.log(`href='${href}'`)
    return `${process.env.NEXT_PUBLIC_SERVER_URL}/rest/import/${recordKind.toLowerCase()}s`
  }, [recordKind])

  const handleDrop = useCallback((files: File[]) => {
    logger.trace("Files dropped: %o", files)
    setFiles(files)
    setError("")
    setImportedRecords([])
  }, [setFiles, setError])

  const handleError = useCallback((error : Error) => {
    logger.error("Error: %o", error)
    setError(error.message)
  }, [setError])

  const handleImport = useCallback(() => {
    if (!files || files.length == 0 || !jwtToken)
      return

    setImportedRecords([])
    const headers : HeadersInit = {
      "Authorization": `Bearer ${jwtToken}`,
    }
    const [file] = files
    const formData = new FormData()
    formData.append("file", file)
    fetch(getHref(), {
      method: "POST",
      headers,
      // Streaming doesn't work - backend not configured to support it?
      // body: file.stream(),
      // duplex: "half",
      // body: file,
      body: formData,
    }) //
    .then(response => {
      if (!response.ok)
        throw new Error(`Upload failed: ${response.status} (${response.statusText})`)
      return response.json()
    }) //
    .then(importedRecords => {
      logger.trace("ImportedRecords: %o", importedRecords)
      setError("")
      setImportedRecords(importedRecords)
      api?.scrollTo(1)
      toast.info(`${(importedRecords as ImportedRecord[]).filter(rec => rec.result === "imported").length} ${recordKind}s imported`)
    }) //
    .catch(error => setError(error.message))
  }, [files, jwtToken, getHref, api])

  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(JSON.stringify(importedRecords, null, 2))
    toast.info("Messages copied to clipboard (JSON format)")
  }, [])

  const handleClose = useCallback(() => {
    setFiles([])
    setImportedRecords([])
  }, [])

  const handleNavigate = useCallback((id?: number) => {
    setIsOpen(false)
    setFilter(recordKind, {
      ...queries[recordKind]?.filter,
      recordId: id
    })
  }, [queries, recordKind, setFilter])

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <ButtonEx
          variant="ghost"
          disabled={!accept || !jwtToken || !hasAuthority("CRE")}
          help="Import (upload) a file"
          title="Import (upload) a file">
          <UploadIcon />
        </ButtonEx>
      </DialogTrigger>
      <DialogContent className="flex flex-col items-center w-2/3 h-2/3 min-h-0 overflow-hidden">
        <DialogHeader>
          <DialogTitle className="text-center"><UploadIcon className="inline" />&nbsp;Import {recordKind}s</DialogTitle>
          <DialogDescription>
            Import {recordKind}s from a file
          </DialogDescription>
          <p className="text-red-600">{error}</p>
        </DialogHeader>
        <Carousel setApi={setApi} className="flex flex-col w-7/8 h-full min-h-0 [&>div[data-slot=carousel-content]]:grow">
          <CarouselContent className="w-full h-full max-h-full">
            <CarouselItem className="h-full max-h-full">
              <Dropzone
                className="h-full max-h-full"
                accept={accept}
                maxFiles={1}
                onDrop={handleDrop}
                onError={handleError}
                src={files}
              >
                <DropzoneEmptyState />
                <DropzoneContent />
              </Dropzone>
            </CarouselItem>
            <CarouselItem className="w-full h-full max-h-full">
              <div className="w-full h-full min-h-0 max-h-full overflow-y-auto relative">
                <Table className="table-fixed w-full max-h-full border caption-top">
                  <colgroup>
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[25%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[10%]" />
                    <col className="w-[25%]" />
                  </colgroup>
                  <TableCaption>Results of importing file {files?.length === 1 ? files[0].name : ''}</TableCaption>
                  <TableHeader className="sticky top-0 z-10 bg-gray-50 border">
                    <TableRow className="border">
                      <TableHead rowSpan={2} className="align-bottom border">Rec. #</TableHead>
                      <TableHead rowSpan={2} className="align-bottom border">ID</TableHead>
                      <TableHead rowSpan={2} className="align-bottom border">Label</TableHead>
                      <TableHead rowSpan={2} className="align-bottom border">Result</TableHead>
                      <TableHead colSpan={3} className="border">Messages</TableHead>
                    </TableRow>
                    <TableRow className="border">
                      <TableHead className="border">Line #</TableHead>
                      <TableHead className="border">Severity</TableHead>
                      <TableHead className="border">Text</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody className="max-h-full border">
                    {
                      importedRecords.map((rec, recIdx) => (
                      <>
                        <TableRow key={recIdx}>
                          <TableCell rowSpan={rec.messages.length || 1} className="align-top border">{recIdx + 1}</TableCell>
                          <TableCell rowSpan={rec.messages.length || 1} className="align-top border">
                            {
                              rec.id
                              ? <span
                                  className="underline cursor-pointer text-blue-800"
                                  onClick={() => handleNavigate(rec.id)}
                                  title="Show the imported record in the main table of the underlying page"
                                >
                                  {rec.id}
                                </span>
                              : ''
                            }
                          </TableCell>
                          <TableCell rowSpan={rec.messages.length || 1} className="align-top wrap-col border">{rec.label ?? ''}</TableCell>
                          <TableCell rowSpan={rec.messages.length || 1} className="align-top border">
                            {
                              rec.result === "imported"
                              ? <CheckIcon className="inline size-6 stroke-2 text-green-600" />
                              : rec.result === "duplicate"
                              ? <CopyMinusIcon className="inline size-6 stroke-2 text-red-600" />
                              : rec.result === "error"
                              ? <XIcon className="inline size-6 stroke-2 text-red-600" />
                              : null
                            }&nbsp;{rec.result}
                          </TableCell>
                          {
                            rec.messages.length != 0
                            ? <>
                              <TableCell className="align-top border">{rec.messages[0].lineNum}</TableCell>
                              <TableCell className="align-top border">
                                {
                                  rec.messages[0].severity === "info"
                                  ? <InfoIcon className="inline size-6 text-blue-600" />
                                  : rec.messages[0].severity === "warning"
                                  ? <CircleAlertIcon className="inline size-6 text-orange-600" />
                                  : rec.messages[0].severity === "error"
                                  ? <CircleXIcon className="inline size-6 text-red-600" />
                                  : null
                                }&nbsp;{rec.messages[0].severity}
                              </TableCell>
                              <TableCell className="whitespace-normal border">{rec.messages[0].text}</TableCell>
                            </>
                            : <TableCell colSpan={3} className="border"></TableCell>
                          }
                        </TableRow>
                        {
                          rec.messages.map((msg, msgIdx) =>
                            msgIdx != 0
                            ? <TableRow key={`${recIdx}-${msgIdx}`}>
                              <TableCell key={`${recIdx}-${msgIdx}-4`} className="align-top border">{msg.lineNum}</TableCell>
                              <TableCell key={`${recIdx}-${msgIdx}-5`} className="align-top border">
                                {
                                  msg.severity === "info"
                                  ? <InfoIcon className="inline size-6 text-blue-600" />
                                  : msg.severity === "warning"
                                  ? <CircleAlertIcon className="inline size-6 text-orange-600" />
                                  : msg.severity === "error"
                                  ? <CircleXIcon className="inline size-6 text-red-600" />
                                  : null
                                }&nbsp;{msg.severity}
                              </TableCell>
                              <TableCell key={`${recIdx}-${msgIdx}-6`} className="align-top wrap-col border">{msg.text}</TableCell>
                            </TableRow>
                            : null
                          )
                        }
                      </>
                    ))}
                  </TableBody>
                  <TableFooter className="sticky bottom-0 z-10">
                    <TableRow>
                      <TableCell colSpan={7} className="text-center border">
                        {
                          importedRecords.length != 0
                          ? <>
                            {importedRecords.filter(rec => rec.result === "imported").length}&nbsp;
                            of&nbsp;
                            {importedRecords.length}&nbsp;
                            {recordKind}s imported,&nbsp;
                            {importedRecords.filter(rec => rec.result === "duplicate").length} duplicate(s),&nbsp;
                            {importedRecords.filter(rec => rec.result === "error").length} errors(s)
                          </>
                          : "-No results-"
                        }
                      </TableCell>
                    </TableRow>
                  </TableFooter>
                </Table>
              </div>
            </CarouselItem>
          </CarouselContent>
          <CarouselPrevious />
          <CarouselNext />
        </Carousel>
        <DialogFooter>
          <DialogClose asChild>
            <Button
              type="button"
              variant="outline"
              onClick={handleClose}
              title="Close this dialog and discard messages"
            >
              Close
            </Button>
          </DialogClose>
          <Button
            type="button"
            variant="outline"
            disabled={files?.length === 0 || importedRecords.length === 0}
            title="Copy import messages to clipboard in JSON format"
            onClick={handleCopy}
          >
            Copy
          </Button>
          <Button
            type="button"
            disabled={!files || files.length === 0}
            title="Import from selected file"
            onClick={handleImport}
          >
            Import
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}