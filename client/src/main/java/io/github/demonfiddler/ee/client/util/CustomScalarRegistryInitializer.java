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

package io.github.demonfiddler.ee.client.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

import graphql.scalars.ExtendedScalars;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public static void initCustomScalarRegistry() {
		if (!CustomScalarRegistryImpl.isCustomScalarRegistryInitialized("")) {
			CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();
			CustomScalarRegistryImpl.setCustomScalarRegistry("", customScalarRegistry);

			customScalarRegistry.registerGraphQLScalarType("ID", ExtendedScalars.GraphQLLong, Long.class);
			customScalarRegistry.registerGraphQLScalarType("Country", CustomScalars.COUNTRY, String.class);
			customScalarRegistry.registerGraphQLScalarType("Date", ExtendedScalars.Date, LocalDate.class);
			customScalarRegistry.registerGraphQLScalarType("DateTime", ExtendedScalars.DateTime, OffsetDateTime.class);
			customScalarRegistry.registerGraphQLScalarType("ISSN", CustomScalars.ISSN, String.class);
			customScalarRegistry.registerGraphQLScalarType("Long", ExtendedScalars.GraphQLLong, Long.class);
			customScalarRegistry.registerGraphQLScalarType("URI", CustomScalars.URI, java.net.URI.class);
			customScalarRegistry.registerGraphQLScalarType("URL", CustomScalars.URL, java.net.URL.class);
			customScalarRegistry.registerGraphQLScalarType("Void", CustomScalars.VOID, Void.class);
		}
	}

}
