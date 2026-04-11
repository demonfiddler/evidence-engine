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

import ButtonEx from "@/app/ui/ext/button-ex"
import LabelEx from "@/app/ui/ext/label-ex"
import Spinner from "@/app/ui/misc/spinner"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Dropzone, DropzoneContent, DropzoneEmptyState } from "@/components/ui/shadcn-io/dropzone"
import useAuth from "@/hooks/use-auth"
import { LoggerEx, page } from "@/lib/logger"
import { AlertTriangleIcon, DatabaseBackupIcon, InfoIcon } from "lucide-react"
import { useCallback, useState } from "react"
import { toast } from "sonner"

const logger = new LoggerEx(page, "[BackupRestore] ")

function formatTimestamp(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0")

  return [
    d.getFullYear(),
    pad(d.getMonth() + 1),
    pad(d.getDate())
  ].join("-")
  + "T" +
  [
    pad(d.getHours()),
    pad(d.getMinutes()),
    pad(d.getSeconds())
  ].join("_")
}

export default function BackupRestore() {
  const { jwtToken, hasAuthority } = useAuth()
  const [kind, setKind] = useState<string>("full")
  const [error, setError] = useState("")
  const [files, setFiles] = useState<File[] | undefined>()
  const [isLoading, setIsLoading] = useState(false)

  const handleDrop = useCallback((files: File[]) => {
    logger.trace("Files dropped: %o", files)
    setFiles(files)
    setError("")
  }, [setFiles, setError])

  const handleError = useCallback((error : Error) => {
    logger.error("Error: %o", error)
    setError(error.message)
  }, [setError])

  const getHref = useCallback((action: string) => {
    const href = `${process.env.NEXT_PUBLIC_SERVER_URL}/rest/${action}?kind=${kind}`
    logger.trace(`href='${href}'`)
    return href
  }, [kind])

  const handleBackup = useCallback(() => {
    if (!jwtToken)
      return

    setError("")
    setIsLoading(true)
    const headers : HeadersInit = {
      "Accept": "application/zip",
      "Authorization": `Bearer ${jwtToken}`
    }
    fetch(getHref("backup"), {
      method: "GET",
      headers
    })
    .then(async resp => {
      if (!resp.ok)
        setError(`${resp.status}${resp.statusText ? ` (${resp.statusText}) ` : ''}: Backup failed: ${await resp.text()}`)

      const cd = resp.headers.get("Content-Disposition")
      const filename = cd?.startsWith("attachment; filename=") ? cd.substring(21) : null

      return {blob: resp.ok ? await resp.blob() : null, filename}
    })
    .then(({blob, filename}) => {
      if (!blob?.size)
        return

      console.log(`blob.type = ${blob.type}`)
      const url = URL.createObjectURL(blob)

      // This commented-out approach works insofar as it loads the file into browser preview, but the 'Save' function
      // doesn't work smoothly as the browser ignores blob.type, pre-populates File name with the blob GUID and only
      // offers Save as type: All files (*.*)
      // location.assign(url)
      // setTimeout(() => URL.revokeObjectURL(url), 10000);

      // The filename from the server has a UTC timestamp, so to be user-friendly, replace this with a local timestamp.
      const timestamp = formatTimestamp(new Date())
      if (filename) {
        const prefix = filename.substring(0, filename.indexOf('@') + 1)
        filename = `${prefix}${timestamp}.zip`
      } else {
        // This code is just a fall-back and shouldn't actually be executed.
        filename = `ee-backup-${kind}@${timestamp}.zip`
      }
      
      // This approach, although klunky, works better as it gives us full control over filename, etc.
      const a = document.createElement("a")
      a.href = url
      a.download = filename
      a.click()
      URL.revokeObjectURL(url)

      toast.info("Database backup completed successfully")
    }).catch((reason) => {
      toast.error("Database backup failed")
      setError(reason)
    }).finally(() => setIsLoading(false))
  }, [jwtToken, getHref])

  const handleRestore = useCallback(() => {
    if (!files || files.length == 0 || !jwtToken ||
      !confirm(`Confirm restore database from ${files[0].name}, ${kind === "incremental" ? "updating/adding changed/new" : "replacing all existing"} data?`)) {

      return
    }

    setError("")
    setIsLoading(true)
    const headers : HeadersInit = {
      "Authorization": `Bearer ${jwtToken}`,
    }
    const [file] = files
    const formData = new FormData()
    formData.append("file", file)
    fetch(getHref("restore"), {
      method: "POST",
      headers,
      // Streaming doesn't work - backend not configured to support it?
      // body: file.stream(),
      // duplex: "half",
      // body: file,
      body: formData,
    }) //
    .then(response => {
      if (!response.ok) {
        response.text().then(value => setError(value))
        return
      }

      toast.info("Database restore completed successfully")
      setFiles(undefined)
      return response.text
    }) //
    .catch(error => {
      toast.error("Database restore failed")
      setError(error.message)
    }).finally(() => setIsLoading(false))
  }, [files, jwtToken, getHref])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <DatabaseBackupIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Backup &amp; Restore</h1>
      </div>
      <p className="w-1/2">
        <InfoIcon className="inline text-blue-600" />
        The Evidence Engine database can be backed up to a downloadable backup set file or restored from such a file, previously downloaded.
        Backup scope can be <b>All</b>&nbsp;(entire database including static lookup tables), <b>Full</b>&nbsp;(application data only) or&nbsp;
        <b>Incremental</b> (just the records added or changed since the last backup).
      </p>
      <p className="w-1/2">
        <AlertTriangleIcon className="inline text-red-600" />
        Be aware that an All or Full backup set includes <b>all application data</b>, including <b>security principals</b> (user, groups and their granted authorities)
        and that the corresponding restore operation <b>completely replaces</b> all existing data. Incremental backups form a numbered series and can only be restored
        in order, starting with the original full backup set, without skipping any incremental backups.
      </p>
      <fieldset className="w-1/2 grid grid-cols-3 justify-items-center p-2 gap-2 border rounded-md">
        <legend>&nbsp;Operations&nbsp;</legend>
        <Spinner loading={isLoading} label="busy" className="absolute inset-0 bg-black/20 z-50" />
        <p className="col-span-3 text-red-600">{error}</p>
        <div className="col-span-2 flex">
          <Label>Scope:</Label>
          <RadioGroup className="flex p-2 gap-2" value={kind} onValueChange={setKind}>
            <div className="flex items-center space-x-2">
              <RadioGroupItem id="backup-all" value="all" />
              <LabelEx htmlFor="backup-all" help="Application data + static lookup tables">All</LabelEx>
            </div>
            <div className="flex items-center space-x-2">
              <RadioGroupItem id="backup-full" value="full" />
              <LabelEx htmlFor="backup-full" help="Application data only">Full</LabelEx>
            </div>
            <div className="flex items-center space-x-2">
              <RadioGroupItem id="backup-incremental" value="incremental" />
              <LabelEx htmlFor="backup-incremental" help="Added/changed application data only">Incremental</LabelEx>
            </div>
          </RadioGroup>
        </div>
        <ButtonEx
          type="button"
          variant="outline"
          disabled={!hasAuthority('ADM')}
          onClick={handleBackup}
          help="Backup the database and download the resulting backup set to this computer"
        >
          Backup
        </ButtonEx>
        <Dropzone
          className="col-span-2"
          accept={{"Evidence Engine Zipped Backup Set": [".zip"]}}
          maxFiles={1}
          onDrop={handleDrop}
          onError={handleError}
          src={files}
        >
          <DropzoneEmptyState />
          <DropzoneContent />
        </Dropzone>
        <ButtonEx
          outerClassName="self-start"
          type="button"
          variant="outline"
          disabled={!hasAuthority('ADM') || !files}
          onClick={handleRestore}
          help="Upload the specified backup set and restore the database from it"
        >
          Restore
        </ButtonEx>
      </fieldset>
    </main>
  )
}