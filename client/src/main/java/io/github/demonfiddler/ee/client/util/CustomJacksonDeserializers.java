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
import java.time.OffsetDateTime;
import java.util.List;

import com.graphql_java_generator.client.response.AbstractCustomJacksonDeserializer;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import io.github.demonfiddler.ee.client.Claim;
import io.github.demonfiddler.ee.client.Declaration;
import io.github.demonfiddler.ee.client.EntityLink;
import io.github.demonfiddler.ee.client.Journal;
import io.github.demonfiddler.ee.client.Log;
import io.github.demonfiddler.ee.client.Person;
import io.github.demonfiddler.ee.client.Publication;
import io.github.demonfiddler.ee.client.Publisher;
import io.github.demonfiddler.ee.client.Quotation;
import io.github.demonfiddler.ee.client.Topic;
import io.github.demonfiddler.ee.client.User;
import io.github.demonfiddler.ee.client.__Directive;
import io.github.demonfiddler.ee.client.__DirectiveLocation;
import io.github.demonfiddler.ee.client.__EnumValue;
import io.github.demonfiddler.ee.client.__Field;
import io.github.demonfiddler.ee.client.__InputValue;
import io.github.demonfiddler.ee.client.__Type;
import io.github.demonfiddler.ee.common.graphql.CustomScalars;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
public class CustomJacksonDeserializers {

	public static class List__InputValue extends AbstractCustomJacksonDeserializer<List<__InputValue>> {

		private static final long serialVersionUID = 1L;
		public List__InputValue() {
			super(null, true, __InputValue.class, null);
		}

	}

	public static class List__Directive extends AbstractCustomJacksonDeserializer<List<__Directive>> {

		private static final long serialVersionUID = 1L;
		public List__Directive() {
			super(null, true, __Directive.class, null);
		}

	}

	public static class ListJournal extends AbstractCustomJacksonDeserializer<List<Journal>> {

		private static final long serialVersionUID = 1L;
		public ListJournal() {
			super(null, true, Journal.class, null);
		}

	}

	public static class ListTopic extends AbstractCustomJacksonDeserializer<List<Topic>> {

		private static final long serialVersionUID = 1L;
		public ListTopic() {
			super(null, true, Topic.class, null);
		}

	}

	public static class ListLog extends AbstractCustomJacksonDeserializer<List<Log>> {

		private static final long serialVersionUID = 1L;
		public ListLog() {
			super(null, true, Log.class, null);
		}

	}

	public static class List__EnumValue extends AbstractCustomJacksonDeserializer<List<__EnumValue>> {

		private static final long serialVersionUID = 1L;
		public List__EnumValue() {
			super(null, true, __EnumValue.class, null);
		}

	}

	public static class List__Field extends AbstractCustomJacksonDeserializer<List<__Field>> {

		private static final long serialVersionUID = 1L;
		public List__Field() {
			super(null, true, __Field.class, null);
		}

	}

	public static class ListClaim extends AbstractCustomJacksonDeserializer<List<Claim>> {

		private static final long serialVersionUID = 1L;
		public ListClaim() {
			super(null, true, Claim.class, null);
		}

	}

	public static class ListDeclaration extends AbstractCustomJacksonDeserializer<List<Declaration>> {

		private static final long serialVersionUID = 1L;
		public ListDeclaration() {
			super(null, true, Declaration.class, null);
		}

	}

	public static class List__Type extends AbstractCustomJacksonDeserializer<List<__Type>> {

		private static final long serialVersionUID = 1L;
		public List__Type() {
			super(null, true, __Type.class, null);
		}

	}

	public static class List__DirectiveLocation extends AbstractCustomJacksonDeserializer<List<__DirectiveLocation>> {

		private static final long serialVersionUID = 1L;
		public List__DirectiveLocation() {
			super(null, true, __DirectiveLocation.class, null);
		}

	}

	public static class ListPerson extends AbstractCustomJacksonDeserializer<List<Person>> {

		private static final long serialVersionUID = 1L;
		public ListPerson() {
			super(null, true, Person.class, null);
		}

	}

	public static class ListEntityLink extends AbstractCustomJacksonDeserializer<List<EntityLink>> {

		private static final long serialVersionUID = 1L;
		public ListEntityLink() {
			super(null, true, EntityLink.class, null);
		}

	}

	public static class URL extends AbstractCustomJacksonDeserializer<URL> {

		private static final long serialVersionUID = 1L;
		public URL() {
			super(null, false, URL.class, CustomScalars.URL);
		}

	}

	public static class ListPublication extends AbstractCustomJacksonDeserializer<List<Publication>> {

		private static final long serialVersionUID = 1L;
		public ListPublication() {
			super(null, true, Publication.class, null);
		}

	}

	public static class ListString extends AbstractCustomJacksonDeserializer<List<String>> {

		private static final long serialVersionUID = 1L;
		public ListString() {
			super(null, true, String.class, null);
		}

	}

	public static class ISSN extends AbstractCustomJacksonDeserializer<String> {

		private static final long serialVersionUID = 1L;
		public ISSN() {
			super(null, false, String.class, CustomScalars.ISSN);
		}

	}

	public static class DateTime extends AbstractCustomJacksonDeserializer<OffsetDateTime> {

		private static final long serialVersionUID = 1L;
		public DateTime() {
			super(null, false, OffsetDateTime.class, ExtendedScalars.DateTime);
		}

	}

	public static class Long extends AbstractCustomJacksonDeserializer<Long> {

		private static final long serialVersionUID = 1L;
		public Long() {
			super(null, false, Long.class, ExtendedScalars.GraphQLLong);
		}

	}

	public static class ListQuotation extends AbstractCustomJacksonDeserializer<List<Quotation>> {

		private static final long serialVersionUID = 1L;
		public ListQuotation() {
			super(null, true, Quotation.class, null);
		}

	}

	public static class Date extends AbstractCustomJacksonDeserializer<LocalDate> {

		private static final long serialVersionUID = 1L;
		public Date() {
			super(null, false, LocalDate.class, ExtendedScalars.Date);
		}

	}

	public static class ListPublisher extends AbstractCustomJacksonDeserializer<List<Publisher>> {

		private static final long serialVersionUID = 1L;
		public ListPublisher() {
			super(null, true, Publisher.class, null);
		}

	}

	public static class ListUser extends AbstractCustomJacksonDeserializer<List<User>> {

		private static final long serialVersionUID = 1L;
		public ListUser() {
			super(null, true, User.class, null);
		}

	}

}
