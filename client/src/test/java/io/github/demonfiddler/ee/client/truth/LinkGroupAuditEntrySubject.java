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
import com.google.common.truth.IterableSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.LinkGroupAuditEntry;

public class LinkGroupAuditEntrySubject extends Subject {

    public static LinkGroupAuditEntrySubject assertThat(/*@Nullable*/ LinkGroupAuditEntry linkGroupAuditEntry) {
        return assertAbout(linkGroupAuditEntries()).that(linkGroupAuditEntry);
    }

    public static Subject.Factory<LinkGroupAuditEntrySubject, LinkGroupAuditEntry> linkGroupAuditEntries() {
        return LinkGroupAuditEntrySubject::new;
    }

    final LinkGroupAuditEntry actual;

    LinkGroupAuditEntrySubject(FailureMetadata failureMetadata, /*@Nullable*/ LinkGroupAuditEntry actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public IterableSubject links() {
        return check("links()").that(actual.getLinks());
    }

    public BooleanSubject pass() {
        return check("pass()").that(actual.getPass());
    }

    public void hasPass(Boolean pass) {
        pass().isEqualTo(pass);
    }

}
