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

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.LongSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.EntityKind;
import io.github.demonfiddler.ee.client.FormatKind;
import io.github.demonfiddler.ee.client.TopicRef;


public class TopicRefSubject extends Subject {

    private final TopicRef actual;

    public static TopicRefSubject assertThat(/*@Nullable*/ TopicRef topicRef) {
        return assertAbout(topicRefs()).that(topicRef);
    }

    public static Subject.Factory<TopicRefSubject, TopicRef> topicRefs() {
        return TopicRefSubject::new;
    }

    TopicRefSubject(FailureMetadata failureMetadata, /*@Nullable*/ TopicRef actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasId(Long id) {
        id().isEqualTo(id);
    }

    public LongSubject id() {
        return check("id()").that(actual.getId());
    }

    public void hasTopicId(Long topicId) {
        topicId().isEqualTo(topicId);
    }

    public LongSubject topicId() {
        return check("topicId()").that(actual.getTopicId());
    }

    public void hasEntityId(Long entityId) {
        entityId().isEqualTo(entityId);
    }

    public LongSubject entityId() {
        return check("entityId()").that(actual.getEntityId());
    }

    public void hasEntityKind(EntityKind entityKind, FormatKind formatKind) {
        switch (formatKind) {
            case SHORT:
                entityKind().isEqualTo(entityKind.name());
                break;
            case LONG:
                entityKind().isEqualTo(entityKind.label());
                break;
            default:
                throw new IllegalArgumentException("Unrecognised format: " + formatKind);
        }
    }

    public void hasEntityKind(String entityKind) {
        entityKind().isEqualTo(entityKind);
    }

    public StringSubject entityKind() {
        return check("entityKind()").that(actual.getEntityKind());
    }

    public void hasLocations(String locations) {
        locations().isEqualTo(locations);
    }

    public StringSubject locations() {
        return check("locations()").that(actual.getLocations());
    }

}
