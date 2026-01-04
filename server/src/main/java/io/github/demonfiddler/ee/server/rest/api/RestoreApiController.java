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

package io.github.demonfiddler.ee.server.rest.api;

import static io.github.demonfiddler.ee.server.rest.util.BackupUtils.*;
import static io.github.demonfiddler.ee.common.util.StringUtils.NL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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

    @Autowired
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
            case "appdata":
                backupKind = BackupKind.APPDATA;
                break;
            case "all":
                backupKind = BackupKind.ALL;
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        // N.B. The remaining code only works if the application and database servers are colocated,
        // because unzipping writes to and the LOAD DATA LOCAL INFILE statement reads from local files.

        // MariaDB doesn't have permission to access ${java.io.tmpdir}, so we'll have to specify a different location.
        String outputPath = tmpDir + "ee-backup" + File.separatorChar;
        File outputDir = new File(outputPath);
        if (!outputDir.mkdirs()) {
            // The directory already existed, so make sure it's empty.
            backupUtils.deleteFile(outputDir, true);
        }

        // Unzip the backup set by copying each entry to disk.
        List<String> csvFilenames = new ArrayList<>();
        byte[] buf = new byte[65536];
        try (ZipInputStream zipIn = new ZipInputStream(file.getInputStream())) {
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
            }
        } catch (IOException e) {
            LOGGER.error("Error unzipping backup set", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                .body("400 Bad Request: Unable to read backup set");
        }

        // First check that the backup set contains all the required files.
        List<String> missingFilenames = new ArrayList<>();
        if (backupKind == BackupKind.ALL)
            checkForMissingFiles(STATIC_TABLES, csvFilenames, missingFilenames);
        checkForMissingFiles(APPDATA_TABLES_BACKUP, csvFilenames, missingFilenames);
        if (!missingFilenames.isEmpty()) {
            String errmsg = "Files missing from backup set: " + missingFilenames;
            LOGGER.error(errmsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                .body("400 Bad Request: " + errmsg);
        }

        // Check that the backup set's database schema version matches that of this server.
        File configFile = new File(outputDir, "config.csv");
        try {
            Map<String, String> config = backupUtils.readConfiguration(configFile);
            int schemaVersion = Integer.parseInt(config.getOrDefault("schema_version", "0"));
            if (!databaseUtils.checkDatabaseSchemaVersion(schemaVersion)) {
                String errmsg = "Backup set has an incompatible schema version; expected: "
                    + DatabaseUtils.CURRENT_SCHEMA_VERSION + ", actual: " + schemaVersion;
                LOGGER.error(errmsg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                    .body("400 Bad Request: " + errmsg);
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Error reading backup set configuration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN)
                .body("400 Bad Request: Unable to read configuration from backup set");
        }

        // The entire restore operation must be performed transactionally.
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Restore");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // If required, restore lookup tables.
            if (backupKind == BackupKind.ALL) {
                for (String table : STATIC_TABLES) {
                    // Delete existing lookup table entries.
                    try {
                        jdbcTemplate.update("DELETE FROM \"" + table + "\";");
                    } catch (DataAccessException e) {
                        txManager.rollback(status);

                        String errmsg = "Error deleting from table: " + table;
                        LOGGER.error(errmsg, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                            .body("500 Internal Server Error: " + errmsg);
                    }
                }
                restoreTables(STATIC_TABLES, outputPath);
            }

            // Delete existing tracked entities (cascades to all joined tables).
            try {
                jdbcTemplate.update("DELETE FROM \"entity\";");
            } catch (DataAccessException e) {
                txManager.rollback(status);

                String errmsg = "Error deleting existing data from table: entity";
                LOGGER.error(errmsg, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("500 Internal Server Error: " + errmsg);
            }

            // Restore entity + user tables together (with RI constraints temporarily disabled).
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
                restoreTables(ENTITY_TABLES_RESTORE, outputPath);
            } catch (DataAccessException e) {
                txManager.rollback(status);

                String errmsg = "Error restoring entity tables";
                LOGGER.error(errmsg, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("500 Internal Server Error: " + errmsg);
            } finally {
                try {
                    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1;");
                } catch (DataAccessException e) {
                    txManager.rollback(status);

                    String errmsg = "Error setting FOREIGN_KEY_CHECKS=1";
                    LOGGER.error(errmsg, e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                        .body("500 Internal Server Error: " + errmsg);
                }
            }

            // Restore remaining application data tables and commit the transaction.
            try {
                restoreTables(APPDATA_TABLES_RESTORE, outputPath);
            } catch (DataAccessException e) {
                txManager.rollback(status);

                String errmsg = "Error restoring application data tables";
                LOGGER.error(errmsg, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("500 Internal Server Error: " + errmsg);
            }
            txManager.commit(status);
        } catch (Throwable t) {
            txManager.rollback(status);
            throw t;
        } finally {
            backupUtils.deleteFile(outputDir, false);
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html")) //
            .body("<html><body>Backup set restored successfully</body></html>");
    }

    private void checkForMissingFiles(String[] tables, List<String> csvFilenames, List<String> missingFilenames) {
        for (String table : tables) {
            String csvFilename = table + ".csv";
            if (!csvFilenames.contains(csvFilename))
                missingFilenames.add(csvFilename);
        }
    }

    private void restoreTables(String[] tables, String inputPath) {
        for (String table : tables) {
            String sql = getImportStatement(table, inputPath);
            jdbcTemplate.execute(sql);
        }
    }

    private String getImportStatement(String table, String inputPath) {
        String sql = IMPORT_STATEMENTS.get(table);
        if (sql == null) {
            StringBuilder colList = new StringBuilder();
            StringBuilder setList = new StringBuilder();
            PreparedStatementSetter pss = ps -> ps.setString(1, table);
            RowCallbackHandler rch = rs -> {
                String colName = rs.getString(1);
                String colType = rs.getString(2);

                if (!colList.isEmpty())
                    colList.append(',');
                if (colType.equals("bit")) {
                    colList.append("@v_").append(colName);
                    if (!setList.isEmpty())
                        setList.append(',');
                    setList.append('"').append(colName).append("\" = CAST(@v_").append(colName).append(" AS UNSIGNED)");
                } else {
                    colList.append('"').append(colName).append('"');
                }
            };
            jdbcTemplate.query(SELECT_TABLE_COLUMNS, pss, rch);

            StringBuilder sqlbuf = new StringBuilder();
            sqlbuf.append("LOAD DATA LOCAL INFILE '").append(inputPath.replace("\\", "\\\\")).append(table)
                .append(".csv").append('\'').append(NL) //
                .append("INTO TABLE \"").append(table).append('"').append(NL) //
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
            IMPORT_STATEMENTS.put(table, sql);
        }
        return sql;
    }

}
