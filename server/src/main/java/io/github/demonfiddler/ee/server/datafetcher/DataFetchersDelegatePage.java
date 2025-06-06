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

import org.dataloader.BatchLoaderEnvironment;

import com.graphql_java_generator.util.GraphqlUtils;

import graphql.GraphQLContext;

/**
 * A generic base interface for all {@code XxxPage} interfaces.
 */
public interface DataFetchersDelegatePage<P, T> {

    /**
     * Loads the data for T.content.<br/>
     * @param batchLoaderEnvironment The environment for this batch loader. You can extract the GraphQLContext from this
     * parameter.
     * @param graphQLContext
     * @param keys The objects for which the value for the content field must be retrieved.
     * @return This method returns <code></code>, as defined by the <code>batchMappingDataFetcherReturnType</code>
     * plugin parameter. <br/>
     * Please look at the spring-graphql annotation for a documentation on how to return the proper values
     */
    Map<P, List<T>> content(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext, List<P> keys);

    /**
     * Loads a list of P, based on the list of ids to be fetched. This method is used by
     * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize the
     * number of requests to the server, when recursing down through the object associations.<BR/>
     * You can find more information on this page:
     * <A HREF="https://www.graphql-java.com/documentation/batching/">graphql-java batching</A><BR/>
     * <B>Important notes:</B>
     * <UL>
     * <LI>The list returned by this method must be sorted in the exact same order as the given <i>keys</i> list. If
     * values are missing (no value for a given key), then the returned list must contain a null value at this key's
     * position.</LI>
     * <LI>One of <code>batchLoader</code> or <code>unorderedReturnBatchLoader</code> must be overridden in the data
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
    default List<P> batchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        List<P> ret = unorderedReturnBatchLoader(keys, environment);
        if (ret == null) {
            throw new NullPointerException("Either batchLoader or unorderedReturnBatchLoader must be overridden in " //$NON-NLS-1$
                + getClass() + " implementation. And unorderedReturnBatchLoader must return a list.");
        }
        return GraphqlUtils.graphqlUtils.orderList(keys, ret, "dummy"); //$NON-NLS-1$
    }

    /**
     * Loads a list of P, based on the list of ids to be fetched. This method is used by
     * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize the
     * number of requests to the server, when recursing down through the object associations.<BR/>
     * You can find more information on this page:
     * <A HREF="https://www.graphql-java.com/documentation/batching/">graphql-java batching</A><BR/>
     * <B>Important notes:</B>
     * <UL>
     * <LI>The list returned may be in any order: this method is called by the default implementation of
     * <code>batchLoader</code>, which will sort the value return by this method, according to the given <i>keys</i>
     * list.</LI>
     * <LI>There may be missing values (no value for a given key): the default implementation of
     * <code>batchLoader</code> will replace these missing values by a null value at this key's position.</LI>
     * <LI>One of <code>batchLoader</code> or <code>unorderedReturnBatchLoader</code> must be overridden in the data
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
    default List<P> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return null;
    }

}
