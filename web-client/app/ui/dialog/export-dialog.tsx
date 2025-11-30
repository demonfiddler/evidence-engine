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

import RecordKind from "@/app/model/RecordKind"
import { GlobalContext } from "@/lib/context"
import { usePathname, useSearchParams } from "next/navigation"
import { useCallback, useContext, useEffect, useRef, useState } from "react"
import { Button } from "@/components/ui/button"
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
  RadioGroup,
  RadioGroupItem,
} from "@/components/ui/radio-group"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Checkbox } from "@/components/ui/checkbox"
import { Label } from "@/components/ui/label"
import ButtonEx from "../ext/button-ex"
import useAuth from "@/hooks/use-auth"
import { DownloadIcon } from "lucide-react"
import Spinner from "../misc/spinner"

const ctFileExt = {
  "text/csv": "csv",
  "text/html": "html",
  "application/pdf": "pdf",
  "application/x-research-info-systems": "ris",
}

type ContentType = "text/csv" | "text/html" | "application/pdf" | "application/x-research-info-systems"

export default function ExportDialog<T>({ recordKind }: { recordKind: RecordKind }) {
  const { jwtToken } = useAuth()
  const { columns, queries } = useContext(GlobalContext)
  const pathname = usePathname()
  const searchParams = useSearchParams()
  const [error, setError] = useState("")
  const [contentType, setContentType] = useState<ContentType>("application/pdf")
  const [renderTable, setRenderTable] = useState(true)
  const [renderDetails, setRenderDetails] = useState(false)
  const [pages, setPages] = useState("all")
  const [paper, setPaper] = useState("A4")
  const [orientation, setOrientation] = useState("portrait")
  const [fontSize, setFontSize] = useState("12")
  const [isOpen, setIsOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  const getHref = useCallback(() => {
    const newSearchParams = new URLSearchParams(searchParams)
    newSearchParams.set("contentType", contentType)
    if (renderTable)
      newSearchParams.set("renderTable", "true")
    if (renderDetails)
      newSearchParams.set("renderDetails", "true")
    if (pages === "current") {
      const pagination = queries[recordKind]?.pagination
      if (pagination) {
        newSearchParams.set("pageNumber", pagination.pageIndex.toString())
        newSearchParams.set("pageSize", pagination.pageSize.toString())
      }
    }
    if (paper)
      newSearchParams.set("paper", paper)
    if (orientation)
      newSearchParams.set("orientation", orientation)
    if (fontSize)
      newSearchParams.set("fontSize", fontSize)
    if (columns[recordKind]?.order) {
      for (const id of columns[recordKind].order) {
        if (columns[recordKind].visibility[id])
          newSearchParams.append("col", id);
      }
    }
    queries[recordKind]?.sorting.forEach(s => newSearchParams.append("sort", `${s.id}${s.desc ? " desc" : ""}`))

    // This is a bit of a kludge to circumvent the non-standard path used by the Security page.
    let path
    switch (recordKind) {
      case "User":
        path = "/users"
        break
      case "Group":
        path = "/groups"
        break
      default:
        path = pathname.substring(pathname.lastIndexOf('/'));
    }

    return `${process.env.NEXT_PUBLIC_SERVER_URL}/rest/export${path}?${newSearchParams.toString()}`
  }, [searchParams, contentType, pages, paper, orientation, fontSize, renderTable, renderDetails, columns, queries, recordKind, pathname])

  const prevRenderTable = useRef(false);
  const prevRenderDetails = useRef(false);
  useEffect(() => {
    if (contentType === "text/csv") {
      // For CSV, table and details are mutually exclusive.
      if (renderTable != prevRenderTable.current) {
        prevRenderTable.current = renderTable
        if (renderTable)
          setRenderDetails(false)
      }
      if (renderDetails != prevRenderDetails.current) {
        prevRenderDetails.current = renderDetails
        if (renderDetails)
          setRenderTable(false)
      }
    } else if (contentType === "application/x-research-info-systems") {
      // RIS export necessarily includes all columns.
      if (renderTable)
        setRenderTable(false)
      if (!renderDetails)
        setRenderDetails(true)
    }
  }, [contentType, renderTable, prevRenderTable, renderDetails, prevRenderDetails])

  const handleExport = useCallback(() => {
    // This commented-out approach works fine for unauthenticated users but doesn't work for authenticated users,
    // because the browser doesn't know how to pass the JWT token in an Authorization header. Thus when an authenticated
    // user requests records with a status other than published, these won't be returned (because the server only
    // returns published records to unauthenticated users).
    // location.assign(getHref())

    setIsLoading(true)
    const headers : HeadersInit = {
      "Accept": contentType
    }
    if (jwtToken)
      headers["Authorization"] = `Bearer ${jwtToken}`
    fetch(getHref(), {
      method: "GET",
      headers
    })
    .then(res => res.blob())
    .then(blob => {
      setError("")
      setIsLoading(false)
      setIsOpen(false)
      console.log(`blob.type = ${blob.type}`)
      const url = URL.createObjectURL(blob)

      // This commented-out approach works insofar as it loads the file into browser preview, but the 'Save' function
      // doesn't work smoothly as the browser ignores blob.type, pre-populates File name with the blob GUID and only
      // offers Save as type: All files (*.*)
      // location.assign(url)
      // setTimeout(() => URL.revokeObjectURL(url), 10000);

      // This approach works better as it gives us full control over filename, etc.
      const fileExt = ctFileExt[contentType]
      let timestamp = new Date().toISOString()
      timestamp = timestamp.substring(0, timestamp.lastIndexOf('.')).replace("T", "_").replaceAll(":", "")
      const a = document.createElement("a")
      a.href = url
      a.download = `${recordKind.toLowerCase()}s-${timestamp}.${fileExt}`
      a.click()
      URL.revokeObjectURL(url)
    }).catch((reason) => {
      setError(reason)
      setIsLoading(false)
    })
  }, [getHref])

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <ButtonEx
          variant="ghost"
          help="Export (download) the table"
          title="Export (download) the table">
          <DownloadIcon />
        </ButtonEx>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle><DownloadIcon className="inline" />&nbsp;Export {recordKind}s</DialogTitle>
          <DialogDescription>
            Specify how to export the table.
          </DialogDescription>
          <p className="text-red-600">{error}</p>
        </DialogHeader>
        <Spinner loading={isLoading} label="Exporting..." />
        <div className="flex gap-2">
          <fieldset className="grow border-1 rounded-md p-2">
            <legend>Format</legend>
            <RadioGroup value={contentType} onValueChange={value => setContentType(value as ContentType)}>
              <div className="flex gap-2">
                <RadioGroupItem value="text/csv" id="export-csv" />
                <Label htmlFor="export-csv">CSV</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem value="text/html" id="export-html" />
                <Label htmlFor="export-html">HTML</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem value="application/pdf" id="export-pdf" />
                <Label htmlFor="export-pdf">PDF</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem
                  value="application/x-research-info-systems"
                  id="export-ris"
                  disabled={recordKind != "Publication"}
                />
                <Label htmlFor="export-ris">RIS</Label>
              </div>
            </RadioGroup>
          </fieldset>
          <fieldset className="grow flex flex-col border-1 rounded-md p-2 gap-2">
            <legend>Include</legend>
            <div className="flex gap-2">
              <Checkbox
                id="render-table"
                disabled={contentType === "application/x-research-info-systems"}
                checked={renderTable}
                onCheckedChange={value => setRenderTable(!!value)}
              />
              <Label htmlFor="render-table">Table</Label>
            </div>
            <div className="flex gap-2">
              <Checkbox
                id="render-details"
                checked={renderDetails}
                onCheckedChange={value => setRenderDetails(!!value)}
              />
              <Label htmlFor="render-details">Details</Label>
            </div>
          </fieldset>
          <fieldset className="grow border-1 rounded-md p-2">
            <legend>Pages</legend>
            <RadioGroup value={pages} onValueChange={setPages}>
              <div className="flex gap-2">
                <RadioGroupItem value="current" id="pages-current" />
                <Label htmlFor="pages-current">Current</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem value="all" id="pages-all" />
                <Label htmlFor="pages-all">All</Label>
              </div>
            </RadioGroup>
          </fieldset>
        </div>
        <div className="flex gap-2">
          <fieldset className="grow border-1 rounded-md p-2">
            <legend>Paper Size</legend>
            <RadioGroup
              value={paper}
              onValueChange={setPaper}
              disabled={contentType === "text/csv" || contentType === "application/x-research-info-systems"}>
              <div className="flex gap-2">
                <RadioGroupItem value="A3" id="paper-a3" />
                <Label htmlFor="paper-a3">A3</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem value="A4" id="paper-a4" />
                <Label htmlFor="paper-a4">A4</Label>
              </div>
            </RadioGroup>
          </fieldset>
          <fieldset className="grow border-1 rounded-md p-2">
            <legend>Orientation</legend>
            <RadioGroup
              value={orientation}
              onValueChange={setOrientation}
              disabled={contentType === "text/csv" || contentType === "application/x-research-info-systems"}
            >
              <div className="flex gap-2">
                <RadioGroupItem value="portrait" id="orientation-portrait" />
                <Label htmlFor="orientation-portrait">Portrait</Label>
              </div>
              <div className="flex gap-2">
                <RadioGroupItem value="landscape" id="orientation-landscape" />
                <Label htmlFor="orientation-landscape">Landscape</Label>
              </div>
            </RadioGroup>
          </fieldset>
          <fieldset className="grow border-1 rounded-md p-2">
            <legend>Font Size</legend>
            <Select value={fontSize} onValueChange={setFontSize}>
              <SelectTrigger id="font-size"
                disabled={contentType === "text/csv" || contentType === "application/x-research-info-systems"}
              >
                <SelectValue placeholder="Font size" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Font Size</SelectLabel>
                  {
                    [8, 9, 10, 11, 12, 13, 14, 15, 16].map(
                      s => <SelectItem key={`font-${s}`} value={s.toString()}>{s}</SelectItem>)
                  }
                </SelectGroup>
              </SelectContent>
            </Select>
          </fieldset>
        </div>
        <DialogFooter>
          <DialogClose asChild>
            <Button type="button" variant="outline">Cancel</Button>
          </DialogClose>
          <Button
            type="button"
            disabled={!renderTable && !renderDetails}
            onClick={handleExport}
          >
            Export
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}