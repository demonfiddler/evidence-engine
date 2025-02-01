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

package io.github.demonfiddler.ee.server.model;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * An {@code IPage} implementation that wraps a Spring Data {@code Page} object.
 */
public abstract class PageWrapper<T extends IBaseEntity> implements IBaseEntityPage<T> {

    private final Page<T> delegate;

    protected PageWrapper(Page<T> delegate) {
        this.delegate = delegate;
    }

    public void setContent(List<T> content) {
        throw new UnsupportedOperationException("Unsupported method 'setContent'");
    }

    public List<T> getContent() {
        return delegate.getContent();
    }

    public void setHasContent(Boolean hasContent) {
        throw new UnsupportedOperationException("Unsupported method 'setHasContent'");
    }

    public Boolean getHasContent() {
        return delegate.hasContent();
    }

    public void setNumber(Integer number) {
        throw new UnsupportedOperationException("Unsupported method 'setNumber'");
    }

    public Integer getNumber() {
        return delegate.getNumber();
    }

    public void setSize(Integer size) {
        throw new UnsupportedOperationException("Unsupported method 'setSize'");
    }

    public Integer getSize() {
        return delegate.getSize();
    }

    public void setNumberOfElements(Integer numberOfElements) {
        throw new UnsupportedOperationException("Unsupported method 'setNumberOfElements'");
    }

    public Integer getNumberOfElements() {
        return delegate.getNumberOfElements();
    }

    public void setTotalPages(Integer totalPages) {
        throw new UnsupportedOperationException("Unsupported method 'setTotalPages'");
    }

    public Integer getTotalPages() {
        return delegate.getTotalPages();
    }

    public void setTotalElements(Long totalElements) {
        throw new UnsupportedOperationException("Unsupported method 'setTotalElements'");
    }

    public Long getTotalElements() {
        return delegate.getTotalElements();
    }

    public void setIsFirst(Boolean isFirst) {
        throw new UnsupportedOperationException("Unsupported method 'setIsFirst'");
    }

    public Boolean getIsFirst() {
        return delegate.isFirst();
    }

    public void setIsLast(Boolean isLast) {
        throw new UnsupportedOperationException("Unsupported method 'setIsLast'");
    }

    public Boolean getIsLast() {
        return delegate.isLast();
    }

    public void setHasNext(Boolean hasNext) {
        throw new UnsupportedOperationException("Unsupported method 'setHasNext'");
    }

    public Boolean getHasNext() {
        return delegate.hasNext();
    }

    public void setHasPrevious(Boolean hasPrevious) {
        throw new UnsupportedOperationException("Unsupported method 'setHasPrevious'");
    }

    public Boolean getHasPrevious() {
        return delegate.hasPrevious();
    }

}
