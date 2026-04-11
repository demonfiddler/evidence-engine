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

import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Various database utilities. Many of the methods are for returning configuration properties held in the {@code config}
 * table in the database.
 */
@Component
public class DatabaseUtils {

    /** The database schema version expected by the server code. */
    public static final int CURRENT_SCHEMA_VERSION = 1;
    /** The property name for the backup set ID. */
    public static final String PROP_BACKUP_ID = "backup_id";
    /** The property name for the backup number. */
    public static final String PROP_BACKUP_NUMBER = "backup_number";
    /** The property name for the last backup timestamp. */
    public static final String PROP_BACKUP_TIMESTAMP = "backup_timestamp";
    /** The property name for the actual database schema version. */
    public static final String PROP_SCHEMA_VERSION = "schema_version";

    /** Selects a single property value from the config table. */
    private static final String SELECT_CONFIG_PROPERTY = """
        SELECT "value"
        FROM "config"
        WHERE "property" = ?
        AND "subscript" = 0;
        """;

    /** Selects an array property value from the config table. */
    private static final String SELECT_CONFIG_PROPERTY_ARRAY = """
        SELECT "value"
        FROM "config"
        WHERE "property" = ?
        ORDER BY "subscript";
        """;

    private static final String UPDATE_PROPERTY = """
        UPDATE "config"
        SET "value" = ?
        WHERE
          "property" = ? AND
          "subscript" = ?;
        """;

    private static final String INSERT_PROPERTY = """
        INSERT INTO "config" ("property", "subscript", "value")
        VALUES (?, ?, ?);
        """;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns a single-valued configuration property as a {@code boolean}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code Boolean} or {@code null}.
     */
    public Boolean getConfigBoolean(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<Boolean>)rs -> {
                if (rs.next()) {
                    boolean value = rs.getBoolean(1);
                    return rs.wasNull() ? null : value;
                }
                return null;
            });
    }

    /**
     * Returns a single-valued configuration property as an {@code int}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as an {@code Integer} or {@code null}.
     */
    public Integer getConfigInteger(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<Integer>)rs -> {
                if (rs.next()) {
                    int value = rs.getInt(1);
                    return rs.wasNull() ? null : value;
                }
                return null;
            });
    }

    /**
     * Returns a single-valued configuration property as a {@code String}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code String} or {@code null}.
     */
    public String getConfigString(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<String>)rs -> {
                if (rs.next())
                    return rs.getString(1);
                return null;
            });
    }

    /**
     * Returns a single-valued configuration property as a {@code Timestamp}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code Timestamp} or {@code null}.
     */
    public Timestamp getConfigTimestamp(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<Timestamp>)rs -> {
                if (rs.next())
                    return rs.getTimestamp(1);
                return null;
            });
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;Boolean&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;Boolean&gt;}.
     */
    public List<Boolean> getConfigBooleanArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property),
            (RowMapper<Boolean>)(rs, idx) -> {
                boolean value = rs.getBoolean(idx);
                return rs.wasNull() ? null : value;
            });
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;Integer&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;Integer&gt;}.
     */
    public List<Integer> getConfigIntegerArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property),
            (RowMapper<Integer>)(rs, idx) -> {
                int value = rs.getInt(idx);
                return rs.wasNull() ? null : value;
            });
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;String&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;String&gt;}.
     */
    public List<String> getConfigStringArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property),
            (RowMapper<String>)(rs, idx) -> rs.getString(1));
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;Timestamp&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;Timestamp&gt;}.
     */
    public List<Timestamp> getConfigTimestampArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property),
            (RowMapper<Timestamp>)(rs, idx) -> rs.getTimestamp(1));
    }

    /**
     * Checks the database schema version against that expected by the server code.
     * @throws IllegalStateException if the schema version in the database does not match that expected by the server.
     */
    public void checkDatabaseSchemaVersion() {
        int schemaVersion = getConfigInteger(PROP_SCHEMA_VERSION);
        if (!checkDatabaseSchemaVersion(schemaVersion)) {
            throw new IllegalStateException("Database schema version mismatch; expected: "
                + DatabaseUtils.CURRENT_SCHEMA_VERSION + ", actual: " + schemaVersion);
        }
    }

    /**
     * Checks a database schema version against that expected by the server code.
     * @param schemaVersion The schema version to check.
     * @return {@code true} if {@code schemaVersion} matches that expected by the server, otherwise {@code false}.
     */
    public boolean checkDatabaseSchemaVersion(int schemaVersion) {
        return schemaVersion == CURRENT_SCHEMA_VERSION;
    }

    /**
     * Indicates whether a database column type is a text type.
     * @param colType The database column type.
     * @return {@code true} if {@code colType} is textual, otherwise {@code false}.
     */
    public boolean isTextType(String colType) {
        return switch (colType) {
            case "char", "varchar", "tinytext", "text", "mediumtext", "longtext", "json" -> true;
            default -> false;
        };
    }

    /**
     * Sets a {@code Boolean} config value.
     * @param property The property name.
     * @param value The property value (can be {@code null}).
     * @return {@code true} if the property value was successfully set.
     */
    public boolean setConfigBoolean(String property, Boolean value) {
        return upsert(property, 0, value == null ? null : String.valueOf(value));
    }

    /**
     * Sets an {@code Integer} config value.
     * @param property The property name.
     * @param value The property value (can be {@code null}).
     * @return {@code true} if the property value was successfully set.
     */
    public boolean setConfigInteger(String property, Integer value) {
        return upsert(property, 0, value == null ? null : String.valueOf(value));
    }

    /**
     * Sets a {@code String} config value.
     * @param property The property name.
     * @param value The property value (can be {@code null}).
     * @return {@code true} if the property value was successfully set.
     */
    public boolean setConfigString(String property, String value) {
        return upsert(property, 0, value);
    }

    /**
     * Sets a {@code Timestamp} config value.
     * @param property The property name.
     * @param value The property value (can be {@code null}).
     * @return {@code true} if the property value was successfully set.
     */
    public boolean setConfigTimestamp(String property, Timestamp value) {
        return upsert(property, 0, value == null ? null : String.valueOf(value));
    }

    /**
     * Sets a {@code String} config value by updating the existing row or inserting a new one.
     * @param property The property name.
     * @param value The property value (can be {@code null}).
     * @return {@code true} if the property value was successfully set.
     */
    private boolean upsert(String property, int subscript, String value) {
        int updateCount = jdbcTemplate.update(UPDATE_PROPERTY, value, property, subscript);
        if (updateCount == 0)
            updateCount = jdbcTemplate.update(INSERT_PROPERTY, property, subscript, value);
        return updateCount == 1;
    }

    /**
     * Parses a string to an {@code Integer}
     * @param s The string (can be {@code null}).
     * @return The corresponding {@code Integer} if parseable, otherwise {@code null}.
     * @throws NumberFormatException - if the string is non-null and cannot be parsed as an integer.
     */
    public Integer parseInteger(String s) {
        return s != null ? Integer.valueOf(s) : null;
    }

    /**
     * Parses a string to a {@code Timestamp}
     * @param s The string (can be {@code null}).
     * @return The corresponding {@code Timestamp} if parseable, otherwise {@code null}.
     * @throws NumberFormatException - if the string is non-null and cannot be parsed as a Timestamp.
     */
    public Timestamp parseTimestamp(String s) {
        return s != null ? Timestamp.valueOf(s) : null;
    }

}
