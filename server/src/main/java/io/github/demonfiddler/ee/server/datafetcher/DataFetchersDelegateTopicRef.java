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

package io.github.demonfiddler.ee.server.datafetcher;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.dataloader.BatchLoaderEnvironment;

import com.graphql_java_generator.util.GraphqlUtils;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.TopicRef;

/**
 * This interface contains the fata fetchers that are delegated in the bean that the implementation has to provide, when
 * fetching fields for the TopicRef GraphQL type, as defined in the provided GraphQL schema. Please read the
 * <a href= "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server"> wiki server page</a>
 * for more information on this.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */

public interface DataFetchersDelegateTopicRef {

	/**
	 * Description for the entityKind field: <br/>
	 * The associated entity kind. <br/>
	 * This method loads the data for TopicRef.entityKind. It may return whatever is accepted by the Spring Controller,
	 * that is:
	 * <ul>
	 * <li>A resolved value of any type (typically, a String)</li>
	 * <li>Mono and Flux for asynchronous value(s). Supported for controller methods and for any DataFetcher as
	 * described in Reactive DataFetcher. This would typically be a Mono&lt;String&gt; or a Flux&lt;String&gt;</li>
	 * <li>Kotlin coroutine and Flow are adapted to Mono and Flux</li>
	 * <li>java.util.concurrent.Callable to have the value(s) produced asynchronously. For this to work,
	 * AnnotatedControllerConfigurer must be configured with an Executor. This would typically by a
	 * Callable&lt;String&gt;</li>
	 * </ul>
	 * As a complement to the spring-graphql documentation, you may also return:
	 * <ul>
	 * <li>A CompletableFuture<?>, for instance CompletableFuture<String>. This allows to use
	 * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize the
	 * number of requests to the server. The principle is this one: The data loader collects all the data to load, avoid
	 * to load several times the same data, and allows parallel execution of the queries, if multiple queries are to be
	 * run.</li>
	 * <li>A Publisher (instead of a Flux), for Subscription for instance</li>
	 * </ul>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the entityKind attribute of the <I>origin</I>, which is an instance of {ObjectType {name:TopicRef,
	 * fields:{Field{name:id, type:ID!, params:[]},Field{name:topic_id, type:Long!, params:[]},Field{name:topic,
	 * type:Topic, params:[]},Field{name:entityKind, type:String, params:[format:FormatKind]},Field{name:entityId,
	 * type:Long!, params:[]},Field{name:entity, type:ITopicalEntity!, params:[]},Field{name:locations, type:[URI!],
	 * params:[]}}, implements IBaseEntity, comments ""}. It depends on your data modle, but it typically contains the
	 * id to use in the query.
	 * @param format The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is consider as null. This allows to use the
	 * {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 */
	Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, TopicRef origin, FormatKind format);

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
	Map<TopicRef, String> locations(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
		List<TopicRef> keys);

	/**
	 * This method loads a list of ${dataFetcher.field.name}, based on the list of id to be fetched. This method is used
	 * by <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize
	 * the number of requests to the server, when recursing down through the object associations.<BR/>
	 * You can find more information on this page:
	 * <A HREF="https://www.graphql-java.com/documentation/batching/">graphql-java batching</A><BR/>
	 * <B>Important notes:</B>
	 * <UL>
	 * <LI>The list returned by this method must be sorted in the exact same order as the given <i>keys</i> list. If
	 * values are missing (no value for a given key), then the returned list must contain a null value at this key's
	 * position.</LI>
	 * <LI>One of <code>batchLoader</code> or <code>unorderedReturnBatchLoader</code> must be overriden in the data
	 * fetcher implementation. If not, then a NullPointerException will be thrown at runtime, with a proper error
	 * message.</LI>
	 * <LI>If your data storage implementation makes it complex to return values in the same order as the keys list,
	 * then it's easier to override <code>unorderedReturnBatchLoader</code>, and let the default implementation of
	 * <code>batchLoader</code> order the values</LI>
	 * </UL>
	 * @param keys A list of ID's id, for which the matching objects must be returned
	 * @param environment The Data Loader environment
	 * @return A list of IDs
	 */
	default List<TopicRef> batchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
		List<TopicRef> ret = unorderedReturnBatchLoader(keys, environment);
		if (ret == null) {
			throw new NullPointerException(
				"Either batchLoader or unorderedReturnBatchLoader must be overriden in DataFetchersDelegateTopicRef implementation. And unorderedReturnBatchLoader must return a list."); //$NON-NLS-1$
		}
		return GraphqlUtils.graphqlUtils.orderList(keys, ret, "id"); //$NON-NLS-1$
	}

	/**
	 * This method loads a list of ${dataFetcher.field.name}, based on the list of id to be fetched. This method is used
	 * by <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize
	 * the number of requests to the server, when recursing down through the object associations.<BR/>
	 * You can find more information on this page:
	 * <A HREF="https://www.graphql-java.com/documentation/batching/">graphql-java batching</A><BR/>
	 * <B>Important notes:</B>
	 * <UL>
	 * <LI>The list returned may be in any order: this method is called by the default implementation of
	 * <code>batchLoader</code>, which will sort the value return by this method, according to the given <i>keys</i>
	 * list.</LI>
	 * <LI>There may be missing values (no value for a given key): the default implementation of
	 * <code>batchLoader</code> will replace these missing values by a null value at this key's position.</LI>
	 * <LI>One of <code>batchLoader</code> or <code>unorderedReturnBatchLoader</code> must be overriden in the data
	 * fetcher implementation. If not, then a NullPointerException will be thrown at runtime, with a proper error
	 * message.</LI>
	 * <LI>If your data storage implementation makes it complex to return values in the same order as the keys list,
	 * then it's easier to override <code>unorderedReturnBatchLoader</code>, and let the default implementation of
	 * <code>batchLoader</code> order the values</LI>
	 * <LI>If your data storage implementation makes it easy to return values in the same order as the keys list, then
	 * the execution is a little quicker if you override <code>batchLoader</code>, as there would be no sort of the
	 * returned list.</LI>
	 * </UL>
	 * @param keys A list of ID's id, for which the matching objects must be returned
	 * @param environment The Data Loader environment
	 * @return
	 */
	default List<TopicRef> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
