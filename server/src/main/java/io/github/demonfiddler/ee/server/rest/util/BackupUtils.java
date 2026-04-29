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

import static io.github.demonfiddler.ee.common.util.StringUtils.unquote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BackupUtils {

    /**
     * Describes a table that is the target of a backup/restore operation.
     * @param name The table name.
     * @param tsColumn The name of the timestamp column. Only applicable if {@code isTrackedEntity} is {@code false}.
     * @param isTrackedEntity {@code true} if the table is used to persist instances of {@code ITrackedEntity}.
     * @param needsJoin {@code true} if the {@code SELECT} query needs to join the {@code entity} table in order to
     * apply the timestamp predicate. Only applicable if {@code isTrackedEntity} is {@code true}.
     */
    public static record TableDescriptor(String name, String tsColumn, boolean isTrackedEntity, boolean needsJoin) {

        /**
         * Describes a static table with no timestamp.
         * @param name The table name.
         * @return A new {@code TableDescriptor}.
         */
        public static TableDescriptor untracked(String name) {
            return new TableDescriptor(name, null, false, false);
        }

        /**
         * Describes an {@code ITrackedEntity} table that is joined to the {@code entity} table.
         * @param name The table name.
         * @return A new {@code TableDescriptor}.
         */
        public static TableDescriptor tracked(String name) {
            return new TableDescriptor(name, null, true, true);
        }

        /**
         * Describes an {@code ITrackedEntity} table.
         * @param name The table name.
         * @param needsJoin {@code true} if the {@code name} table is joined to the {@code entity} table.
         * @return A new {@code TableDescriptor}.
         */
        public static TableDescriptor tracked(String name, boolean needsJoin) {
            return new TableDescriptor(name, null, true, needsJoin);
        }

        /**
         * Describes a timestamped table that is not an {@code ITrackedEntity} table.
         * @param name The table name.
         * @param tsColumn The name of the timestamp column.
         * @return A new {@code TableDescriptor}.
         */
        public static TableDescriptor timestamped(String name, String tsColumn) {
            return new TableDescriptor(name, tsColumn, false, false);
        }

        /**
         * Returns whether the table supports incremental backup. To do so it must either be a tracked entity or have a timestamp column.
         * @return {@code true} if the table supports incremental backup.
         */
        public boolean supportsIncrementalBackup() {
            return isTrackedEntity || tsColumn != null;
        }

        @Override
        public String toString() {
            return "TableDescriptor[name=" + name + ", tsColumn=" + tsColumn + ", isTrackedEntity=" + isTrackedEntity
                + ", needsJoin=" + needsJoin + ']';
        }

    }

    /**
     * A matched pair of SQL select statements for retrieving a row count and the actual rows themselves.
     */
    public static record QueryPair(String countQuerySql, String selectQuerySql) {

        @Override
        public String toString() {
            return "-- Count query SQL:\n" + countQuerySql + "\n-- Select query SQL:\n" + selectQuerySql;
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupUtils.class);

    /** Static lookup tables. */
    public static final TableDescriptor[] STATIC_TABLES = { //
        TableDescriptor.untracked("abbreviation"), //
        TableDescriptor.untracked("authority_kind"), //
        TableDescriptor.untracked("country"), //
        TableDescriptor.untracked("declaration_kind"), //
        TableDescriptor.untracked("entity_kind"), //
        TableDescriptor.untracked("publication_kind"), //
        TableDescriptor.untracked("status_kind"), //
        TableDescriptor.untracked("transaction_kind"), //
    };

    /** Application data tables. */
    public static final TableDescriptor[] APPDATA_TABLES = { //
        TableDescriptor.tracked("claim"), //
        TableDescriptor.tracked("comment"), //
        TableDescriptor.untracked("config"), //
        TableDescriptor.tracked("declaration"), //
        TableDescriptor.tracked("entity", false), //
        TableDescriptor.tracked("entity_link"), //
        TableDescriptor.tracked("group"), //
        TableDescriptor.untracked("group_authority"), //
        TableDescriptor.untracked("group_user"), //
        TableDescriptor.tracked("journal"), //
        TableDescriptor.timestamped("log", "timestamp"), //
        TableDescriptor.tracked("person"), //
        TableDescriptor.tracked("publication"), //
        TableDescriptor.tracked("publisher"), //
        TableDescriptor.tracked("quotation"), //
        TableDescriptor.tracked("topic"), //
        TableDescriptor.tracked("user"), //
        TableDescriptor.untracked("user_authority"), //
    };

    /** Selects column name and type for a given table. */
    public static final String SELECT_TABLE_COLUMNS = """
        SELECT COLUMN_NAME, DATA_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = ?
        ORDER BY ORDINAL_POSITION;
        """;

    @Value("${data.server.tmpdir}")
    private String tmpDir;

    /**
     * Returns the path to the backup/restore temporary directory.
     * @return The backup/restore path, with a trailing file separator character.
     */
    public String getBackupPath() {
        return tmpDir + (tmpDir.endsWith(File.separator) ? "" : File.separatorChar) + "ee-backup" + File.separatorChar;
    }

    /**
     * Prepares a temporary directory for a backup or restore operation. The backup path is given by {@link #getBackupPath()}.
     * @return An HTTP error response if the operation failed; otherwise {@code null}.
     */
    public String createBackupDirectory() {
        String outputPath = getBackupPath();
        Path tmpdir = Paths.get(outputPath);
        File outputDir = tmpdir.toFile();

        try {
            Files.createDirectories(tmpdir);
            deleteFile(outputDir, true);
            LOGGER.info("Files.createDirectories succeeded");
        } catch (Exception e) {
            String errmsg = "Failed to create directory " + tmpdir;
            LOGGER.error(errmsg);
            return errmsg;
        }
        if (!outputDir.exists()) {
            String errmsg = "Failed to create temporary directory " + outputDir.getAbsolutePath();
            LOGGER.error(errmsg);
            return errmsg;
        } else if (!outputDir.isDirectory()) {
            String errmsg = "Path " + tmpdir + " exists but is not a directory";
            LOGGER.error(errmsg);
            return errmsg;
        }

        // Ensure that MariaDB can write to the temporary directory in production.

        try {
            // Linux: apply POSIX permissions
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwx---"); // 770
            Files.setPosixFilePermissions(tmpdir, perms);
            LOGGER.info("Set POSIX file permissions on " + tmpdir);
        } catch (UnsupportedOperationException e) {
            // Windows: ignore, filesystem does not support POSIX permissions
            // LOGGER.error("Failed to set POSIX file permissions on " + tmpdir, e);
        } catch (IOException e) {
            String errmsg = "Failed to set permissions on " + tmpdir;
            LOGGER.error(errmsg);
            return errmsg;
        }

        try {
            // Linux: change POSIX group to ee so that both Tomcat and MariaDB can read/write to the directory.
            PosixFileAttributeView view = Files.getFileAttributeView(outputDir.toPath(), PosixFileAttributeView.class);
            GroupPrincipal eeGroup =
                tmpdir.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("ee");
            view.setGroup(eeGroup);
            LOGGER.info("Set POSIX file group on " + tmpdir);
        } catch (UnsupportedOperationException _) {
            // Windows: ignore, filesystem does not support POSIX permissions
        } catch (UserPrincipalNotFoundException _) {
            // Assume this means we're on Windows, where the group doesn't matter.
        } catch (IOException e) {
            String errmsg = "Failed to set group on " + tmpdir;
            LOGGER.error(errmsg);
            return errmsg;
        }

        return null;
    }

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

}
