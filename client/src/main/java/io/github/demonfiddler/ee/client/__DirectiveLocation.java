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

package io.github.demonfiddler.ee.client;

import com.graphql_java_generator.annotation.GraphQLEnumType;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLEnumType("__DirectiveLocation")
public enum __DirectiveLocation {

		QUERY("QUERY"), //$NON-NLS-1$
		MUTATION("MUTATION"), //$NON-NLS-1$
		SUBSCRIPTION("SUBSCRIPTION"), //$NON-NLS-1$
		FIELD("FIELD"), //$NON-NLS-1$
		FRAGMENT_DEFINITION("FRAGMENT_DEFINITION"), //$NON-NLS-1$
		FRAGMENT_SPREAD("FRAGMENT_SPREAD"), //$NON-NLS-1$
		INLINE_FRAGMENT("INLINE_FRAGMENT"), //$NON-NLS-1$
		VARIABLE_DEFINITION("VARIABLE_DEFINITION"), //$NON-NLS-1$
		SCHEMA("SCHEMA"), //$NON-NLS-1$
		SCALAR("SCALAR"), //$NON-NLS-1$
		OBJECT("OBJECT"), //$NON-NLS-1$
		FIELD_DEFINITION("FIELD_DEFINITION"), //$NON-NLS-1$
		ARGUMENT_DEFINITION("ARGUMENT_DEFINITION"), //$NON-NLS-1$
		INTERFACE("INTERFACE"), //$NON-NLS-1$
		UNION("UNION"), //$NON-NLS-1$
		ENUM("ENUM"), //$NON-NLS-1$
		ENUM_VALUE("ENUM_VALUE"), //$NON-NLS-1$
		INPUT_OBJECT("INPUT_OBJECT"), //$NON-NLS-1$
		INPUT_FIELD_DEFINITION("INPUT_FIELD_DEFINITION"); //$NON-NLS-1$

	// The graphQlValue is needed on server side, to map the enum value to the value defined in the GraphQL schema. They
	// are different when the value in the GraphQL schema is a Java reserved keyword.
	private final String graphQlValue;

	__DirectiveLocation(String graphQlValue) {
		this.graphQlValue = graphQlValue;
	}

	/**
	 * Returns the value of this constant, as specified in the GraphQL schema. This is usually the same as the enum
	 * item's name. But it will differ if this name is a Java reserved keyword (in which case the name is prefixed by an
	 * underscore)
	 * @return the enum constant with the specified name, as defined in the GraphQL schema
	 */
	public String graphQlValue() {
		return this.graphQlValue;
	}

	/**
	 * Returns the enum constant of this type with the specified name (as specified in the GraphQL schema). The string
	 * must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters
	 * are not permitted.)
	 * @param graphQlValue The value, as defined in the GraphQL schema. This is usually the same as the enum item's
	 * name. But it will differ if this name is a Java reserved keyword (in which case the name is prefixed by an
	 * underscore)
	 * @return the enum constant with the specified name
	 * @throws IllegalArgumentException if this enum type has no constant with the specified GraphQL name
	 */
	public static __DirectiveLocation fromGraphQlValue(String graphQlValue) {
		if (graphQlValue == null) {
			return null;
		}
		for (__DirectiveLocation e : __DirectiveLocation.values()) {
			if (e.graphQlValue().equals(graphQlValue)) {
				return e;
			}
		}
		throw new IllegalArgumentException(
			"No __DirectiveLocation exists with '" + graphQlValue + "' as a GraphQL value"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
