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

import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveLocation;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.client.request.InputParameter;

public class DirectiveRegistryInitializer {

	/**
	 * Initialization of the {@link DirectiveRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public static DirectiveRegistry initDirectiveRegistry() {
		DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();
		Directive directive;

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive skip
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("skip");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments().add(InputParameter.newHardCodedParameter("", "if", null, "Boolean", true, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive include
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("include");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments().add(InputParameter.newHardCodedParameter("", "if", null, "Boolean", true, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive defer
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("defer");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments().add(InputParameter.newHardCodedParameter("", "if", null, "Boolean", true, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive deprecated
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("deprecated");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments()
			.add(InputParameter.newHardCodedParameter("", "reason", null, "String", false, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive specifiedBy
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("specifiedBy");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments().add(InputParameter.newHardCodedParameter("", "url", null, "String", true, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.SCALAR);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive auth
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("auth");
		directive.setPackageName("io.github.demonfiddler.ee.client.util");
		directive.getArguments()
			.add(InputParameter.newHardCodedParameter("", "permission", null, "PermissionKind", false, 1, true));
		directive.getDirectiveLocations().add(DirectiveLocation.OBJECT);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive label
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("label"); //
		directive.setPackageName("io.github.demonfiddler.ee.client.util"); //
		directive.getArguments().add(InputParameter.newHardCodedParameter("", "label", null, "String", true, 0, false));
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directiveRegistry.registerDirective(directive);

		DirectiveRegistryImpl.directiveRegistry = directiveRegistry;
		return directiveRegistry;
	}

}
