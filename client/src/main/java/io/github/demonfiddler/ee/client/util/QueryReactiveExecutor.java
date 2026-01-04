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

import static com.graphql_java_generator.client.request.InputParameter.InputParameterType.MANDATORY;
import static com.graphql_java_generator.client.request.InputParameter.InputParameterType.OPTIONAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLDirective;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLQueryReactiveExecutor;
import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.InputParameter.InputParameterType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

import io.github.demonfiddler.ee.client.Claim;
import io.github.demonfiddler.ee.client.ClaimPage;
import io.github.demonfiddler.ee.client.Comment;
import io.github.demonfiddler.ee.client.CommentPage;
import io.github.demonfiddler.ee.client.CommentQueryFilter;
import io.github.demonfiddler.ee.client.Declaration;
import io.github.demonfiddler.ee.client.DeclarationPage;
import io.github.demonfiddler.ee.client.EntityAudit;
import io.github.demonfiddler.ee.client.EntityLink;
import io.github.demonfiddler.ee.client.EntityLinkPage;
import io.github.demonfiddler.ee.client.EntityLinkQueryFilter;
import io.github.demonfiddler.ee.client.EntityStatistics;
import io.github.demonfiddler.ee.client.Group;
import io.github.demonfiddler.ee.client.GroupPage;
import io.github.demonfiddler.ee.client.Journal;
import io.github.demonfiddler.ee.client.JournalPage;
import io.github.demonfiddler.ee.client.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.client.LogPage;
import io.github.demonfiddler.ee.client.LogQueryFilter;
import io.github.demonfiddler.ee.client.PageableInput;
import io.github.demonfiddler.ee.client.Person;
import io.github.demonfiddler.ee.client.PersonPage;
import io.github.demonfiddler.ee.client.Publication;
import io.github.demonfiddler.ee.client.PublicationPage;
import io.github.demonfiddler.ee.client.Publisher;
import io.github.demonfiddler.ee.client.PublisherPage;
import io.github.demonfiddler.ee.client.Query;
import io.github.demonfiddler.ee.client.Quotation;
import io.github.demonfiddler.ee.client.QuotationPage;
import io.github.demonfiddler.ee.client.StatisticsQueryFilter;
import io.github.demonfiddler.ee.client.Topic;
import io.github.demonfiddler.ee.client.TopicPage;
import io.github.demonfiddler.ee.client.TopicQueryFilter;
import io.github.demonfiddler.ee.client.TopicStatistics;
import io.github.demonfiddler.ee.client.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.client.User;
import io.github.demonfiddler.ee.client.UserPage;
import io.github.demonfiddler.ee.client.__Schema;
import io.github.demonfiddler.ee.client.__Type;
import reactor.core.publisher.Mono;

/**
 * Available queries. <BR/>
 * This class contains the methods that allows the execution of the queries or mutations that are defined in the Query
 * of the GraphQL schema. All the methods for this executor are in spring reactive, that is: they return a
 * {@link Mono}.<BR/>
 * These methods allows:
 * <UL>
 * <LI>Preparation of full requests</LI>
 * <LI>Execution of prepared full requests</LI>
 * <LI>Execution of direct full direct requests</LI>
 * <LI>Preparation of partial requests</LI>
 * <LI>Execution of prepared partial requests</LI>
 * <LI>Execution of direct partial requests</LI>
 * </UL>
 * You'll find all the documentation on the
 * <A HREF="https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_spring">client page
 * doc</A>.
 * @author generated by graphql-java-generator
 */
@Component

public class QueryReactiveExecutor implements GraphQLQueryReactiveExecutor {

	/** Logger for this class */
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryReactiveExecutor.class);

	@Autowired
	@Qualifier("httpGraphQlClient")
	GraphQlClient graphQlClient;

	@Autowired
	GraphqlClientUtilsEx graphqlClientUtils;

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils; // must be set that way, to be used in the constructor

	public QueryReactiveExecutor() {
		if (!"2.8".equals(this.graphqlUtils.getRuntimeVersion())) {
			throw new RuntimeException(
				"The GraphQL runtime version doesn't match the GraphQL plugin version. The runtime's version is '"
					+ this.graphqlUtils.getRuntimeVersion() + "' whereas the GraphQL plugin version is '2.8'");
		}
		CustomScalarRegistryInitializer.initCustomScalarRegistry();
		DirectiveRegistryInitializer.initDirectiveRegistry();
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request. This request will be executed in reactive
	 * mode, that is: it returns a {@link Mono}<Query><BR/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<BR/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 *     @Autowired
	 *     QueryExecutor executor;
	 * 
	 *     void myMethod() {
	 * 	        Map<String, Object> params = new HashMap<>();
	 *          params.put("param", paramValue);   // param is optional, as it is marked by a "?" in the request
	 *          params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 *          
	 *          Mono<Query> mono = executor.execWithBindValues(
	 *              "query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}",
	 *              callback,
	 *              params);
	 *          Query query = mono.block();
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * 
	 * 
	 * 
	 * Map<String, Object> params = new HashMap<>();
	 * params.put("heroParam", heroParamValue);
	 * params.put("skip", Boolean.FALSE);
	 * 
	 * Mono<Query> mono = myQueryType.execWithBindValues(
	 * 		"{hero(param:?heroParam) @include(if:true) {id name @skip(if: ?skip) appearsIn friends {id name}}}",
	 * 		params);
	 * ...
	 * Query response = mono.block();
	 * Character c = response.getHero();
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above). It
	 * must ommit the query/mutation/subscription keyword, and start by the first { that follows.It may contain
	 * directives, as explained in the GraphQL specs.
	 * @param parameters The map of values, for the bind variables defined in the query. If there is no bind variable in
	 * the defined Query, this argument may be null or an empty {@link Map}. The key is the parameter name, as defined
	 * in the query (in the above sample: heroParam is an optional parameter and skip is a mandatory one). The value is
	 * the parameter vale in its Java type (for instance a {@link java.util.Date} for the
	 * {@link GraphQLScalarTypeDate}). The parameters which value is missing in this map will no be transmitted toward
	 * the GraphQL server.
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> execWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query {} ", queryResponseDef);
		ObjectResponse objectResponse = getResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return execWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request. This request will be executed in reactive
	 * mode, that is: it returns a {@link Mono}<Query><BR/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<BR/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 *     @Autowired
	 *     QueryExecutor executor;
	 * 
	 *     void myMethod() {
	 *          Mono<Query> mono = executor.exec(
	 *              "query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}",
	 *              "param", paramValue,   // param is optional, as it is marked by a "?" in the request
	 *              "skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 *              );
	 *          Query query = mono.block();
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above). It
	 * must ommit the query/mutation/subscription keyword, and start by the first { that follows.It may contain
	 * directives, as explained in the GraphQL specs.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> exec(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query {} ", queryResponseDef);
		ObjectResponse objectResponse = getResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return execWithBindValues(objectResponse,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request. This request will be executed in reactive
	 * mode, that is: it returns a {@link Mono}<Query><BR/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<BR/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 *     &#64;Autowired
	 *     QueryExecutor executor;
	 *     
	 *     GraphQLRequest preparedRequest;
	 *     
	 *     @PostConstruct
	 *     public void setup() {
	 *         // Preparation of the query, so that it is prepared once then executed several times
	 *         preparedRequest = executor
	 *             .getResponseBuilder()
	 *             .withQueryResponseDef("query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}")
	 *             .build();
	 *     }
	 * 
	 *     void myMethod() {
	 * 	        Map<String, Object> params = new HashMap<>();
	 *          params.put("param", paramValue);   // param is optional, as it is marked by a "?" in the request
	 *          params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 *          
	 *          Mono<Query> mono = executor.execWithBindValues(
	 *              preparedRequest,
	 *              params);
	 *          Query query = mono.block();
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGraphQLRequest(String)} method or one of the <code>getXxxxGraphQLRequest(String)</code> methods.
	 * @param parameters The list of values, for the bind variables defined in the query. If there is no bind variable
	 * in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> execWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			if (parameters == null) {
				LOGGER.trace("Executing query without parameters");
			} else {
				StringBuilder sb = new StringBuilder("Executing root query with parameters: ");
				boolean addComma = false;
				for (String key : parameters.keySet()) {
					sb.append(key).append(":").append(parameters.get(key));
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
				LOGGER.trace(sb.toString());
			}
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'Query'");
		}

		return objectResponse.execReactive(Query.class, (parameters != null) ? parameters : new HashMap<>());
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request. This request will be executed in reactive
	 * mode, that is: it returns a {@link Mono}<Query><BR/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<BR/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 *     &#64;Autowired
	 *     QueryExecutor executor;
	 *     
	 *     GraphQLRequest preparedRequest;
	 *     
	 *     @PostConstruct
	 *     public void setup() {
	 *         // Preparation of the query, so that it is prepared once then executed several times
	 *         preparedRequest = executor
	 *             .getResponseBuilder()
	 *             .withQueryResponseDef("query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}")
	 *             .build();
	 *     }
	 * 
	 *     void myMethod() {
	 *          Mono<Query> mono = executor.exec(
	 *              preparedRequest,
	 *              "param", paramValue,   // param is optional, as it is marked by a "?" in the request
	 *              "skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 *              );
	 *          Query query = mono.block();
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGraphQLRequest(String)} method or one of the <code>getXxxxGraphQLRequest(String)</code> methods.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> exec(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return execWithBindValues(objectResponse,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Get the {@link Builder} for a <B>full request</B>, as expected by the exec and execWithBindValues methods.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class);
	}

	/**
	 * Get the {@link GraphQLReactiveRequest} for <B>full request</B>. For instance:
	 * 
	 * <PRE>
	 * 
	 * GraphQLReactiveRequest request = new GraphQLRequest(fullRequest);
	 * </PRE>
	 * 
	 * @param fullRequest The full GraphQL Request, as specified in the GraphQL specification
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getGraphQLRequest(String fullRequest) throws GraphQLRequestPreparationException {
		return new GraphQLReactiveRequest(fullRequest);
	}

	/**
	 * Returns a paged list of claims. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<ClaimPage> mono = executor.claimsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for claims's filter input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			params);
	 * 		ClaimPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public Mono<Optional<ClaimPage>> claimsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'claims': {} ", queryResponseDef);
		ObjectResponse objectResponse = getClaimsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return claimsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<ClaimPage> mono = executor.claims(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for claims's filter input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		ClaimPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public Mono<Optional<ClaimPage>> claims(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'claims': {} ", queryResponseDef);
		ObjectResponse objectResponse = getClaimsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return claimsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getClaimsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<ClaimPage> mono = executor.claimsWithBindValues(preparedRequest, filter, // A value for claims's
	 * 																						// filter input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			params);
	 * 		ClaimPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getClaimsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)

	public Mono<Optional<ClaimPage>> claimsWithBindValues(ObjectResponse objectResponse,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'claims' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'claims'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryClaimsFilter", filter);
		parametersLocal.put("queryClaimsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getClaims() == null) ? Optional.empty() : Optional.of(t.getClaims()));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getClaimsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<ClaimPage> mono = executor.claims(preparedRequest, filter, // A value for claims's filter input
	 * 																		// parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		ClaimPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getClaimsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public Mono<Optional<ClaimPage>> claims(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'claims' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'claims' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryClaimsFilter", filter);
		parameters.put("queryClaimsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getClaims() == null) ? Optional.empty() : Optional.of(t.getClaims()));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * Get the {@link Builder} for the ClaimPage, as expected by the claims query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getClaimsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "claims", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryClaimsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryClaimsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the claims REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getClaimsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "claims",
			InputParameter.newBindParameter("", "filter", "queryClaimsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryClaimsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a claim given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Claim> mono = executor.claimByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for claimById's id input parameter
	 * 			params);
	 * 		Claim field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Mono<Optional<Claim>> claimByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'claimById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getClaimByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return claimByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Claim> mono = executor.claimById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for claimById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Claim field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Mono<Optional<Claim>> claimById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'claimById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getClaimByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return claimByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getClaimByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Claim> mono = executor.claimByIdWithBindValues(preparedRequest, id, // A value for claimById's id
	 * 																					// input parameter
	 * 			params);
	 * 		Claim field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getClaimByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Mono<Optional<Claim>> claimByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'claimById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'claimById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryClaimByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getClaimById() == null) ? Optional.empty() : Optional.of(t.getClaimById()));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getClaimByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Claim> mono = executor.claimById(preparedRequest, id, // A value for claimById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Claim field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getClaimByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Mono<Optional<Claim>> claimById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'claimById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'claimById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryClaimByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getClaimById() == null) ? Optional.empty() : Optional.of(t.getClaimById()));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * Get the {@link Builder} for the Claim, as expected by the claimById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getClaimByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "claimById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryClaimByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the claimById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getClaimByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "claimById",
			InputParameter.newBindParameter("", "id", "queryClaimByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of comments. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<CommentPage> mono = executor.commentsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for comments's filter input parameter
	 * 			pageSort, // A value for comments's pageSort input parameter
	 * 			params);
	 * 		CommentPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage", javaClass = CommentPage.class)
	public Mono<Optional<CommentPage>> commentsWithBindValues(String queryResponseDef, CommentQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'comments': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCommentsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return commentsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of comments.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<CommentPage> mono = executor.comments(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for comments's filter input parameter
	 * 			pageSort, // A value for comments's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		CommentPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage", javaClass = CommentPage.class)
	public Mono<Optional<CommentPage>> comments(String queryResponseDef, CommentQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'comments': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCommentsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return commentsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of comments.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCommentsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<CommentPage> mono = executor.commentsWithBindValues(preparedRequest, filter, // A value for
	 * 																							// comments's filter
	 * 																							// input parameter
	 * 			pageSort, // A value for comments's pageSort input parameter
	 * 			params);
	 * 		CommentPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCommentsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage", javaClass = CommentPage.class)
	public Mono<Optional<CommentPage>> commentsWithBindValues(ObjectResponse objectResponse, CommentQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'comments' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'comments'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryCommentsFilter", filter);
		parametersLocal.put("queryCommentsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getComments() == null) ? Optional.empty() : Optional.of(t.getComments()));
	}

	/**
	 * Returns a paged list of comments.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCommentsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<CommentPage> mono = executor.comments(preparedRequest, filter, // A value for comments's filter
	 * 																			// input parameter
	 * 			pageSort, // A value for comments's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		CommentPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCommentsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage", javaClass = CommentPage.class)
	public Mono<Optional<CommentPage>> comments(ObjectResponse objectResponse, CommentQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'comments' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'comments' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryCommentsFilter", filter);
		parameters.put("queryCommentsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getComments() == null) ? Optional.empty() : Optional.of(t.getComments()));
	}

	/**
	 * Returns a paged list of comments.<br/>
	 * Get the {@link Builder} for the CommentPage, as expected by the comments query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getCommentsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "comments", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryCommentsFilter", InputParameterType.OPTIONAL,
				"CommentQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryCommentsPageSort", InputParameterType.OPTIONAL,
				"PageableInput", false, 0, false));
	}

	/**
	 * Returns a paged list of comments.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the comments REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getCommentsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "comments",
			InputParameter.newBindParameter("", "filter", "queryCommentsFilter", InputParameterType.OPTIONAL,
				"CommentQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryCommentsPageSort", InputParameterType.OPTIONAL,
				"PageableInput", false, 0, false));
	}

	/**
	 * Returns a comment given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Comment> mono = executor.commentByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for commentById's id input parameter
	 * 			params);
	 * 		Comment field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the commentById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "commentById", graphQLTypeSimpleName = "Comment", javaClass = Comment.class)
	public Mono<Optional<Comment>> commentByIdWithBindValues(String queryResponseDef, String id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'commentById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCommentByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return commentByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a comment given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Comment> mono = executor.commentById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for commentById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Comment field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the commentById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "commentById", graphQLTypeSimpleName = "Comment", javaClass = Comment.class)
	public Mono<Optional<Comment>> commentById(String queryResponseDef, String id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'commentById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCommentByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return commentByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a comment given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCommentByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Comment> mono = executor.commentByIdWithBindValues(preparedRequest, id, // A value for commentById's
	 * 																						// id input parameter
	 * 			params);
	 * 		Comment field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCommentByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the commentById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "commentById", graphQLTypeSimpleName = "Comment", javaClass = Comment.class)
	public Mono<Optional<Comment>> commentByIdWithBindValues(ObjectResponse objectResponse, String id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'commentById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'commentById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryCommentByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getCommentById() == null) ? Optional.empty() : Optional.of(t.getCommentById()));
	}

	/**
	 * Returns a comment given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCommentByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Comment> mono = executor.commentById(preparedRequest, id, // A value for commentById's id input
	 * 																		// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Comment field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCommentByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the commentById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "commentById", graphQLTypeSimpleName = "Comment", javaClass = Comment.class)
	public Mono<Optional<Comment>> commentById(ObjectResponse objectResponse, String id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'commentById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'commentById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryCommentByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getCommentById() == null) ? Optional.empty() : Optional.of(t.getCommentById()));
	}

	/**
	 * Returns a comment given its identifier.<br/>
	 * Get the {@link Builder} for the Comment, as expected by the commentById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getCommentByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "commentById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryCommentByIdId", InputParameterType.MANDATORY, "ID", true, 0,
				false));
	}

	/**
	 * Returns a comment given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the commentById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getCommentByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "commentById",
			InputParameter.newBindParameter("", "id", "queryCommentByIdId", InputParameterType.MANDATORY, "ID", true, 0,
				false));
	}

	/**
	 * Returns a paged list of declarations. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<DeclarationPage> mono = executor.declarationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for declarations's filter input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			params);
	 * 		DeclarationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public Mono<Optional<DeclarationPage>> declarationsWithBindValues(String queryResponseDef,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'declarations': {} ", queryResponseDef);
		ObjectResponse objectResponse = getDeclarationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return declarationsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<DeclarationPage> mono = executor.declarations(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for declarations's filter input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		DeclarationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public Mono<Optional<DeclarationPage>> declarations(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'declarations': {} ", queryResponseDef);
		ObjectResponse objectResponse = getDeclarationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return declarationsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getDeclarationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<DeclarationPage> mono = executor.declarationsWithBindValues(preparedRequest, filter, // A value for
	 * 																									// declarations's
	 * 																									// filter
	 * 																									// input
	 * 																									// parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			params);
	 * 		DeclarationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getDeclarationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)

	public Mono<Optional<DeclarationPage>> declarationsWithBindValues(ObjectResponse objectResponse,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'declarations' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'declarations'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryDeclarationsFilter", filter);
		parametersLocal.put("queryDeclarationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getDeclarations() == null) ? Optional.empty() : Optional.of(t.getDeclarations()));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getDeclarationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<DeclarationPage> mono = executor.declarations(preparedRequest, filter, // A value for declarations's
	 * 																					// filter input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		DeclarationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getDeclarationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public Mono<Optional<DeclarationPage>> declarations(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'declarations' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'declarations' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryDeclarationsFilter", filter);
		parameters.put("queryDeclarationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getDeclarations() == null) ? Optional.empty() : Optional.of(t.getDeclarations()));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * Get the {@link Builder} for the DeclarationPage, as expected by the declarations query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getDeclarationsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "declarations", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryDeclarationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryDeclarationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the declarations REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getDeclarationsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "declarations",
			InputParameter.newBindParameter("", "filter", "queryDeclarationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryDeclarationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a declaration given its identifier. This method executes a partial query against the GraphQL server. That
	 * is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the
	 * part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Declaration> mono = executor.declarationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for declarationById's id input parameter
	 * 			params);
	 * 		Declaration field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Mono<Optional<Declaration>> declarationByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'declarationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getDeclarationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return declarationByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Declaration> mono = executor.declarationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for declarationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Declaration field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Mono<Optional<Declaration>> declarationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'declarationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getDeclarationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return declarationByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getDeclarationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Declaration> mono = executor.declarationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																								// declarationById's
	 * 																								// id input
	 * 																								// parameter
	 * 			params);
	 * 		Declaration field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getDeclarationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Mono<Optional<Declaration>> declarationByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'declarationById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'declarationById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryDeclarationByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getDeclarationById() == null) ? Optional.empty() : Optional.of(t.getDeclarationById()));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getDeclarationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Declaration> mono = executor.declarationById(preparedRequest, id, // A value for declarationById's
	 * 																				// id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Declaration field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getDeclarationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Mono<Optional<Declaration>> declarationById(ObjectResponse objectResponse, Long id,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'declarationById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'declarationById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryDeclarationByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getDeclarationById() == null) ? Optional.empty() : Optional.of(t.getDeclarationById()));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * Get the {@link Builder} for the Declaration, as expected by the declarationById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getDeclarationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "declarationById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryDeclarationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the declarationById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getDeclarationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "declarationById",
			InputParameter.newBindParameter("", "id", "queryDeclarationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of entity links. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<EntityLinkPage> mono = executor.entityLinksWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityLinks's filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			params);
	 * 		EntityLinkPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public Mono<Optional<EntityLinkPage>> entityLinksWithBindValues(String queryResponseDef,
		EntityLinkQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinks': {} ", queryResponseDef);
		ObjectResponse objectResponse = getEntityLinksResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinksWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLinkPage> mono = executor.entityLinks(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityLinks's filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLinkPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public Mono<Optional<EntityLinkPage>> entityLinks(String queryResponseDef, EntityLinkQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinks': {} ", queryResponseDef);
		ObjectResponse objectResponse = getEntityLinksResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinksWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinksGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLinkPage> mono = executor.entityLinksWithBindValues(preparedRequest, filter, // A value for
	 * 																								// entityLinks's
	 * 																								// filter input
	 * 																								// parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			params);
	 * 		EntityLinkPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinksGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public Mono<Optional<EntityLinkPage>> entityLinksWithBindValues(ObjectResponse objectResponse,
		EntityLinkQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'entityLinks' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinks'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryEntityLinksFilter", filter);
		parametersLocal.put("queryEntityLinksPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getEntityLinks() == null) ? Optional.empty() : Optional.of(t.getEntityLinks()));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinksGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLinkPage> mono = executor.entityLinks(preparedRequest, filter, // A value for entityLinks's
	 * 																					// filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLinkPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinksGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public Mono<Optional<EntityLinkPage>> entityLinks(ObjectResponse objectResponse, EntityLinkQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'entityLinks' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinks' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryEntityLinksFilter", filter);
		parameters.put("queryEntityLinksPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getEntityLinks() == null) ? Optional.empty() : Optional.of(t.getEntityLinks()));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * Get the {@link Builder} for the EntityLinkPage, as expected by the entityLinks query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinksResponseBuilder() throws GraphQLRequestPreparationException {

		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "entityLinks", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryEntityLinksFilter", MANDATORY, "EntityLinkQueryFilter",
				true, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryEntityLinksPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the entityLinks REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getEntityLinksGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "entityLinks",
			InputParameter.newBindParameter("", "filter", "queryEntityLinksFilter", MANDATORY, "EntityLinkQueryFilter",
				true, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryEntityLinksPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns an entity link given its identifier. This method executes a partial query against the GraphQL server.
	 * That is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains
	 * the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<EntityLink> mono = executor.entityLinkByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for entityLinkById's id input parameter
	 * 			params);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinkById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityLinkByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinkByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for entityLinkById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinkById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityLinkByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinkByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinkByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkByIdWithBindValues(preparedRequest, id, // A value for
	 * 																							// entityLinkById's
	 * 																							// id input parameter
	 * 			params);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinkByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'entityLinkById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinkById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryEntityLinkByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getEntityLinkById() == null) ? Optional.empty() : Optional.of(t.getEntityLinkById()));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinkByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkById(preparedRequest, id, // A value for entityLinkById's id
	 * 																				// input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinkByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'entityLinkById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinkById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryEntityLinkByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getEntityLinkById() == null) ? Optional.empty() : Optional.of(t.getEntityLinkById()));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * Get the {@link Builder} for the EntityLink, as expected by the entityLinkById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinkByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "entityLinkById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryEntityLinkByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the entityLinkById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getEntityLinkByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "entityLinkById",
			InputParameter.newBindParameter("", "id", "queryEntityLinkByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers. This method executes a partial query against
	 * the GraphQL server. That is, the query that is one of the queries defined in the GraphQL query object. The
	 * queryResponseDef contains the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<EntityLink> mono = executor.entityLinkByEntityIdsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			fromEntityId, // A value for entityLinkByEntityIds's fromEntityId input parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			params);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByEntityIdsWithBindValues(String queryResponseDef, Long fromEntityId,
		Long toEntityId, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinkByEntityIds': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityLinkByEntityIdsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinkByEntityIdsWithBindValues(objectResponse, fromEntityId, toEntityId, parameters);
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkByEntityIds(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			fromEntityId, // A value for entityLinkByEntityIds's fromEntityId input parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByEntityIds(String queryResponseDef, Long fromEntityId, Long toEntityId,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'entityLinkByEntityIds': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityLinkByEntityIdsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityLinkByEntityIdsWithBindValues(objectResponse, fromEntityId, toEntityId,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinkByEntityIdsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkByEntityIdsWithBindValues(preparedRequest, fromEntityId, // A
	 * 																											// value
	 * 																											// for
	 * 																											// entityLinkByEntityIds's
	 * 																											// fromEntityId
	 * 																											// input
	 * 																											// parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			params);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinkByEntityIdsGraphQLRequest(String)} method.
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByEntityIdsWithBindValues(ObjectResponse objectResponse,
		Long fromEntityId, Long toEntityId, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'entityLinkByEntityIds' with parameters: {}, {} ", fromEntityId, toEntityId);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinkByEntityIds'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryEntityLinkByEntityIdsFromEntityId", fromEntityId);
		parametersLocal.put("queryEntityLinkByEntityIdsToEntityId", toEntityId);

		return objectResponse.execReactive(Query.class, parametersLocal).map(
			t -> (t.getEntityLinkByEntityIds() == null) ? Optional.empty() : Optional.of(t.getEntityLinkByEntityIds()));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityLinkByEntityIdsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityLink> mono = executor.entityLinkByEntityIds(preparedRequest, fromEntityId, // A value for
	 * 																								// entityLinkByEntityIds's
	 * 																								// fromEntityId
	 * 																								// input
	 * 																								// parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityLink field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityLinkByEntityIdsGraphQLRequest(String)} method.
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public Mono<Optional<EntityLink>> entityLinkByEntityIds(ObjectResponse objectResponse, Long fromEntityId,
		Long toEntityId, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'entityLinkByEntityIds' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityLinkByEntityIds' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryEntityLinkByEntityIdsFromEntityId", fromEntityId);
		parameters.put("queryEntityLinkByEntityIdsToEntityId", toEntityId);

		return objectResponse.execReactive(Query.class, parameters).map(
			t -> (t.getEntityLinkByEntityIds() == null) ? Optional.empty() : Optional.of(t.getEntityLinkByEntityIds()));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * Get the {@link Builder} for the EntityLink, as expected by the entityLinkByEntityIds query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinkByEntityIdsResponseBuilder() throws GraphQLRequestPreparationException {

		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "entityLinkByEntityIds", RequestType.query,
			InputParameter.newBindParameter("", "fromEntityId", "queryEntityLinkByEntityIdsFromEntityId", MANDATORY,
				"ID", true, 0, false),
			InputParameter.newBindParameter("", "toEntityId", "queryEntityLinkByEntityIdsToEntityId", MANDATORY, "ID",
				true, 0, false));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the entityLinkByEntityIds REACTIVE_EXECUTOR, created with the given
	 * Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getEntityLinkByEntityIdsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query,
			"entityLinkByEntityIds",
			InputParameter.newBindParameter("", "fromEntityId", "queryEntityLinkByEntityIdsFromEntityId", MANDATORY,
				"ID", true, 0, false),
			InputParameter.newBindParameter("", "toEntityId", "queryEntityLinkByEntityIdsToEntityId", MANDATORY, "ID",
				true, 0, false));
	}

	/**
	 * Returns a paged list of journals. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<JournalPage> mono = executor.journalsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for journals's filter input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			params);
	 * 		JournalPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public Mono<Optional<JournalPage>> journalsWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'journals': {} ", queryResponseDef);
		ObjectResponse objectResponse = getJournalsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return journalsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<JournalPage> mono = executor.journals(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for journals's filter input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		JournalPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public Mono<Optional<JournalPage>> journals(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'journals': {} ", queryResponseDef);
		ObjectResponse objectResponse = getJournalsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return journalsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getJournalsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<JournalPage> mono = executor.journalsWithBindValues(preparedRequest, filter, // A value for
	 * 																							// journals's filter
	 * 																							// input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			params);
	 * 		JournalPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getJournalsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)

	public Mono<Optional<JournalPage>> journalsWithBindValues(ObjectResponse objectResponse,
		TrackedEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'journals' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'journals'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryJournalsFilter", filter);
		parametersLocal.put("queryJournalsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getJournals() == null) ? Optional.empty() : Optional.of(t.getJournals()));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getJournalsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<JournalPage> mono = executor.journals(preparedRequest, filter, // A value for journals's filter
	 * 																			// input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		JournalPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getJournalsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public Mono<Optional<JournalPage>> journals(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'journals' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'journals' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryJournalsFilter", filter);
		parameters.put("queryJournalsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getJournals() == null) ? Optional.empty() : Optional.of(t.getJournals()));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * Get the {@link Builder} for the JournalPage, as expected by the journals query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getJournalsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "journals", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryJournalsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryJournalsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the journals REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getJournalsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "journals",
			InputParameter.newBindParameter("", "filter", "queryJournalsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryJournalsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a journal given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Journal> mono = executor.journalByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for journalById's id input parameter
	 * 			params);
	 * 		Journal field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Mono<Optional<Journal>> journalByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'journalById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getJournalByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return journalByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Journal> mono = executor.journalById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for journalById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Journal field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Mono<Optional<Journal>> journalById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'journalById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getJournalByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return journalByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getJournalByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Journal> mono = executor.journalByIdWithBindValues(preparedRequest, id, // A value for journalById's
	 * 																						// id input parameter
	 * 			params);
	 * 		Journal field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getJournalByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Mono<Optional<Journal>> journalByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'journalById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'journalById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryJournalByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getJournalById() == null) ? Optional.empty() : Optional.of(t.getJournalById()));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getJournalByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Journal> mono = executor.journalById(preparedRequest, id, // A value for journalById's id input
	 * 																		// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Journal field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getJournalByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Mono<Optional<Journal>> journalById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'journalById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'journalById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryJournalByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getJournalById() == null) ? Optional.empty() : Optional.of(t.getJournalById()));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * Get the {@link Builder} for the Journal, as expected by the journalById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getJournalByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "journalById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryJournalByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the journalById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getJournalByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "journalById",
			InputParameter.newBindParameter("", "id", "queryJournalByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of log entries. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<LogPage> mono = executor.logWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			params);
	 * 		LogPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public Mono<Optional<LogPage>> logWithBindValues(String queryResponseDef, LogQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'log': {} ", queryResponseDef);
		ObjectResponse objectResponse = getLogResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return logWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<LogPage> mono = executor.log(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		LogPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public Mono<Optional<LogPage>> log(String queryResponseDef, LogQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'log': {} ", queryResponseDef);
		ObjectResponse objectResponse = getLogResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return logWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getLogGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<LogPage> mono = executor.logWithBindValues(preparedRequest, filter, // A value for log's filter
	 * 																					// input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			params);
	 * 		LogPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getLogGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)

	public Mono<Optional<LogPage>> logWithBindValues(ObjectResponse objectResponse, LogQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'log' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'log'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryLogFilter", filter);
		parametersLocal.put("queryLogPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getLog() == null) ? Optional.empty() : Optional.of(t.getLog()));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getLogGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<LogPage> mono = executor.log(preparedRequest, filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		LogPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getLogGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public Mono<Optional<LogPage>> log(ObjectResponse objectResponse, LogQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'log' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'log' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryLogFilter", filter);
		parameters.put("queryLogPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getLog() == null) ? Optional.empty() : Optional.of(t.getLog()));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * Get the {@link Builder} for the LogPage, as expected by the log query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getLogResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "log", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryLogFilter", OPTIONAL, "LogQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryLogPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the log REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getLogGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "log",
			InputParameter.newBindParameter("", "filter", "queryLogFilter", OPTIONAL, "LogQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryLogPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of persons. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<PersonPage> mono = executor.personsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for persons's filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			params);
	 * 		PersonPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public Mono<Optional<PersonPage>> personsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'persons': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPersonsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return personsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<PersonPage> mono = executor.persons(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for persons's filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PersonPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public Mono<Optional<PersonPage>> persons(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'persons': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPersonsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return personsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPersonsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PersonPage> mono = executor.personsWithBindValues(preparedRequest, filter, // A value for persons's
	 * 																						// filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			params);
	 * 		PersonPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPersonsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)

	public Mono<Optional<PersonPage>> personsWithBindValues(ObjectResponse objectResponse,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'persons' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'persons'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPersonsFilter", filter);
		parametersLocal.put("queryPersonsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPersons() == null) ? Optional.empty() : Optional.of(t.getPersons()));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPersonsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PersonPage> mono = executor.persons(preparedRequest, filter, // A value for persons's filter input
	 * 																			// parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PersonPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPersonsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public Mono<Optional<PersonPage>> persons(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'persons' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'persons' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPersonsFilter", filter);
		parameters.put("queryPersonsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPersons() == null) ? Optional.empty() : Optional.of(t.getPersons()));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * Get the {@link Builder} for the PersonPage, as expected by the persons query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPersonsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "persons", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryPersonsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPersonsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the persons REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPersonsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "persons",
			InputParameter.newBindParameter("", "filter", "queryPersonsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPersonsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a person given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Person> mono = executor.personByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for personById's id input parameter
	 * 			params);
	 * 		Person field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Mono<Optional<Person>> personByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'personById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPersonByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return personByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Person> mono = executor.personById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for personById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Person field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Mono<Optional<Person>> personById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'personById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPersonByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return personByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPersonByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Person> mono = executor.personByIdWithBindValues(preparedRequest, id, // A value for personById's id
	 * 																					// input parameter
	 * 			params);
	 * 		Person field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPersonByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Mono<Optional<Person>> personByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'personById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'personById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPersonByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPersonById() == null) ? Optional.empty() : Optional.of(t.getPersonById()));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPersonByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Person> mono = executor.personById(preparedRequest, id, // A value for personById's id input
	 * 																		// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Person field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPersonByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Mono<Optional<Person>> personById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'personById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'personById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPersonByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPersonById() == null) ? Optional.empty() : Optional.of(t.getPersonById()));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * Get the {@link Builder} for the Person, as expected by the personById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPersonByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "personById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryPersonByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the personById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPersonByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "personById",
			InputParameter.newBindParameter("", "id", "queryPersonByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of publications. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<PublicationPage> mono = executor.publicationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publications's filter input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			params);
	 * 		PublicationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public Mono<Optional<PublicationPage>> publicationsWithBindValues(String queryResponseDef,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publications': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPublicationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publicationsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublicationPage> mono = executor.publications(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publications's filter input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PublicationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public Mono<Optional<PublicationPage>> publications(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publications': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPublicationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publicationsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublicationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublicationPage> mono = executor.publicationsWithBindValues(preparedRequest, filter, // A value for
	 * 																									// publications's
	 * 																									// filter
	 * 																									// input
	 * 																									// parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			params);
	 * 		PublicationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublicationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)

	public Mono<Optional<PublicationPage>> publicationsWithBindValues(ObjectResponse objectResponse,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'publications' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publications'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPublicationsFilter", filter);
		parametersLocal.put("queryPublicationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPublications() == null) ? Optional.empty() : Optional.of(t.getPublications()));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublicationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublicationPage> mono = executor.publications(preparedRequest, filter, // A value for publications's
	 * 																					// filter input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PublicationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublicationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public Mono<Optional<PublicationPage>> publications(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'publications' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publications' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPublicationsFilter", filter);
		parameters.put("queryPublicationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPublications() == null) ? Optional.empty() : Optional.of(t.getPublications()));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * Get the {@link Builder} for the PublicationPage, as expected by the publications query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublicationsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "publications", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryPublicationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublicationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the publications REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPublicationsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "publications",
			InputParameter.newBindParameter("", "filter", "queryPublicationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublicationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a publication given its identifier. This method executes a partial query against the GraphQL server. That
	 * is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the
	 * part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Publication> mono = executor.publicationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publicationById's id input parameter
	 * 			params);
	 * 		Publication field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Mono<Optional<Publication>> publicationByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publicationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getPublicationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publicationByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publication> mono = executor.publicationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publicationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Publication field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Mono<Optional<Publication>> publicationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publicationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getPublicationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publicationByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublicationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publication> mono = executor.publicationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																								// publicationById's
	 * 																								// id input
	 * 																								// parameter
	 * 			params);
	 * 		Publication field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublicationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)

	public Mono<Optional<Publication>> publicationByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'publicationById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publicationById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPublicationByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPublicationById() == null) ? Optional.empty() : Optional.of(t.getPublicationById()));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublicationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publication> mono = executor.publicationById(preparedRequest, id, // A value for publicationById's
	 * 																				// id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Publication field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublicationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Mono<Optional<Publication>> publicationById(ObjectResponse objectResponse, Long id,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'publicationById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publicationById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPublicationByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPublicationById() == null) ? Optional.empty() : Optional.of(t.getPublicationById()));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * Get the {@link Builder} for the Publication, as expected by the publicationById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublicationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "publicationById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryPublicationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the publicationById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPublicationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "publicationById",
			InputParameter.newBindParameter("", "id", "queryPublicationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of publishers. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<PublisherPage> mono = executor.publishersWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publishers's filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			params);
	 * 		PublisherPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public Mono<Optional<PublisherPage>> publishersWithBindValues(String queryResponseDef,
		TrackedEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publishers': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPublishersResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publishersWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublisherPage> mono = executor.publishers(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publishers's filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PublisherPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public Mono<Optional<PublisherPage>> publishers(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publishers': {} ", queryResponseDef);
		ObjectResponse objectResponse = getPublishersResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publishersWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublishersGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublisherPage> mono = executor.publishersWithBindValues(preparedRequest, filter, // A value for
	 * 																								// publishers's
	 * 																								// filter input
	 * 																								// parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			params);
	 * 		PublisherPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublishersGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)

	public Mono<Optional<PublisherPage>> publishersWithBindValues(ObjectResponse objectResponse,
		TrackedEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'publishers' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publishers'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPublishersFilter", filter);
		parametersLocal.put("queryPublishersPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPublishers() == null) ? Optional.empty() : Optional.of(t.getPublishers()));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublishersGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<PublisherPage> mono = executor.publishers(preparedRequest, filter, // A value for publishers's
	 * 																				// filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		PublisherPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublishersGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public Mono<Optional<PublisherPage>> publishers(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'publishers' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publishers' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPublishersFilter", filter);
		parameters.put("queryPublishersPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPublishers() == null) ? Optional.empty() : Optional.of(t.getPublishers()));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * Get the {@link Builder} for the PublisherPage, as expected by the publishers query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublishersResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "publishers", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryPublishersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublishersPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the publishers REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPublishersGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "publishers",
			InputParameter.newBindParameter("", "filter", "queryPublishersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublishersPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a publisher given its identifier. This method executes a partial query against the GraphQL server. That
	 * is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the
	 * part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Publisher> mono = executor.publisherByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publisherById's id input parameter
	 * 			params);
	 * 		Publisher field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Mono<Optional<Publisher>> publisherByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publisherById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getPublisherByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publisherByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publisher> mono = executor.publisherById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publisherById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Publisher field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Mono<Optional<Publisher>> publisherById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'publisherById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getPublisherByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return publisherByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublisherByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publisher> mono = executor.publisherByIdWithBindValues(preparedRequest, id, // A value for
	 * 																							// publisherById's id
	 * 																							// input parameter
	 * 			params);
	 * 		Publisher field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublisherByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Mono<Optional<Publisher>> publisherByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'publisherById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publisherById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryPublisherByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getPublisherById() == null) ? Optional.empty() : Optional.of(t.getPublisherById()));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getPublisherByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Publisher> mono = executor.publisherById(preparedRequest, id, // A value for publisherById's id
	 * 																			// input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Publisher field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getPublisherByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Mono<Optional<Publisher>> publisherById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'publisherById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'publisherById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryPublisherByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getPublisherById() == null) ? Optional.empty() : Optional.of(t.getPublisherById()));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * Get the {@link Builder} for the Publisher, as expected by the publisherById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublisherByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "publisherById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryPublisherByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the publisherById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getPublisherByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "publisherById",
			InputParameter.newBindParameter("", "id", "queryPublisherByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of quotations. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<QuotationPage> mono = executor.quotationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for quotations's filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			params);
	 * 		QuotationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public Mono<Optional<QuotationPage>> quotationsWithBindValues(String queryResponseDef,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'quotations': {} ", queryResponseDef);
		ObjectResponse objectResponse = getQuotationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return quotationsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<QuotationPage> mono = executor.quotations(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for quotations's filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		QuotationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public Mono<Optional<QuotationPage>> quotations(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'quotations': {} ", queryResponseDef);
		ObjectResponse objectResponse = getQuotationsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return quotationsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getQuotationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<QuotationPage> mono = executor.quotationsWithBindValues(preparedRequest, filter, // A value for
	 * 																								// quotations's
	 * 																								// filter input
	 * 																								// parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			params);
	 * 		QuotationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getQuotationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)

	public Mono<Optional<QuotationPage>> quotationsWithBindValues(ObjectResponse objectResponse,
		LinkableEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'quotations' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'quotations'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryQuotationsFilter", filter);
		parametersLocal.put("queryQuotationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getQuotations() == null) ? Optional.empty() : Optional.of(t.getQuotations()));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getQuotationsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<QuotationPage> mono = executor.quotations(preparedRequest, filter, // A value for quotations's
	 * 																				// filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		QuotationPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getQuotationsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public Mono<Optional<QuotationPage>> quotations(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'quotations' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'quotations' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryQuotationsFilter", filter);
		parameters.put("queryQuotationsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getQuotations() == null) ? Optional.empty() : Optional.of(t.getQuotations()));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * Get the {@link Builder} for the QuotationPage, as expected by the quotations query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getQuotationsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "quotations", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryQuotationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryQuotationsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the quotations REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getQuotationsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "quotations",
			InputParameter.newBindParameter("", "filter", "queryQuotationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryQuotationsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a quotation given its identifier. This method executes a partial query against the GraphQL server. That
	 * is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the
	 * part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Quotation> mono = executor.quotationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for quotationById's id input parameter
	 * 			params);
	 * 		Quotation field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Mono<Optional<Quotation>> quotationByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'quotationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getQuotationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return quotationByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Quotation> mono = executor.quotationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for quotationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Quotation field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Mono<Optional<Quotation>> quotationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'quotationById': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getQuotationByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return quotationByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getQuotationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Quotation> mono = executor.quotationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																							// quotationById's id
	 * 																							// input parameter
	 * 			params);
	 * 		Quotation field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getQuotationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Mono<Optional<Quotation>> quotationByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'quotationById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'quotationById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryQuotationByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getQuotationById() == null) ? Optional.empty() : Optional.of(t.getQuotationById()));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getQuotationByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Quotation> mono = executor.quotationById(preparedRequest, id, // A value for quotationById's id
	 * 																			// input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Quotation field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getQuotationByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Mono<Optional<Quotation>> quotationById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'quotationById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'quotationById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryQuotationByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getQuotationById() == null) ? Optional.empty() : Optional.of(t.getQuotationById()));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * Get the {@link Builder} for the Quotation, as expected by the quotationById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getQuotationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "quotationById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryQuotationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the quotationById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getQuotationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "quotationById",
			InputParameter.newBindParameter("", "id", "queryQuotationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of topics or sub-topics. This method executes a partial query against the GraphQL server.
	 * That is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains
	 * the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<TopicPage> mono = executor.topicsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for topics's filter input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			params);
	 * 		TopicPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public Mono<Optional<TopicPage>> topicsWithBindValues(String queryResponseDef, TopicQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'topics': {} ", queryResponseDef);
		ObjectResponse objectResponse = getTopicsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<TopicPage> mono = executor.topics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for topics's filter input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		TopicPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public Mono<Optional<TopicPage>> topics(String queryResponseDef, TopicQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'topics': {} ", queryResponseDef);
		ObjectResponse objectResponse = getTopicsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<TopicPage> mono = executor.topicsWithBindValues(preparedRequest, filter, // A value for topics's
	 * 																						// filter input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			params);
	 * 		TopicPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public Mono<Optional<TopicPage>> topicsWithBindValues(ObjectResponse objectResponse, TopicQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'topics' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topics'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryTopicsFilter", filter);
		parametersLocal.put("queryTopicsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getTopics() == null) ? Optional.empty() : Optional.of(t.getTopics()));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<TopicPage> mono = executor.topics(preparedRequest, filter, // A value for topics's filter input
	 * 																		// parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		TopicPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public Mono<Optional<TopicPage>> topics(ObjectResponse objectResponse, TopicQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'topics' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topics' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryTopicsFilter", filter);
		parameters.put("queryTopicsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getTopics() == null) ? Optional.empty() : Optional.of(t.getTopics()));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * Get the {@link Builder} for the TopicPage, as expected by the topics query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "topics", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryTopicsFilter", OPTIONAL, "TopicQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryTopicsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the topics REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getTopicsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "topics",
			InputParameter.newBindParameter("", "filter", "queryTopicsFilter", OPTIONAL, "TopicQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryTopicsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a topic given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Topic> mono = executor.topicByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for topicById's id input parameter
	 * 			params);
	 * 		Topic field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Mono<Optional<Topic>> topicByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'topicById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getTopicByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Topic> mono = executor.topicById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for topicById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Topic field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Mono<Optional<Topic>> topicById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'topicById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getTopicByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Topic> mono = executor.topicByIdWithBindValues(preparedRequest, id, // A value for topicById's id
	 * 																					// input parameter
	 * 			params);
	 * 		Topic field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)

	public Mono<Optional<Topic>> topicByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'topicById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topicById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryTopicByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getTopicById() == null) ? Optional.empty() : Optional.of(t.getTopicById()));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Topic> mono = executor.topicById(preparedRequest, id, // A value for topicById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Topic field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Mono<Optional<Topic>> topicById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'topicById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topicById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryTopicByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getTopicById() == null) ? Optional.empty() : Optional.of(t.getTopicById()));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * Get the {@link Builder} for the Topic, as expected by the topicById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "topicById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryTopicByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the topicById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getTopicByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "topicById",
			InputParameter.newBindParameter("", "id", "queryTopicByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of users. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<UserPage> mono = executor.usersWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for users's filter input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			params);
	 * 		UserPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<UserPage>> usersWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'users': {} ", queryResponseDef);
		ObjectResponse objectResponse = getUsersResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return usersWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<UserPage> mono = executor.users(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for users's filter input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		UserPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<UserPage>> users(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'users': {} ", queryResponseDef);
		ObjectResponse objectResponse = getUsersResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return usersWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUsersGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<UserPage> mono = executor.usersWithBindValues(preparedRequest, filter, // A value for users's filter
	 * 																					// input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			params);
	 * 		UserPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUsersGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<UserPage>> usersWithBindValues(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'users' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'users'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryUsersFilter", filter);
		parametersLocal.put("queryUsersPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getUsers() == null) ? Optional.empty() : Optional.of(t.getUsers()));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUsersGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<UserPage> mono = executor.users(preparedRequest, filter, // A value for users's filter input
	 * 																		// parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		UserPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUsersGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<UserPage>> users(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'users' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'users' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryUsersFilter", filter);
		parameters.put("queryUsersPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getUsers() == null) ? Optional.empty() : Optional.of(t.getUsers()));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * Get the {@link Builder} for the UserPage, as expected by the users query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUsersResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "users", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryUsersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryUsersPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the users REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getUsersGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "users",
			InputParameter.newBindParameter("", "filter", "queryUsersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryUsersPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a user given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<User> mono = executor.userByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for userById's id input parameter
	 * 			params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'userById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getUserByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return userByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for userById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'userById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getUserByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return userByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUserByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userByIdWithBindValues(preparedRequest, id, // A value for userById's id input
	 * 																				// parameter
	 * 			params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUserByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'userById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'userById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryUserByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getUserById() == null) ? Optional.empty() : Optional.of(t.getUserById()));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUserByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userById(preparedRequest, id, // A value for userById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUserByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'userById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'userById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryUserByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getUserById() == null) ? Optional.empty() : Optional.of(t.getUserById()));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * Get the {@link Builder} for the User, as expected by the userById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUserByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "userById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryUserByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the userById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getUserByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "userById",
			InputParameter.newBindParameter("", "id", "queryUserByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a user given its username. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<User> mono = executor.userByUsernameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for userByUsername's username input parameter
	 * 			params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByUsernameWithBindValues(String queryResponseDef, String username,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'userByUsername': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getUserByUsernameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return userByUsernameWithBindValues(objectResponse, username, parameters);
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userByUsername(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for userByUsername's username input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByUsername(String queryResponseDef, String username, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'userByUsername': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getUserByUsernameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return userByUsernameWithBindValues(objectResponse, username,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUserByUsernameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userByUsernameWithBindValues(preparedRequest, username, // A value for
	 * 																							// userByUsername's
	 * 																							// username input
	 * 																							// parameter
	 * 			params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUserByUsernameGraphQLRequest(String)} method.
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByUsernameWithBindValues(ObjectResponse objectResponse, String username,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'userByUsername' with parameters: {} ", username);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'userByUsername'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryUserByUsernameUsername", username);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getUserByUsername() == null) ? Optional.empty() : Optional.of(t.getUserByUsername()));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getUserByUsernameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.userByUsername(preparedRequest, username, // A value for userByUsername's
	 * 																				// username input
	 * 			// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getUserByUsernameGraphQLRequest(String)} method.
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<User>> userByUsername(ObjectResponse objectResponse, String username,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'userByUsername' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'userByUsername' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryUserByUsernameUsername", username);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getUserByUsername() == null) ? Optional.empty() : Optional.of(t.getUserByUsername()));
	}

	/**
	 * Returns a user given its username.<br/>
	 * Get the {@link Builder} for the User, as expected by the userByUsername query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUserByUsernameResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "userByUsername", RequestType.query,
			InputParameter.newBindParameter("", "username", "queryUserByUsernameUsername", MANDATORY, "String", true, 0,
				false));
	}

	/**
	 * Returns a user given its username.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the userByUsername REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getUserByUsernameGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "userByUsername",
			InputParameter.newBindParameter("", "username", "queryUserByUsernameUsername", MANDATORY, "String", true, 0,
				false));
	}

	/**
	 * Returns the currently logged-in user. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<User> mono = executor.currentUserWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for currentUser's username input parameter
	 * 			params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public Mono<Optional<User>> currentUserWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'currentUser': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCurrentUserResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return currentUserWithBindValues(objectResponse, parameters);
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.currentUser(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for currentUser's username input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public Mono<Optional<User>> currentUser(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'currentUser': {} ", queryResponseDef);
		ObjectResponse objectResponse = getCurrentUserResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return currentUserWithBindValues(objectResponse,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCurrentUserGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.currentUserWithBindValues(preparedRequest, params);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCurrentUserGraphQLRequest(String)} method.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public Mono<Optional<User>> currentUserWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'currentUser'");
		}

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getCurrentUser() == null) ? Optional.empty() : Optional.of(t.getCurrentUser()));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getCurrentUserGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<User> mono = executor.currentUser(preparedRequest, "param", paramValue, // param is optional, as it
	 * 																						// is marked by a "?" in
	 * 																						// the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		User field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getCurrentUserGraphQLRequest(String)} method.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public Mono<Optional<User>> currentUser(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'currentUser' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'currentUser' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getCurrentUser() == null) ? Optional.empty() : Optional.of(t.getCurrentUser()));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * Get the {@link Builder} for the User, as expected by the currentUser query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getCurrentUserResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "currentUser", RequestType.query);
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the currentUser REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getCurrentUserGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "currentUser");
	}

	/**
	 * Returns a paged list of groups. This method executes a partial query against the GraphQL server. That is, the
	 * query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part of
	 * the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<GroupPage> mono = executor.groupsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for groups's filter input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			params);
	 * 		GroupPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<GroupPage>> groupsWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groups': {} ", queryResponseDef);
		ObjectResponse objectResponse = getGroupsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupsWithBindValues(objectResponse, filter, pageSort, parameters);
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<GroupPage> mono = executor.groups(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for groups's filter input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		GroupPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<GroupPage>> groups(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groups': {} ", queryResponseDef);
		ObjectResponse objectResponse = getGroupsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupsWithBindValues(objectResponse, filter, pageSort,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<GroupPage> mono = executor.groupsWithBindValues(preparedRequest, filter, // A value for groups's
	 * 																						// filter
	 * 																						// input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			params);
	 * 		GroupPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<GroupPage>> groupsWithBindValues(ObjectResponse objectResponse,
		TrackedEntityQueryFilter filter, PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'groups' with parameters: {}, {} ", filter, pageSort);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groups'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryGroupsFilter", filter);
		parametersLocal.put("queryGroupsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getGroups() == null) ? Optional.empty() : Optional.of(t.getGroups()));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<GroupPage> mono = executor.groups(preparedRequest, filter, // A value for groups's filter input
	 * 																		// parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		GroupPage field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupsGraphQLRequest(String)} method.
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<GroupPage>> groups(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'groups' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groups' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryGroupsFilter", filter);
		parameters.put("queryGroupsPageSort", pageSort);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getGroups() == null) ? Optional.empty() : Optional.of(t.getGroups()));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * Get the {@link Builder} for the GroupPage, as expected by the groups query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "groups", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryGroupsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryGroupsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the groups REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getGroupsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "groups",
			InputParameter.newBindParameter("", "filter", "queryGroupsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryGroupsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a group given its identifier. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Group> mono = executor.groupByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for groupById's id input parameter
	 * 			params);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByIdWithBindValues(String queryResponseDef, Long id,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groupById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getGroupByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupByIdWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for groupById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groupById': {} ", queryResponseDef);
		ObjectResponse objectResponse = getGroupByIdResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupByIdWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupByIdWithBindValues(preparedRequest, id, // A value for groupById's id
	 * 																					// input
	 * 																					// parameter
	 * 			params);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'groupById' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groupById'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryGroupByIdId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getGroupById() == null) ? Optional.empty() : Optional.of(t.getGroupById()));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupByIdGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupById(preparedRequest, id, // A value for groupById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupByIdGraphQLRequest(String)} method.
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'groupById' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groupById' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryGroupByIdId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getGroupById() == null) ? Optional.empty() : Optional.of(t.getGroupById()));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * Get the {@link Builder} for the Group, as expected by the groupById query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "groupById", RequestType.query,
			InputParameter.newBindParameter("", "id", "queryGroupByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the groupById REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getGroupByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "groupById",
			InputParameter.newBindParameter("", "id", "queryGroupByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a group given its groupname. This method executes a partial query against the GraphQL server. That is,
	 * the query that is one of the queries defined in the GraphQL query object. The queryResponseDef contains the part
	 * of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<Group> mono = executor.groupByGroupnameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			groupname, // A value for groupByGroupname's groupname input parameter
	 * 			params);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByGroupnameWithBindValues(String queryResponseDef, String groupname,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groupByGroupname': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getGroupByGroupnameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupByGroupnameWithBindValues(objectResponse, groupname, parameters);
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupByGroupname(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			groupname, // A value for groupByGroupname's groupname input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByGroupname(String queryResponseDef, String groupname, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query 'groupByGroupname': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getGroupByGroupnameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return groupByGroupnameWithBindValues(objectResponse, groupname,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupByGroupnameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupByGroupnameWithBindValues(preparedRequest, groupname, // A value for
	 * 																								// groupByGroupname's
	 * 																								// groupname
	 * 																								// input
	 * 																								// parameter
	 * 			params);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupByGroupnameGraphQLRequest(String)} method.
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByGroupnameWithBindValues(ObjectResponse objectResponse, String groupname,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'groupByGroupname' with parameters: {} ", groupname);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groupByGroupname'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryGroupByGroupnameGroupname", groupname);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getGroupByGroupname() == null) ? Optional.empty() : Optional.of(t.getGroupByGroupname()));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getGroupByGroupnameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<Group> mono = executor.groupByGroupname(preparedRequest, groupname, // A value for
	 * 																					// groupByGroupname's
	 * 																					// groupname input
	 * 			// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		Group field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getGroupByGroupnameGraphQLRequest(String)} method.
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	@GraphQLDirective(name = "@auth", parameterNames = { "authority" }, parameterTypes = { "[AuthorityKind!]" },
		parameterValues = { "[ADM]" })
	public Mono<Optional<Group>> groupByGroupname(ObjectResponse objectResponse, String groupname,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'groupByGroupname' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'groupByGroupname' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryGroupByGroupnameGroupname", groupname);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getGroupByGroupname() == null) ? Optional.empty() : Optional.of(t.getGroupByGroupname()));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * Get the {@link Builder} for the Group, as expected by the groupByGroupname query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupByGroupnameResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "groupByGroupname", RequestType.query,
			InputParameter.newBindParameter("", "groupname", "queryGroupByGroupnameGroupname", MANDATORY, "String",
				true, 0, false));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the groupByGroupname REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getGroupByGroupnameGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "groupByGroupname",
			InputParameter.newBindParameter("", "groupname", "queryGroupByGroupnameGroupname", MANDATORY, "String",
				true, 0, false));
	}

	/**
	 * Returns statistics on the specified entity kinds. This method executes a partial query against the GraphQL
	 * server. That is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef
	 * contains the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<List<EntityStatistics>> mono = executor.entityStatisticsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityStatistics's filter input parameter
	 * 			params);
	 * 		List<EntityStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public Mono<Optional<List<EntityStatistics>>> entityStatisticsWithBindValues(String queryResponseDef,
		StatisticsQueryFilter filter, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'entityStatistics': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityStatisticsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityStatisticsWithBindValues(objectResponse, filter, parameters);
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<EntityStatistics>> mono = executor.entityStatistics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityStatistics's filter input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		List<EntityStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public Mono<Optional<List<EntityStatistics>>> entityStatistics(String queryResponseDef,
		StatisticsQueryFilter filter, Object... paramsAndValues)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'entityStatistics': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getEntityStatisticsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return entityStatisticsWithBindValues(objectResponse, filter,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityStatisticsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<EntityStatistics>> mono = executor.entityStatisticsWithBindValues(preparedRequest, filter, // A
	 * 																												// value
	 * 																												// for
	 * 																												// entityStatistics's
	 * 																												// filter
	 * 																												// input
	 * 																												// parameter
	 * 			params);
	 * 		List<EntityStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityStatisticsGraphQLRequest(String)} method.
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public Mono<Optional<List<EntityStatistics>>> entityStatisticsWithBindValues(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'entityStatistics' with parameters: {} ", filter);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityStatistics'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryEntityStatisticsFilter", filter);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getEntityStatistics() == null) ? Optional.empty() : Optional.of(t.getEntityStatistics()));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getEntityStatisticsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<EntityStatistics>> mono = executor.entityStatistics(preparedRequest, filter, // A value for
	 * 																								// entityStatistics's
	 * 																								// filter input
	 * 																								// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		List<EntityStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getEntityStatisticsGraphQLRequest(String)} method.
	 * @param filter Selects the entities to include.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public Mono<Optional<List<EntityStatistics>>> entityStatistics(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'entityStatistics' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'entityStatistics' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryEntityStatisticsFilter", filter);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getEntityStatistics() == null) ? Optional.empty() : Optional.of(t.getEntityStatistics()));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * Get the {@link Builder} for the EntityStatistics, as expected by the entityStatistics query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityStatisticsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "entityStatistics", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryEntityStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the entityStatistics REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getEntityStatisticsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "entityStatistics",
			InputParameter.newBindParameter("", "filter", "queryEntityStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s). This method executes a partial query against the
	 * GraphQL server. That is, the query that is one of the queries defined in the GraphQL query object. The
	 * queryResponseDef contains the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<List<TopicStatistics>> mono = executor.topicStatisticsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			topicFilter, // A value for topicStatistics's topicFilter input parameter
	 * 			entityFilter, // A value for topicStatistics's entityFilter input parameter
	 * 			params);
	 * 		List<TopicStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param topicFilter Selects the topics to include.
	 * @param filter Selects the linked entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public Mono<Optional<List<TopicStatistics>>> topicStatisticsWithBindValues(String queryResponseDef,
		StatisticsQueryFilter filter, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'topicStatistics': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getTopicStatisticsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicStatisticsWithBindValues(objectResponse, filter, parameters);
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<TopicStatistics>> mono = executor.topicStatistics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for topicStatistics's filter input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		List<TopicStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param filter Selects the linked entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public Mono<Optional<List<TopicStatistics>>> topicStatistics(String queryResponseDef, StatisticsQueryFilter filter,
		Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'topicStatistics': {} ", queryResponseDef);
		ObjectResponse objectResponse =
			getTopicStatisticsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return topicStatisticsWithBindValues(objectResponse, filter,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicStatisticsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<TopicStatistics>> mono = executor.topicStatisticsWithBindValues(preparedRequest, filter, // A
	 * 																											// value
	 * 																											// for
	 * 																											// topicStatistics's
	 * 																											// filter
	 * 																											// input
	 * 																											// parameter
	 * 			params);
	 * 		List<TopicStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicStatisticsGraphQLRequest(String)} method.
	 * @param filter Selects the linked entities to include.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public Mono<Optional<List<TopicStatistics>>> topicStatisticsWithBindValues(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'topicStatistics' with parameters: {}", filter);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topicStatistics'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryTopicStatisticsFilter", filter);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getTopicStatistics() == null) ? Optional.empty() : Optional.of(t.getTopicStatistics()));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getTopicStatisticsGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<List<TopicStatistics>> mono = executor.topicStatistics(preparedRequest, filter, // A value for
	 * 																								// topicStatistics's
	 * 																								// filter input
	 * 																								// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		List<TopicStatistics> field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getTopicStatisticsGraphQLRequest(String)} method.
	 * @param filter Selects the entities to include.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public Mono<Optional<List<TopicStatistics>>> topicStatistics(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'topicStatistics' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'topicStatistics' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryTopicStatisticsFilter", filter);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getTopicStatistics() == null) ? Optional.empty() : Optional.of(t.getTopicStatistics()));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * Get the {@link Builder} for the TopicStatistics, as expected by the topicStatistics query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicStatisticsResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "topicStatistics", RequestType.query,
			InputParameter.newBindParameter("", "filter", "queryTopicStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * Get the {@link GraphQLReactiveRequest} for the topicStatistics REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getTopicStatisticsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "topicStatistics",
			InputParameter.newBindParameter("", "filter", "queryTopicStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * Returns audit information on the specified entity. This method executes a partial query against the GraphQL
	 * server. That is, the query that is one of the queries defined in the GraphQL query object. The queryResponseDef
	 * contains the part of the query that <B><U>is after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<EntityAudit> mono = executor.auditWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for audit's id input parameter
	 * 			params);
	 * 		EntityAudit field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the audit field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "audit", graphQLTypeSimpleName = "EntityAudit", javaClass = EntityAudit.class)
	public Mono<Optional<EntityAudit>> auditWithBindValues(String queryResponseDef, String id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'audit': {} ", queryResponseDef);
		ObjectResponse objectResponse = getAuditResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return auditWithBindValues(objectResponse, id, parameters);
	}

	/**
	 * Returns audit information on the specified entity.<br/>
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityAudit> mono = executor.audit(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for audit's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityAudit field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param id Parameter for the audit field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "audit", graphQLTypeSimpleName = "EntityAudit", javaClass = EntityAudit.class)
	public Mono<Optional<EntityAudit>> audit(String queryResponseDef, String id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		LOGGER.debug("Executing query 'audit': {} ", queryResponseDef);
		ObjectResponse objectResponse = getAuditResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return auditWithBindValues(objectResponse, id,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * Returns audit information on the specified entity.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getAuditGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityAudit> mono = executor.auditWithBindValues(preparedRequest, id, // A value for audit's id
	 * 																					// input parameter
	 * 			params);
	 * 		EntityAudit field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getAuditGraphQLRequest(String)} method.
	 * @param id Parameter for the audit field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "audit", graphQLTypeSimpleName = "EntityAudit", javaClass = EntityAudit.class)
	public Mono<Optional<EntityAudit>> auditWithBindValues(ObjectResponse objectResponse, String id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query 'audit' with parameters: {} ", id);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'audit'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("queryAuditId", id);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.getAudit() == null) ? Optional.empty() : Optional.of(t.getAudit()));
	}

	/**
	 * Returns audit information on the specified entity.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.getAuditGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<EntityAudit> mono = executor.audit(preparedRequest, id, // A value for audit's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		EntityAudit field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link getAuditGraphQLRequest(String)} method.
	 * @param id Parameter for the audit field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "audit", graphQLTypeSimpleName = "EntityAudit", javaClass = EntityAudit.class)
	public Mono<Optional<EntityAudit>> audit(ObjectResponse objectResponse, String id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query 'audit' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query 'audit' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("queryAuditId", id);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.getAudit() == null) ? Optional.empty() : Optional.of(t.getAudit()));
	}

	/**
	 * Returns audit information on the specified entity.<br/>
	 * Get the {@link Builder} for the EntityAudit, as expected by the audit query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getAuditResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "audit", RequestType.query, InputParameter
			.newBindParameter("", "id", "queryAuditId", InputParameterType.MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns audit information on the specified entity.<br/>
	 * Get the {@link GraphQLReactiveRequest} for the audit REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest getAuditGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "audit", InputParameter
			.newBindParameter("", "id", "queryAuditId", InputParameterType.MANDATORY, "ID", true, 0, false));
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<__Schema> mono = executor.__schemaWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			params);
	 * 		__Schema field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public Mono<Optional<__Schema>> __schemaWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__schema': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__schemaResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __schemaWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Schema> mono = executor.__schema(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		__Schema field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public Mono<Optional<__Schema>> __schema(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__schema': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__schemaResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __schemaWithBindValues(objectResponse,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__schemaGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Schema> mono = executor.__schemaWithBindValues(preparedRequest, params);
	 * 		__Schema field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__schemaGraphQLRequest(String)} method.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)

	public Mono<Optional<__Schema>> __schemaWithBindValues(ObjectResponse objectResponse,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query '__schema' with parameters: ");
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__schema'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.get__schema() == null) ? Optional.empty() : Optional.of(t.get__schema()));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__schemaGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Schema> mono = executor.__schema(preparedRequest, "param", paramValue, // param is optional, as it
	 * 																						// is marked by a "?" in
	 * 																						// the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		__Schema field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__schemaGraphQLRequest(String)} method.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public Mono<Optional<__Schema>> __schema(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query '__schema' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__schema' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.get__schema() == null) ? Optional.empty() : Optional.of(t.get__schema()));
	}

	/**
	 * Get the {@link Builder} for the __Schema, as expected by the __schema query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__schemaResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "__schema", RequestType.query);
	}

	/**
	 * Get the {@link GraphQLReactiveRequest} for the __schema REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest get__schemaGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "__schema");
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<__Type> mono = executor.__typeWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			name, // A value for __type's name input parameter
	 * 			params);
	 * 		__Type field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public Mono<Optional<__Type>> __typeWithBindValues(String queryResponseDef, String name,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__type': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__typeResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __typeWithBindValues(objectResponse, name, parameters);
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Type> mono = executor.__type(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			name, // A value for __type's name input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		__Type field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public Mono<Optional<__Type>> __type(String queryResponseDef, String name, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__type': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__typeResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __typeWithBindValues(objectResponse, name,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__typeGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Type> mono = executor.__typeWithBindValues(preparedRequest, name, // A value for __type's name
	 * 																					// input parameter
	 * 			params);
	 * 		__Type field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__typeGraphQLRequest(String)} method.
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public Mono<Optional<__Type>> __typeWithBindValues(ObjectResponse objectResponse, String name,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query '__type' with parameters: {} ", name);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__type'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();
		parametersLocal.put("query__typeName", name);

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.get__type() == null) ? Optional.empty() : Optional.of(t.get__type()));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__typeGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<__Type> mono = executor.__type(preparedRequest, name, // A value for __type's name input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		__Type field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__typeGraphQLRequest(String)} method.
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public Mono<Optional<__Type>> __type(ObjectResponse objectResponse, String name, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query '__type' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__type' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);
		parameters.put("query__typeName", name);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.get__type() == null) ? Optional.empty() : Optional.of(t.get__type()));
	}

	/**
	 * Get the {@link Builder} for the __Type, as expected by the __type query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__typeResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "__type", RequestType.query,
			InputParameter.newBindParameter("", "name", "query__typeName", MANDATORY, "String", true, 0, false));
	}

	/**
	 * Get the {@link GraphQLReactiveRequest} for the __type REACTIVE_EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest get__typeGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "__type",
			InputParameter.newBindParameter("", "name", "query__typeName", MANDATORY, "String", true, 0, false));
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Map<String, Object> params = new HashMap<>();
	 * 		params.put("param", paramValue); // param is optional, as it is marked by a "?" in the request
	 * 		params.put("skip", Boolean.FALSE); // skip is mandatory, as it is marked by a "&" in the request
	 * 
	 * 		Mono<String> mono = executor.__typenameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			params);
	 * 		String field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation. The given
	 * queryResponseDef describes the format of the response of the server response, that is the expected fields of the
	 * {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this type.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public Mono<Optional<String>> __typenameWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__typename': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__typenameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __typenameWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method executes a partial query against the GraphQL server. That is, the query that is one of the queries
	 * defined in the GraphQL query object. The queryResponseDef contains the part of the query that <B><U>is
	 * after</U></B> the query name.<BR/>
	 * For instance, if the query hero has one parameter (as defined in the GraphQL schema):
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Mono<String> mono = executor.__typename(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		String field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method takes care of writing the query/mutation name, and the parameter(s) for the query/mutation . The
	 * given queryResponseDef describes the format of the response of the server response, that is the expected fields
	 * of the {@link Character} GraphQL type. It can be something like "{ id name }", if you want these fields of this
	 * type. Please take a look at the StarWars, Forum and other samples for more complex queries.<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * @param queryResponseDef The response definition of the query/mutation, in the native GraphQL format (see here
	 * above)
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public Mono<Optional<String>> __typename(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		LOGGER.debug("Executing query '__typename': {} ", queryResponseDef);
		ObjectResponse objectResponse = get__typenameResponseBuilder().withQueryResponseDef(queryResponseDef).build();
		return __typenameWithBindValues(objectResponse,
			this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__typenameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<String> mono = executor.__typenameWithBindValues(preparedRequest, params);
	 * 		String field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__typenameGraphQLRequest(String)} method.
	 * @param parameters The list of values, for the bind variables defined in the query/mutation. If there is no bind
	 * variable in the defined query/mutation, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public Mono<Optional<String>> __typenameWithBindValues(ObjectResponse objectResponse,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing query '__typename' with parameters: ");
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__typename'");
		}

		// Given values for the BindVariables
		Map<String, Object> parametersLocal = (parameters != null) ? parameters : new HashMap<>();

		return objectResponse.execReactive(Query.class, parametersLocal)
			.map(t -> (t.get__typename() == null) ? Optional.empty() : Optional.of(t.get__typename()));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<BR/>
	 * Here is a sample:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	&#64;Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	GraphQLRequest preparedRequest;
	 * 
	 * 	@PostConstruct
	 * 	public void setup() {
	 * 		// Preparation of the query, so that it is prepared once then executed several times
	 * 		preparedRequest = executor.get__typenameGraphQLRequest(
	 * 			"query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}");
	 * 	}
	 * 
	 * 	void myMethod() {
	 * 		Mono<String> mono = executor.__typename(preparedRequest, "param", paramValue, // param is optional, as it
	 * 																						// is marked by a "?" in
	 * 																						// the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 		String field = mono.block();
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param objectResponse The definition of the response format, that describes what the GraphQL server is expected
	 * to return<br/>
	 * Note: the <code>ObjectResponse</code> type of this parameter is defined for backward compatibility. In new
	 * implementations, the expected type is the generated GraphQLRequest POJO, as returned by the
	 * {@link get__typenameGraphQLRequest(String)} method.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public Mono<Optional<String>> __typename(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		if (LOGGER.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Executing query '__typename' with bind variables: ");
			boolean addComma = false;
			for (Object o : paramsAndValues) {
				if (o != null) {
					sb.append(o.toString());
					if (addComma)
						sb.append(", ");
					addComma = true;
				}
			}
			LOGGER.trace(sb.toString());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing query '__typename' (with bind variables)");
		}

		Map<String, Object> parameters = this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues);

		return objectResponse.execReactive(Query.class, parameters)
			.map(t -> (t.get__typename() == null) ? Optional.empty() : Optional.of(t.get__typename()));
	}

	/**
	 * Get the {@link Builder} for the String, as expected by the __typename query/mutation.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__typenameResponseBuilder() throws GraphQLRequestPreparationException {
		return new Builder(this.graphQlClient, GraphQLReactiveRequest.class, "__typename", RequestType.query);
	}

	/**
	 * Get the {@link GraphQLReactiveRequest} for the __typename REACTIVE_EXECUTOR, created with the given Partial
	 * request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest get__typenameGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLReactiveRequest(this.graphQlClient, partialRequest, RequestType.query, "__typename");
	}

}
