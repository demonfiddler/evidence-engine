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

import java.net.URL;
import java.time.LocalDate;

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Declaration;

public class DeclarationSubject extends LinkableEntitySubject<Declaration> {

    public static DeclarationSubject assertThat(/*@Nullable*/ Declaration declaration) {
        return assertAbout(declarations()).that(declaration);
    }

    public static Subject.Factory<DeclarationSubject, Declaration> declarations() {
        return DeclarationSubject::new;
    }

    DeclarationSubject(FailureMetadata failureMetadata, /*@Nullable*/ Declaration actual) {
        super(failureMetadata, actual);
    }

    public void hasKind(String kind) {
        kind().isEqualTo(kind);
    }

    public StringSubject kind() {
        return check("kind()").that(actual.getKind());
    }

    public void hasTitle(String title) {
        title().isEqualTo(title);
    }

    public StringSubject title() {
        return check("title()").that(actual.getTitle());
    }

    public void hasDate(LocalDate when) {
        date().isEqualTo(when);
    }

    public ComparableSubject<LocalDate> date() {
        return check("date()").that(actual.getDate());
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

    public void hasCached(Boolean cached) {
        cached().isEqualTo(cached);
    }        

    public BooleanSubject cached() {
        return check("cached()").that(actual.getCached());
    }

    public void hasSignatories(String signatories) {
        signatories().isEqualTo(signatories);
    }

    public StringSubject signatories() {
        return check("signatories()").that(actual.getSignatories());
    }

    public void hasSignatoryCount(Integer signatoryCount) {
        signatoryCount().isEqualTo(signatoryCount);
    }

    public IntegerSubject signatoryCount() {
        return check("signatoryCount()").that(actual.getSignatoryCount());
    }

    public void hasNotes(String notes) {
        notes().isEqualTo(notes);
    }

    public StringSubject notes() {
        return check("notes()").that(actual.getNotes());
    }

}
