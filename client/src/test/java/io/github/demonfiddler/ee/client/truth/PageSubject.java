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

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.IterableSubject;
import com.google.common.truth.LongSubject;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

import io.github.demonfiddler.ee.client.AbstractPage;
import io.github.demonfiddler.ee.client.IGraphQLObject;

public class PageSubject<ActualT extends AbstractPage<EntityT>, EntityT extends IGraphQLObject> extends Subject {

    final ActualT actual;

    public static <ActualT extends AbstractPage<EntityT>, EntityT extends IGraphQLObject> PageSubject<ActualT, EntityT> assertThat(ActualT page) {
        return Truth.<PageSubject<ActualT, EntityT>, ActualT>assertAbout(pages()).that(page);
    }

    public static <ActualT extends AbstractPage<EntityT>, EntityT extends IGraphQLObject> Subject.Factory<PageSubject<ActualT, EntityT>, ActualT> pages() {
        return PageSubject<ActualT, EntityT>::new;
    }

    PageSubject(FailureMetadata failureMetadata, /*@Nullable*/ ActualT actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public IterableSubject content() {
        return check("content()").that(actual.getContent());
    }

    public BooleanSubject hasContent() {
        return check("hasContent()").that(actual.getHasContent());
    }

    public BooleanSubject hasNext() {
        return check("hasNext()").that(actual.getHasNext());
    }

    public BooleanSubject hasPrevious() {
        return check("hasPrevious()").that(actual.getHasPrevious());
    }

    public BooleanSubject isEmpty() {
        return check("isEmpty()").that(actual.getIsEmpty());
    }

    public BooleanSubject isFirst() {
        return check("isFirst()").that(actual.getIsFirst());
    }

    public BooleanSubject isLast() {
        return check("isLast()").that(actual.getIsLast());
    }

    public void hasNumber(int number) {
        number().isEqualTo(number);
    }

    public IntegerSubject number() {
        return check("number()").that(actual.getNumber());
    }

    public void hasSize(int size) {
        size().isEqualTo(size);
    }

    public IntegerSubject size() {
        return check("size()").that(actual.getSize());
    }

    public void hasNumberOfElements(int numberOfElements) {
        numberOfElements().isEqualTo(numberOfElements);
    }

    public IntegerSubject numberOfElements() {
        return check("numberOfElements()").that(actual.getNumberOfElements());
    }

    public void hasTotalPages(int totalPages) {
        totalPages().isEqualTo(totalPages);
    }

    public IntegerSubject totalPages() {
        return check("totalPages()").that(actual.getTotalPages());
    }

    public void hasTotalElements(long TotalElements) {
        totalElements().isEqualTo(TotalElements);
    }

    public LongSubject totalElements() {
        return check("totalElements()").that(actual.getTotalElements());
    }

}
