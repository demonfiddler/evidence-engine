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

import java.util.Collections;
import java.util.List;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IterableSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Topic;

public class TopicSubject extends TrackedEntitySubject<Topic> {

    public static TopicSubject assertThat(/*@Nullable*/ Topic topic) {
        return assertAbout(topics()).that(topic);
    }

    public static Subject.Factory<TopicSubject, Topic> topics() {
        return TopicSubject::new;
    }

    TopicSubject(FailureMetadata failureMetadata, /*@Nullable*/ Topic actual) {
        super(failureMetadata, actual);
    }

    public void hasLabel(String label) {
        label().isEqualTo(label);
    }

    public StringSubject label() {
        return check("label()").that(actual.getLabel());
    }

    public void hasDescription(String description) {
        description().isEqualTo(description);
    }

    public StringSubject description() {
        return check("description()").that(actual.getDescription());
    }

    public TopicSubject parent() {
        return check("parent()").about(TopicSubject.topics()).that(actual.getParent());
    }

    public IterableSubject children() {
        return check("children").that(getNonNullChildren(actual));
    }

    public IterableSubject childIds() {
        return check("childIds").that(getNonNullChildren(actual).stream().map(t -> t.getId()).toList());
    }

    private List<Topic> getNonNullChildren(Topic topic) {
        List<Topic> children = topic.getChildren();
        return children == null ? Collections.emptyList() : children;
    }
}
