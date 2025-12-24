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

package io.github.demonfiddler.ee.server.rest.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BackupUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupUtils.class);

    /** Static lookup tables. */
    public static final String[] STATIC_TABLES = { //
        "abbreviation", //
        "authority_kind", //
        "country", //
        "declaration_kind", //
        "entity_kind", //
        "publication_kind", //
        "status_kind", //
        "transaction_kind", //
    };

    /** Application data tables for backup. */
    public static final String[] APPDATA_TABLES_BACKUP = { //
        "claim", //
        "comment", //
        "declaration", //
        "entity", //
        "entity_link", //
        "group", //
        "group_authority", //
        "group_user", //
        "journal", //
        "log", //
        "person", //
        "publication", //
        "publisher", //
        "quotation", //
        "topic", //
        "user", //
        "user_authority", //
    };

    /** Tables that must be populated together (without RI constraints) when restoring users. */
    public static final String[] ENTITY_TABLES_RESTORE = { //
        "entity", //
        "user", //
    };

    /** Application data tables for restore. */
    public static final String[] APPDATA_TABLES_RESTORE = { //
        "claim", //
        "comment", //
        "declaration", //
        "entity_link", //
        "group", //
        "group_authority", //
        "group_user", //
        "journal", //
        "log", //
        "person", //
        "publication", //
        "publisher", //
        "quotation", //
        "topic", //
        "user_authority", //
    };

    /** Selects column name and type for a given table. */
    public static final String SELECT_TABLE_COLUMNS = """
        SELECT COLUMN_NAME, DATA_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = ?
        ORDER BY ORDINAL_POSITION;
        """;

    /**
     * Recursively deletes a file/directory.
     * @param file The file/directory to delete.
     * @param keep For a directory, delete the contents but retain the directory itself.
     */
    public void deleteFile(File file, boolean keep) {
        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles())
                    deleteFile(child, false);
                if (!keep)
                    file.delete();
            } else {
                file.delete();
            }
        } catch (SecurityException e) {
            try {
                LOGGER.error("Error deleting " + file.getCanonicalPath(), e);
            } catch (IOException _) {
                // Ignore
            }
        }
    }

}
