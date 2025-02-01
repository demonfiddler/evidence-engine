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
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopicRef;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.TopicRef;
import reactor.core.publisher.Mono;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Controller
@SchemaMapping(typeName = "TopicRef")

public class TopicRefController {

	@Autowired
	protected DataFetchersDelegateTopicRef dataFetchersDelegateTopicRef;

	@Autowired
	protected GraphqlServerUtils graphqlServerUtils;

	public TopicRefController(BatchLoaderRegistry registry) {
		// Registering the data loaders is useless if the @BatchMapping is used. But we
		// need it here, for backward
		// compatibility with code developed against the previous plugin versions
		registry.forTypePair(Long.class, TopicRef.class).registerMappedBatchLoader((keysSet, env) -> {
			List<Long> keys = new ArrayList<>(keysSet.size());
			keys.addAll(keysSet);
			return Mono.fromCallable(() -> {
				Map<Long, TopicRef> map = new HashMap<>();
				// Values are returned in the same order as the keys list
				List<TopicRef> values = this.dataFetchersDelegateTopicRef.batchLoader(keys, env);
				for (int i = 0; i < keys.size(); i++) {
					map.put(keys.get(i), values.get(i));
				}
				return map;
			});
		});
	}

	/**
	 * This method loads the data for ${dataFetcher.graphQLType}.entityKind. It returns an Object: the data fetcher
	 * implementation may return any type that is accepted by a spring-graphql controller<BR/>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
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
	 * @param format The parameter that will receive the field argument of the same name for the current data to fetch
	 * @return It may return any value that is valid for a spring-graphql controller, annotated by the
	 * <code>@SchemaMapping</code> annotation
	 */
	@SchemaMapping(field = "entityKind")
	public Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, TopicRef origin,
		@Argument("format") String format) {

		return this.dataFetchersDelegateTopicRef.entityKind(dataFetchingEnvironment, origin,
			(FormatKind)GraphqlUtils.graphqlUtils.stringToEnumValue(format, FormatKind.class));
	}

	/**
	 * This methods loads the data for ${dataFetcher.graphQLType}.locations. It is generated as the
	 * <code>generateBatchMappingDataFetchers</code> plugin parameter is true. <br/>
	 * @param batchLoaderEnvironment The environement for this batch loaded. You can extract the GraphQLContext from
	 * this parameter.
	 * @param graphQLContext
	 * @param keys The objects for which the value for the locations field must be retrieved.
	 * @return This method returns <code>${dataFetcher.batchMappingReturnType.value}</code>, as defined by the
	 * <code>batchMappingDataFetcherReturnType</code> plugin parameter. <br/>
	 * Please look at the spring-graphql annotation for a documentation on how to return the proper values
	 */
	@BatchMapping(field = "locations")
	public Map<TopicRef, String> locations(BatchLoaderEnvironment batchLoaderEnvironment,
		GraphQLContext graphQLContext, List<TopicRef> keys) {

		return this.dataFetchersDelegateTopicRef.locations(batchLoaderEnvironment, graphQLContext, keys);
	}

}
