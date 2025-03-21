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

import java.time.LocalDate;

import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Claim;

public class ClaimSubject extends LinkableEntitySubject<Claim> {

    public static ClaimSubject assertThat(/*@Nullable*/ Claim claim) {
        return assertAbout(claims()).that(claim);
    }

    public static Subject.Factory<ClaimSubject, Claim> claims() {
        return ClaimSubject::new;
    }

    ClaimSubject(FailureMetadata failureMetadata, /*@Nullable*/ Claim actual) {
        super(failureMetadata, actual);
    }

    public void hasDate(LocalDate when) {
        date().isEqualTo(when);
    }

    public ComparableSubject<LocalDate> date() {
        return check("date()").that(actual.getDate());
    }

    public void hasText(String text) {
        text().isEqualTo(text);
    }

    public StringSubject text() {
        return check("text()").that(actual.getText());
    }

    public void hasNotes(String notes) {
        notes().isEqualTo(notes);
    }

    public StringSubject notes() {
        return check("notes()").that(actual.getNotes());
    }

}
