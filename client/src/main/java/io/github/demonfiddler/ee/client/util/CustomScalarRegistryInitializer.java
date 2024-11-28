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

package io.github.demonfiddler.ee.client.util;

import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public static void initCustomScalarRegistry() {
		if (!CustomScalarRegistryImpl.isCustomScalarRegistryInitialized("")) {
			CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();
			CustomScalarRegistryImpl.setCustomScalarRegistry("", customScalarRegistry); //$NON-NLS-1$

			customScalarRegistry.registerGraphQLScalarType("ID", graphql.scalars.ExtendedScalars.GraphQLLong,
				Long.class);
			customScalarRegistry.registerGraphQLScalarType("Country",
				io.github.demonfiddler.ee.common.graphql.CustomScalars.COUNTRY, String.class);
			customScalarRegistry.registerGraphQLScalarType("Date", graphql.scalars.ExtendedScalars.Date,
				java.time.LocalDate.class);
			customScalarRegistry.registerGraphQLScalarType("DateTime", graphql.scalars.ExtendedScalars.DateTime,
				java.time.OffsetDateTime.class);
			customScalarRegistry.registerGraphQLScalarType("ISSN",
				io.github.demonfiddler.ee.common.graphql.CustomScalars.ISSN, String.class);
			customScalarRegistry.registerGraphQLScalarType("Long", graphql.scalars.ExtendedScalars.GraphQLLong,
				Long.class);
			customScalarRegistry.registerGraphQLScalarType("URI",
				io.github.demonfiddler.ee.common.graphql.CustomScalars.URI, java.net.URI.class);
			customScalarRegistry.registerGraphQLScalarType("URL",
				io.github.demonfiddler.ee.common.graphql.CustomScalars.URL, java.net.URL.class);
			customScalarRegistry.registerGraphQLScalarType("Void",
				io.github.demonfiddler.ee.common.graphql.CustomScalars.VOID, Void.class);
		}
	}

}
