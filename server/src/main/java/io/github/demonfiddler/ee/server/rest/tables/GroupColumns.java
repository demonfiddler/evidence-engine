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

package io.github.demonfiddler.ee.server.rest.tables;

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.FULL;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.SINGLE;

import java.util.List;

import io.github.demonfiddler.ee.common.util.CompositeList;
import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;

public class GroupColumns {

    private static final Column<Group> GROUPNAME = new Column<>("groupname", "Name", ColumnType.STRING, SINGLE, null);
    private static final Column<Group> AUTHORITIES =
        new Column<>("authorities", "Authorities", ColumnType.STRING, FULL, null);
    private static final Column<Group> MEMBERS = new Column<>("members", "Members", ColumnType.STRING, FULL,
        (rec, raw) -> rec.getMembers().stream().map(u -> u.getUsername()).toList().toString());

    public static final List<Column<Group>> OWN_COLUMNS = List.of(GROUPNAME, AUTHORITIES, MEMBERS);
    public static final List<Column<Group>> OWN_DEFAULT_COLUMNS = List.of(GROUPNAME);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private GroupColumns() {
        throw new UnsupportedOperationException();
    }

}
