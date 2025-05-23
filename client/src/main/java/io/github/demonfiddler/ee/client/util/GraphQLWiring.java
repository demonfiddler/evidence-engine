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

package io.github.demonfiddler.ee.client.util;

/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

/**
 * Thanks to spring-graphql, the POJO classes are auto-magically discovered and mapped. But the custom scalars still
 * needs to be 'manually' wired. This is the objective of this class. <BR/>
 * <BR/>
 * Based on the https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/ tutorial
 * @author etienne-sf
 */
public class GraphQLWiring implements RuntimeWiringConfigurer {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(GraphQLWiring.class);

	public void configure(graphql.schema.idl.RuntimeWiring.Builder builder) {
		builder //
			.scalar(GraphQLScalarType.newScalar(CustomScalars.COUNTRY).name("Country").build())
			.scalar(GraphQLScalarType.newScalar(ExtendedScalars.Date).name("Date").build())
			.scalar(GraphQLScalarType.newScalar(ExtendedScalars.DateTime).name("DateTime").build())
			.scalar(GraphQLScalarType.newScalar(CustomScalars.ISSN).name("ISSN").build())
			.scalar(GraphQLScalarType.newScalar(ExtendedScalars.GraphQLLong).name("Long").build())
			.scalar(GraphQLScalarType.newScalar(CustomScalars.URI).name("URI").build())
			.scalar(GraphQLScalarType.newScalar(CustomScalars.URL).name("URL").build())
			.scalar(GraphQLScalarType.newScalar(CustomScalars.VOID).name("Void").build());
	}

}
