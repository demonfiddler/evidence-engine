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

import com.graphql_java_generator.client.GraphQLObjectMapper;

public interface IGraphQLObject {

    /**
     * This method is called during the JSON deserialization process, by the {@link GraphQLObjectMapper}, each time an
     * alias value is read from the JSON.
     * @param aliasName
     * @param aliasDeserializedValue
     */
    void setAliasValue(String aliasName, Object aliasDeserializedValue);

    /**
     * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
     * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
     * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
     * this method with a basis JSON deserialization, but this value won't be the proper custom scalar value.
     * @param alias
     * @return
     */
    Object getAliasValue(String alias);

}
