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

package io.github.demonfiddler.ee.client.truth;

import com.google.common.truth.CustomSubjectBuilder;
import com.google.common.truth.FailureMetadata;

import io.github.demonfiddler.ee.client.User;

public class UserSubjectBuilder extends CustomSubjectBuilder {

    /** Factory for UserSubjectBuilder. */
    private static class Factory implements CustomSubjectBuilder.Factory<UserSubjectBuilder> {
        private static final Factory INSTANCE = new Factory();

        @Override
        public UserSubjectBuilder createSubjectBuilder(FailureMetadata failureMetadata) {
            return new UserSubjectBuilder(failureMetadata);
        }
    }

    static CustomSubjectBuilder.Factory<UserSubjectBuilder> factory() {
        return Factory.INSTANCE;
    }

    private UserSubjectBuilder(FailureMetadata failureMetadata) {
        super(failureMetadata);
    }

    public UserSubject that(User subject) {
        return new UserSubject(metadata(), subject);
    }

}
