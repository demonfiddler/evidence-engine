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

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.EntityLink;

public class EntityLinkSubject extends TrackedEntitySubject<EntityLink> {

    public static EntityLinkSubject assertThat(/*@Nullable*/ EntityLink entityLink) {
        return assertAbout(entityLinks()).that(entityLink);
    }

    public static Subject.Factory<EntityLinkSubject, EntityLink> entityLinks() {
        return EntityLinkSubject::new;
    }

    EntityLinkSubject(FailureMetadata failureMetadata, /*@Nullable*/ EntityLink actual) {
        super(failureMetadata, actual);
    }

    public LinkableEntitySubject<?> fromEntity() {
        return check("fromEntity()").about(LinkableEntitySubject.linkableEntities()).that(actual.getFromEntity());
    }

    public void hasFromEntityLocations(String locations) {
        fromEntityLocations().isEqualTo(locations);
    }

    public StringSubject fromEntityLocations() {
        return check("fromEntityLocations()").that(actual.getFromEntityLocations());
    }

    public LinkableEntitySubject<?> toEntity() {
        return check("toEntity()").about(LinkableEntitySubject.linkableEntities()).that(actual.getToEntity());
    }
    public void hasToEntityLocations(String locations) {
        toEntityLocations().isEqualTo(locations);
    }

    public StringSubject toEntityLocations() {
        return check("toEntityLocations()").that(actual.getToEntityLocations());
    }

}
