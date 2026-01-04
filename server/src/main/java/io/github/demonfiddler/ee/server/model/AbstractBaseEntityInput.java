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

package io.github.demonfiddler.ee.server.model;

import com.graphql_java_generator.annotation.GraphQLScalar;

public abstract class AbstractBaseEntityInput {

    /**
     * The entity identifier, required if updating an existing record.
     */
    @GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
    Long id;

    /**
     * The entity identifier, required if updating an existing record.
     */
    public final void setId(Long id) {
        this.id = id;
    }

    /**
     * The entity identifier, required if updating an existing record.
     */
    public final Long getId() {
        return this.id;
    }

    /**
     * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
     * {@link #builderForTrackedEntityQueryFilter()}
     */
    @SuppressWarnings("unchecked")
    abstract static class Builder<B extends Builder<B, I>, I extends AbstractBaseEntityInput> {

        private Long id;

        /**
         * The entity identifier, required if updating an existing record.
         */
        public final B withId(Long idParam) {
            this.id = idParam;
            return (B)this;
        }

        I build(I input) {
            input.setId(id);
            return input;
        }

        public abstract I build();

    }

}
