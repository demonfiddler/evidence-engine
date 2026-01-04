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

package io.github.demonfiddler.ee.server.rest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
        "config", //
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
        "config", //
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
     * @param empty For a directory, delete the contents but retain the directory itself.
     */
    public void deleteFile(File file, boolean empty) {
        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles())
                    deleteFile(child, false);
                if (!empty)
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

    public Map<String, String> readConfiguration(File configFile) throws IOException {
        Map<String, String> config = new LinkedHashMap<>();
        try (BufferedReader rdr = new BufferedReader(new FileReader(configFile, Charset.forName("UTF-8")))) {
            String line;
            int lineNum = 0;
            while ((line = rdr.readLine()) != null) {
                if (lineNum++ == 0)
                    continue;
                StringTokenizer tok = new StringTokenizer(line, ",");
                if (tok.countTokens() == 3) {
                    String property = unquote(tok.nextToken());
                    String subscript = unquote(tok.nextToken());
                    String value = unquote(tok.nextToken());
                    if (subscript.equals("0")) {
                        config.put(property, value);
                        LOGGER.trace("readConfiguration: read property {} = \"value\"", property, value);
                    } else {
                        LOGGER.warn("readConfiguration: ignoring multi-valued property {}[{}] = \"{}\"", property, subscript, value);
                    }
                } else {
                    LOGGER.error("readConfiguration: malformed line #{}: {}", lineNum, line);
                }
            }
        }
        LOGGER.debug("readConfiguration: returning {}", config);

        return config;
    }

    private String unquote(String s) {
        int start = s.charAt(0) == '"' ? 1 : 0;
        int end = s.charAt(s.length() - 1) == '"' ? s.length() - 1 : s.length();
        return s.substring(start, end);
    }

}
