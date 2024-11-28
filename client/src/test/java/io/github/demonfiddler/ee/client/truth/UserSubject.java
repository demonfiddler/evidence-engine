/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
 * Copyright © 2024 Adrian Price. All rights reserved.
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

package io.github.demonfiddler.ee.client.truth;

import static com.google.common.truth.Truth.assertAbout;

import java.util.List;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IterableSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.PermissionKind;
import io.github.demonfiddler.ee.client.User;

public class UserSubject extends TrackedEntitySubject<User> {

    public static UserSubject assertThat(/*@Nullable*/ User user) {
        return assertAbout(users()).that(user);
    }

    // Static method for getting the subject factory (for use with assertAbout())
    public static Subject.Factory<UserSubject, User> users() {
        return UserSubject::new;
    }

    UserSubject(FailureMetadata failureMetadata, /*@Nullable*/ User actual) {
        super(failureMetadata, actual);
    }

    public void hasLogin(String text) {
        login().isEqualTo(text);
    }

    public StringSubject login() {
        return check("login()").that(actual.getLogin());
    }

    public void hasFirstName(String text) {
        firstName().isEqualTo(text);
    }

    public StringSubject firstName() {
        return check("firstName()").that(actual.getFirstName());
    }

    public void hasLastName(String text) {
        lastName().isEqualTo(text);
    }

    public StringSubject lastName() {
        return check("lastName()").that(actual.getLastName());
    }

    public void hasEmail(String text) {
        email().isEqualTo(text);
    }

    public StringSubject email() {
        return check("email()").that(actual.getEmail());
    }

    public void hasPasswordHash(String text) {
        passwordHash().isEqualTo(text);
    }

    public StringSubject passwordHash() {
        return check("passwordHash()").that(actual.getPasswordHash());
    }

    public void hasPermissions(PermissionKind... permissions) {
        permissions().containsExactlyElementsIn(List.of(permissions).stream().map(p -> p.name()).toList());
    }

    public void hasPermissions(String... permissions) {
        permissions().containsExactlyElementsIn(permissions);
    }

    public IterableSubject permissions() {
        return check("permissions()").that(actual.getPermissions());
    }

}
