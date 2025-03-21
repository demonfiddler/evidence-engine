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

import java.util.NoSuchElementException;
import java.util.Optional;

import org.dataloader.DataLoader;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.AbstractLinkableEntity;
import io.github.demonfiddler.ee.server.model.EntityLinkPage;
import io.github.demonfiddler.ee.server.model.PageableInput;

/**
 * This interface contains the data fetchers that are delegated in the bean that the implementation has to provide, when
 * fetching fields for the ILinkableEntity GraphQL type, as defined in the provided GraphQL schema. Please read the
 * <a href= "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server"> wiki server page</a>
 * for more information on this.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
public interface DataFetchersDelegateILinkableEntity<T extends AbstractLinkableEntity>
	extends DataFetchersDelegateITrackedEntity<T> {

	/**
	 * Description for the fromEntityLinks field: <br/>
	 * Outbound links for which the receiver is the 'linked-from' entity <br/>
	 * Loads the data for ILinkableEntity.fromEntityLinks. It may return whatever is accepted by the Spring
	 * Controller, that is:
	 * <ul>
	 * <li>A resolved value of any type (typically, an EntityLinkPage)</li>
	 * <li>Mono and Flux for asynchronous value(s). Supported for controller methods and for any DataFetcher as
	 * described in Reactive DataFetcher. This would typically be a Mono&lt;EntityLinkPage&gt; or a
	 * Flux&lt;EntityLinkPage&gt;</li>
	 * <li>Kotlin coroutine and Flow are adapted to Mono and Flux</li>
	 * <li>java.util.concurrent.Callable to have the value(s) produced asynchronously. For this to work,
	 * AnnotatedControllerConfigurer must be configured with an Executor. This would typically by a
	 * Callable&lt;EntityLinkPage&gt;</li>
	 * </ul>
	 * As a complement to the spring-graphql documentation, you may also return:
	 * <ul>
	 * <li>A CompletableFuture<?>, for instance CompletableFuture<io.github.demonfiddler.ee.server.EntityLinkPage>. This
	 * allows to use <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to
	 * highly optimize the number of requests to the server. The principle is this one: The data loader collects all the
	 * data to load, avoid to load several times the same data, and allows parallel execution of the queries, if
	 * multiple queries are to be run.</li>
	 * <li>A Publisher (instead of a Flux), for Subscription for instance</li>
	 * </ul>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param dataLoader The {@link DataLoader} allows to load several data in one query. It allows to solve the (n+1)
	 * queries issues, and greatly optimizes the response time.<BR/>
	 * You'll find more informations here:
	 * <A HREF= "https://github.com/graphql-java/java-dataloader">https://github.com/graphql-java/java-dataloader</A>
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the fromEntityLinks attribute of the <I>origin</I>, which is an instance of {ObjectType {name:? extends
	 * ILinkableEntity, fields:{Field{name:id, type:ID!, params:[]},Field{name:entityKind, type:String,
	 * params:[format:FormatKind]},Field{name:status, type:String, params:[format:FormatKind]},Field{name:created,
	 * type:DateTime, params:[]},Field{name:createdByUser, type:User, params:[]},Field{name:updated, type:DateTime,
	 * params:[]},Field{name:updatedByUser, type:User, params:[]},Field{name:log, type:LogPage!,
	 * params:[filter:LogQueryFilter,pageSort:PageableInput]},Field{name:fromEntityLinks, type:EntityLinkPage,
	 * params:[filter:LinkableEntityQueryFilter,pageSort:PageableInput]},Field{name:toEntityLinks, type:EntityLinkPage,
	 * params:[filter:LinkableEntityQueryFilter,pageSort:PageableInput]}, ...}, implements
	 * IBaseEntity,ITrackedEntity,ILinkableEntity, comments=empty}. It depends on your data model, but it typically
	 * contains the id to use in the query.
	 * @param filter The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @param pageSort The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is considered as {@code null}. This allows to use
	 * the {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 */
	Object fromEntityLinks(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, EntityLinkPage> dataLoader,
		T origin, LinkableEntityQueryFilter filter, PageableInput pageSort);

	/**
	 * Description for the toEntityLinks field: <br/>
	 * Inbound links for which the receiver is the 'linked-to' entity <br/>
	 * Loads the data for Claim.toEntityLinks. It may return whatever is accepted by the Spring Controller,
	 * that is:
	 * <ul>
	 * <li>A resolved value of any type (typically, an EntityLinkPage)</li>
	 * <li>Mono and Flux for asynchronous value(s). Supported for controller methods and for any DataFetcher as
	 * described in Reactive DataFetcher. This would typically be a Mono&lt;EntityLinkPage&gt; or a
	 * Flux&lt;EntityLinkPage&gt;</li>
	 * <li>Kotlin coroutine and Flow are adapted to Mono and Flux</li>
	 * <li>java.util.concurrent.Callable to have the value(s) produced asynchronously. For this to work,
	 * AnnotatedControllerConfigurer must be configured with an Executor. This would typically by a
	 * Callable&lt;EntityLinkPage&gt;</li>
	 * </ul>
	 * As a complement to the spring-graphql documentation, you may also return:
	 * <ul>
	 * <li>A CompletableFuture<?>, for instance CompletableFuture<EntityLinkPage>. This allows to use
	 * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize the
	 * number of requests to the server. The principle is this one: The data loader collects all the data to load, avoid
	 * to load several times the same data, and allows parallel execution of the queries, if multiple queries are to be
	 * run.</li>
	 * <li>A Publisher (instead of a Flux), for Subscription for instance</li>
	 * </ul>
	 * @param dataFetchingEnvironment The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full
	 * GraphQL context for this DataFetcher
	 * @param dataLoader The {@link DataLoader} allows to load several data in one query. It allows to solve the (n+1)
	 * queries issues, and greatly optimizes the response time.<BR/>
	 * You'll find more informations here:
	 * <A HREF= "https://github.com/graphql-java/java-dataloader">https://github.com/graphql-java/java-dataloader</A>
	 * @param origin The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch
	 * the toEntityLinks attribute of the <I>origin</I>, which is an instance of {ObjectType {name:T extends
	 * ILinkedEntity, fields:{Field{name:id, type:ID!, params:[]},Field{name:entityKind, type:String,
	 * params:[format:FormatKind]},Field{name:status, type:String, params:[format:FormatKind]},Field{name:created,
	 * type:DateTime, params:[]},Field{name:createdByUser, type:User, params:[]},Field{name:updated, type:DateTime,
	 * params:[]},Field{name:updatedByUser, type:User, params:[]},Field{name:log, type:LogPage!,
	 * params:[filter:LogQueryFilter,pageSort:PageableInput]},Field{name:fromEntityLinks, type:EntityLinkPage,
	 * params:[filter:LinkableEntityQueryFilter,pageSort:PageableInput]},Field{name:toEntityLinks, type:EntityLinkPage,
	 * params:[filter:LinkableEntityQueryFilter,pageSort:PageableInput]}, ...}, implements
	 * IBaseEntity,ITrackedEntity,ILinkableEntity, comments=empty}. It depends on your data model, but it typically
	 * contains the id to use in the query.
	 * @param filter The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @param pageSort The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException This method may return a {@link NoSuchElementException} exception. In this case,
	 * the exception is trapped by the calling method, and the return is considered as {@code null}. This allows to use
	 * the {@link Optional#get()} method directly, without caring of whether or not there is a value. The generated code
	 * will take care of the {@link NoSuchElementException} exception.
	 */
	Object toEntityLinks(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, EntityLinkPage> dataLoader,
		T origin, LinkableEntityQueryFilter filter, PageableInput pageSort);

}
