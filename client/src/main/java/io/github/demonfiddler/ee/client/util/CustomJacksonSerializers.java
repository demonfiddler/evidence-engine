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

import java.time.LocalDate;

import com.graphql_java_generator.client.request.AbstractCustomJacksonSerializer;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
public class CustomJacksonSerializers {

	public static class Country extends AbstractCustomJacksonSerializer<String> {

		private static final long serialVersionUID = 1L;
		public Country() {
			super(String.class, 0, CustomScalars.COUNTRY);
		}

	}

	public static class ISSN extends AbstractCustomJacksonSerializer<String> {

		private static final long serialVersionUID = 1L;
		public ISSN() {
			super(String.class, 0, io.github.demonfiddler.ee.common.graphql.CustomScalars.ISSN);
		}

	}

	public static class Long extends AbstractCustomJacksonSerializer<Long> {

		private static final long serialVersionUID = 1L;
		public Long() {
			super(Long.class, 0, graphql.scalars.ExtendedScalars.GraphQLLong);
		}

	}

	public static class DateTime extends AbstractCustomJacksonSerializer<java.time.OffsetDateTime> {

		private static final long serialVersionUID = 1L;
		public DateTime() {
			super(java.time.OffsetDateTime.class, 0, graphql.scalars.ExtendedScalars.DateTime);
		}

	}

	public static class Date extends AbstractCustomJacksonSerializer<LocalDate> {

		private static final long serialVersionUID = 1L;
		public Date() {
			super(LocalDate.class, 0, ExtendedScalars.Date);
		}

	}

	public static class URL extends AbstractCustomJacksonSerializer<URL> {

		private static final long serialVersionUID = 1L;
		public URL() {
			super(URL.class, 0, CustomScalars.URL);
		}

	}

}
