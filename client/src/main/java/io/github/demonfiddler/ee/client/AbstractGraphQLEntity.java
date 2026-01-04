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

package io.github.demonfiddler.ee.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLScalar;

public abstract class AbstractGraphQLEntity extends AbstractGraphQLObject implements IGraphQLEntity {

    @JsonProperty("__typename")
    @GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
    String __typename;

    @JsonIgnore
    public final void set__typename(String __typename) {
        this.__typename = __typename;
    }

    @JsonIgnore
    public final String get__typename() {
        return this.__typename;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((__typename == null) ? 0 : __typename.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        AbstractGraphQLEntity other = (AbstractGraphQLEntity)obj;
        if (__typename == null) {
            if (other.__typename != null)
                return false;
        } else if (!__typename.equals(other.__typename))
            return false;
        return true;
    }

    abstract static class Builder<B extends Builder<B, T>, T extends IGraphQLEntity> {

        T build(T t) {
            t.set__typename(getTypeName());
            return t;
        }

		public abstract T build();

		abstract String getTypeName();

    }

}
