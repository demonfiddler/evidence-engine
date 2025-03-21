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

package io.github.demonfiddler.ee.server.repository;

import java.util.List;

import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.User;
import jakarta.persistence.Query;

public class CustomUserRepositoryImpl extends CustomTrackedEntityRepositoryImpl<User> implements CustomUserRepository {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected String getFulltextColumns() {
        return "\"username\", \"first_name\", \"last_name\", \"email\", \"notes\"";
    }

    // FIXME: these permission-related methods need rewriting/refactoring to work with the Spring Security data model.

    @Override
    @SuppressWarnings("unchecked")
    public List<PermissionKind> findUserPermissions(Long userId) {
        String sql = "SELECT permission_code FROM user_permission WHERE user_id = :userId";
        Query query = em.createNativeQuery(sql, PermissionKind.class);
        query.setParameter("user_id", userId);
        return (List<PermissionKind>)query.getResultList();
    }

    @Override
    public int addUserPermissions(Long userId, List<PermissionKind> permissions) {
        String sql = "REPLACE INTO user_permission (user_id, permission_code) VALUES (:userId, :permissionCode);";
        Query query = em.createNativeQuery(sql);
        int updateCount = 0;
        for (PermissionKind permission : permissions) {
            query.setParameter("user_id", userId);
            query.setParameter("permission_code", permission.toString());
            updateCount += query.executeUpdate();
        }
        return updateCount;
    }

    @Override
    public int removeUserPermissions(Long userId, List<PermissionKind> permissions) {
        String sql = "DELETE FROM user_permission WHERE user_id = :userId AND permission_code IN (:permissions);";
        Query query = em.createNativeQuery(sql);
        query.setParameter("user_id", userId);
        query.setParameter("permission_code", permissions);
        return query.executeUpdate();
    }

}
