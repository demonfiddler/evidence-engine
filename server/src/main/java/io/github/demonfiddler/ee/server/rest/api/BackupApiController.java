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
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.util.InMemoryResource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import io.github.demonfiddler.ee.server.rest.model.BackupKind;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils;
import io.github.demonfiddler.ee.server.rest.util.DatabaseUtils;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils.QueryPair;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils.TableDescriptor;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-12-21T13:57:39.773008Z[Europe/London]", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.evidenceEngineRESTInterfaceOpenAPI311.base-path:/rest}")
public class BackupApiController implements BackupApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupApiController.class);

    private static final Map<String, QueryPair> EXPORT_STATEMENTS = Collections.synchronizedMap(new HashMap<>());

    private final NativeWebRequest request;
    private final PlatformTransactionManager txManager;
    private final JdbcTemplate jdbcTemplate;
    private final BackupUtils backupUtils;
    private final DatabaseUtils databaseUtils;
    @Value("${data.server.tmpdir}")
    private String tmpDir;

    public BackupApiController(NativeWebRequest request, PlatformTransactionManager txManager,
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
    @PreAuthorize("hasAuthority('ADM')")
    public ResponseEntity<Resource> backup(@NotNull @Valid String kind) {
        BackupKind backupKind = switch (kind) {
            case "all" -> BackupKind.ALL;
            case "full" -> BackupKind.FULL;
            case "incremental" -> BackupKind.INCREMENTAL;
            default -> null;
        };
        if (backupKind == null)
            return ResponseEntity.badRequest().build();

        // MariaDB may not have permission to access ${java.io.tmpdir}, so we'll have to use a different location.
        String outputPath = tmpDir + (tmpDir.endsWith(File.separator) ? "" : File.separatorChar) + "ee-backup" + File.separatorChar;
        File outputDir = new File(outputPath);
        if (!outputDir.mkdirs()) {
            // The directory already existed, so make sure it's empty.
            backupUtils.deleteFile(outputDir, true);
        }

        String backupId = databaseUtils.getConfigString(DatabaseUtils.PROP_BACKUP_ID);
        Integer backupNumber = databaseUtils.getConfigInteger(DatabaseUtils.PROP_BACKUP_NUMBER);
        Timestamp backupTimestamp = databaseUtils.getConfigTimestamp(DatabaseUtils.PROP_BACKUP_TIMESTAMP);

        // The entire backup operation must be performed transactionally.
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Backup");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // For incremental backup:
            // - if any of backup_id, backup_number or last_backup_date are missing, treat as a full backup
            // - increment config.backup_number and use that number
            // - use stored config.last_backup_date for the SELECT queries
            // For all/full backup:
            // - assign a new backup_id
            // - reset backup_number to 0
            // In all cases:
            // - store changes to config so they get included in the backup set
            // - update config.last_backup_timestamp to NOW

            // If incremental backup was requested but we haven't yet done a full backup, do the latter instead.
            if (backupKind == BackupKind.INCREMENTAL
                && (backupId == null || backupNumber == null || backupTimestamp == null)) {

                backupKind = BackupKind.FULL;
            }
            if (backupKind == BackupKind.INCREMENTAL) {
                backupNumber = (backupNumber != null ? backupNumber : 0) + 1;
                databaseUtils.setConfigInteger(DatabaseUtils.PROP_BACKUP_NUMBER, backupNumber);
            } else {
                backupId = UUID.randomUUID().toString();
                databaseUtils.setConfigString(DatabaseUtils.PROP_BACKUP_ID, backupId);

                backupNumber = 0;
                databaseUtils.setConfigInteger(DatabaseUtils.PROP_BACKUP_NUMBER, backupNumber);

                backupTimestamp = null;
            }
            // Store UTC NOW as backup_last_timestamp, ready for the next incremental backup.
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            databaseUtils.setConfigTimestamp(DatabaseUtils.PROP_BACKUP_TIMESTAMP, Timestamp.valueOf(now));

            // N.B. The remaining code only works if the application and database servers are colocated,
            // because the SELECT ... INTO OUTFILE SQL statement writes to a local file.

            // ZIP all the CSV files into a single backup set.
            byte[] buf = new byte[65536];
            File zipFile = new File(tmpDir, "ee-backup.zip");
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
                // Export static lookup table contents to CSV files.
                if (backupKind == BackupKind.ALL)
                    backupTables(STATIC_TABLES, outputPath, backupKind, null);
                
                // Export application tables to CSV files.
                int incrementalRowCount = backupTables(APPDATA_TABLES, outputPath, backupKind, backupTimestamp);

                // If an incremental backup found no files to back up, return a failure code.
                if (backupKind == BackupKind.INCREMENTAL && incrementalRowCount == 0) {
                    txManager.rollback(status);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN)
                        .body(new InMemoryResource("Incremental backup set is empty"));
                }

                for (File csvFile : outputDir.listFiles((f, n) -> n.toLowerCase().endsWith(".csv"))) {
                    ZipEntry csvEntry = new ZipEntry(csvFile.getName());
                    csvEntry.setLastModifiedTime(FileTime.fromMillis(csvFile.lastModified()));
                    csvEntry.setSize(csvFile.length());
                    zipOut.putNextEntry(csvEntry);
                    int count;
                    try (FileInputStream csvIn = new FileInputStream(csvFile)) {
                        while ((count = csvIn.read(buf)) != -1)
                            zipOut.write(buf, 0, count);
                    } finally {
                        zipOut.closeEntry();
                    }
                }
            } catch (IOException e) {
                txManager.rollback(status);

                LOGGER.error("Error creating backup set ZIP", e);
                backupUtils.deleteFile(outputDir, false);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body(new InMemoryResource("500: Internal Server Error: Unable to create backup set"));
            }

            String ts = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now);
            ts = ts.substring(0, ts.indexOf('.')).replace(":", "_");
            String contentDisposition = "attachment; filename=ee-backup-" + backupKind.toString().toLowerCase() + '-'
                + backupNumber + '@' + ts + ".zip";

            try {
                InputStream in = new FileInputStream(zipFile);
                
                // Finally, commit the transaction.
                txManager.commit(status);
                LOGGER.debug("Backup complete");
                
                Resource resource = new InputStreamResource(in);
                return ResponseEntity.ok() //
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) //
                    .header(HttpHeaders.CONTENT_ENCODING, UTF_8.name()) //
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition", "Content-Length",
                        "Content-Type") //
                    .contentType(MediaType.parseMediaType("application/zip")) //
                    .contentLength(zipFile.length()) //
                    .body(resource);
            } catch (IOException e) {
                txManager.rollback(status);

                String errmsg = "Error returning backup set";
                LOGGER.error(errmsg, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body(new InMemoryResource("500: Internal Server Error: " + errmsg));
            }
        } catch (Throwable t) {
            txManager.rollback(status);
            throw t;
        } finally {
            backupUtils.deleteFile(outputDir, false);
        }
    }

    /**
     * Backs up the specified tables.
     * @param tables Descriptors for the tables to back up.
     * @param outputPath The output path to which CSV files should be written.
     * @param backupKind The kind of backup.
     * @param lastBackupTimestamp When the previous backup was performed.
     * @return In incremental backup mode, the number of records that were incrementally backed up, otherwise {@code 0}.
     * The count excludes static tables and those which cannot be backed up incrementally.
     */
    private int backupTables(TableDescriptor[] tables, String outputPath, BackupKind backupKind,
        Timestamp lastBackupTimestamp) {

        int[] rowCount = {0};
        boolean isIncremental = backupKind == BackupKind.INCREMENTAL;
        for (TableDescriptor table : tables) {
            boolean isIncrementalTable = isIncremental && table.supportsIncrementalBackup();
            PreparedStatementSetter pss = ps -> {
                if (isIncrementalTable) {
                    ps.setTimestamp(1, lastBackupTimestamp);
                    if (table.isTrackedEntity())
                        ps.setTimestamp(2, lastBackupTimestamp);
                }
            };
            QueryPair queries = getExportStatement(table, outputPath, backupKind);
            jdbcTemplate.query(queries.countQuerySql(), pss, (RowCallbackHandler)rs -> {
                if (isIncrementalTable)
                    rowCount[0] += rs.getInt(1);
            });
            jdbcTemplate.query(queries.selectQuerySql(), pss, (RowCallbackHandler)null);
        }
        return rowCount[0];
    }

    private QueryPair getExportStatement(TableDescriptor table, String outputPath, BackupKind backupKind) {
        boolean isIncremental = backupKind == BackupKind.INCREMENTAL;
        boolean isIncrementalTable = isIncremental && table.supportsIncrementalBackup();
        String key = table.name() + (isIncrementalTable ? "_incremental" : "_full");
        QueryPair queries = EXPORT_STATEMENTS.get(key);
        if (queries == null) {
            StringBuilder headerList = new StringBuilder();
            StringBuilder selectList = new StringBuilder();
            PreparedStatementSetter pss = ps -> ps.setString(1, table.name());
            RowCallbackHandler rch = rs -> {
                String colName = rs.getString(1);
                String colType = rs.getString(2);
                if (!headerList.isEmpty())
                    headerList.append(", ");
                headerList.append('\'').append(colName).append('\'');

                if (!selectList.isEmpty())
                    selectList.append(", ");
                if (databaseUtils.isTextType(colType))
                    // To emit valid CSV per RFC-4180, embedded double quote characters must be doubled.
                    selectList.append("REPLACE(t.\"").append(colName).append("\", '\"', '\"\"')");
                else
                    selectList.append("t.\"").append(colName).append('"');
            };
            jdbcTemplate.query(SELECT_TABLE_COLUMNS, pss, rch);

            StringBuilder countBuf = new StringBuilder();
            countBuf.append("SELECT COUNT(*)").append(NL) //
                .append("FROM \"").append(table.name()).append("\" t");
            if (isIncrementalTable)
                countBuf.append(NL);
            appendPredicate(table, isIncremental, countBuf);
            countBuf.append(';');

            StringBuilder selectBuf = new StringBuilder();
            selectBuf.append("SELECT ").append(headerList).append(NL);
            selectBuf.append("UNION ALL").append(NL) //
                .append("SELECT ").append(selectList).append(NL) //
                .append("FROM \"").append(table.name()).append("\" t").append(NL); //
            appendPredicate(table, isIncremental, selectBuf);
            if (isIncrementalTable)
                selectBuf.append(NL);
            selectBuf.append("INTO OUTFILE '").append(outputPath.replace("\\", "\\\\")).append(table.name()) //
                .append(".csv'").append(NL) //
                .append("FIELDS TERMINATED BY ','").append(NL) //
                .append("OPTIONALLY ENCLOSED BY '\"'").append(NL) //
                .append("ESCAPED BY ''").append(NL) //
                .append("LINES TERMINATED BY '\\n';");

            String countSql = countBuf.toString();
            String selectSql = selectBuf.toString();
            queries = new QueryPair(countSql, selectSql);
            EXPORT_STATEMENTS.put(key, queries);

            LOGGER.trace("Backing up table \"{}\" with SQL statements:\n{}", table.name(), queries);
        }
        return queries;
    }

    private void appendPredicate(TableDescriptor table, boolean isIncremental, StringBuilder sqlBuf) {
        if (isIncremental && table.supportsIncrementalBackup()) {
            // Only timestamped tables can support incremental backup.
            if (table.needsJoin()) {
                sqlBuf.append("JOIN \"entity\" e").append(NL) //
                    .append("ON e.\"id\" = t.\"id\"").append(NL);
            }
            sqlBuf.append("WHERE ");
            if (table.isTrackedEntity()) {
                String alias = table.needsJoin() ? "e" : "t";
                sqlBuf.append(alias).append(".\"created\" > ? OR ") //
                    .append(alias).append(".\"updated\" > ?");
            } else if (table.tsColumn() != null) {
                sqlBuf.append("t.\"").append(table.tsColumn()).append("\" > ?");
            } else {
                throw new IllegalStateException(
                    "To support incremental backup, a table must either be for a tracked entity or have a timestamp");
            }
        }
    }

}
