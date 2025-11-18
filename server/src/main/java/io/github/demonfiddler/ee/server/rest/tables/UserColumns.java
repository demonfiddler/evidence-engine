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
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public class UserColumns {

    private static final Column<User> USERNAME = new Column<>("username", "User Name", ColumnType.STRING, SINGLE, null);
    private static final Column<User> FIRST_NAME =
        new Column<>("firstName", "First Name", ColumnType.STRING, SINGLE, null);
    private static final Column<User> LAST_NAME =
        new Column<>("lastName", "Last Name", ColumnType.STRING, SINGLE, null);
    private static final Column<User> EMAIL = new Column<>("email", "E-mail", ColumnType.STRING, SINGLE, null);
    private static final Column<User> PASSWORD = new Column<>("password", "Password", ColumnType.STRING, FULL, null);
    private static final Column<User> COUNTRY = new Column<>("country", "Country", ColumnType.STRING, SINGLE,
        (rec, raw) -> RenderUtils.instance.renderCountry(rec.getCountry()));
    private static final Column<User> NOTES = new Column<>("notes", "Notes", ColumnType.STRING, FULL, null);
    private static final Column<User> AUTHORITIES =
        new Column<>("authorities", "Authorities", ColumnType.STRING, SINGLE, null);
    private static final Column<User> GROUPS = new Column<>("groups", "Groups", ColumnType.STRING, FULL,
        (rec, raw) -> rec.getGroups().stream().map(g -> g.getGroupname()).toList().toString());

    public static final List<Column<User>> OWN_COLUMNS =
        List.of(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, COUNTRY, NOTES, AUTHORITIES, GROUPS);
    public static final List<Column<User>> OWN_DEFAULT_COLUMNS = List.of(USERNAME, FIRST_NAME, LAST_NAME);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private UserColumns() {
        throw new UnsupportedOperationException();
    }

}
