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

import java.util.HashMap;
import java.util.Map;

import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.client.GraphQLObjectMapper;

/**
 * An abstract base class for all GraphQL model objects.
 */
public abstract class AbstractGraphQLObject implements IGraphQLObject {

    /**
     * Contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL server. The key
     * is the alias name, the value is the deserialiazed value (taking into account custom scalars, lists, ...)
     */
    @GraphQLIgnore
    Map<String, Object> aliasValues = new HashMap<>();

    /**
     * This method is called during the JSON deserialization process, by the {@link GraphQLObjectMapper}, each time an
     * alias value is read from the JSON.
     * @param aliasName
     * @param aliasDeserializedValue
     */
    public final void setAliasValue(String aliasName, Object aliasDeserializedValue) {
        this.aliasValues.put(aliasName, aliasDeserializedValue);
    }

    /**
     * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
     * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
     * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
     * this method with a basis JSON deserialization, but this value won't be the proper custom scalar value.
     * @param alias
     * @return
     */
    public final Object getAliasValue(String alias) {
        return this.aliasValues.get(alias);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aliasValues == null) ? 0 : aliasValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractGraphQLObject other = (AbstractGraphQLObject)obj;
        if (aliasValues == null) {
            if (other.aliasValues != null)
                return false;
        } else if (!aliasValues.equals(other.aliasValues))
            return false;
        return true;
    }

}
