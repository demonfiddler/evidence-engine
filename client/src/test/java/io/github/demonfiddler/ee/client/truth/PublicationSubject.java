/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
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
import java.time.LocalDate;

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Publication;

public class PublicationSubject extends TrackedEntitySubject<Publication> {

    public static PublicationSubject assertThat(/*@Nullable*/ Publication publication) {
        return assertAbout(publications()).that(publication);
    }

    public static Subject.Factory<PublicationSubject, Publication> publications() {
        return PublicationSubject::new;
    }

    PublicationSubject(FailureMetadata failureMetadata, /*@Nullable*/ Publication actual) {
        super(failureMetadata, actual);
    }

    public void hasTitle(String title) {
        title().isEqualTo(title);
    }

    public StringSubject title() {
        return check("title()").that(actual.getTitle());
    }

    public void hasAuthors(String authors) {
        authors().isEqualTo(authors);
    }

    public StringSubject authors() {
        return check("authors()").that(actual.getAuthors());
    }

    public JournalSubject journal() {
        return check("journal()").about(JournalSubject.journals()).that(actual.getJournal());
    }

    public void hasKind(String kind) {
        kind().isEqualTo(kind);
    }

    public StringSubject kind() {
        return check("kind()").that(actual.getKind());
    }

    public void hasDate(LocalDate when) {
        date().isEqualTo(when);
    }

    public ComparableSubject<LocalDate> date() {
        return check("date()").that(actual.getDate());
    }

    public void hasYear(Integer year) {
        year().isEqualTo(year);
    }

    public IntegerSubject year() {
        return check("year()").that(actual.getYear());
    }

    public void hasAbstract(String _abstract) {
        _abstract().isEqualTo(_abstract);
    }

    public StringSubject _abstract() {
        return check("_abstract()").that(actual.getAbstract());
    }

    public void hasNotes(String notes) {
        notes().isEqualTo(notes);
    }

    public StringSubject notes() {
        return check("notes()").that(actual.getNotes());
    }

    public void hasPeerReviewed(Boolean peerReviewed) {
        peerReviewed().isEqualTo(peerReviewed);
    }

    public BooleanSubject peerReviewed() {
        return check("peerReviewed()").that(actual.getPeerReviewed());
    }

    public void hasDoi(String doi) {
        doi().isEqualTo(doi);
    }

    public StringSubject doi() {
        return check("doi()").that(actual.getDoi());
    }

    public void hasIsbn(String isbn) {
        isbn().isEqualTo(isbn);
    }

    public StringSubject isbn() {
        return check("isbn()").that(actual.getIsbn());
    }

    public void hasUrl(URL url) {
        url().isEqualTo(url);
    }

    public UrlSubject url() {
        return check("url()").about(UrlSubject.urls()).that(actual.getUrl());
    }

    public void hasCached(Boolean cached) {
        cached().isEqualTo(cached);
    }

    public BooleanSubject cached() {
        return check("cached()").that(actual.getCached());
    }

    public void hasAccessed(LocalDate when) {
        accessed().isEqualTo(when);
    }

    public ComparableSubject<LocalDate> accessed() {
        return check("accessed()").that(actual.getAccessed());
    }

}
