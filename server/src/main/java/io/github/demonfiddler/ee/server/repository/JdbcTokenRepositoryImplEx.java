
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

package io.github.demonfiddler.ee.server.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.core.log.LogMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * A custom reimplementation of the built-in Spring {@code JdbcTokenRepositoryImpl}. This was necessary because the
 * Spring version is inextensible&mdash;subclasses cannot override the SQL statements.
 */
public class JdbcTokenRepositoryImplEx extends JdbcDaoSupport implements PersistentTokenRepository {

    /** Default SQL for creating the database table to store the tokens. */
    private static final String DEF_CREATE_TABLE_SQL = """
        CREATE TABLE "persistent_login" (
            "username" VARCHAR(64) NOT NULL,
            "series" VARCHAR(64) PRIMARY KEY,
            "token" VARCHAR(64) NOT NULL,
            "last_used" TIMESTAMP NOT NULL
        );
        """;

    /** The default SQL used by the <tt>getTokenBySeries</tt> query */
    private static final String DEF_TOKEN_BY_SERIES_SQL = """
        SELECT "username", "series", "token", "last_used"
        FROM "persistent_login"
        WHERE "series" = ?;
        """;

    /** The default SQL used by <tt>createNewToken</tt> */
    private static final String DEF_INSERT_TOKEN_SQL = """
        INSERT INTO "persistent_login"
        ("username", "series", "token", "last_used")
        VALUES (?, ?, ?, ?);
        """;

    /** The default SQL used by <tt>updateToken</tt> */
    private static final String DEF_UPDATE_TOKEN_SQL = """
        UPDATE "persistent_login"
        SET "token" = ?, "last_used" = ?
        WHERE "series" = ?;
        """;

    /** The default SQL used by <tt>removeUserTokens</tt> */
    private static final String DEF_REMOVE_USER_TOKENS_SQL = """
        DELETE FROM "persistent_login"
        WHERE "username" = ?;
        """;

    private String createTableSql = DEF_CREATE_TABLE_SQL;
    private String tokensBySeriesSql = DEF_TOKEN_BY_SERIES_SQL;
    private String insertTokenSql = DEF_INSERT_TOKEN_SQL;
    private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;
    private String removeUserTokensSql = DEF_REMOVE_USER_TOKENS_SQL;
    private boolean createTableOnStartup;

    @Override
    protected void initDao() {
        if (createTableOnStartup)
            getJdbcTemplate().execute(createTableSql);
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        getJdbcTemplate().update(insertTokenSql, token.getUsername(), token.getSeries(), token.getTokenValue(),
            token.getDate());
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        getJdbcTemplate().update(updateTokenSql, tokenValue, lastUsed, series);
    }

    /**
     * Loads the token data for the supplied series identifier. If an error occurs, it will be reported and null will be
     * returned (since the result should just be a failed persistent login).
     * @param seriesId
     * @return the token matching the series, or null if no match found or an exception occurred.
     */
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return getJdbcTemplate().queryForObject(tokensBySeriesSql, this::createRememberMeToken, seriesId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(LogMessage.format("Querying token for series '%s' returned no results.", seriesId), ex);
        } catch (IncorrectResultSizeDataAccessException ex) {
            logger.error(LogMessage.format(
                "Querying token for series '%s' returned more than one value. Series" + " should be unique", seriesId));
        } catch (DataAccessException ex) {
            logger.error("Failed to load token for series " + seriesId, ex);
        }
        return null;
    }

    private PersistentRememberMeToken createRememberMeToken(ResultSet rs, int rowNum) throws SQLException {
        return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
    }

    @Override
    public void removeUserTokens(String username) {
        getJdbcTemplate().update(removeUserTokensSql, username);
    }

    /**
     * Intended for convenience in debugging. Will create the persistent_token database table when the class is
     * initialized during the initDao method.
     * @param createTableOnStartup set to true to auto-create the table.
     */
    public void setCreateTableOnStartup(boolean createTableOnStartup) {
        this.createTableOnStartup = createTableOnStartup;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public void setTokensBySeriesSql(String tokensBySeriesSql) {
        this.tokensBySeriesSql = tokensBySeriesSql;
    }

    public void setInsertTokenSql(String insertTokenSql) {
        this.insertTokenSql = insertTokenSql;
    }

    public void setUpdateTokenSql(String updateTokenSql) {
        this.updateTokenSql = updateTokenSql;
    }

    public void setRemoveUserTokensSql(String removeUserTokensSql) {
        this.removeUserTokensSql = removeUserTokensSql;
    }

}
