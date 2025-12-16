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
  // ColumnFiltersState,
  ColumnOrderState,
  ColumnSizingState,
  ExpandedState,
  PaginationState,
  RowSelectionState,
  SortingState,
  Updater,
  VisibilityState,
  flexRender,
  getCoreRowModel,
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
import { CSSProperties, JSX, useCallback, useContext, useEffect, useRef, useState } from "react"
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
import { DataTablePaginator } from "./data-table-paginator"
import type IPage from "@/app/model/IPage"
import IBaseEntity from "@/app/model/IBaseEntity"
import { ColumnState, GlobalContext, QueryState } from "@/lib/context"
import { RecordKind } from "@/app/model/RecordKinds"
import { cn, getValue, isEqual, isLinkableEntity } from "@/lib/utils"
import DataTableColumnHeader from "./data-table-column-header"
import Spinner from "../misc/spinner"
import { getExpandedRowModelEx } from "./data-table-expanded-row-model"
import { DetailState } from "../details/detail-actions"
import DataTableFilterProps, { ImportAccept } from "./data-table-filter"
import { QueryFilter } from "@/app/model/schema"
import { LoggerEx, table } from "@/lib/logger"

const logger = new LoggerEx(table, "[DataTable] ")

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
  page: IPage<TData> | undefined
  state?: DetailState
  loading: boolean
  filterComponent: ({ table, recordKind, isLinkableEntity, auxRecordId, refetch, }: DataTableFilterProps<TData>) => JSX.Element,
  manualPagination: boolean
  manualSorting: boolean
  getSubRows?: (row: TData) => TData[] | undefined
  onRowSelectionChange?: (recordId?: string) => void
  auxRecordId?: string
  refetch: () => void
  loadingPathWithSearchParams: boolean
  importAccept?: ImportAccept
}

interface ColumnMetaData {
  className?: string
}

export default function DataTable<TData extends IBaseEntity, TValue>({
  className,
  recordKind,
  defaultColumns,
  page,
  loading,
  filterComponent: DataTableFilter,
  manualPagination,
  manualSorting,
  getSubRows,
  onRowSelectionChange,
  auxRecordId,
  refetch,
  loadingPathWithSearchParams,
  importAccept,
}: DataTableProps<TData, TValue>) {
  logger.debug("render")

  const {
    selectedRecords,
    columns : columnsMap,
    queries,
    setSelectedRecord,
    setColumnOrder,
    setColumnSizing,
    setColumnVisibility,
    setSorting,
    setPagination,
  } = useContext(GlobalContext)
  const [columns] = useState<typeof defaultColumns>(() => [
    ...defaultColumns,
  ])
  const {sorting, pagination} = queries[recordKind] as QueryState<QueryFilter>
  const {visibility : columnVisibility, order : columnOrder, sizing : columnSizing} = columnsMap[recordKind] as ColumnState
  const onColumnOrderChange = useCallback((updaterOrValue: Updater<ColumnOrderState>) => {
    const newColumnOrder = getValue(updaterOrValue, columnOrder)
    if (!isEqual(newColumnOrder, columnOrder)) {
      logger.trace("onColumnOrderChange: %s columnOrder changed from %o to %o", recordKind, columnOrder, newColumnOrder)
      setColumnOrder(recordKind, newColumnOrder)
    }
  }, [recordKind, columnOrder, setColumnOrder])
  const onColumnSizingChange = useCallback((updaterOrValue: Updater<ColumnSizingState>) => {
    const newColumnSizing = getValue(updaterOrValue, columnSizing)
    if (!isEqual(newColumnSizing, columnSizing)) {
      logger.trace("onColumnSizingChange: %s columnSizing changed from %o to %o", recordKind, columnSizing, newColumnSizing)
      setColumnSizing(recordKind, newColumnSizing)
    }
  }, [recordKind, columnSizing, setColumnSizing])
  const onColumnVisibilityChange = useCallback((updaterOrValue: Updater<VisibilityState>) => {
    const newColumnVisibility = getValue(updaterOrValue, columnVisibility)
    if (!isEqual(newColumnVisibility, columnVisibility)) {
      logger.trace("onColumnVisibilityChange: %s columnVisibility changed from %o to %o", recordKind, columnVisibility, newColumnVisibility)
      setColumnVisibility(recordKind, newColumnVisibility)
    }
  }, [recordKind, columnVisibility, setColumnVisibility])
  const onSortingChange = useCallback((updaterOrValue: Updater<SortingState>) => {
    const newSorting = getValue(updaterOrValue, sorting)
    if (!isEqual(newSorting, sorting)) {
      logger.trace("onSortingChange: %s sorting changed from %o to %o", recordKind, sorting, newSorting)
      setSorting(recordKind, newSorting)
    }
  }, [recordKind, sorting, setSorting])
  const onPaginationChange = useCallback((updaterOrValue: Updater<PaginationState>) => {
    const newPagination = getValue(updaterOrValue, pagination)
    if (!isEqual(newPagination, pagination)) {
      logger.trace("onPaginationChange: %s pagination changed from %o to %o", recordKind, pagination, newPagination)
      setPagination(recordKind, newPagination)
    }
  }, [recordKind, pagination, setPagination])

  const selectedRecord = selectedRecords[recordKind]
  const [rowSelection, setRowSelection] = useState(selectedRecord ? {[selectedRecord.id]: true} : {})
  const [expanded, setExpanded] = useState<ExpandedState>({})
  const sensors = useSensors(
    useSensor(MouseSensor, {}),
    useSensor(TouchSensor, {}),
    useSensor(KeyboardSensor, {})
  )
  const findItem = useCallback((rowId: string, data?: TData[]) : TData | undefined => {
    if (!data)
      return undefined
    for (const row of data) {
      if (row.id == rowId)
        return row
      const found = findItem(rowId, getSubRows?.(row))
      if (found)
        return found
    }
  }, [getSubRows])

  const handleRowSelectionChange = useCallback((selection: Updater<RowSelectionState>) => {
    setRowSelection(selection)

    let selectedRecord
    const rowSelectionState = typeof selection == "function" ? selection({}) : selection
    const entries = Object.entries(rowSelectionState)
    if (entries.length == 1) {
      const [id, selected] = entries[0]
      if (selected)
        selectedRecord = findItem(id, page?.content)
    }
    setSelectedRecord(recordKind, selectedRecord)
    if (onRowSelectionChange)
      onRowSelectionChange(selectedRecord?.id)
  }, [findItem, page, setSelectedRecord, recordKind, onRowSelectionChange])

  const table = useReactTable({
    // aggregationFns: ,
    // autoResetAll: boolean,
    // autoResetExpanded: boolean,
    // autoResetPageIndex: false, // must be false (the default) if manualPagination == true
    columnResizeMode: "onChange",
    columnResizeDirection: "ltr",
    columns,
    data: page?.content ?? [],
    // debugAll: true,
    // debugCells: boolean,
    // debugColumns: boolean,
    // debugHeaders: boolean,
    // debugRows: boolean,
    // debugTable: boolean,
    // debugTable: true,
    // defaultColumn: {
    //   size: 150, //starting column size
    //   minSize: 10, //enforced during column resizing
    //   maxSize: 500, //enforced during column resizing
    // },
    // enableColumnFilters: boolean,
    // enableColumnPinning: boolean,
    // enableColumnResizing: boolean,
    enableExpanding: true,
    // enableFilters: boolean,
    // enableGlobalFilter: boolean,
    // enableGrouping: boolean,
    // enableHiding: boolean,
    enableMultiSort: true, // default is true
    enableMultiRemove: true, // default is true
    enableMultiRowSelection: false,
    // enableRowPinning: boolean,
    enableRowSelection: true,
    enableSubRowSelection: false,
    // enableSorting: boolean,
    // enableSortingRemoval: boolean,
    // filterFns: Record<string, FilterFn<any>>,
    // filterFromLeafRows: false,
    // getColumnCanGlobalFilter: (column: Column<TData, unknown>) => boolean,
    getCoreRowModel: getCoreRowModel(),
    getExpandedRowModel: getExpandedRowModelEx(),//getExpandedRowModel(),
    // getFacetedMinMaxValues: (table: Table<TData>, columnId: string) => () => undefined | [number, number]),
    // getFacetedRowModel: ((table: Table<TData>, columnId: string) => () => RowModel<TData>),
    // getFacetedUniqueValues: ((table: Table<TData>, columnId: string) => () => Map<any, number>),
    getFilteredRowModel: getFilteredRowModel(),
    // getGroupedRowModel: ((table: Table<any>) => () => RowModel<any>),
    // getIsRowExpanded: ((row: Row<TData>) => boolean),
    getPaginationRowModel: manualPagination ? undefined: getPaginationRowModel(), // not needed for manual/server-side pagination
    getRowId: originalRow => originalRow.id ?? "",
    // getRowCanExpand: getRowCanExpand?: ((row: Row<TData>) => boolean),
    getSortedRowModel: manualSorting ? undefined: getSortedRowModel(), // not needed for manual/server-side sorting
    getSubRows,
    // globalFilterFn: FilterFnOption<TData>,
    // groupedColumnMode: false | "reorder" | "remove" | undefined,
    initialState: {
      columnPinning: {
        left: ['select'],
        right: ['action'],
      },
    },
    // isMultiSortEvent: ((e: unknown) => boolean),
    // keepPinnedRows: true,
    // manualExpanding: false,
    // manualExpanding: false,
    // manualFiltering: true,
    // manualGrouping: true,
    manualPagination, // set to true for server-side pagination
    manualSorting, // set to true for server-side sorting, access as table.getState().sorting
    // maxLeafRowFilterDepth: 0,
    maxMultiSortColCount: 5, // default is unlimited
    // mergeOptions: ((defaultOptions: TableOptions<TData>, options: Partial<TableOptions<TData>>) => TableOptions<TData>),
    // pageCount: page?.totalPages ?? -1, // can now pass in `rowCount` instead of pageCount and `pageCount` will be calculated internally (new in v8.13.0)
    paginateExpandedRows: false,
    rowCount: manualPagination ? page?.totalElements : undefined,
    // onColumnPinningChange: OnChangeFn<ColumnPinningState>,
    onColumnSizingChange, //: OnChangeFn<ColumnSizingState>,
    // onColumnSizingInfoChange: OnChangeFn<ColumnSizingInfoState>,
    onExpandedChange: setExpanded,
    // onGlobalFilterChange: OnChangeFn<any>,
    // onGroupingChange: OnChangeFn<GroupingState>,
    onPaginationChange, // OnChangeFn<PaginationState>, // If provided, called when pagination state changes. Pass managed state back via tableOptions.state.pagination
    // onRowPinningChange: OnChangeFn<RowPinningState>,
    onRowSelectionChange: handleRowSelectionChange,
    // onStateChange: ((updater: Updater<TableState>) => void),
    // onColumnFiltersChange: setColumnFilters,
    onColumnOrderChange,
    onColumnVisibilityChange,
    onSortingChange,
    state: {
      // columnFilters,
      columnOrder,
      columnVisibility,
      columnSizing,
      expanded,
      sorting,
      pagination,
      rowSelection,
    },
  })

  const prevTotalElements = useRef(0)
  useEffect(() => {
    const newTotalElements = page?.totalElements ?? 0
    if (manualPagination && (newTotalElements !== prevTotalElements.current)) {
      table.setOptions({
        ...table.options,
        rowCount: newTotalElements,
      })
      logger.trace(`effect: %s totalElements changed from %d to %d`, recordKind, prevTotalElements.current, newTotalElements)
      prevTotalElements.current = newTotalElements
    }
  }, [manualPagination, table, page]);

  // function computeTableMetrics() {
  //   let length = 0
  //   const widths: {[key: string] : number} = {}
  //   table.getVisibleLeafColumns().forEach(col => {
  //     length += col.getSize()
  //     widths[col.columnDef.id ?? 'unknown'] = col.getSize()
  //   })
  //   widths.length = length
  //   return widths
  // }

  const handleDragEnd = useCallback((event: DragEndEvent) => {
    const { active, over } = event
    if (active && over && active.id !== over.id) {
      onColumnOrderChange((columnOrder) => {
        const oldIndex = columnOrder.indexOf(active.id as string)
        const newIndex = columnOrder.indexOf(over.id as string)
        return arrayMove(columnOrder, oldIndex, newIndex)
      })
    }
  }, [onColumnOrderChange])

  return (
    // NOTE: This provider creates div elements, so don't nest inside of <table> elements
    <DndContext
      collisionDetection={closestCenter}
      modifiers={[restrictToHorizontalAxis]}
      onDragEnd={handleDragEnd}
      sensors={sensors}
    >
      <fieldset className={cn("relative p-2 border rounded-md shadow-lg", className)}>
        <Spinner loading={loading} className="absolute inset-0 bg-black/20 z-50" />
        <div className="flex flex-col gap-2">
          <DataTableFilter
            table={table}
            recordKind={recordKind}
            recordId={selectedRecord?.id}
            isLinkableEntity={isLinkableEntity(recordKind)}
            auxRecordId={auxRecordId}
            refetch={refetch}
            loadingPathWithSearchParams={loadingPathWithSearchParams}
            importAccept={importAccept}
          />
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
                      typeof header.column.columnDef.header == "function"
                      ? <TableHead key={header.id} className="border">
                          {flexRender(header.column.columnDef.header, header.getContext())}
                        </TableHead>
                      : <DataTableColumnHeader
                        key={header.id}
                        table={table}
                        header={header}
                        column={header.column}
                        title={header.column.columnDef.header ?? ''}
                      />
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
          <DataTablePaginator table={table} />
        </div>
      </fieldset>
    </DndContext>
  )
}

DataTable.whyDidYouRender = true