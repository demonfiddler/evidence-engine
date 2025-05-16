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
  Cell,
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
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { CSSProperties, useContext, useState } from "react"
import {
  DndContext,
  KeyboardSensor,
  MouseSensor,
  TouchSensor,
  closestCenter,
  type DragEndEvent,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import { restrictToHorizontalAxis } from '@dnd-kit/modifiers'
import {
  arrayMove,
  SortableContext,
  horizontalListSortingStrategy,
} from '@dnd-kit/sortable'
import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { DataTablePagination } from "./data-table-pagination"
import type IPage from "@/app/model/IPage"
import DataTableFilter from "./data-table-filter"
import IBaseEntity from "@/app/model/IBaseEntity"
import { MasterLinkContext, SelectedRecordsContext } from "@/lib/context"
import RecordKind from "@/app/model/RecordKind"
import { cn, isLinkableEntity } from "@/lib/utils"

function DragAlongCell<TData>({ cell }: { cell: Cell<TData, unknown> }) {
  const { isDragging, setNodeRef, transform } = useSortable({
    id: cell.column.id,
  })

  const style: CSSProperties = {
    opacity: isDragging ? 0.8 : 1,
    position: 'relative',
    transform: CSS.Translate.toString(transform), // translate instead of transform to avoid squishing
    transition: 'width transform 0.2s ease-in-out',
    width: cell.column.getSize(),
    zIndex: isDragging ? 1 : 0,
  }
  const className = (cell.column.columnDef?.meta as ColumnMetaData)?.className

  return (
    <TableCell key={cell.id} className={cn("truncate border box-border", className)} style={style} ref={setNodeRef} title={String(cell.getValue() ?? '')}>
      {flexRender(cell.column.columnDef.cell, cell.getContext())}
    </TableCell>
  )
}

interface DataTableProps<TData, TValue> {
  className?: string
  recordKind: RecordKind
  defaultColumns: ColumnDef<TData, TValue>[]
  defaultColumnVisibility: VisibilityState
  page?: IPage<TData>
  getSubRows?: (row: TData) => TData[] | undefined
  onSelect: (row?: TData) => void
}

interface ColumnMetaData {
  className?: string
}

export default function DataTable<TData extends IBaseEntity, TValue>({
  className,
  recordKind,
  defaultColumns,
  defaultColumnVisibility,
  page,
  getSubRows,
  onSelect
}: DataTableProps<TData, TValue>) {
  const masterLinkContext = useContext(MasterLinkContext)
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [columns] = useState<typeof defaultColumns>(() => [
    ...defaultColumns,
  ])
  const [sorting, setSorting] = useState<SortingState>([/*{id: "id", desc: false}*/])
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({...defaultColumnVisibility})
  const [columnOrder, setColumnOrder] = useState<string[]>(() => columns.map(c => c.id!))
  const selectedRecord = selectedRecordsContext[recordKind]
  const [rowSelection, setRowSelection] = useState(selectedRecord ? {[selectedRecord.id.toString()]: true} : {})
  // const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 5 })
  const [expanded, setExpanded] = useState<ExpandedState>({})
  const sensors = useSensors(
    useSensor(MouseSensor, {}),
    useSensor(TouchSensor, {}),
    useSensor(KeyboardSensor, {})
  )

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
    onColumnOrderChange: setColumnOrder,
    columnResizeMode: "onChange",
    columnResizeDirection: "ltr",
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
    // defaultColumn: {
    //   size: 150, //starting column size
    //   minSize: 10, //enforced during column resizing
    //   maxSize: 500, //enforced during column resizing
    // },
    initialState: {
      columnPinning: {
        left: ['select'],
        right: ['action'],
      },
    },
    state: {
      sorting,
      columnOrder,
      columnFilters,
      columnVisibility,
      rowSelection,
      expanded,
      // pagination,
    }
  })

  function computeTableMetrics() {
    let length = 0
    const widths: {[key: string] : number} = {}
    table.getVisibleLeafColumns().forEach(col => {
      length += col.getSize()
      widths[col.columnDef.id ?? 'unknown'] = col.getSize()
    })
    widths.length = length
    return widths
  }

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

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event
    if (active && over && active.id !== over.id) {
      setColumnOrder(columnOrder => {
        const oldIndex = columnOrder.indexOf(active.id as string)
        const newIndex = columnOrder.indexOf(over.id as string)
        return arrayMove(columnOrder, oldIndex, newIndex)
      })
    }
  }

  // console.log(`DataTable(): table.getState().rowSelection = ${JSON.stringify(table.getState().rowSelection)}`)
  // console.log(`DataTable(): masterLinkContext = ${JSON.stringify(masterLinkContext)}`)
  // console.log(`DataTable(): selectedRecordsContext = ${JSON.stringify(selectedRecordsContext)}`)
  // console.log(`DataTable(): ${JSON.stringify({columnSizing: table.getState().columnSizing, columnSizingInfo: table.getState().columnSizingInfo})}`)
  // console.log(`table.getTotalSize() = ${table.getTotalSize()}, computeTableMetrics() = ${JSON.stringify(computeTableMetrics())}`)

  return (
    // NOTE: This provider creates div elements, so don't nest inside of <table> elements
    <DndContext
      collisionDetection={closestCenter}
      modifiers={[restrictToHorizontalAxis]}
      onDragEnd={handleDragEnd}
      sensors={sensors}
    >
      {/* <fieldset className="p-2 border box-content rounded-md shadow-lg"> */}
      <fieldset className={cn("p-2 border rounded-md shadow-lg", className)}>
        {/* <legend>&nbsp;Filter&nbsp;</legend> */}
        <div className="flex flex-col gap-2">
          <DataTableFilter table={table} isLinkableEntity={isLinkableEntity(recordKind)} />
          <Table className="table-fixed box-border" style={{width: `${table.getTotalSize()}px`}}>
            <colgroup>
              {table.getHeaderGroups().map(headerGroup => headerGroup.headers.map(header => (
                <col key={header.id} style={{width: `${header.getSize()}px`}} />)))
              }
            </colgroup>
            <TableHeader className="bg-gray-50">
              {table.getHeaderGroups().map(headerGroup => (
                <TableRow key={headerGroup.id}>
                  <SortableContext
                    items={columnOrder}
                    strategy={horizontalListSortingStrategy}
                  >
                    {headerGroup.headers.map(header =>
                      flexRender(header.column.columnDef.header, header.getContext())
                    )}
                  </SortableContext>
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
                      <SortableContext
                        key={cell.id}
                        items={columnOrder}
                        strategy={horizontalListSortingStrategy}
                      >
                        <DragAlongCell key={cell.id} cell={cell} />
                      </SortableContext>
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
          <DataTablePagination table={table} />
        </div>
      </fieldset>
    </DndContext>
  )
}
