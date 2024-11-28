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

import java.time.OffsetDateTime;

import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

public class OffsetDateTimeSubject extends ComparableSubject<OffsetDateTime> {

    public static OffsetDateTimeSubject assertThat(/*@Nullable*/ OffsetDateTime dateTime) {
        return assertAbout(offsetDateTimes()).that(dateTime);
    }

    // Static method for getting the subject factory (for use with assertAbout())
    public static Subject.Factory<OffsetDateTimeSubject, OffsetDateTime> offsetDateTimes() {
        return OffsetDateTimeSubject::new;
    }

    private OffsetDateTimeSubject(FailureMetadata failureMetadata, /*@Nullable*/ OffsetDateTime subject) {
        super(failureMetadata, subject);
    }

}