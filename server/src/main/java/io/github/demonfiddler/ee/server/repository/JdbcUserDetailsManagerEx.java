/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java AND web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it AND/or modify it under the terms of the
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

import javax.sql.DataSource;

import org.springframework.security.provisioning.JdbcUserDetailsManager;

/**
 * Spring {@code UserDetailsManager} extension in order to override SQL statements for H2- and MariaDB-compatibility.
 */
public class JdbcUserDetailsManagerEx extends JdbcUserDetailsManager {

	private static final String DEF_USERS_BY_USERNAME_QUERY = """
        SELECT "username", "password", "enabled"
        FROM "user"
        WHERE "username" = ?
        """;

	private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = """
        SELECT "username", "authority"
		FROM "user_authority"
		WHERE "username" = ?
        """;

	private static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = """
        SELECT g."id", g."group_name", ga."authority"
        FROM "group" g
        JOIN "group_authority" ga
        ON ga."group_id" = g."id"
        JOIN "group_user" gu
        ON gu."group_id" = g."id"
        WHERE gu."username" = ?
        """;

    private static final String DEF_CREATE_USER_SQL = """
        INSERT INTO "user"
        ("username", "password", "enabled")
        VALUES (?, ?, ?);
        """;

    private static final String DEF_DELETE_USER_SQL = """
        DELETE FROM "user"
        WHERE "username" = ?;
        """;

    private static final String DEF_UPDATE_USER_SQL = """
        UPDATE "user"
        SET "password" = ?, "enabled" = ?
        WHERE "username" = ?;
        """;

    private static final String DEF_INSERT_AUTHORITY_SQL = """
        INSERT INTO "user_authority"
        ("username", "authority")
        VALUES (?, ?);
        """;

    private static final String DEF_DELETE_USER_AUTHORITIES_SQL = """
        DELETE FROM "user_authority"
        WHERE "username" = ?;
        """;

    private static final String DEF_USER_EXISTS_SQL = """
        SELECT "username"
        FROM "user"
        WHERE "username" = ?;
        """;

    private static final String DEF_CHANGE_PASSWORD_SQL = """
        UPDATE "user"
        SET "password" = ?
        WHERE "username" = ?;
        """;

    private static final String DEF_FIND_GROUPS_SQL = """
        SELECT "group_name"
        FROM "group";
        """;

    private static final String DEF_FIND_USERS_IN_GROUP_SQL = """
        SELECT "username"
        FROM "group_user" gu
        JOIN "group" g
        ON g."id" = gu."group_id"
        WHERE g."group_name" = ?;
        """;

    private static final String DEF_INSERT_GROUP_SQL = """
        INSERT INTO "group"
        ("group_name")
        VALUES (?);
        """;

    private static final String DEF_FIND_GROUP_ID_SQL = """
        SELECT "id"
        FROM "group"
        WHERE "group_name" = ?;
        """;

    private static final String DEF_INSERT_GROUP_AUTHORITY_SQL = """
        INSERT INTO "group_authority"
        ("group_id", "authority")
        VALUES (?, ?);
        """;

    private static final String DEF_DELETE_GROUP_SQL = """
        DELETE FROM "group"
        WHERE "id" = ?;
        """;

    private static final String DEF_DELETE_GROUP_AUTHORITIES_SQL = """
        DELETE FROM "group_authority"
        WHERE "group_id" = ?;
        """;

    private static final String DEF_DELETE_GROUP_MEMBERS_SQL = """
        DELETE FROM "group_user"
        WHERE "group_id" = ?;
        """;

    private static final String DEF_RENAME_GROUP_SQL = """
        UPDATE "group"
        SET "group_name" = ?
        WHERE "group_name" = ?;
        """;

    private static final String DEF_INSERT_GROUP_MEMBER_SQL = """
        INSERT INTO "group_user"
        ("group_id", "username")
        VALUES (?, ?);
        """;

    private static final String DEF_DELETE_GROUP_MEMBER_SQL = """
        DELETE FROM "group_user"
        WHERE "group_id" = ? AND "username" = ?;
        """;

    private static final String DEF_GROUP_AUTHORITIES_QUERY_SQL = """
        SELECT g."id", g."group_name", ga."authority"
        FROM "group" g
        JOIN "group_authority" ga
        ON ga."group_id" = g."id"
        WHERE g."group_name" = ?;
        """;

    private static final String DEF_DELETE_GROUP_AUTHORITY_SQL = """
        DELETE FROM "group_authority"
        WHERE "group_id" = ? AND "authority" = ?;
        """;

    public JdbcUserDetailsManagerEx(DataSource dataSource) {
        super(dataSource);
		setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);
		setAuthoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY);
		setGroupAuthoritiesByUsernameQuery(DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY);
        setCreateUserSql(DEF_CREATE_USER_SQL);
        setDeleteUserSql(DEF_DELETE_USER_SQL);
        setUpdateUserSql(DEF_UPDATE_USER_SQL);
        setCreateAuthoritySql(DEF_INSERT_AUTHORITY_SQL);
        setDeleteUserAuthoritiesSql(DEF_DELETE_USER_AUTHORITIES_SQL);
        setUserExistsSql(DEF_USER_EXISTS_SQL);
        setChangePasswordSql(DEF_CHANGE_PASSWORD_SQL);
        setFindAllGroupsSql(DEF_FIND_GROUPS_SQL);
        setFindUsersInGroupSql(DEF_FIND_USERS_IN_GROUP_SQL);
        setInsertGroupSql(DEF_INSERT_GROUP_SQL);
        setFindGroupIdSql(DEF_FIND_GROUP_ID_SQL);
        setInsertGroupAuthoritySql(DEF_INSERT_GROUP_AUTHORITY_SQL);
        setDeleteGroupSql(DEF_DELETE_GROUP_SQL);
        setDeleteGroupAuthoritiesSql(DEF_DELETE_GROUP_AUTHORITIES_SQL);
        setDeleteGroupMembersSql(DEF_DELETE_GROUP_MEMBERS_SQL);
        setRenameGroupSql(DEF_RENAME_GROUP_SQL);
        setInsertGroupMemberSql(DEF_INSERT_GROUP_MEMBER_SQL);
        setDeleteGroupMemberSql(DEF_DELETE_GROUP_MEMBER_SQL);
        setGroupAuthoritiesSql(DEF_GROUP_AUTHORITIES_QUERY_SQL);
        setDeleteGroupAuthoritySql(DEF_DELETE_GROUP_AUTHORITY_SQL);
        setEnableGroups(true);
    }

}
