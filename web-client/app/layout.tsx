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

import '@/app/ui/global.css'
import { inter } from '@/app/ui/fonts'
// import { Metadata } from 'next'
import { Toaster } from "@/components/ui/sonner"
import { MasterLinkContext, MasterLinkContextBase, MasterLinkContextType, SecurityContext, SecurityContextBase, SecurityContextType, SelectedRecordsContext, SelectedRecordsContextBase, SelectedRecordsContextType } from '@/lib/context'
import RecordKind from './model/RecordKind'
import ILinkableEntity from './model/ILinkableEntity'
import { getRecordLabel } from '@/lib/utils'
import { useState } from 'react'
import { useSessionStorage } from 'usehooks-ts'
import Topic from './model/Topic'
import User from './model/User'

// export const metadata: Metadata = {
//   title: {
//     template: '%s | Evidence Engine',
//     default: 'Evidence Engine',
//   },
//   description: 'Scientific evidence curated by campaign-resources.org',
//   // metadataBase: new URL('https://ee.campaign-resources.org'),
// };

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [securityContextSs, storeSecurityContext] = useSessionStorage<SecurityContextBase>('security-context', {})
  const [masterLinkContextSs, storeMasterLinkContext] = useSessionStorage<MasterLinkContextBase>('master-link', { masterRecordKind: "None" })
  const [selectedRecordsContextSs, storeSelectedRecordsContext] = useSessionStorage<SelectedRecordsContextBase>('selected-records', {})
  const [securityContext, setSecurityContext] = useState<SecurityContextType>({
    ...securityContextSs,
    setSecurityContext: setSecurityPrincipal
  })
  const [masterLinkContext, setMasterLinkContext] = useState<MasterLinkContextType>({
    ...masterLinkContextSs,
    setMasterTopic: setMasterTopic,
    setMasterRecord: setMasterRecord,
    setMasterRecordKind: setMasterRecordKind,
  })
  const [selectedRecordsContext, setSelectedRecordsContext] = useState<SelectedRecordsContextType>({
    ...selectedRecordsContextSs,
    setSelectedRecord: setSelectedRecord
  })

  function setSecurityPrincipal(secCtx: SecurityContextType, user?: User) {
    const newSecCtxSs = {
      ...secCtx,
      username: user?.username,
      authorities: user?.authorities
    }
    const newCtx = {
      ...newSecCtxSs,
      setSecurityPrincipal: setSecurityPrincipal
    }
    storeSecurityContext(newSecCtxSs)
    setSecurityContext(newCtx)
  }

  function setMasterTopic(mlCtx: MasterLinkContextType, topic?: Topic) {
    console.log(`enter RootLayout.setMasterTopic(topicId: ${topic?.id}), mlCtx: ${JSON.stringify(mlCtx)}`)

    const newCtxSs = {
      ...mlCtx,
      masterTopicId: topic?.id,
      masterTopicDescription: topic?.description,
      masterTopicPath: topic?.path,
    }
    const newCtx = {
      ...newCtxSs,
      setMasterTopic: setMasterTopic,
      setMasterRecord: setMasterRecord,
      setMasterRecordKind: setMasterRecordKind,
    }
    storeMasterLinkContext(newCtxSs)
    setMasterLinkContext(newCtx)

    console.log(`\tRootLayout.setMasterTopic(topicId: ${topic?.id}), newCtx=${JSON.stringify(newCtx)}`)
  }

  function setMasterRecord(mlCtx: MasterLinkContextType, masterRecord?: ILinkableEntity) {
    console.log(`enter RootLayout.setMasterRecord(masterRecordId: ${masterRecord?.id}), mlCtx: ${JSON.stringify(mlCtx)}`)

    const newCtxSs = {
      ...mlCtx,
      masterRecordId: masterRecord?.id,
      masterRecordLabel: getRecordLabel(mlCtx.masterRecordKind, masterRecord),
    }
    const newCtx = {
      ...newCtxSs,
      setMasterTopic: setMasterTopic,
      setMasterRecord: setMasterRecord,
      setMasterRecordKind: setMasterRecordKind,
    }
    storeMasterLinkContext(newCtxSs)
    setMasterLinkContext(newCtx)

    console.log(`\tRootLayout.setMasterRecord(masterRecordId: ${masterRecord?.id}), newCtx=${JSON.stringify(newCtx)}`)
  }

  function setMasterRecordKind(mlCtx: MasterLinkContextType, srCtx: SelectedRecordsContextType, masterRecordKind: RecordKind) {
    console.log(`enter RootLayout.setMasterRecordKind(masterRecordKind: ${masterRecordKind}), mlCtx: ${JSON.stringify(mlCtx)}, srCtx: ${JSON.stringify(srCtx)}`)
    console.log(`\tselectedRecordsContext = ${JSON.stringify(selectedRecordsContext)}`)
    const masterRecord = srCtx[masterRecordKind]
    console.log(`\tmasterRecord = ${JSON.stringify(masterRecord)}`)

    const newCtxSs = {
      ...mlCtx,
      masterRecordId: masterRecord?.id,
      masterRecordLabel: masterRecord?.label,
      masterRecordKind: masterRecordKind,
    }
    const newCtx = {
      ...newCtxSs,
      setMasterTopic: setMasterTopic,
      setMasterRecord: setMasterRecord,
      setMasterRecordKind: setMasterRecordKind,
    }
    storeMasterLinkContext(newCtxSs)
    setMasterLinkContext(newCtx)

    console.log(`\tRootLayout.setMasterRecordKind(masterRecordKind: ${masterRecordKind}), newCtx=${JSON.stringify(newCtx)}`)
  }

  function setSelectedRecord(srCtx: SelectedRecordsContextType, recordKind: RecordKind, record?: ILinkableEntity) {
    console.log(`enter RootLayout.setSelectedRecord(recordKind: ${recordKind}), recordId: ${record?.id}, srCtx: ${JSON.stringify(srCtx)}`)

    const newCtxSs = {
      ...srCtx,
      [recordKind]: record?.id ? { id: record.id, label: getRecordLabel(recordKind, record) } : undefined,
    }
    const newCtx = {
      ...newCtxSs,
      setSelectedRecord: setSelectedRecord
    }
    storeSelectedRecordsContext(newCtxSs)
    setSelectedRecordsContext(newCtx)

    console.log(`\tRootLayout.setSelectedRecord(masterRecordKind: ${recordKind}), newCtx=${JSON.stringify(newCtx)}`)
  }

  console.log(`RootLayout(): masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
  console.log(`RootLayout(): selectedRecordsContext = ${JSON.stringify(selectedRecordsContext)}`)

  return (
    <html lang="en">
      <body className={`${inter.className} antialiased`}>
        <SecurityContext value={securityContext}>
          <MasterLinkContext value={masterLinkContext}>
            <SelectedRecordsContext value={selectedRecordsContext}>
              {children}
              <Toaster />
            </SelectedRecordsContext>
          </MasterLinkContext>
        </SecurityContext>
      </body>
    </html>
  );
}
