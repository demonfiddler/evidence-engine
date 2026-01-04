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

import ButtonEx from "@/app/ui/ext/button-ex"
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

export default function BackupRestore() {
  const { jwtToken, hasAuthority } = useAuth()
  const [kind, setKind] = useState<string>("appdata")
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
    console.log(`href='${href}'`)
    return href
  }, [kind])

  const handleBackup = useCallback(() => {
    if (!jwtToken)
      return

    setError("")
    setIsLoading(true)
    const headers : HeadersInit = {
      "Accept": "application/zip"
    }
    if (jwtToken)
      headers["Authorization"] = `Bearer ${jwtToken}`
    fetch(getHref("backup"), {
      method: "GET",
      headers
    })
    .then(res => {
      if (!res.ok)
        setError(`${res.status} (${res.statusText}): backup failed`)

      return res.blob()
    })
    .then(blob => {
      console.log(`blob.type = ${blob.type}`)
      const url = URL.createObjectURL(blob)

      // This commented-out approach works insofar as it loads the file into browser preview, but the 'Save' function
      // doesn't work smoothly as the browser ignores blob.type, pre-populates File name with the blob GUID and only
      // offers Save as type: All files (*.*)
      // location.assign(url)
      // setTimeout(() => URL.revokeObjectURL(url), 10000);

      // TODO: handle HTTP codes other than 200 (e.g., 302 Found -> "Please Login")

      // This approach works better as it gives us full control over filename, etc.
      let timestamp = new Date().toISOString()
      timestamp = timestamp.substring(0, timestamp.lastIndexOf('.')).replace("T", "_").replaceAll(":", "")
      const a = document.createElement("a")
      a.href = url
      a.download = `ee-backup-${timestamp}.zip`
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
      !confirm(`Confirm restore database from ${files[0].name}, replacing all existing data?`)) {

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
        The Evidence Engine database can be backed up to a downloadable backup set file or restored from such a backup set, previously downloaded.
        Backups are <b>full</b>; that is, there is currently no support for incremental backup/restore of just the records changed since the last backup.
        There is a 'Scope' option to backup/restore just the application data, or to backup/restore the entire database including static lookup tables.
      </p>
      <p className="w-1/2">
        <AlertTriangleIcon className="inline text-red-600" />
        Be aware that a backup set includes <b>all application data</b>, including <b>security principals</b> (user, groups and their granted authorities)
        and that the restore operation <b>completely replaces</b> all existing application records. There is currently no support for merging
        existing records with those from a different database.
      </p>
      <fieldset className="grid grid-cols-3 justify-items-center p-2 gap-2 border rounded-md">
        <legend>&nbsp;Operations&nbsp;</legend>
        <Spinner loading={isLoading} label="busy" className="absolute inset-0 bg-black/20 z-50" />
        <p className="col-span-3 text-red-600">{error}</p>
        <div className="col-span-2 flex">
          <Label>Scope:</Label>
          <RadioGroup className="flex p-2 gap-2" value={kind} onValueChange={setKind}>
            <div className="flex items-center space-x-2">
              <RadioGroupItem id="backup-appdata" value="appdata" />
              <Label htmlFor="backup-appdata">Application data only</Label>
            </div>
            <div className="flex items-center space-x-2">
              <RadioGroupItem id="backup-all" value="all" />
              <Label htmlFor="backup-all">All (application data + lookup tables)</Label>
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