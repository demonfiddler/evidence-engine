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

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.FULL;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.SINGLE;

import java.util.List;

import io.github.demonfiddler.ee.common.util.CompositeList;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public class JournalColumns {

    private static final Column<Journal> TITLE = new Column<>("title", "Title", ColumnType.STRING, FULL, null);
    private static final Column<Journal> ABBREVIATION =
        new Column<>("abbreviation", "Abbreviation", ColumnType.STRING, FULL, null);
    private static final Column<Journal> URL = new Column<>("url", "URL", ColumnType.URL, FULL, null);
    private static final Column<Journal> ISSN = new Column<>("issn", "ISSN", ColumnType.STRING, SINGLE, (rec, raw) -> {
        String issn = rec.getIssn() != null ? rec.getIssn() : "";
        return raw ? issn : !issn.isEmpty()
            ? RenderUtils.instance.renderUrl("https://portal.issn.org/resource/ISSN/" + issn, issn, raw) : "";
    });
    private static final Column<Journal> PUBLISHER = new Column<>("publisher", "Publisher", ColumnType.STRING, FULL,
        (rec, raw) -> RenderUtils.instance.renderEntityLabel(rec.getPublisher()));
    private static final Column<Journal> NOTES = new Column<>("notes", "Notes", ColumnType.STRING, FULL, null);

    public static final List<Column<Journal>> OWN_COLUMNS = List.of(TITLE, ABBREVIATION, URL, ISSN, PUBLISHER, NOTES);
    public static final List<Column<Journal>> OWN_DEFAULT_COLUMNS = List.of(TITLE, ISSN);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private JournalColumns() {
        throw new UnsupportedOperationException();
    }

}
