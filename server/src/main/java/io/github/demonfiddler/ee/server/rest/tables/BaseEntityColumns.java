/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

package io.github.demonfiddler.ee.server.rest.tables;

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.SINGLE;

import java.util.List;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;

public class BaseEntityColumns {

    private static final Column<IBaseEntity> ID = new Column<>("id", "ID", ColumnType.ID, SINGLE,
        (rec, raw) -> rec.getId() != null ? rec.getId().toString() : "");

    public static final List<Column<IBaseEntity>> OWN_COLUMNS = List.of(ID);
    public static final List<Column<IBaseEntity>> OWN_DEFAULT_COLUMNS = List.of(ID);
    public static final List<Column<IBaseEntity>> ALL_COLUMNS = OWN_COLUMNS;
    public static final List<Column<IBaseEntity>> ALL_DEFAULT_COLUMNS = OWN_DEFAULT_COLUMNS;
    // public static final Map<String, Column<IBaseEntity>> ALL_DEFAULT_COLUMNS_MAP = Column.map(ALL_DEFAULT_COLUMNS);
    // public static final Map<String, Column<IBaseEntity>> ALL_COLUMNS_MAP = Column.map(ALL_COLUMNS);

    /** Private ctor prevents instantiation. */
    private BaseEntityColumns() {
        throw new UnsupportedOperationException();
    }

}
