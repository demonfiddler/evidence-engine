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

import ChartBarIcon from "@heroicons/react/24/outline/ChartBarIcon"
import TopicStatistics from "@/app/model/TopicStatistics"
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { READ_ALL_STATISTICS } from "@/lib/graphql-queries"
import { useQuery } from "@apollo/client"
import { flexRender, getCoreRowModel, getPaginationRowModel, useReactTable } from "@tanstack/react-table"
import { BeakerIcon, RotateCw, UserIcon } from 'lucide-react'
import { cn } from "@/lib/utils"
import { useCallback, useEffect, useMemo, useState } from "react"
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectValue } from "@/components/ui/select"
import useAuth from "@/hooks/use-auth"
import EntityStatistics from "@/app/model/EntityStatistics"
import { getExpandedRowModelEx } from "@/app/ui/data-table/data-table-expanded-row-model"
import { DataTablePaginator } from "@/app/ui/data-table/data-table-paginator"
import ButtonEx from "@/app/ui/ext/button-ex"
import SelectTriggerEx from "@/app/ui/ext/select-ex"
import Spinner from "@/app/ui/misc/spinner"
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card"
import { Bars3BottomRightIcon, ExclamationCircleIcon, EnvelopeOpenIcon, ChatBubbleBottomCenterTextIcon } from "@heroicons/react/24/outline"
import { columns } from "@/app/ui/tables/topic-statistics-columns"
import Link from "next/link"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import LabelEx from "@/app/ui/ext/label-ex"

interface ColumnMetaData {
  className?: string
}

const entityItems = [
  { entityKind: "TOP", heading: "Topics", description: "Total topics (top-level + nested)", icon: Bars3BottomRightIcon, property: "topics", href: "/admin/topics"},
  { entityKind: "CLA", heading: "Claims", description: "Total claims of fact", icon: ExclamationCircleIcon, property: "claims", href: "/claims"},
  { entityKind: "DEC", heading: "Declarations", description: "Total declarations, public letters, etc.", icon: EnvelopeOpenIcon, property: "declarations", href: "/declarations"},
  { entityKind: "PER", heading: "Persons", description: "Total scientists, professionals, etc.", icon: UserIcon, property: "persons", href: "/persons"},
  { entityKind: "PUB", heading: "Publications", description: "Total scientific publications, papers, etc.", icon: BeakerIcon, property: "publications", href: "/publications"},
  { entityKind: "QUO", heading: "Quotations", description: "Total quotations", icon: ChatBubbleBottomCenterTextIcon, property: "quotations", href: "/quotations"},
  // { entityKind: "NOB", heading: "Nobel Prizes", description: "Total Nobel Laureates", icon: NobelPrizeIcon, property: "nobels"},
  // { entityKind: "PRO", heading: "Professors", description: "Total university professors (past and present)", icon: SchoolTeacherIcon, property: "professors"},
  // { entityKind: "PHD", heading: "Doctorates", description: "Total qualified to doctoral level", icon: AcademicCapIcon, property: "doctorates"},
]

const empty = [] as unknown

function copyStats(inStats: TopicStatistics[], outStats: TopicStatistics[]) {
  for (let inStat of inStats) {
    const outStat = {
      topic: {
        ...inStat.topic
      },
      entityStatistics: [] as EntityStatistics[],
      children: [] as TopicStatistics[],
    }
    for (let inEntityStat of inStat.entityStatistics) {
      const outEntityStat = {
        ...inEntityStat
      }
      outStat.entityStatistics.push(outEntityStat)
    }
    if (inStat.children)
      copyStats(inStat.children, outStat.children)
    for (let i = 0; i < (outStat.entityStatistics?.length ?? 0); i++) {
      const outEntityStat = outStat.entityStatistics[i]
      for (let j = 0; j < outStat.children.length; j++)
        outEntityStat.count += outStat.children[j]?.entityStatistics
          .find(s => s.entityKind == outEntityStat.entityKind)?.count ?? 0
    }
    outStats.push(outStat)
  }
}

export default function Dashboard() {
  const user = useAuth()
  const [status, setStatus] = useState<string>("PUB")
  const [rollup, setRollup] = useState<string>("full")

  const result = useQuery(READ_ALL_STATISTICS, {
    variables: {
      filter: {
        status: status ? [status] : undefined
      }
    }
  })
  const refetch = useCallback(() => {
    result.refetch({
      filter: {
        status: status ? [status] : undefined
      }
    })
  }, [result, status])
  useEffect(refetch, [status])

  const {loading, data} = result
  const entityStats = (data?.entityStatistics ?? result.previousData?.entityStatistics ?? empty) as unknown as EntityStatistics[]
  const rawTopicStats = (data?.topicStatistics ?? result.previousData?.topicStatistics ?? empty) as unknown as TopicStatistics[]
  const topicStats = useMemo(() => {
    let newStats;
    if (rollup === "full") {
      newStats = [] as TopicStatistics[]
      copyStats(rawTopicStats, newStats)
    } else {
      newStats = rawTopicStats
    }
    return newStats
  }, [rawTopicStats, rollup])

  const handleStatusChange = useCallback((value: string) => {
    setStatus(value === "ALL" ? '' : value)
  }, [])

  const table = useReactTable({
    columns,
    data: topicStats,
    defaultColumn: {
      size: 100
    },
    enableExpanding: true,
    getCoreRowModel: getCoreRowModel(),
    getExpandedRowModel: getExpandedRowModelEx(),
    getPaginationRowModel: getPaginationRowModel(),
    getSubRows: row => row.children,
    paginateExpandedRows: false,
  })

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <Spinner loading={loading} className="absolute inset-0 bg-black/20 z-50" />
      <div className="flex flex-row items-center">
        <ChartBarIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Dashboard</h1>
      </div>
      <h2>Topics & Record Links</h2>
      <div className="shrink flex-auto shadow-lg">
        <fieldset className="relative p-2 border rounded-md shadow-lg">
          <div className="flex flex-col pb-2">
            <div className="flex items-center gap-2">
              <Label htmlFor="status">Show items with status:</Label>
              <Select
                value={status}
                onValueChange={handleStatusChange}
              >
                <SelectTriggerEx
                  id="status"
                  disabled={!user}
                  help="Filter the table to show only topics and counts of linked records with this status.">
                  <SelectValue placeholder="Status" />
                </SelectTriggerEx>
                <SelectContent>
                  <SelectGroup>
                    <SelectLabel>Status Kinds</SelectLabel>
                    {
                      status
                      ? <SelectItem value="ALL">-Clear-</SelectItem>
                      : null
                    }
                    <SelectItem value="DRA">Draft</SelectItem>
                    <SelectItem value="PUB">Published</SelectItem>
                    <SelectItem value="SUS">Suspended</SelectItem>
                    <SelectItem value="DEL">Deleted</SelectItem>
                  </SelectGroup>
                </SelectContent>
              </Select>
              <Label htmlFor="">Aggregation:</Label>
              <RadioGroup defaultValue={rollup} value={rollup} onValueChange={setRollup}>
                <div className="flex items-center gap-3">
                  <RadioGroupItem id="none" value="none" />
                  <LabelEx htmlFor="non" help="Count values in each row relate to records linked to that row's topic alone. Drill down to reveal sub-topic statistics.">None</LabelEx>
                </div>
                <div className="flex items-center gap-3">
                  <RadioGroupItem id="full" value="full" />
                  <LabelEx htmlFor="full" help="Count values in each row include those from the corresponding columns in all sub-topics of the current row's topic.">Include sub-topics</LabelEx>
                </div>
              </RadioGroup>
              <ButtonEx
                variant="ghost"
                help="Refresh the table using the same filter settings."
                onClick={e => refetch()}
              >
                <RotateCw />
              </ButtonEx>
            </div>
          </div>
          <h2>Topic Statistics</h2>
          <div className="flex flex-col gap-2">
            <Table className="table-fixed box-border" style={{width: `${table.getTotalSize()}px`}}>
              <TableCaption>Topics and counts of linked records by type</TableCaption>
              <colgroup>
                {table.getHeaderGroups().map(headerGroup => headerGroup.headers.map(header => (
                  <col key={header.id} style={{width: `${header.getSize()}px`}} />)))
                }
              </colgroup>
              <TableHeader className="border bg-cyan-50">
                {table.getHeaderGroups().map(headerGroup => (
                  <TableRow key={headerGroup.id}>
                    {headerGroup.headers.map(header =>
                      <TableHead key={header.id} className="text-center border">
                        {flexRender(header.column.columnDef.header, header.getContext())}
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
                        <TableCell
                          key={cell.id}
                          className={cn("truncate border box-border", (cell.column.columnDef?.meta as ColumnMetaData)?.className)}
                          title={String(cell.getValue() ?? '')}>
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </TableCell>
                      ))}
                    </TableRow>
                    ))
                ) : (
                  <TableRow>
                    <TableCell className="border h-24 text-center" colSpan={columns.length}>
                      -No results-
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
            <DataTablePaginator table={table} />
          </div>
          <h2>Record Statistics</h2>
          <div className="flex flex-wrap gap-4">
            {
              entityItems.map(item => (
                <Link key={item.property} href={item.href ?? ""}>
                  <Card className="w-60 h-56 bg-cyan-50 shadow-lg transition-transform duration-300 ease-in-out transform hover:scale-105">
                    <CardHeader className="flex justify-center">
                      <item.icon className="w-8" />
                      <CardTitle className="text-2xl">{item.heading}</CardTitle>
                    </CardHeader>
                    <CardContent className="grid justify-items-center text-4xl">
                      {entityStats.find(s => s.entityKind === item.entityKind)?.count || "0"}
                    </CardContent>
                    <CardFooter className="text-center">
                      <p className="flex-grow text-center">{item.description}</p>
                    </CardFooter>
                  </Card>
                </Link>
              ))
            }
          </div>
        </fieldset>
      </div>
    </main>
  );
}
// Dashboard.whyDidYouRender = true