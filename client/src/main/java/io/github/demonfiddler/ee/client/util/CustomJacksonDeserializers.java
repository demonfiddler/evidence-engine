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

package io.github.demonfiddler.ee.client.util;

import java.util.List;

import com.graphql_java_generator.client.response.AbstractCustomJacksonDeserializer;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
public class CustomJacksonDeserializers {

	public static class ListClaim
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Claim>> {
		private static final long serialVersionUID = 1L;
		public ListClaim() {
			super(null, true, io.github.demonfiddler.ee.client.Claim.class, null);
		}
	}

	public static class ListPublication
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Publication>> {
		private static final long serialVersionUID = 1L;
		public ListPublication() {
			super(null, true, io.github.demonfiddler.ee.client.Publication.class, null);
		}
	}

	public static class List__DirectiveLocation
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__DirectiveLocation>> {
		private static final long serialVersionUID = 1L;
		public List__DirectiveLocation() {
			super(null, true, io.github.demonfiddler.ee.client.__DirectiveLocation.class, null);
		}
	}

	public static class List__Type
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__Type>> {
		private static final long serialVersionUID = 1L;
		public List__Type() {
			super(null, true, io.github.demonfiddler.ee.client.__Type.class, null);
		}
	}

	public static class ListQuotation
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Quotation>> {
		private static final long serialVersionUID = 1L;
		public ListQuotation() {
			super(null, true, io.github.demonfiddler.ee.client.Quotation.class, null);
		}
	}

	public static class URL extends AbstractCustomJacksonDeserializer<java.net.URL> {
		private static final long serialVersionUID = 1L;
		public URL() {
			super(null, false, java.net.URL.class, io.github.demonfiddler.ee.common.graphql.CustomScalars.URL);
		}
	}

	public static class ListDeclaration
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Declaration>> {
		private static final long serialVersionUID = 1L;
		public ListDeclaration() {
			super(null, true, io.github.demonfiddler.ee.client.Declaration.class, null);
		}
	}

	public static class Country extends AbstractCustomJacksonDeserializer<String> {
		private static final long serialVersionUID = 1L;
		public Country() {
			super(null, false, String.class, io.github.demonfiddler.ee.common.graphql.CustomScalars.COUNTRY);
		}
	}

	public static class DateTime extends AbstractCustomJacksonDeserializer<java.time.OffsetDateTime> {
		private static final long serialVersionUID = 1L;
		public DateTime() {
			super(null, false, java.time.OffsetDateTime.class, graphql.scalars.ExtendedScalars.DateTime);
		}
	}

	public static class URI extends AbstractCustomJacksonDeserializer<java.net.URI> {
		private static final long serialVersionUID = 1L;
		public URI() {
			super(null, false, java.net.URI.class, io.github.demonfiddler.ee.common.graphql.CustomScalars.URI);
		}
	}

	public static class ListURI extends AbstractCustomJacksonDeserializer<List<java.net.URI>> {
		private static final long serialVersionUID = 1L;
		public ListURI() {
			super(new URI(), true, java.net.URI.class, null);
		}
	}

	public static class List__EnumValue
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__EnumValue>> {
		private static final long serialVersionUID = 1L;
		public List__EnumValue() {
			super(null, true, io.github.demonfiddler.ee.client.__EnumValue.class, null);
		}
	}

	public static class Date extends AbstractCustomJacksonDeserializer<java.time.LocalDate> {
		private static final long serialVersionUID = 1L;
		public Date() {
			super(null, false, java.time.LocalDate.class, graphql.scalars.ExtendedScalars.Date);
		}
	}

	public static class ListUser
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.User>> {
		private static final long serialVersionUID = 1L;
		public ListUser() {
			super(null, true, io.github.demonfiddler.ee.client.User.class, null);
		}
	}

	public static class ListITopicalEntity
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.ITopicalEntity>> {
		private static final long serialVersionUID = 1L;
		public ListITopicalEntity() {
			super(null, true, io.github.demonfiddler.ee.client.ITopicalEntity.class, null);
		}
	}

	public static class List__Field
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__Field>> {
		private static final long serialVersionUID = 1L;
		public List__Field() {
			super(null, true, io.github.demonfiddler.ee.client.__Field.class, null);
		}
	}

	public static class Long extends AbstractCustomJacksonDeserializer<Long> {
		private static final long serialVersionUID = 1L;
		public Long() {
			super(null, false, Long.class, graphql.scalars.ExtendedScalars.GraphQLLong);
		}
	}

	public static class Void extends AbstractCustomJacksonDeserializer<Void> {
		private static final long serialVersionUID = 1L;
		public Void() {
			super(null, false, Void.class, io.github.demonfiddler.ee.common.graphql.CustomScalars.VOID);
		}
	}

	public static class List__Directive
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__Directive>> {
		private static final long serialVersionUID = 1L;
		public List__Directive() {
			super(null, true, io.github.demonfiddler.ee.client.__Directive.class, null);
		}
	}

	public static class List__InputValue
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.__InputValue>> {
		private static final long serialVersionUID = 1L;
		public List__InputValue() {
			super(null, true, io.github.demonfiddler.ee.client.__InputValue.class, null);
		}
	}

	public static class ISSN extends AbstractCustomJacksonDeserializer<String> {
		private static final long serialVersionUID = 1L;
		public ISSN() {
			super(null, false, String.class, io.github.demonfiddler.ee.common.graphql.CustomScalars.ISSN);
		}
	}

	public static class ListTopic
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Topic>> {
		private static final long serialVersionUID = 1L;
		public ListTopic() {
			super(null, true, io.github.demonfiddler.ee.client.Topic.class, null);
		}
	}

	public static class ListTopicRef
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.TopicRef>> {
		private static final long serialVersionUID = 1L;
		public ListTopicRef() {
			super(null, true, io.github.demonfiddler.ee.client.TopicRef.class, null);
		}
	}

	public static class ListLog extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Log>> {
		private static final long serialVersionUID = 1L;
		public ListLog() {
			super(null, true, io.github.demonfiddler.ee.client.Log.class, null);
		}
	}

	public static class ListString extends AbstractCustomJacksonDeserializer<List<String>> {
		private static final long serialVersionUID = 1L;
		public ListString() {
			super(null, true, String.class, null);
		}
	}

	public static class ListPerson
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Person>> {
		private static final long serialVersionUID = 1L;
		public ListPerson() {
			super(null, true, io.github.demonfiddler.ee.client.Person.class, null);
		}
	}

	public static class ListJournal
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Journal>> {
		private static final long serialVersionUID = 1L;
		public ListJournal() {
			super(null, true, io.github.demonfiddler.ee.client.Journal.class, null);
		}
	}

	public static class ListPublisher
		extends AbstractCustomJacksonDeserializer<List<io.github.demonfiddler.ee.client.Publisher>> {
		private static final long serialVersionUID = 1L;
		public ListPublisher() {
			super(null, true, io.github.demonfiddler.ee.client.Publisher.class, null);
		}
	}

}
