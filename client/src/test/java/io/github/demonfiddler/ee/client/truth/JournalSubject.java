/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
 * Copyright © 2024 Adrian Price. All rights reserved.
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
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Journal;

public class JournalSubject extends TrackedEntitySubject<Journal> {

    public static JournalSubject assertThat(/*@Nullable*/ Journal journal) {
        return assertAbout(journals()).that(journal);
    }

    public static Subject.Factory<JournalSubject, Journal> journals() {
        return JournalSubject::new;
    }

    JournalSubject(FailureMetadata failureMetadata, /*@Nullable*/ Journal actual) {
        super(failureMetadata, actual);
    }

    public void hasTitle(String title) {
        title().isEqualTo(title);
    }

    public StringSubject title() {
        return check("title()").that(actual.getTitle());
    }

    public void hasAbbreviation(String abbreviation) {
        abbreviation().isEqualTo(abbreviation);
    }

    public StringSubject abbreviation() {
        return check("abbreviation()").that(actual.getAbbreviation());
    }

    public void hasUrl(URL url) {
        url().isEqualTo(url);
    }

    public UrlSubject url() {
        return check("url()").about(UrlSubject.urls()).that(actual.getUrl());
    }

    public void hasIssn(String issn) {
        issn().isEqualTo(issn);
    }

    public StringSubject issn() {
        return check("issn()").that(actual.getIssn());
    }

    public PublisherSubject publisher() {
        return check("publisher()").about(PublisherSubject.publishers()).that(actual.getPublisher());
    }

    public void hasNotes(String notes) {
        notes().isEqualTo(notes);
    }

    public StringSubject notes() {
        return check("notes()").that(actual.getNotes());
    }

}
