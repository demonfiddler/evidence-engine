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

package io.github.demonfiddler.ee.client.truth;

import static com.google.common.truth.Truth.assertAbout;

import java.time.OffsetDateTime;

import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.LongSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Log;

public class LogSubject extends Subject {

    final Log actual;

    public static LogSubject assertThat(/*@Nullable*/ Log log) {
        return assertAbout(logs()).that(log);
    }

    public static Subject.Factory<LogSubject, Log> logs() {
        return LogSubject::new;
    }

    LogSubject(FailureMetadata failureMetadata, /*@Nullable*/ Log actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasId(Long id) {
        id().isEqualTo(id);
    }

    public LongSubject id() {
        return check("id()").that(actual.getId());
    }

    public void wasTimestampedAfter(OffsetDateTime when) {
        timestamp().isAtLeast(when);
    }

    public void hasTimestamp(OffsetDateTime when) {
        timestamp().isEqualTo(when);
    }

    public ComparableSubject<OffsetDateTime> timestamp() {
        return check("timestamp()").that(actual.getTimestamp());
    }

    public UserSubject user() {
        return check("user()").about(UserSubject.users()).that(actual.getUser());
    }

    public void hasTransactionKind(String txnKind) {
        transactionKind().isEqualTo(txnKind);
    }

    public StringSubject transactionKind() {
        return check("transactionKind()").that(actual.getTransactionKind());
    }

    public void hasEntityKind(String entityKind) {
        entityKind().isEqualTo(entityKind);
    }

    public StringSubject entityKind() {
        return check("entityKind()").that(actual.getEntityKind());
    }

    public void hasEntityId(long entityId) {
        entityId().isEqualTo(entityId);
    }

    public LongSubject entityId() {
        return check("entityId()").that(actual.getEntityId());
    }

    public void haslinkedEntityKind(String linkedEntityKind) {
        linkedEntityKind().isEqualTo(linkedEntityKind);
    }

    public StringSubject linkedEntityKind() {
        return check("linkedEntityKind()").that(actual.getLinkedEntityKind());
    }

    public void haslinkedEntityId(long linkedEntityId) {
        linkedEntityId().isEqualTo(linkedEntityId);
    }

    public LongSubject linkedEntityId() {
        return check("linkedEntityId()").that(actual.getLinkedEntityId());
    }

}
