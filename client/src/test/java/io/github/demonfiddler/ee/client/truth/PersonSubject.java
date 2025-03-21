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

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

import io.github.demonfiddler.ee.client.Person;

public class PersonSubject extends LinkableEntitySubject<Person> {

    public static PersonSubject assertThat(/*@Nullable*/ Person person) {
        return assertAbout(persons()).that(person);
    }

    public static Subject.Factory<PersonSubject, Person> persons() {
        return PersonSubject::new;
    }

    PersonSubject(FailureMetadata failureMetadata, /*@Nullable*/ Person actual) {
        super(failureMetadata, actual);
    }

    public void hasTitle(String title) {
        title().isEqualTo(title);
    }

    public StringSubject title() {
        return check("title()").that(actual.getTitle());
    }

    public void hasFirstName(String firstName) {
        firstName().isEqualTo(firstName);
    }

    public StringSubject firstName() {
        return check("firstName()").that(actual.getFirstName());
    }

    public void hasNickname(String nickname) {
        nickname().isEqualTo(nickname);
    }

    public StringSubject nickname() {
        return check("nickname()").that(actual.getNickname());
    }

    public void hasPrefix(String prefix) {
        prefix().isEqualTo(prefix);
    }

    public StringSubject prefix() {
        return check("prefix()").that(actual.getPrefix());
    }

    public void hasLastName(String lastName) {
        lastName().isEqualTo(lastName);
    }

    public StringSubject lastName() {
        return check("lastName()").that(actual.getLastName());
    }

    public void hasSuffix(String suffix) {
        suffix().isEqualTo(suffix);
    }

    public StringSubject suffix() {
        return check("suffix()").that(actual.getSuffix());
    }

    public void hasAlias(String Alias) {
        Alias().isEqualTo(Alias);
    }

    public StringSubject Alias() {
        return check("Alias()").that(actual.getAlias());
    }

    public void hasNotes(String notes) {
        notes().isEqualTo(notes);
    }

    public StringSubject notes() {
        return check("notes()").that(actual.getNotes());
    }

    public void hasQualifications(String qualifications) {
        qualifications().isEqualTo(qualifications);
    }

    public StringSubject qualifications() {
        return check("qualifications()").that(actual.getQualifications());
    }

    public void hasCountry(String country) {
        country().isEqualTo(country);
    }

    public StringSubject country() {
        return check("country()").that(actual.getCountry());
    }

    public void hasRating(Integer rating) {
        rating().isEqualTo(rating);
    }

    public IntegerSubject rating() {
        return check("rating()").that(actual.getRating());
    }

    public void hasChecked(Boolean checked) {
        checked().isEqualTo(checked);
    }

    public BooleanSubject checked() {
        return check("checked()").that(actual.getChecked());
    }

    public void hasPublished(Boolean published) {
        published().isEqualTo(published);
    }

    public BooleanSubject published() {
        return check("published()").that(actual.getPublished());
    }

}
