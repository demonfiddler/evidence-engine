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

import java.net.URL;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Publisher;

public class PublisherSubject extends TrackedEntitySubject<Publisher> {

    public static PublisherSubject assertThat(/*@Nullable*/ Publisher publisher) {
        return assertAbout(publishers()).that(publisher);
    }

    public static Subject.Factory<PublisherSubject, Publisher> publishers() {
        return PublisherSubject::new;
    }

    PublisherSubject(FailureMetadata failureMetadata, /*@Nullable*/ Publisher actual) {
        super(failureMetadata, actual);
    }

    public void hasName(String name) {
        name().isEqualTo(name);
    }

    public StringSubject name() {
        return check("name()").that(actual.getName());
    }

    public void hasLocation(String location) {
        location().isEqualTo(location);
    }

    public StringSubject location() {
        return check("location()").that(actual.getLocation());
    }

    public void hasCountry(String country) {
        country().isEqualTo(country);
    }

    public StringSubject country() {
        return check("country()").that(actual.getCountry());
    }

    public void hasUrl(URL url) {
        url().isEqualTo(url);
    }

    public UrlSubject url() {
        return check("url()").about(UrlSubject.urls()).that(actual.getUrl());
    }

    public void hasJournalCount(Integer journalCount) {
        journalCount().isEqualTo(journalCount);
    }

    public IntegerSubject journalCount() {
        return check("journalCount()").that(actual.getJournalCount());
    }

}
