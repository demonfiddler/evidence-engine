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

// import type { Metadata } from "next";
import { useContext, useState } from "react";
import { ListBulletIcon } from '@heroicons/react/24/outline';

import LogDetails from "@/app/ui/details/log-details";
import DataTable from "@/app/ui/data-table/data-table";

import { columns, columnVisibility } from "@/app/ui/tables/log-columns"
import rawPage from "@/data/logs.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Log from "@/app/model/Log";
import { SelectedRecordsContext } from "@/lib/context";

// export const metadata: Metadata = {
//   title: "Logs",
//   description: "Record change log",
// };

export default function Logs() {
  const page = rawPage as unknown as IPage<Log>
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRecordId, setSelectedRecordId] = useState<string|BigInt|undefined>(selectedRecordsContext.Log?.id)
  const selectedRecord = page.content.find(r => r.id == selectedRecordId)

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ListBulletIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Logs</h1>
      </div>
      <DataTable<Log, unknown>
        recordKind="Log"
        defaultColumns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        onSelect={setSelectedRecordId}
      />
      <LogDetails record={selectedRecord} />
    </main>
  );
}
