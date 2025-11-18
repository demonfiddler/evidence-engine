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
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public class PersonColumns {

    private static final Column<Person> TITLE = new Column<>("title", "Title", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> FIRST_NAME =
        new Column<>("firstName", "First name", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> NICKNAME =
        new Column<>("nickname", "Nickname", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> PREFIX = new Column<>("prefix", "Prefix", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> LAST_NAME =
        new Column<>("lastName", "Last name", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> SUFFIX = new Column<>("suffix", "Suffix", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> ALIAS = new Column<>("alias", "Alias", ColumnType.STRING, SINGLE, null);
    private static final Column<Person> QUALIFICATIONS =
        new Column<>("qualifications", "Qualifications", ColumnType.STRING, FULL, null);
    private static final Column<Person> NOTES = new Column<>("notes", "Notes", ColumnType.STRING, FULL, null);
    private static final Column<Person> COUNTRY = new Column<>("country", "Country", ColumnType.STRING, SINGLE,
        (rec, raw) -> RenderUtils.instance.renderCountry(rec.getCountry()));
    private static final Column<Person> CHECKED = new Column<>("checked", "Checked", ColumnType.BOOLEAN, SINGLE, null);
    private static final Column<Person> PUBLISHED =
        new Column<>("published", "Published", ColumnType.BOOLEAN, SINGLE, null);

    public static final List<Column<Person>> OWN_COLUMNS = List.of(TITLE, FIRST_NAME, NICKNAME, PREFIX, LAST_NAME,
        SUFFIX, ALIAS, QUALIFICATIONS, NOTES, COUNTRY, CHECKED, PUBLISHED);
    public static final List<Column<Person>> ONW_DEFAULT_COLUMNS =
        List.of(TITLE, FIRST_NAME, LAST_NAME, COUNTRY, CHECKED, PUBLISHED);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, ONW_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private PersonColumns() {
        throw new UnsupportedOperationException();
    }

}
