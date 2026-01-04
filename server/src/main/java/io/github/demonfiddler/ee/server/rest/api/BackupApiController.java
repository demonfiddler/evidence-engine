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

import static io.github.demonfiddler.ee.server.rest.util.BackupUtils.*;
import static io.github.demonfiddler.ee.common.util.StringUtils.NL;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import io.github.demonfiddler.ee.server.rest.model.BackupKind;
import io.github.demonfiddler.ee.server.rest.util.BackupUtils;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-12-21T13:57:39.773008Z[Europe/London]", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.evidenceEngineRESTInterfaceOpenAPI311.base-path:/rest}")
public class BackupApiController implements BackupApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupApiController.class);

    private static final Map<String, String> EXPORT_STATEMENTS = Collections.synchronizedMap(new HashMap<>());

    private final NativeWebRequest request;
    private final JdbcTemplate jdbcTemplate;
    private final BackupUtils backupUtils;
    @Value("${data.server.tmpdir}")
    private String tmpDir;

    @Autowired
    public BackupApiController(NativeWebRequest request, JdbcTemplate jdbcTemplate, BackupUtils backupUtils) {
        this.request = request;
        this.jdbcTemplate = jdbcTemplate;
        this.backupUtils = backupUtils;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public ResponseEntity<Resource> backup(@NotNull @Valid String kind) {
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
        // because the SELECT ... INTO OUTFILE SQL statement writes to a local file.

        // MariaDB doesn't have permission to access ${java.io.tmpdir}, so we'll have to specify a different location.
        String outputPath = tmpDir + "ee-backup" + File.separatorChar;
        File outputDir = new File(outputPath);
        if (!outputDir.mkdirs()) {
            // The directory already existed, so make sure it's empty.
            backupUtils.deleteFile(outputDir, true);
        }

        // ZIP all the CSV files into a single backup set.
        byte[] buf = new byte[65536];
        File zipFile = new File(tmpDir, "ee-backup.zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Export table contents to CSV files.
            if (backupKind == BackupKind.ALL)
                backupTables(STATIC_TABLES, outputPath);
            backupTables(APPDATA_TABLES_BACKUP, outputPath);

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
            LOGGER.error("Error creating backup set ZIP", e);
            backupUtils.deleteFile(outputDir, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InMemoryResource(
                "<html><body>500: Internal Server Error<br>Unable to create backup set</body></html>"));
        }

        String ts = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        ts = ts.substring(0, ts.indexOf('.')).replace(":", "_");
        String contentDisposition = "attachment; filename=ee-backup-" + ts + ".zip";

        try {
            InputStream in = new FileInputStream(zipFile);
            Resource resource = new InputStreamResource(in);
            return ResponseEntity.ok() //
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) //
                .header(HttpHeaders.CONTENT_ENCODING, UTF_8.name()) //
                .contentType(MediaType.parseMediaType("application/zip")) //
                .contentLength(zipFile.length()) //
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InMemoryResource(
                "<html><body>500: Internal Server Error<br>Error returning backup set</body></html>"));
        } finally {
            backupUtils.deleteFile(outputDir, false);
        }
    }

    private void backupTables(String[] tables, String outputPath) {
        for (String table : tables) {
            String sql = getExportStatement(table, outputPath);
            jdbcTemplate.execute(sql);
        }
    }

    private String getExportStatement(String table, String outputPath) {
        String sql = EXPORT_STATEMENTS.get(table);
        if (sql == null) {
            StringBuilder headerList = new StringBuilder();
            StringBuilder selectList = new StringBuilder();
            PreparedStatementSetter pss = ps -> ps.setString(1, table);
            RowCallbackHandler rch = rs -> {
                String colName = rs.getString(1);
                String colType = rs.getString(2);
                if (!headerList.isEmpty())
                    headerList.append(',');
                headerList.append('\'').append(colName).append('\'');

                if (!selectList.isEmpty())
                    selectList.append(',');
                selectList.append('"').append(colName).append(colType.equals("bit") ? "\" + 0" : '"');
            };
            jdbcTemplate.query(SELECT_TABLE_COLUMNS, pss, rch);

            StringBuilder sqlbuf = new StringBuilder();
            sqlbuf.append("SELECT ").append(headerList).append(NL) //
                .append("UNION ALL").append(NL) //
                .append("SELECT ").append(selectList).append(NL) //
                .append("FROM \"").append(table).append('"').append(NL) //
                .append("INTO OUTFILE '").append(outputPath.replace("\\", "\\\\")).append(table).append(".csv'")
                .append(NL) //
                .append("FIELDS TERMINATED BY ','").append(NL) //
                .append("OPTIONALLY ENCLOSED BY '\"'").append(NL) //
                .append("ESCAPED BY ''").append(NL) //
                .append("LINES TERMINATED BY '\\n'");
            sql = sqlbuf.toString();
            EXPORT_STATEMENTS.put(table, sql);
        }
        return sql;
    }

}
