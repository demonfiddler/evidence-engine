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

package io.github.demonfiddler.ee.common.graphql;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * An ID scalar, that stores IDs as Longs.<BR/>
 */
class GraphQLScalarTypeID {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLScalarTypeID.class);

	// Note: String is the way the data is stored in GraphQL queries Long is the type while in the Java code, either in
	// the client and in the server.
	/** Custom Scalar for IDs. It serializes IDs as Strings */
	static GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar().name("ID")
		.description("Custom Scalar for IDs. It serializes IDs as Strings.").coercing(new Coercing<Long, String>() {

			/**
			 * Called to convert a Java object result of a DataFetcher to a valid runtime value for the scalar type.
			 * <br/>
			 * Note : Throw {@link graphql.schema.CoercingSerializeException} if there is fundamental problem during
			 * serialization, don't return null to indicate failure. <br/>
			 * Note : You should not allow {@link RuntimeException}s to come out of your serialize method, but rather
			 * catch them and fire them as {@link graphql.schema.CoercingSerializeException} instead as per the method
			 * contract.
			 * @param input is never null
			 * @return a serialized value which may be null.
			 * @throws graphql.schema.CoercingSerializeException if value input can't be serialized
			 */
			@Override
			public String serialize(@NonNull Object input) throws CoercingSerializeException {
				if (input instanceof Long) {
					return input.toString();
				} else {
					throw new CoercingSerializeException(
						"Can't convert the '" + input.toString() + "' ID to a String (it should be a "
							+ Long.class.getName() + " but is a " + input.getClass().getName() + ')');
				}
			}

			/**
			 * Called to resolve an input from a query variable into a Java object acceptable for the scalar type. <br/>
			 * Note : You should not allow {@link RuntimeException}s to come out of your parseValue method, but rather
			 * catch them and fire them as {@link graphql.schema.CoercingParseValueException} instead as per the method
			 * contract.
			 * @param input is never null
			 * @return a parsed value which is never null
			 * @throws graphql.schema.CoercingParseValueException if value input can't be parsed
			 */
			@Override
			public Long parseValue(@NonNull Object input) throws CoercingParseValueException {
				if (!(input instanceof String)) {
					throw new CoercingParseValueException("Can't parse the '" + input.toString()
						+ "' string to a java.lang.Long (it should be a String but is a " + input.getClass().getName() + ')');
				} else {
					try {
						return Long.valueOf((String)input);
					} catch (NumberFormatException e) {
						throw new CoercingParseValueException(e.getMessage(), e);
					}
				}
			}

			/**
			 * Called during query validation to convert a query input AST node into a Java object acceptable for the
			 * scalar type. The input object will be an instance of {@link graphql.language.Value}. <br/>
			 * Note : You should not allow {@link RuntimeException}s to come out of your parseLiteral method, but rather
			 * catch them and fire them as {@link graphql.schema.CoercingParseLiteralException} instead as per the
			 * method contract.
			 * @param input is never null
			 * @return a parsed value which is never null
			 * @throws graphql.schema.CoercingParseLiteralException if input literal can't be parsed
			 */
			@Override
			public Long parseLiteral(@NonNull Object input) throws CoercingParseLiteralException {
				// input is an AST, that is: an instance of a class that implements graphql.language.Value
				if (!(input instanceof StringValue)) {
					throw new CoercingParseValueException("Can't parse the '" + input.toString()
						+ "' string value to a java.lang.Long (it should be a StringValue but is a " + input.getClass().getName()
						+ ")");
				} else {
					String val = null;
					try {
						val = ((StringValue)input).getValue();
						logger.trace("Parsing Long from this literal: '{}'", val);
						return Long.valueOf((String)input);
					} catch (NumberFormatException e) {
						throw new CoercingParseValueException(
							e.getMessage() + " when trying to parse a java.lang.Long from: '" + val + '\'', e);
					}
				}
			}
		}).build();

}
