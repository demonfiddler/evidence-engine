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

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.FieldAuditEntry;
import io.github.demonfiddler.ee.client.SeverityKind;

public class FieldAuditEntrySubject extends Subject {

    public static FieldAuditEntrySubject assertThat(/*@Nullable*/ FieldAuditEntry linkAudit) {
        return assertAbout(fieldAuditEntries()).that(linkAudit);
    }

    public static Subject.Factory<FieldAuditEntrySubject, FieldAuditEntry> fieldAuditEntries() {
        return FieldAuditEntrySubject::new;
    }

    final FieldAuditEntry actual;

    FieldAuditEntrySubject(FailureMetadata failureMetadata, /*@Nullable*/ FieldAuditEntry actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasFieldName(String fieldName) {
        fieldName().isEqualTo(fieldName);
    }

    public StringSubject fieldName() {
        return check("fieldName()").that(actual.getFieldName());
    }

    public void hasMessage(String message) {
        message().isEqualTo(message);
    }

    public StringSubject message() {
        return check("message()").that(actual.getMessage());
    }

    public void hasSeverity(SeverityKind severity) {
        severity().isEqualTo(severity);
    }

    public ComparableSubject<SeverityKind> severity() {
        return check("severity").that(actual.getSeverity());
    }

    public BooleanSubject pass() {
        return check("pass()").that(actual.getPass());
    }

    public void hasPass(Boolean pass) {
        pass().isEqualTo(pass);
    }

}
