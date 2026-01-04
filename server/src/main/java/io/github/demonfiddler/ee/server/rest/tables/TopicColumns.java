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

import java.util.List;

import io.github.demonfiddler.ee.common.util.CompositeList;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;

public class TopicColumns {

    private static final Column<Topic> LABEL = new Column<>("label", "Label", ColumnType.STRING, FULL, null);
    private static final Column<Topic> DESCRIPTION =
        new Column<>("description", "Description", ColumnType.STRING, FULL, null);
    private static final Column<Topic> PARENT = new Column<>("parent", "Parent", ColumnType.STRING, FULL,
        (rec, raw) -> rec.getParent() != null ? rec.getParent().getLabel() : "");

    public static final List<Column<Topic>> OWN_COLUMNS = List.of(LABEL, DESCRIPTION, PARENT);
    public static final List<Column<Topic>> OWN_DEFAULT_COLUMNS = List.of(LABEL, DESCRIPTION);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private TopicColumns() {
        throw new UnsupportedOperationException();
    }

}
