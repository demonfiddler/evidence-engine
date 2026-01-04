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

import java.time.OffsetDateTime;

import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.LongSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

import io.github.demonfiddler.ee.client.ITrackedEntity;

public class TrackedEntitySubject<T extends ITrackedEntity> extends Subject {

    public static <T extends ITrackedEntity> TrackedEntitySubject<T> assertThatTrackedEntity(/*@Nullable*/ T entity) {

        return Truth.<TrackedEntitySubject<T>, T>assertAbout(trackedEntities()).that(entity);
    }

    public static <T extends ITrackedEntity> Subject.Factory<TrackedEntitySubject<T>, T> trackedEntities() {
        return TrackedEntitySubject::new;
    }

    final T actual;

    TrackedEntitySubject(FailureMetadata failureMetadata, /*@Nullable*/ T actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasEntityKind(String entityKind) {
        entityKind().isEqualTo(entityKind);
    }

    public StringSubject entityKind() {
        return check("entityKind").that(actual.getEntityKind());
    }

    public void hasId(Long id) {
        id().isEqualTo(id);
    }

    public LongSubject id() {
        return check("id()").that(actual.getId());
    }

    public void hasStatus(String status) {
        status().isEqualTo(status);
    }

    public StringSubject status() {
        return check("status()").that(actual.getStatus());
    }

    public void hasRating(Integer rating) {
        rating().isEqualTo(rating);
    }

    public IntegerSubject rating() {
        return check("rating()").that(actual.getRating());
    }

    public void wasCreatedAfter(OffsetDateTime when) {
        created().isAtLeast(when);
    }

    public ComparableSubject<OffsetDateTime> created() {
        return check("created()").that(actual.getCreated());
    }

    public UserSubject createdByUser() {
        return check("createdByUser()").about(UserSubject.users()).that(actual.getCreatedByUser());
    }

    public void wasUpdatedAfter(OffsetDateTime when) {
        updated().isAtLeast(when);
    }

    public ComparableSubject<OffsetDateTime> updated() {
        return check("updated()").that(actual.getUpdated());
    }

    public UserSubject updatedByUser() {
        return check("updatedByUser()").about(UserSubject.users()).that(actual.getUpdatedByUser());
    }

    public LogPageSubject log() {
        return check("log()").about(LogPageSubject.logPages()).that(actual.getLog());
    }

}
