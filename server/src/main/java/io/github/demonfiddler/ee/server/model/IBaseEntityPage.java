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

package io.github.demonfiddler.ee.server.model;

import java.util.List;

/**
 * Interface for a slice of a paginated result set.
 */
public interface IBaseEntityPage<T extends IBaseEntity> {

    void setContent(List<T> content);

    List<T> getContent();

    void setHasContent(Boolean hasContent);

    Boolean getHasContent();

    void setIsEmpty(Boolean isEmpty);

    Boolean getIsEmpty();

    void setNumber(Integer number);

    Integer getNumber();

    void setSize(Integer size);

    Integer getSize();

    void setNumberOfElements(Integer numberOfElements);

    Integer getNumberOfElements();

    void setTotalPages(Integer totalPages);

    Integer getTotalPages();

    void setTotalElements(Long totalElements);

    Long getTotalElements();

    void setIsFirst(Boolean isFirst);

    Boolean getIsFirst();

    void setIsLast(Boolean isLast);

    Boolean getIsLast();

    void setHasNext(Boolean hasNext);

    Boolean getHasNext();

    void setHasPrevious(Boolean hasPrevious);

    Boolean getHasPrevious();

}
