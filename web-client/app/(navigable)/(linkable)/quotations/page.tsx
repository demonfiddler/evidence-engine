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

// import type { Metadata } from "next"
import { useContext, useState } from "react"
import { ChatBubbleBottomCenterTextIcon } from '@heroicons/react/24/outline'

import QuotationDetails from "@/app/ui/details/quotation-details"
import DataTable from "@/app/ui/data-table/data-table"

import { columns, columnVisibility } from "@/app/ui/tables/quotation-columns"
import rawPage from "@/data/quotations.json" assert {type: 'json'}
import IPage from "@/app/model/IPage"
import Quotation from "@/app/model/Quotation"
import { SelectedRecordsContext } from "@/lib/context"

// export const metadata: Metadata = {
//   title: "Quotations",
//   description: "Public quotations, open letters & petitions",
// };

export default function Quotations() {
  const page = rawPage as unknown as IPage<Quotation>
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedRow, setSelectedRow] = useState<Quotation|undefined>(() => {
    const selectedRecordId = selectedRecordsContext.Quotation?.id
    return page.content.find(record => record.id == selectedRecordId)
  });

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ChatBubbleBottomCenterTextIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Quotations</h1>
      </div>
      <DataTable<Quotation, unknown>
        recordKind="Quotation"
        columns={columns}
        defaultColumnVisibility={columnVisibility}
        page={page}
        onSelect={setSelectedRow}
      />
      <QuotationDetails record={selectedRow} />
    </main>
  );
}
