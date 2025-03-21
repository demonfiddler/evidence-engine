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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;

import com.graphql_java_generator.server.util.GraphqlServerUtils;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublisher;
import io.github.demonfiddler.ee.server.model.CountryFormatKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.User;
import reactor.core.publisher.Mono;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Controller
@SchemaMapping(typeName = "Publisher")
public class PublisherController {

	@Autowired
	protected DataFetchersDelegatePublisher dataFetchersDelegatePublisher;

	@Autowired
	protected GraphqlServerUtils graphqlServerUtils;

	public PublisherController(BatchLoaderRegistry registry) {
		// Registering the data loaders is useless if @BatchMapping is used. But we need it here, for backward
		// compatibility with code developed against previous plugin versions
		registry.forTypePair(Long.class, Publisher.class).registerMappedBatchLoader((keysSet, env) -> {
			List<Long> keys = new ArrayList<>(keysSet.size());
			keys.addAll(keysSet);
			return Mono.fromCallable(() -> {
				Map<Long, Publisher> map = new HashMap<>();
				// Values are returned in the same order as the keys list
				List<Publisher> values = this.dataFetchersDelegatePublisher.batchLoader(keys, env);
				for (int i = 0; i < keys.size(); i++) {
					map.put(keys.get(i), values.get(i));
				}
				return map;
			});
		});
	}

	/**
	 * Loads the data for Publisher.entityKind. It returns an Object: the data fetcher implementation may return any
	 * type that is accepted by a spring-graphql controller<BR/>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the author attribute of the <I>origin</I>, which is an instance of {ObjectType {name:Post, fields:{Field{name:id,
	 * type:ID!, params:[]},Field{name:date, type:Date!, params:[]},Field{name:author, type:Member,
	 * params:[]},Field{name:publiclyAvailable, type:Boolean, params:[]},Field{name:title, type:String!,
	 * params:[]},Field{name:content, type:String!, params:[]},Field{name:authorId, type:ID,
	 * params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data model, but it
	 * typically contains the id to use in the query.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is consider as null. This allows to use the
	 * {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 * @param format The parameter that will receive the field argument of the same name for the current data to fetch
	 * @return It may return any value that is valid for a spring-graphql controller, annotated by the
	 * <code>@SchemaMapping</code> annotation
	 */
	@SchemaMapping(field = "entityKind")
	public Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, Publisher origin,
		@Argument("format") String format) {

		return this.dataFetchersDelegatePublisher.entityKind(dataFetchingEnvironment, origin,
			(FormatKind)GraphqlUtils.graphqlUtils.stringToEnumValue(format, FormatKind.class));
	}

	/**
	 * Loads the data for Publisher.status. It returns an Object: the data fetcher implementation may return any type
	 * that is accepted by a spring-graphql controller<BR/>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the author attribute of the <I>origin</I>, which is an instance of {ObjectType {name:Post, fields:{Field{name:id,
	 * type:ID!, params:[]},Field{name:date, type:Date!, params:[]},Field{name:author, type:Member,
	 * params:[]},Field{name:publiclyAvailable, type:Boolean, params:[]},Field{name:title, type:String!,
	 * params:[]},Field{name:content, type:String!, params:[]},Field{name:authorId, type:ID,
	 * params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data model, but it
	 * typically contains the id to use in the query.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is consider as null. This allows to use the
	 * {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 * @param format The parameter that will receive the field argument of the same name for the current data to fetch
	 * @return It may return any value that is valid for a spring-graphql controller, annotated by the
	 * <code>@SchemaMapping</code> annotation
	 */
	@SchemaMapping(field = "status")
	public Object status(DataFetchingEnvironment dataFetchingEnvironment, Publisher origin,
		@Argument("format") String format) {

		return this.dataFetchersDelegatePublisher.status(dataFetchingEnvironment, origin,
			(FormatKind)GraphqlUtils.graphqlUtils.stringToEnumValue(format, FormatKind.class));
	}

	/**
	 * Loads the data for Publisher.createdByUser. It is generated as the
	 * <code>generateBatchMappingDataFetchers</code> plugin parameter is true. <br/>
	 * @param batchLoaderEnvironment The environment for this batch loader. You can extract the GraphQLContext from
	 * this parameter.
	 * @param graphQLContext
	 * @param keys The objects for which the value for the createdByUser field must be retrieved.
	 * @return This method returns <code>${dataFetcher.batchMappingReturnType.value}</code>, as defined by the
	 * <code>batchMappingDataFetcherReturnType</code> plugin parameter. <br/>
	 * Please look at the spring-graphql annotation for a documentation on how to return the proper values
	 */
	@BatchMapping(field = "createdByUser")
	public Map<Publisher, User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment,
		GraphQLContext graphQLContext, List<Publisher> keys) {

		return this.dataFetchersDelegatePublisher.createdByUser(batchLoaderEnvironment, graphQLContext, keys);
	}

	/**
	 * Loads the data for Publisher.updatedByUser. It is generated as the
	 * <code>generateBatchMappingDataFetchers</code> plugin parameter is true. <br/>
	 * @param batchLoaderEnvironment The environment for this batch loader. You can extract the GraphQLContext from
	 * this parameter.
	 * @param graphQLContext
	 * @param keys The objects for which the value for the updatedByUser field must be retrieved.
	 * @return This method returns <code>${dataFetcher.batchMappingReturnType.value}</code>, as defined by the
	 * <code>batchMappingDataFetcherReturnType</code> plugin parameter. <br/>
	 * Please look at the spring-graphql annotation for a documentation on how to return the proper values
	 */
	@BatchMapping(field = "updatedByUser")
	public Map<Publisher, User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment,
		GraphQLContext graphQLContext, List<Publisher> keys) {

		return this.dataFetchersDelegatePublisher.updatedByUser(batchLoaderEnvironment, graphQLContext, keys);
	}

	/**
	 * Loads the data for Publisher.log. It returns an Object: the data fetcher implementation may return any type that
	 * is accepted by a spring-graphql controller<BR/>
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
	 * params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data model, but it
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
	@SchemaMapping(field = "log")
	public Object log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
		Publisher origin, @Argument("filter") LogQueryFilter filter, @Argument("pageSort") PageableInput pageSort) {

		return this.dataFetchersDelegatePublisher.log(dataFetchingEnvironment, dataLoader, origin, filter, pageSort);
	}

	/**
	 * Loads the data for Publisher.country. It returns an Object: the data fetcher implementation may return any type
	 * that is accepted by a spring-graphql controller<BR/>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the author attribute of the <I>origin</I>, which is an instance of {ObjectType {name:Post, fields:{Field{name:id,
	 * type:ID!, params:[]},Field{name:date, type:Date!, params:[]},Field{name:author, type:Member,
	 * params:[]},Field{name:publiclyAvailable, type:Boolean, params:[]},Field{name:title, type:String!,
	 * params:[]},Field{name:content, type:String!, params:[]},Field{name:authorId, type:ID,
	 * params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data model, but it
	 * typically contains the id to use in the query.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is consider as null. This allows to use the
	 * {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 * @param format The parameter that will receive the field argument of the same name for the current data to fetch
	 * @return It may return any value that is valid for a spring-graphql controller, annotated by the
	 * <code>@SchemaMapping</code> annotation
	 */
	@SchemaMapping(field = "country")
	public Object country(DataFetchingEnvironment dataFetchingEnvironment, Publisher origin,
		@Argument("format") String format) {

		return this.dataFetchersDelegatePublisher.country(dataFetchingEnvironment, origin,
			(CountryFormatKind)GraphqlUtils.graphqlUtils.stringToEnumValue(format, CountryFormatKind.class));
	}

}
