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

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.graphql_java_generator.client.request.AbstractCustomJacksonSerializer;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
@SuppressWarnings("unused")
public class CustomJacksonSerializers {

	public static class Country extends AbstractCustomJacksonSerializer<String> {

		private static final long serialVersionUID = 1L;
		public Country() {
			super(String.class, 0, CustomScalars.Country);
		}

	}

	public static class Date extends AbstractCustomJacksonSerializer<LocalDate> {

		private static final long serialVersionUID = 1L;
		public Date() {
			super(LocalDate.class, 0, ExtendedScalars.Date);
		}

	}

	public static class DateTime extends AbstractCustomJacksonSerializer<OffsetDateTime> {

		private static final long serialVersionUID = 1L;
		public DateTime() {
			super(OffsetDateTime.class, 0, ExtendedScalars.DateTime);
		}

	}

	public static class ID extends AbstractCustomJacksonSerializer<java.lang.Long> {

		private static final long serialVersionUID = 1L;
		public ID() {
			super(java.lang.Long.class, 0, ExtendedScalars.GraphQLLong);
		}

	}

	public static class ISSN extends AbstractCustomJacksonSerializer<String> {

		private static final long serialVersionUID = 1L;
		public ISSN() {
			super(String.class, 0, CustomScalars.ISSN);
		}

	}

	public static class Long extends AbstractCustomJacksonSerializer<java.lang.Long> {

		private static final long serialVersionUID = 1L;
		public Long() {
			super(java.lang.Long.class, 0, ExtendedScalars.GraphQLLong);
		}

	}

	public static class URI extends AbstractCustomJacksonSerializer<java.net.URI> {

		private static final long serialVersionUID = 1L;
		public URI() {
			super(java.net.URI.class, 0, CustomScalars.URI);
		}

	}

	public static class URL extends AbstractCustomJacksonSerializer<java.net.URL> {

		private static final long serialVersionUID = 1L;
		public URL() {
			super(java.net.URL.class, 0, CustomScalars.URL);
		}

	}

}
