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

package io.github.demonfiddler.ee.server.rest.api;

import static io.github.demonfiddler.ee.common.util.StringUtils.NL;
import static io.github.demonfiddler.ee.server.rest.util.BackupUtils.APPDATA_TABLES;
import static io.github.demonfiddler.ee.server.rest.util.BackupUtils.SELECT_TABLE_COLUMNS;
import static io.github.demonfiddler.ee.server.rest.util.BackupUtils.STATIC_TABLES;
import static io.github.demonfiddler.ee.server.rest.util.DatabaseUtils.PROP_SCHEMA_VERSION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import io.github.demonfiddler.ee.server.rest.model.BackupKind;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils;
import io.github.demonfiddler.ee.server.rest.util.DatabaseUtils;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils.TableDescriptor;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-12-21T13:57:39.773008Z[Europe/London]", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.evidenceEngineRESTInterfaceOpenAPI311.base-path:/rest}")
public class RestoreApiController implements RestoreApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestoreApiController.class);
    private static final Map<String, String> IMPORT_STATEMENTS = Collections.synchronizedMap(new HashMap<>());

    private final NativeWebRequest request;
    private final PlatformTransactionManager txManager;
    private final JdbcTemplate jdbcTemplate;
    private final BackupUtils backupUtils;
    private final DatabaseUtils databaseUtils;
    @Value("${data.server.tmpdir}")
    private String tmpDir;

    public RestoreApiController(NativeWebRequest request, PlatformTransactionManager txManager,
        JdbcTemplate jdbcTemplate, BackupUtils backupUtils, DatabaseUtils databaseUtils) {
        this.request = request;
        this.txManager = txManager;
        this.jdbcTemplate = jdbcTemplate;
        this.backupUtils = backupUtils;
        this.databaseUtils = databaseUtils;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<String> restore(@NotNull @Valid String kind, MultipartFile file) {
        BackupKind backupKind;
        switch (kind) {
            case "all":
                backupKind = BackupKind.ALL;
                break;
            case "full":
                backupKind = BackupKind.FULL;
                break;
            case "incremental":
                backupKind = BackupKind.INCREMENTAL;
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        LOGGER.debug("Restoring backup, backupKind='{}'", backupKind);

        // N.B. The remaining code only works if the application and database servers are colocated,
        // because unzipping writes to, and the LOAD DATA LOCAL INFILE statement reads from, local files.

        // MariaDB may not have permission to access ${java.io.tmpdir}, so we'll have to use a different location.
        String outputPath = backupUtils.getBackupPath();
        File outputDir = new File(outputPath);
        String errmsg = backupUtils.createBackupDirectory();
        if (errmsg != null)
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);

        // Unzip the backup set by copying each entry to disk.
        List<String> csvFilenames = new ArrayList<>();
        byte[] buf = new byte[65536];
        try (ZipInputStream zipIn = new ZipInputStream(file.getInputStream())) {
            LOGGER.trace("Opened backup set in '{}'", file.getOriginalFilename());

            ZipEntry csvEntry;
            while ((csvEntry = zipIn.getNextEntry()) != null) {
                String csvFilename = csvEntry.getName();
                File csvFile = new File(outputDir, csvFilename);
                int count;
                try (FileOutputStream csvOut = new FileOutputStream(csvFile)) {
                    while ((count = zipIn.read(buf)) != -1)
                        csvOut.write(buf, 0, count);
                } finally {
                    zipIn.closeEntry();
                    csvFilenames.add(csvFilename);
                }
                LOGGER.trace("Read ZipEntry '{}'", csvFilename);
            }
        } catch (IOException e) {
            LOGGER.error("Error unzipping backup set", e);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Unable to read backup set");
        }

        // First check that the backup set contains all the required files.
        List<String> missingFilenames = new ArrayList<>();
        if (backupKind == BackupKind.ALL)
            checkForMissingFiles(STATIC_TABLES, csvFilenames, missingFilenames);
        checkForMissingFiles(APPDATA_TABLES, csvFilenames, missingFilenames);
        if (!missingFilenames.isEmpty()) {
            errmsg = "Files missing from backup set: " + missingFilenames;
            LOGGER.error(errmsg);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
        }

        // Read the backup set config file.
        Map<String, String> config;
        try {
            File configFile = new File(outputDir, "config.csv");
            config = backupUtils.readConfiguration(configFile);
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Error reading backup set configuration", e);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
                .body("Unable to read configuration from backup set");
        }

        // Check that the backup set's database schema version matches that of this server.
        int schemaVersion = Integer.parseInt(config.getOrDefault(PROP_SCHEMA_VERSION, "0"));
        if (!databaseUtils.checkDatabaseSchemaVersion(schemaVersion)) {
            errmsg = "Backup set has an incompatible schema version; expected: "
                + DatabaseUtils.CURRENT_SCHEMA_VERSION + ", found: " + schemaVersion;
            LOGGER.error(errmsg);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
        }

        // Read and validate incoming configuration.
        String newBackupId = config.get(DatabaseUtils.PROP_BACKUP_ID);
        Integer newBackupNumber = databaseUtils.parseInteger(config.get(DatabaseUtils.PROP_BACKUP_NUMBER));
        Timestamp newBackupTimestamp = databaseUtils.parseTimestamp(config.get(DatabaseUtils.PROP_BACKUP_TIMESTAMP));
        if (newBackupId == null || newBackupNumber == null || newBackupTimestamp == null) {
            errmsg = "Backup set configuration invalid; backup_id = " + newBackupId + ", backup_number = "
                + newBackupNumber + ", backup_timestamp = " + newBackupTimestamp;
            LOGGER.error(errmsg);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
        }

        // If restoring an incremental backup, make sure that we have already restored the full backup and all previous
        // backups in this series.
        if (backupKind == BackupKind.INCREMENTAL) {
            // Read previous configuration.
            String previousBackupId = databaseUtils.getConfigString(DatabaseUtils.PROP_BACKUP_ID);
            Integer previousBackupNumber = databaseUtils.getConfigInteger(DatabaseUtils.PROP_BACKUP_NUMBER);
            Timestamp previousBackupTimestamp = databaseUtils.getConfigTimestamp(DatabaseUtils.PROP_BACKUP_TIMESTAMP);

            if (previousBackupId == null || previousBackupNumber == null || previousBackupTimestamp == null) {
                errmsg = "Database configuration invalid; backup_id = " + previousBackupId + ", backup_number = "
                    + previousBackupNumber + ", last_backup_timestamp = " + previousBackupTimestamp;
                LOGGER.error(errmsg);
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            }

            if (!Objects.equals(newBackupId, previousBackupId)) {
                errmsg =
                    "Cannot restore incremental backup set because it is from a different series; expected: "
                        + previousBackupId + ", found: " + newBackupId;
                LOGGER.error(errmsg);
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            }

            if (newBackupNumber == 0) {
                errmsg = "Cannot restore a full backup set incrementally";
                LOGGER.error(errmsg);
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            }

            int expectedBackupNumber = previousBackupNumber + 1;
            if (newBackupNumber != expectedBackupNumber) {
                errmsg =
                    "Cannot restore incremental backup set out of sequence; expected backup number: "
                        + expectedBackupNumber + ", found: " + newBackupNumber;
                LOGGER.error(errmsg);
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            }

            // Check whether any records have been added or updated since the incoming backup timestamp.
            Timestamp[] timestamps = { null, null };
            jdbcTemplate.query("SELECT MAX(\"created\"), MAX(\"updated\") FROM \"entity\";",
                (PreparedStatementSetter)null, (RowCallbackHandler)rs -> {
                    timestamps[0] = rs.getTimestamp(1);
                    timestamps[1] = rs.getTimestamp(2);
                });
            if (timestamps[0] != null && timestamps[0].after(previousBackupTimestamp)
                || timestamps[1] != null && timestamps[1].after(previousBackupTimestamp)) {

                errmsg =
                    "Cannot restore incremental backup set as target database has been modified since the backup date of: "
                        + previousBackupTimestamp;
                LOGGER.error(errmsg);
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            }
        } else if (newBackupNumber != 0) {
            errmsg = "Cannot perform a full restore: supplied backup set is incremental";
            LOGGER.error(errmsg);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(errmsg);
        }

        // The entire restore operation must be performed transactionally.
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Restore");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // If required, restore lookup tables.
            if (backupKind == BackupKind.ALL) {
                for (TableDescriptor table : STATIC_TABLES) {
                    // Delete existing lookup table entries.
                    try {
                        jdbcTemplate.update("DELETE FROM \"" + table.name() + "\";");
                    } catch (DataAccessException e) {
                        txManager.rollback(status);

                        errmsg = "Error deleting lookup data from table: " + table.name();
                        LOGGER.error(errmsg, e);
                        return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);
                    }
                }
                restoreTables(STATIC_TABLES, outputPath, backupKind);
            }

            // For all/full restore, delete existing tracked entities (cascades to all joined tables).
            if (backupKind != BackupKind.INCREMENTAL) {
                try {
                    jdbcTemplate.update("DELETE FROM \"config\";");
                    jdbcTemplate.update("DELETE FROM \"entity\";");
                } catch (DataAccessException e) {
                    txManager.rollback(status);
    
                    errmsg = "Error deleting existing config/application data";
                    LOGGER.error(errmsg, e);
                    return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);
                }
            }

            try {
                // Temporarily disable RI constraints while we restore application data.
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");

                // Restore application data tables.
                restoreTables(APPDATA_TABLES, outputPath, backupKind);
            } catch (DataAccessException e) {
                txManager.rollback(status);

                errmsg = "Error restoring entity tables";
                LOGGER.error(errmsg, e);
                return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);
            } finally {
                try {
                    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1;");
                } catch (DataAccessException e) {
                    txManager.rollback(status);

                    errmsg = "Error setting FOREIGN_KEY_CHECKS=1";
                    LOGGER.error(errmsg, e);
                    return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);
                }
                // This 'nice-to-have' code requires the database user (typically 'ee') to have ALTER permission.
                try {
                    resetAutoIncrement("entity");
                    resetAutoIncrement("log");
                } catch (DataAccessException e) {
                    txManager.rollback(status);

                    errmsg = "Error resetting AUTO_INCREMENT";
                    LOGGER.error(errmsg, e);
                    return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(errmsg);
                }
            }

            // Finally, commit the transaction.
            txManager.commit(status);

            LOGGER.debug("Restore complete");

            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain")) //
                .body("Backup set restored successfully");
        } catch (Throwable t) {
            txManager.rollback(status);
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(t.getMessage());
        } finally {
            backupUtils.deleteFile(outputDir, false);
        }
    }

    private void checkForMissingFiles(TableDescriptor[] tables, List<String> csvFilenames, List<String> missingFilenames) {
        for (TableDescriptor table : tables) {
            String csvFilename = table.name() + ".csv";
            if (!csvFilenames.contains(csvFilename))
                missingFilenames.add(csvFilename);
        }
    }

    private void restoreTables(TableDescriptor[] tables, String inputPath, BackupKind backupKind) {
        for (TableDescriptor table : tables) {
            String sql = getImportStatement(table, inputPath, backupKind);
            jdbcTemplate.execute(sql);

            LOGGER.debug("Restored table \"{}\"", table.name());
        }
    }

    private String getImportStatement(TableDescriptor table, String inputPath, BackupKind backupKind) {
        boolean isIncremental = backupKind == BackupKind.INCREMENTAL;
        boolean isIncrementalTable = isIncremental && table.supportsIncrementalBackup();
        String key = table.name() + (isIncrementalTable ? "_incremental" : "_full");
        String sql = IMPORT_STATEMENTS.get(key);
        if (sql == null) {
            StringBuilder colList = new StringBuilder();
            StringBuilder setList = new StringBuilder();
            PreparedStatementSetter pss = ps -> ps.setString(1, table.name());
            RowCallbackHandler rch = rs -> {
                String colName = rs.getString(1);
                String colType = rs.getString(2);

                if (!colList.isEmpty())
                    colList.append(',');
                if (colType.equals("bit")) {
                    colList.append('@').append(colName);
                    if (!setList.isEmpty())
                        setList.append(',');
                    setList.append('"').append(colName).append("\" = CAST(@").append(colName).append(" AS UNSIGNED)");
                } else {
                    colList.append('"').append(colName).append('"');
                }
            };
            jdbcTemplate.query(SELECT_TABLE_COLUMNS, pss, rch);

            StringBuilder sqlbuf = new StringBuilder();
            sqlbuf.append("LOAD DATA LOCAL INFILE '").append(inputPath.replace("\\", "\\\\")).append(table.name())
                .append(".csv").append('\'').append(NL); //
            if (isIncremental)
                sqlbuf.append("REPLACE ");
            sqlbuf.append("INTO TABLE \"").append(table.name()).append('"').append(NL) //
                .append("CHARACTER SET utf8mb4").append(NL) //
                .append("FIELDS TERMINATED BY ','").append(NL) //
                .append("OPTIONALLY ENCLOSED BY '\"'").append(NL) //
                .append("ESCAPED BY ''").append(NL) //
                .append("LINES TERMINATED BY '\\n'").append(NL) //
                .append("IGNORE 1 LINES").append(NL) //
                .append('(').append(colList).append(')'); //
            if (!setList.isEmpty())
                sqlbuf.append(NL).append("SET ").append(setList);
            sqlbuf.append(';');
            sql = sqlbuf.toString();
            IMPORT_STATEMENTS.put(key, sql);

            LOGGER.trace("Restoring table \"{}\" with SQL statement:\n{}", table.name(), sql);
        }
        return sql;
    }

    private void resetAutoIncrement(String table) {
        Long[] max = {0L};
        jdbcTemplate.query("SELECT COALESCE(MAX(\"id\"), 0) FROM \"" + table + "\";", rs -> {
            max[0] = rs.getLong(1);
        });
        jdbcTemplate.execute("ALTER TABLE \"" + table + "\" AUTO_INCREMENT = " + ++max[0] + ';');
        LOGGER.trace("Reset table \"{}\" AUTO_INCREMENT = {}", table, max[0]);
    }
    
}
