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

package io.github.demonfiddler.ee.common.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * A URI scalar, that stores URIs as Strings.<BR/>
 * Copied from com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.
 */
class GraphQLScalarTypeVoid {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLScalarTypeVoid.class);

	// Note: String is the way the data is stored in GraphQL queries, void is the type while in the Java code, either in
	// the client and in the server.
	/** Custom Scalar for Void. It serializes void values as null. */
	static GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar().name("Void").description("Void scalar wrapper.")
		.coercing(new Coercing<Void, String>() {
			/**
			 * @inheritDoc
			 * @return null always
			 */
			@Override
			public String serialize(Object input) throws CoercingSerializeException {
				return null;
			}

			/**
			 * @inheritDoc
			 * @return null always
			 */
			@Override
			public Void parseValue(Object input) throws CoercingParseValueException {
				return null;
			}

			/**
			 * @inheritDoc
			 * @return null always
			 */
			@Override
			public Void parseLiteral(Object input) throws CoercingParseLiteralException {
				// input is an AST, that is: an instance of a class that implements graphql.language.Value
				if (!(input instanceof StringValue)) {
					throw new CoercingParseValueException("Can't parse the '" + input.toString()
						+ "' string value to a Void (it should be a StringValue but is a " + input.getClass().getName()
						+ ")");
				} else {
					String val = ((StringValue)input).getValue();
					logger.trace("Parsing URL from this literal: '{}'", val);
					return null;
				}
			}
		}).build();

}
