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

package io.github.demonfiddler.ee.client;

import com.graphql_java_generator.annotation.GraphQLEnumType;

/**
 * Enumeration of the possible transaction kinds.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLEnumType("TransactionKind")
public enum TransactionKind {

	CRE("CRE", "Created"), //$NON-NLS-1$
	UPD("UPD", "Updated"), //$NON-NLS-1$
	DEL("DEL", "Deleted"), //$NON-NLS-1$
	LNK("LNK", "Linked"), //$NON-NLS-1$
	UNL("UNL", "Unlinked"); //$NON-NLS-1$

	// The graphQlValue is needed on server side, to map the enum value to the value defined in the GraphQL schema. They
	// are different
	// when the value in the GraphQL schema is a Java reserved keyword.
	private final String graphQlValue;
	private final String label;

	private TransactionKind(String graphQlValue, String label) {
		this.graphQlValue = graphQlValue;
		this.label = label;
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
	 * Returns the label for this constant.
	 * @return the label
	 */
	public String label() {
		return this.label;
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
	public static TransactionKind fromGraphQlValue(String graphQlValue) {
		if (graphQlValue == null) {
			return null;
		}
		for (TransactionKind e : TransactionKind.values()) {
			if (e.graphQlValue().equals(graphQlValue)) {
				return e;
			}
		}
		throw new IllegalArgumentException("No TransactionKind exists with '" + graphQlValue + "' as a GraphQL value"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
