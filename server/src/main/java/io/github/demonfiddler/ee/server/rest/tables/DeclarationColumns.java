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
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.DeclarationKind;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public class DeclarationColumns {

    private static final Column<Declaration> KIND = new Column<>("kind", "Kind", ColumnType.STRING, SINGLE,
        (rec, raw) -> DeclarationKind.valueOf(rec.getKind()).label());
    private static final Column<Declaration> TITLE = new Column<>("title", "Title", ColumnType.STRING, FULL, null);
    private static final Column<Declaration> DATE = new Column<>("date", "Date", ColumnType.DATE, SINGLE, null);
    private static final Column<Declaration> COUNTRY = new Column<>("country", "Country", ColumnType.STRING, SINGLE,
        (rec, raw) -> RenderUtils.instance.renderCountry(rec.getCountry()));
    private static final Column<Declaration> URL = new Column<>("url", "URL", ColumnType.URL, SINGLE, null);
    private static final Column<Declaration> CACHED = new Column<>("cached", "Cached", ColumnType.BOOLEAN, SINGLE, null);
    private static final Column<Declaration> SIGNATORIES =
        new Column<>("signatories", "Signatories", ColumnType.STRING, FULL, null);
    private static final Column<Declaration> SIGNATORY_COUNT =
        new Column<>("signatoryCount", "Sig. count", ColumnType.NUMBER, SINGLE, null);
    private static final Column<Declaration> NOTES = new Column<>("notes", "Notes", ColumnType.STRING, FULL, null);

    public static final List<Column<Declaration>> OWN_COLUMNS =
        List.of(KIND, TITLE, DATE, COUNTRY, URL, CACHED, SIGNATORIES, SIGNATORY_COUNT, NOTES);
    public static final List<Column<Declaration>> OWN_DEFAULT_COLUMNS = List.of(KIND, TITLE, DATE, COUNTRY);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private DeclarationColumns() {
        throw new UnsupportedOperationException();
    }

}
