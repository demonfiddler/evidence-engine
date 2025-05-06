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

import {
  ColumnDef,
  ColumnFiltersState,
  ExpandedState,
  RowSelectionState,
  SortingState,
  Updater,
  VisibilityState,
  flexRender,
  getCoreRowModel,
  getExpandedRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { useContext, useState } from "react"
import { DataTablePagination } from "./data-table-pagination"
import type IPage from "@/app/model/IPage"
import DataTableFilter from "./data-table-filter"
import IBaseEntity from "@/app/model/IBaseEntity"
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context"
import RecordKind from "@/app/model/RecordKind"
import { isLinkableEntity } from "@/lib/utils"

// These strings are present solely to enable TailwindCSS to detect that we might be using these classes.
const widthClasses = [
  "max-w-50",
  "max-w-100",
  "max-w-150",
  "max-w-200",
]

interface DataTableProps<TData, TValue> {
  recordKind: RecordKind
  columns: ColumnDef<TData, TValue>[]
  defaultColumnVisibility: VisibilityState
  page?: IPage<TData>
  getSubRows?: (row: TData) => TData[] | undefined
  onSelect: (row?: TData) => void
}

export default function DataTable<TData extends IBaseEntity, TValue>({
  recordKind,
  columns,
  defaultColumnVisibility,
  page,
  getSubRows,
  onSelect
}: DataTableProps<TData, TValue>) {
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [sorting, setSorting] = useState<SortingState>([{id: "id", desc: false}])
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({...defaultColumnVisibility})
  // const [columnOrder, setColumnOrder] = useState<string[]>([])
  const selectedRecord = selectedRecordsContext[recordKind]
  const [rowSelection, setRowSelection] = useState(selectedRecord ? {[selectedRecord.id.toString()]: true} : {})
  // const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 5 })
  const [expanded, setExpanded] = useState<ExpandedState>({})

  const table = useReactTable({
    data: page?.content ?? [],
    columns,
    getRowId: originalRow => String(originalRow.id),
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(), // not required for manual/server-side pagination
    manualPagination: false, // default is false
    // pageCount: number, // obtain from IPage.totalPages
    // rowCount: number, // obtain from IPage.totalElements
    // autoResetPageIndex: false, // defaults to false if manualPagination == true
    // onPaginationChange?: OnChangeFn<PaginationState>, // If provided, called when pagination state changes. Pass managed state back via tableOptions.state.pagination
    paginateExpandedRows: false,
    onSortingChange: setSorting,
    getSortedRowModel: getSortedRowModel(), // actually not needed for manual/server-side sorting
    manualSorting: false, // set to false for server-side sorting, access as table.getState().sorting
    enableMultiSort: true, // default is true
    enableMultiRemove: true, // default is true
    enableExpanding: true,
    maxMultiSortColCount: 5, // default is unlimited
    onColumnFiltersChange: setColumnFilters,
    // columnResizeMode: 'onChange', // default "onEnd"
    getFilteredRowModel: getFilteredRowModel(),
    // filterFromLeafRows: false,
    // maxLeafRowFilterDepth: 0,
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: rowSelectionChanged,
    enableMultiRowSelection: false,
    enableRowSelection: true,
    enableSubRowSelection: false,
    getExpandedRowModel: getExpandedRowModel(),
    onExpandedChange: setExpanded,
    getSubRows: getSubRows,
    // getFacetedMinMaxValues: getFacetedMinMaxValues(),
    // getFacetedRowModel: getFacetedRowModel(),
    // getFacetedUniqueValues: getFacetedUniqueValues(),
    // getFilteredRowModel: getFilteredRowModel(),
    // manualFiltering: true,
    // manualGrouping: true,
    // getGroupedRowModel: getGroupedRowModel(),
    // debugTable: true,
    // debugHeaders: true,
    // debugColumns: true,
    state: {
      sorting,
      // columnOrder,
      columnFilters,
      columnVisibility,
      rowSelection,
      expanded,
      // pagination,
    }
  })

  function findItem(rowId: any, data?: TData[]) : TData | undefined {
    if (!data)
      return undefined
    for (let row of data) {
      if (row.id == rowId)
        return row
      if (getSubRows) {
        const subRows = getSubRows(row)
        if (subRows) {
          const found = findItem(rowId, subRows)
          if (found)
            return found
        }
      }
    }
  }

  function rowSelectionChanged(selection: Updater<RowSelectionState>) {
    console.log(`enter DataTable.rowSelectionChanged, masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
    setRowSelection(selection)

    let selectedRecord
    const rowSelectionState = typeof selection == "function" ? selection({}) : selection
    const entries = Object.entries(rowSelectionState)
    if (entries.length == 1) {
      const [id, selected] = entries[0]
      if (selected)
        selectedRecord = findItem(id, page?.content)
    }
    console.log(`\tselectedRecord = ${JSON.stringify(selectedRecord)}`)
    if (masterLinkContext.masterRecordKind == recordKind)
      masterLinkContext.setMasterRecord(masterLinkContext, selectedRecord)
    selectedRecordsContext.setSelectedRecord(selectedRecordsContext, recordKind, selectedRecord)
    onSelect(selectedRecord)
    console.log(`exit DataTable.rowSelectionChanged, masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
  }

  console.log(`DataTable(): table.getState().rowSelection = ${JSON.stringify(table.getState().rowSelection)}`)
  console.log(`DataTable(): masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
  console.log(`DataTable(): selectedRecordsContext = ${JSON.stringify(selectedRecordsContext)}`)

  return (
    <fieldset className="p-2 border rounded-md">
      {/* <legend>&nbsp;Filter&nbsp;</legend> */}
      <div className="flex flex-col gap-2">
        <DataTableFilter table={table} isLinkableEntity={isLinkableEntity(recordKind)} />
        <div className="rounded-md border">
          <Table>
            <TableHeader className="bg-gray-50">
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) =>
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  )}
                </TableRow>
              ))}
            </TableHeader>
            <TableBody>
              {table.getRowModel().rows?.length ? (
                table.getRowModel().rows.map((row) => (
                  <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell key={cell.id} className={`max-w-${cell.column.columnDef.size} overflow-hidden`} title={String(cell.getValue() ?? '')}>
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </TableCell>
                    ))}
                  </TableRow>
                  ))
              ) : (
                <TableRow>
                  <TableCell colSpan={columns.length} className="h-24 text-center">
                    -No results-
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
        <DataTablePagination table={table} />
      </div>
    </fieldset>
  )
}
