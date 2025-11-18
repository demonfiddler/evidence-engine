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

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.SINGLE;

import java.util.List;
import io.github.demonfiddler.ee.common.util.CompositeList;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;

public class TrackedEntityColumns {

    static final String renderRating(ITrackedEntity record, boolean raw) {
        Integer rating = record.getRating();
        if (raw) {
            return rating != null ? rating.toString() : "";
        } else {
            StringBuffer buffer = new StringBuffer();
            int stars = rating != null ? rating : 0;
            for (int i = 1; i <= 5; i++) {
                buffer.append(i <= stars ? "&#9733;" : "&#9734;");
            }
            return buffer.toString();
        }
    }

    static final String renderUser(User user) {
        return user != null ? user.getUsername() : "";
    }

    private static final Column<ITrackedEntity> STATUS = new Column<>("status", "Status", ColumnType.STRING, SINGLE,
        (rec, raw) -> StatusKind.valueOf(rec.getStatus()).label());
    private static final Column<ITrackedEntity> RATING =
        new Column<>("rating", "Rating", ColumnType.NUMBER, SINGLE, TrackedEntityColumns::renderRating);
    private static final Column<ITrackedEntity> CREATED = new Column<>("created", "Created", ColumnType.DATETIME, SINGLE, null);
    private static final Column<ITrackedEntity> CREATED_BY = new Column<>("createdByUsername", "Created by", ColumnType.STRING,
        SINGLE, (rec, raw) -> renderUser(rec.getCreatedByUser()));
    private static final Column<ITrackedEntity> UPDATED = new Column<>("updated", "Updated", ColumnType.DATETIME, SINGLE, null);
    private static final Column<ITrackedEntity> UPDATED_BY = new Column<>("updatedByUsername", "Updated by", ColumnType.STRING,
        SINGLE, (rec, raw) -> renderUser(rec.getUpdatedByUser()));

    public static final List<Column<ITrackedEntity>> OWN_COLUMNS =
        List.of(STATUS, RATING, CREATED, CREATED_BY, UPDATED, UPDATED_BY);
    public static final List<Column<ITrackedEntity>> OWN_DEFAULT_COLUMNS = List.of(STATUS, RATING);
    static List<List<? extends Column<? extends IBaseEntity>>> x = List.of(BaseEntityColumns.ALL_COLUMNS, OWN_COLUMNS);
    public static final CompositeList<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(BaseEntityColumns.ALL_COLUMNS, OWN_COLUMNS));
    public static final CompositeList<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(BaseEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    // public static final Map<String, Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS_MAP = Column.map(ALL_DEFAULT_COLUMNS);
    // public static final Map<String, Column<? extends IBaseEntity>> ALL_COLUMNS_MAP = Column.map(ALL_COLUMNS);

    /** Private ctor prevents instantiation. */
    private TrackedEntityColumns() {
        throw new UnsupportedOperationException();
    }

}
