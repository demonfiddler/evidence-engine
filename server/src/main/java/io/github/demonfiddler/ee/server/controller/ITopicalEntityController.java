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

package io.github.demonfiddler.ee.server.controller;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.graphql_java_generator.server.util.GraphqlServerUtils;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateITopicalEntity;
import io.github.demonfiddler.ee.server.model.ITopicalEntity;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Controller
@SchemaMapping(typeName = "ITopicalEntity")
public class ITopicalEntityController {

	@Autowired
	protected DataFetchersDelegateITopicalEntity dataFetchersDelegateITopicalEntity;

	@Autowired
	protected GraphqlServerUtils graphqlServerUtils;

	/**
	 * This method loads the data for ${dataFetcher.graphQLType}.topicRefs. It returns an Object: the data fetcher
	 * implementation may return any type that is accepted by a spring-graphql controller<BR/>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param dataLoader The {@link DataLoader} allows to load several data in one query. It allows to solve the (n+1)
	 * queries issues, and greatly optimizes the response time.<BR/>
	 * You'll find more informations here:
	 * <A HREF= "https://github.com/graphql-java/java-dataloader">https://github.com/graphql-java/java-dataloader</A>
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the author attribute of the <I>origin</I>, which is an instance of {ObjectType {name:Post, fields:{Field{name:id,
	 * type:ID!, params:[]},Field{name:date, type:Date!, params:[]},Field{name:author, type:Member,
	 * params:[]},Field{name:publiclyAvailable, type:Boolean, params:[]},Field{name:title, type:String!,
	 * params:[]},Field{name:content, type:String!, params:[]},Field{name:authorId, type:ID,
	 * params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data modle, but it
	 * typically contains the id to use in the query.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is consider as null. This allows to use the
	 * {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 * @param filter The parameter that will receive the field argument of the same name for the current data to fetch
	 * @param pageSort The parameter that will receive the field argument of the same name for the current data to fetch
	 * @return It may return any value that is valid for a spring-graphql controller, annotated by the
	 * <code>@SchemaMapping</code> annotation
	 */
	@SchemaMapping(field = "topicRefs")
	public Object topicRefs(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, TopicRefPage> dataLoader,
		ITopicalEntity origin, @Argument("filter") TopicRefQueryFilter filter,
		@Argument("pageSort") PageableInput pageSort) {

		return this.dataFetchersDelegateITopicalEntity.topicRefs(dataFetchingEnvironment, dataLoader, origin, filter,
			pageSort);
	}

}
