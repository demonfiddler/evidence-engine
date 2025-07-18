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

import { expandRows, getMemoOptions, memo, RowData, RowModel, Table } from "@tanstack/react-table"

/**
 * Fixes {@link https://github.com/TanStack/table/issues/5110}. Tables with {@code paginateExpandedRows == false} and
 * {manualPagination == true} do not auto-expand the row model when requested.
 * @returns The expanded row model
 */
export function getExpandedRowModelEx<TData extends RowData>(): (
  table: Table<TData>
) => () => RowModel<TData> {
  return table =>
    memo(
      () => [
        table.getState().expanded,
        table.getPreExpandedRowModel(),
        table.options.paginateExpandedRows,
        table.options.manualPagination,
      ],
      (expanded, rowModel, paginateExpandedRows, manualPagination) => {
        if (
          !rowModel.rows.length ||
          (expanded !== true && !Object.keys(expanded ?? {}).length)
        ) {
          return rowModel
        }

        if (!paginateExpandedRows && !manualPagination) {
          // Only expand rows at this point if they are being paginated
          return rowModel
        }

        return expandRows(rowModel)
      },
      getMemoOptions(table.options, 'debugTable', 'getExpandedRowModel')
    )
}