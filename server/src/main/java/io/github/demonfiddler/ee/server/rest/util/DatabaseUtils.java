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

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns a single-valued configuration property as a {@code String}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code String}.
     */
    public String getConfigString(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<String>)rs -> {
                rs.next();
                return rs.getString(1);
            });
    }

    /**
     * Returns a single-valued configuration property as an {@code int}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as an {@code int}.
     */
    public int getConfigInteger(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<Integer>)rs -> {
                rs.next();
                return rs.getInt(1);
            });
    }

    /**
     * Returns a single-valued configuration property as a {@code boolean}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code boolean}.
     */
    public boolean getConfigBoolean(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY, (PreparedStatementSetter)ps -> ps.setString(1, property),
            (ResultSetExtractor<Boolean>)rs -> {
                rs.next();
                return rs.getBoolean(1);
            });
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;String&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;String&gt;}.
     */
    public List<String> getConfigStringArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property), (RowMapper<String>)(rs, idx) -> rs.getString(1));
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;Integer&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;Integer&gt;}.
     */
    public List<Integer> getConfigIntegerArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property), (RowMapper<Integer>)(rs, idx) -> rs.getInt(1));
    }

    /**
     * Returns a multi-valued configuration property as a {@code List&lt;Boolean&gt;}.
     * @param property The name of the property to return.
     * @return The value of {@code property} as a {@code List&lt;Boolean&gt;}.
     */
    public List<Boolean> getConfigBooleanArray(String property) {
        return jdbcTemplate.query(SELECT_CONFIG_PROPERTY_ARRAY,
            (PreparedStatementSetter)ps -> ps.setString(1, property),
            (RowMapper<Boolean>)(rs, idx) -> rs.getBoolean(1));
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

}
