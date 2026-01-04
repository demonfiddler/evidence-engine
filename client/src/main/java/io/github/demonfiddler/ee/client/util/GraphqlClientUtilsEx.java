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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * Extended to support field and method inheritance. The standard implementation only checks the most-derived class or
 * interface, not superclasses.
 */
@Component
public class GraphqlClientUtilsEx extends GraphqlClientUtils {

	/** This singleton is usable in default method, within interfaces */
	public static GraphqlClientUtilsEx graphqlClientUtils = new GraphqlClientUtilsEx();
	private static GraphqlUtils graphqlUtils = new GraphqlUtils();

	static {
		GraphqlClientUtils.graphqlClientUtils = GraphqlClientUtilsEx.graphqlClientUtils;
		try {
			Field graphqlClientUtilsField = InputParameter.class.getDeclaredField("graphqlClientUtils");
			graphqlClientUtilsField.setAccessible(true);
			graphqlClientUtilsField.set(null, graphqlClientUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a {@link Field} from the given class.
	 * @param owningClass The class that should contain this field. If the class's name finishes by Response, as an
	 * empty XxxResponse class is created for each Query/Mutation/Subscription (to be compatible with previsous
	 * version), then this method also looks in the owningClass's superclass.
	 * @param fieldName The name of the searched field
	 * @param mustFindField If true and the field is not found, a {@link GraphQLRequestPreparationException} is
	 * thrown.<BR/>
	 * If false an the field is not found, the method returns null
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	@Override
	public Field getDeclaredField(Class<?> owningClass, String fieldName, boolean mustFindField)
		throws GraphQLRequestPreparationException {

		// This approach detects inherited fields of the specified name.
		Class<?> currentClass = owningClass;
		while (currentClass != null) {
			try {
				return currentClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				currentClass = currentClass.getSuperclass();
			}
		}
		if (mustFindField) {
			throw new GraphQLRequestPreparationException(
				"Could not find field '" + fieldName + "' in " + owningClass.getName()); //
		}
		return null;
	}

	/**
	 * Check if the given field is owned by the class of this {@link ObjectResponse}. This method returns the class for
	 * this field.
	 * @param name The name of the field we want to check
	 * @param shouldBeScalar if true: also checks that the field is a scalar (throws a
	 * GraphQLRequestPreparationException if not). If false: also checks that the field is not a scalar (throws a
	 * GraphQLRequestPreparationException if not). If null: no check whether the field is a scalar or not
	 * @param owningClass The class in which will search for name as a GraphQL field
	 * @return the class of this field
	 * @throws NullPointerException if name is null
	 * @throws GraphQLRequestPreparationException if the check is KO
	 */
	@Override
	public Class<?> checkFieldOfGraphQLType(String name, Boolean shouldBeScalar, Class<?> owningClass)
		throws GraphQLRequestPreparationException {

		// Let's be sure that the identifier is a valid GraphQL identifier (also checks that it's not null)
		checkName(name);

		// Let's check that this fieldName is either a method name or a field of the class for this ObjectResponse.
		Class<?> fieldClass = null;

		Field field = getDeclaredField(owningClass, graphqlUtils.getJavaName(name), false);
		if (field != null) {
			// If we need to check that this field is (or is not) a scalar
			fieldClass = checkIsScalar(field, shouldBeScalar);
		}
		if (fieldClass == null && !owningClass.isInterface()) {
			// This class is a concrete class (not an interface). As the search field is not an attribute, the
			// owningClass should be a Query, a Mutation or a Subscription
			for (Method method : owningClass.getMethods()) {
				if (method.getName().equals(name)) {
					// If we need to check that this field is (or is not) a scalar
					fieldClass = checkIsScalar(name, method, shouldBeScalar);
					break;
				}
			}
		}
		if (fieldClass == null && owningClass.isInterface()) {
			// The class is an interface. So it's logical we didn't find this field as an attribute. Let's search for
			// the relevant setter
			String expectedMethodName = "get" + graphqlUtils.getPascalCase(name);
			// for (Method method : owningClass.getDeclaredMethods()) {
			for (Method method : owningClass.getMethods()) {
				if (method.getName().equals(expectedMethodName)) {
					// If we need to check that this field is (or is not) a scalar
					fieldClass = checkIsScalar(name, method, shouldBeScalar);
					break;
				}
			}
		}

		if (fieldClass == null) {
			throw new GraphQLRequestPreparationException("The GraphQL type '" + owningClass.getSimpleName() + "' ("
				+ owningClass.getName() + ") has no field of name '" + name + "'");
		}

		return fieldClass;
	}

}
