/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
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

import java.util.regex.Pattern;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

/** Custom scalar types. */
public class CustomScalars {

	/**
	 * Custom Scalar for ISO-3166-1 alpha-2 country code. Values represented as strings and validated using a regular
	 * expression.
	 */
	public static GraphQLScalarType COUNTRY =
		ExtendedScalars.newRegexScalar("Country").addPattern(Pattern.compile("^[A-Z]{2}$")).build();
	/**
	 * Custom Scalar for ISSN values (International Standard Serial Number). Values represented as strings and validated
	 * using a regular expression.
	 */
	public static GraphQLScalarType ISSN =
		ExtendedScalars.newRegexScalar("ISSN").addPattern(Pattern.compile("^[0-9]{4}-[0-9]{3}[0-9X]$")).build();
	/** Custom Scalar for URI values. Values represented as {@code java.net.URI} instances. */
	public static GraphQLScalarType URI = GraphQLScalarTypeURI.INSTANCE;
	/** Custom Scalar for URL values. Values represented as {@code java.net.URL} instances. */
	public static GraphQLScalarType URL = GraphQLScalarTypeURL.INSTANCE;
	/** Custom Scalar for Void values. Values represented as {@code null} references. */
	public static GraphQLScalarType VOID = GraphQLScalarTypeVoid.INSTANCE;

}
