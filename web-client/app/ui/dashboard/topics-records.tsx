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
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"

const topicData = [
    {label: "Climate", description: "Contrarian climate science", claims: 22, declarations: 11, persons: 2977, publications: 147, quotations: 21, total: 3178},
    {label: "RF Radiation", description: "Adverse effects of RF radiation on living systems", claims: 18, declarations: 7, persons: 271, publications: 3238, quotations: 13, total: 3547},
    {label: "Fluoride", description: "Fluoridation of public water supplies", claims: 9, declarations: 4, persons: 37, publications: 52, quotations: 4, total: 106},
    {label: "Vaccines", description: "Adverse vaccine reactions", claims: 17, declarations: 22, persons: 123, publications: 208, quotations: 12, total: 382},
];

export default function TopicsAndRecords({
    headerClassName,
    className,
    captionClassName
  }: {
    headerClassName?: string;
    className?: string;
    captionClassName?: string;
  }) {
  return (
    <Table className={className}>
      <TableCaption className={captionClassName}>Top-level topics and linked records</TableCaption>
      <TableHeader className={headerClassName}>
        <TableRow className={className}>
          <TableHead className={`${className} text-center`}>Topic</TableHead>
          <TableHead className={`${className} text-center`}>Description</TableHead>
          <TableHead className={`${className} text-center`}>Claims</TableHead>
          <TableHead className={`${className} text-center`}>Declarations</TableHead>
          <TableHead className={`${className} text-center`}>Persons</TableHead>
          <TableHead className={`${className} text-center`}>Publications</TableHead>
          <TableHead className={`${className} text-center`}>Quotations</TableHead>
          <TableHead className={`${className} text-center`}>Total</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {
          topicData.map(item => (
            <TableRow key={item.label} className={className}>
              <TableCell className={className}>{item.label}</TableCell>
              <TableCell className={className}>{item.description}</TableCell>
              <TableCell className={`${className} text-right`}>{item.claims}</TableCell>
              <TableCell className={`${className} text-right`}>{item.declarations}</TableCell>
              <TableCell className={`${className} text-right`}>{item.persons}</TableCell>
              <TableCell className={`${className} text-right`}>{item.publications}</TableCell>
              <TableCell className={`${className} text-right`}>{item.quotations}</TableCell>
              <TableCell className={`${className} text-right`}>{item.total}</TableCell>
            </TableRow>
          ))
        }
      </TableBody>
    </Table>
  )
}