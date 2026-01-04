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
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.EntityAudit;

public class EntityAuditSubject extends Subject {

    public static EntityAuditSubject assertThat(/*@Nullable*/ EntityAudit entityAudit) {
        return assertAbout(entityAudits()).that(entityAudit);
    }

    public static Subject.Factory<EntityAuditSubject, EntityAudit> entityAudits() {
        return EntityAuditSubject::new;
    }

    final EntityAudit actual;

    EntityAuditSubject(FailureMetadata failureMetadata, /*@Nullable*/ EntityAudit actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public TrackedEntitySubject<?> entity() {
        return check("entity()").about(TrackedEntitySubject.trackedEntities()).that(actual.getEntity());
    }

    public LinkAuditSubject linkAudit() {
        return check("linkAudit()").about(LinkAuditSubject.linkAudits()).that(actual.getLinkAudit());
    }

    public FieldAuditSubject fieldAudit() {
        return check("fieldAudit()").about(FieldAuditSubject.fieldAudits()).that(actual.getFieldAudit());
    }

    public BooleanSubject pass() {
        return check("pass()").that(actual.getPass());
    }

    public void hasPass(Boolean pass) {
        pass().isEqualTo(pass);
    }

}
