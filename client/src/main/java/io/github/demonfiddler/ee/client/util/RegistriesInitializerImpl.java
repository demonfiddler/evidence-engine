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

import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.graphql_java_generator.client.CustomScalarRegistryImpl;
import com.graphql_java_generator.client.RegistriesInitializer;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveLocation;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeIDClient;

import graphql.scalars.ExtendedScalars;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

/**
 * Initialization of all the customization for this schema generation, including registries 
 * (custom scalars, directives, type mapping). 
 * This class is the way to transmit data spécific to this GraphQL schema to the runtime
 */
public class RegistriesInitializerImpl implements RegistriesInitializer {

	/**
	 * The singleton instance of this class, that will be used to initialize the various registries
	 * This singleton is the only way to instanciate this class, and the constructor is private, so that
	 * we are sure that the various commands are executed only once.
	 * The way to execute this initialization is to use this static attribute
	 */
	public final static RegistriesInitializer registriesInitializer = new RegistriesInitializerImpl();

	/**
	 * Initialization of the {@link DirectiveRegistry} with all directives defined in the current schema
	 */
	private static void initDirectiveRegistry() {
		DirectiveRegistryImpl.registerDirectiveRegistry("", (directiveRegistry) -> {
			Directive directive;

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive skip
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("skip");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "if", null, "Boolean", true, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
			directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive include
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("include");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "if", null, "Boolean", true, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
			directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive defer
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("defer");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "if", null, "Boolean", true, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive deprecated
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("deprecated");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "reason", null, "String", false, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive specifiedBy
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("specifiedBy");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "url", null, "String", false, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive specifiedBy
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("specifiedBy");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "url", null, "String", true, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.SCALAR);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive auth
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("auth");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "authority", null, "AuthorityKind", false, 1, true)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.OBJECT);
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive label
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive("");
			directive.setName("label");
			directive.setPackageName("io.github.demonfiddler.ee.client.util");
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(
							"", "label", null, "String", true, 0, false)); //$NON-NLS-2$ //$NON-NLS-3$
			directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
			directiveRegistry.registerDirective(directive);

		});
	}

	/**
	 * Initialization of the {@link registry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	private static void initCustomScalarRegistry() {
		CustomScalarRegistryImpl.registerCustomScalarRegistry("", (registry) -> {
			registry.registerGraphQLScalarType("ID", GraphQLScalarTypeIDClient.ID, String.class);
			registry.registerGraphQLScalarType("Country", CustomScalars.COUNTRY, String.class);
			registry.registerGraphQLScalarType("Date", ExtendedScalars.Date, LocalDate.class);
			registry.registerGraphQLScalarType("DateTime", ExtendedScalars.DateTime, OffsetDateTime.class);
			registry.registerGraphQLScalarType("ISSN", CustomScalars.ISSN, String.class);
			registry.registerGraphQLScalarType("Long", ExtendedScalars.GraphQLLong, Long.class);
			registry.registerGraphQLScalarType("URI", CustomScalars.URI, URI.class);
			registry.registerGraphQLScalarType("URL", CustomScalars.URL, URL.class);
			registry.registerGraphQLScalarType("Void", CustomScalars.VOID, Void.class);
		});
	}

	/**
	 * The constructor may only be called from this class, when assigning the {@link #registriesInitializer} field.
	 * This insures that the various commands are executed only once.
	 */
	private RegistriesInitializerImpl() {
		initCustomScalarRegistry();
		initDirectiveRegistry();
		GraphQLTypeMappingImpl.initGraphQLTypeMappingRegistry();
	}

	@Override
	public String getPackageName() {
		return "io.github.demonfiddler.ee.client";
	}

	@Override
	public String getSchema() {
		return "";
	}

	@Override
	public Class<?> getQueryRootResponseClass() {
		return QueryRootResponse.class;
	}

	@Override
	public Class<?> getMutationRootResponseClass() {
		return MutationRootResponse.class;
	}

	@Override
	public Class<?> getSubscriptionRootResponseClass() {
	// No subscription in this GraphQL schema
	return null;
	}
}