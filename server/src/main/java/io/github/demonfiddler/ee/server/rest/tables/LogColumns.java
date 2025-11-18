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
import io.github.demonfiddler.ee.server.model.Log;
import io.github.demonfiddler.ee.server.model.TransactionKind;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;

public class LogColumns {

    private static final Column<Log> TIMESTAMP =
        new Column<>("timestamp", "Timestamp", ColumnType.DATETIME, SINGLE, null);
    private static final Column<Log> USER = new Column<>("user", "User", ColumnType.STRING, SINGLE,
        (rec, raw) -> rec.getUser() != null ? rec.getUser().getUsername() : "");
    private static final Column<Log> TRANSACTION_KIND = new Column<>("transactionKind", "Transaction", ColumnType.STRING, SINGLE,
        (rec, raw) -> raw ? rec.getTransactionKind() : TransactionKind.valueOf(rec.getTransactionKind()).label());
    private static final Column<Log> ENTITY_KIND = new Column<>("entityKind", "Record Kind", ColumnType.STRING, SINGLE,
        (rec, raw) -> raw ? rec.getEntityKind() : EntityKind.valueOf(rec.getEntityKind()).label());
    private static final Column<Log> ENTITY_ID = new Column<>("entityId", "Record ID", ColumnType.ID, SINGLE, null);
    private static final Column<Log> LINKED_ENTITY_KIND = new Column<>("linkedEntityKind", "Linked Record Kind",
        ColumnType.STRING, SINGLE, (rec, raw) -> rec.getLinkedEntityKind() != null
            ? raw ? rec.getLinkedEntityKind() : EntityKind.valueOf(rec.getLinkedEntityKind()).label() : "");
    private static final Column<Log> LINKED_ENTITY_ID =
        new Column<>("linkedEntityId", "Linked Record ID", ColumnType.ID, SINGLE, null);

    public static final List<Column<Log>> OWN_COLUMNS =
        List.of(TIMESTAMP, USER, TRANSACTION_KIND, ENTITY_KIND, ENTITY_ID, LINKED_ENTITY_KIND, LINKED_ENTITY_ID);
    public static final List<Column<Log>> OWN_DEFAULT_COLUMNS =
        List.of(TIMESTAMP, USER, TRANSACTION_KIND, ENTITY_KIND, ENTITY_ID);
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(BaseEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(BaseEntityColumns.ALL_COLUMNS, OWN_COLUMNS));

    /** Private ctor prevents instantiation. */
    private LogColumns() {
        throw new UnsupportedOperationException();
    }

}
