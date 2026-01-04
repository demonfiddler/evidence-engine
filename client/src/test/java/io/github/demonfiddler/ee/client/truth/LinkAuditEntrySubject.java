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

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.LinkAuditEntry;

public class LinkAuditEntrySubject extends Subject {

    public static LinkAuditEntrySubject assertThat(/*@Nullable*/ LinkAuditEntry linkAudit) {
        return assertAbout(linkAuditEntries()).that(linkAudit);
    }

    public static Subject.Factory<LinkAuditEntrySubject, LinkAuditEntry> linkAuditEntries() {
        return LinkAuditEntrySubject::new;
    }

    final LinkAuditEntry actual;

    LinkAuditEntrySubject(FailureMetadata failureMetadata, /*@Nullable*/ LinkAuditEntry actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasLinkedEntityKind(String linkedEntityKind) {
        linkedEntityKind().isEqualTo(linkedEntityKind);
    }

    public StringSubject linkedEntityKind() {
        return check("linkedEntityKind").that(actual.getLinkedEntityKind());
    }

    public void hasMin(int min) {
        min().isEqualTo(min);
    }

    public IntegerSubject min() {
        return check("min()").that(actual.getMin());
    }

    public void hasActual(int actual) {
        actual().isEqualTo(actual);
    }

    public IntegerSubject actual() {
        return check("actual()").that(actual.getActual());
    }

    public BooleanSubject pass() {
        return check("pass()").that(actual.getPass());
    }

    public void hasPass(Boolean pass) {
        pass().isEqualTo(pass);
    }

}
