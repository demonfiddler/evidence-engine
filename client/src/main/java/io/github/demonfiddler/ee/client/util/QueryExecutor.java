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

import static com.graphql_java_generator.client.request.InputParameter.InputParameterType.MANDATORY;
import static com.graphql_java_generator.client.request.InputParameter.InputParameterType.OPTIONAL;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.InputParameter.InputParameterType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestExecutionUncheckedException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

import io.github.demonfiddler.ee.client.Claim;
import io.github.demonfiddler.ee.client.ClaimPage;
import io.github.demonfiddler.ee.client.Declaration;
import io.github.demonfiddler.ee.client.DeclarationPage;
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
 * Available queries. <br/>
 * This class contains the methods that allows the execution of the queries or mutations that are defined in the Query
 * of the GraphQL schema.<br/>
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
public class QueryExecutor implements GraphQLQueryExecutor {

	/** Logger for this class */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);

	@Autowired
	@Qualifier("httpGraphQlClient")
	GraphQlClient graphQlClient;

	@Autowired
	GraphqlClientUtilsEx graphqlClientUtils;

	@Autowired
	@Qualifier("queryReactiveExecutor")
	QueryReactiveExecutor queryReactiveExecutor;

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils; // must be set that way, to be used in the constructor
	public QueryExecutor() {
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
	 * a query executor, the provided request must be a query full request.<br/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<br/>
	 * Here is a sample on how to use it: This method takes a <B>full request</B> definition, and executes it against
	 * the GraphQL server. That is, the query contains the full string that <B><U>follows</U></B> the query
	 * keyword.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
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
	 *          Query query = executor.execWithBindValues(
	 *              "query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}",
	 *              callback,
	 *              params);
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above). It
	 * must omit the query keyword, and start by the first { that follows. It may contain directives, as explained in
	 * the GraphQL specs.
	 * @param parameters The map of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}. The key is the parameter
	 * name, as declared in the request you defined (in the above sample: param is an optional parameter and skip is a
	 * mandatory one). The value is the parameter value in its Java type (for instance a {@link java.util.Date} for the
	 * {@link GraphQLScalarTypeDate}). The parameters which value is missing in this map will be ignored.
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Query execWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMono(this.queryReactiveExecutor.execWithBindValues(queryResponseDef, parameters));
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request.<br/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<br/>
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
	 *          Query query = executor.exec(
	 *              "query { sampleQueryOrMutationField(param: ?param)  {subfield1 @skip(if: &skip) subfield2 {id name}}}",
	 *              "param", paramValue,   // param is optional, as it is marked by a "?" in the request
	 *              "skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 *              );
	 *          FieldType field = query.getSampleQueryOrMutationField();
	 *
	 *          .... do something with this field's value
	 *     }
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above). It
	 * must omit the query keyword, and start by the first { that follows. It may contain directives, as explained in
	 * the GraphQL specs.
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Query exec(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMono(this.queryReactiveExecutor.exec(queryResponseDef, paramsAndValues));
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request.<br/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<br/>
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
	 *          Query query = executor.execWithBindValues(
	 *              preparedRequest,
	 *              params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Query execWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMono(this.queryReactiveExecutor.execWithBindValues(objectResponse, parameters));
	}

	/**
	 * This method takes a <B>full request</B> definition, and executes it against the GraphQL server. As this class is
	 * a query executor, the provided request must be a query full request.<br/>
	 * This method offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace
	 * mode).<br/>
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
	 *          Query query = executor.exec(
	 *              preparedRequest,
	 *              "param", paramValue,   // param is optional, as it is marked by a "?" in the request
	 *              "skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 *              );
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
	 * {@link getGraphQLRequest(String)} method or one of the <code>getXxxxGraphQLRequest(String)</code>
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Query exec(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMono(this.queryReactiveExecutor.exec(objectResponse, paramsAndValues));
	}

	/**
	 * Get the {@link Builder} for a <B>full request</B>, as expected by the exec and execWithBindValues methods.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getResponseBuilder();
	}

	/**
	 * Get the {@link GraphQLRequest} for <B>full request</B>. For instance:
	 * 
	 * <PRE>
	 * 
	 * GraphQLRequest request = new GraphQLRequest(fullRequest);
	 * </PRE>
	 * 
	 * @param fullRequest The full GraphQL Request, as specified in the GraphQL specification
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getGraphQLRequest(String fullRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(fullRequest);
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method executes a partial query on the claims query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>claims</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		ClaimPage claims = executor.claimsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for claims's filter input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public ClaimPage claimsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claimsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method executes a partial query on the claims query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>claims</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		ClaimPage claims = executor.claims(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for claims's filter input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public ClaimPage claims(String queryResponseDef, LinkableEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claims(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		ClaimPage claims = executor.claimsWithBindValues(preparedRequest, filter, // A value for claims's filter
	 * 																					// input parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public ClaimPage claimsWithBindValues(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claimsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		ClaimPage claims = executor.claims(preparedRequest, filter, // A value for claims's filter input
	 * 																	// parameter
	 * 			pageSort, // A value for claims's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claims", graphQLTypeSimpleName = "ClaimPage", javaClass = ClaimPage.class)
	public ClaimPage claims(ObjectResponse objectResponse, LinkableEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claims(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * Get the {@link Builder} for the ClaimPage, as expected by the claims query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getClaimsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getClaimsResponseBuilder();
	}

	/**
	 * Returns a paged list of claims.<br/>
	 * Get the {@link GraphQLRequest} for the claims EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getClaimsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "claims",
			InputParameter.newBindParameter("", "filter", "queryClaimsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryClaimsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method executes a partial query on the claimById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>claimById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Claim claimById = executor.claimByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for claimById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Claim claimByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claimByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method executes a partial query on the claimById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>claimById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Claim claimById = executor.claimById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for claimById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the claimById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Claim claimById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.claimById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Claim claimById = executor.claimByIdWithBindValues(preparedRequest, id, // A value for claimById's id
	 * 																				// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Claim claimByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.claimByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Claim claimById = executor.claimById(preparedRequest, id, // A value for claimById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "claimById", graphQLTypeSimpleName = "Claim", javaClass = Claim.class)
	public Claim claimById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.claimById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * Get the {@link Builder} for the Claim, as expected by the claimById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getClaimByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getClaimByIdResponseBuilder();
	}

	/**
	 * Returns a claim given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the claimById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getClaimByIdGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "claimById",
			InputParameter.newBindParameter("", "id", "queryClaimByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method executes a partial query on the declarations query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>declarations</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		DeclarationPage declarations = executor.declarationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for declarations's filter input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public DeclarationPage declarationsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method executes a partial query on the declarations query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>declarations</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		DeclarationPage declarations = executor.declarations(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for declarations's filter input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public DeclarationPage declarations(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarations(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		DeclarationPage declarations = executor.declarationsWithBindValues(preparedRequest, filter, // A value
	 * 																									// for
	 * 																									// declarations's
	 * 																									// filter
	 * 																									// input
	 * 																									// parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public DeclarationPage declarationsWithBindValues(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		DeclarationPage declarations = executor.declarations(preparedRequest, filter, // A value for
	 * 																						// declarations's filter
	 * 																						// input parameter
	 * 			pageSort, // A value for declarations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarations", graphQLTypeSimpleName = "DeclarationPage",
		javaClass = DeclarationPage.class)
	public DeclarationPage declarations(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarations(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * Get the {@link Builder} for the DeclarationPage, as expected by the declarations query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getDeclarationsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getDeclarationsResponseBuilder();
	}

	/**
	 * Returns a paged list of declarations.<br/>
	 * Get the {@link GraphQLRequest} for the declarations EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getDeclarationsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "declarations",
			InputParameter.newBindParameter("", "filter", "queryDeclarationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryDeclarationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method executes a partial query on the declarationById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>declarationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Declaration declarationById = executor.declarationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for declarationById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Declaration declarationByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method executes a partial query on the declarationById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>declarationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Declaration declarationById = executor.declarationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for declarationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the declarationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Declaration declarationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Declaration declarationById = executor.declarationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																									// declarationById's
	 * 																									// id input
	 * 																									// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Declaration declarationByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Declaration declarationById = executor.declarationById(preparedRequest, id, // A value for
	 * 																					// declarationById's id input
	 * 																					// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "declarationById", graphQLTypeSimpleName = "Declaration",
		javaClass = Declaration.class)
	public Declaration declarationById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.declarationById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * Get the {@link Builder} for the Declaration, as expected by the declarationById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getDeclarationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getDeclarationByIdResponseBuilder();
	}

	/**
	 * Returns a declaration given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the declarationById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getDeclarationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "declarationById",
			InputParameter.newBindParameter("", "id", "queryDeclarationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method executes a partial query on the entityLinks query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinks</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		EntityLinkPage entityLinks = executor.entityLinksWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityLinks's filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public EntityLinkPage entityLinksWithBindValues(String queryResponseDef, EntityLinkQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinksWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method executes a partial query on the entityLinks query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinks</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		EntityLinkPage entityLinks = executor.entityLinks(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityLinks's filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and paginates results
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public EntityLinkPage entityLinks(String queryResponseDef, EntityLinkQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinks(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLinkPage entityLinks = executor.entityLinksWithBindValues(preparedRequest, filter, // A value for
	 * 																									// entityLinks's
	 * 																									// filter
	 * 																									// input
	 * 																									// parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public EntityLinkPage entityLinksWithBindValues(ObjectResponse objectResponse, EntityLinkQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinksWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLinkPage entityLinks = executor.entityLinks(preparedRequest, filter, // A value for entityLinks's
	 * 																					// filter input parameter
	 * 			pageSort, // A value for entityLinks's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class)
	public EntityLinkPage entityLinks(ObjectResponse objectResponse, EntityLinkQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinks(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * Get the {@link Builder} for the EntityLinkPage, as expected by the entityLinks query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinksResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getEntityLinksResponseBuilder();
	}

	/**
	 * Returns a paged list of entity links.<br/>
	 * Get the {@link GraphQLRequest} for the entityLinks EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getEntityLinksGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "entityLinks",
			InputParameter.newBindParameter("", "filter", "queryEntityLinksFilter", MANDATORY, "EntityLinkQueryFilter",
				true, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryEntityLinksPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method executes a partial query on the entityLinkById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinkById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		EntityLink entityLinkById = executor.entityLinkByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for entityLinkById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public EntityLink entityLinkByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinkByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method executes a partial query on the entityLinkById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinkById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		EntityLink entityLinkById = executor.entityLinkById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for entityLinkById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the entityLinkById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public EntityLink entityLinkById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinkById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLink entityLinkById = executor.entityLinkByIdWithBindValues(preparedRequest, id, // A value for
	 * 																								// entityLinkById's
	 * 																								// id input
	 * 																								// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public EntityLink entityLinkByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityLinkByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLink entityLinkById = executor.entityLinkById(preparedRequest, id, // A value for entityLinkById's
	 * 																					// id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkById", graphQLTypeSimpleName = "EntityLink", javaClass = EntityLink.class)
	public EntityLink entityLinkById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.entityLinkById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * Get the {@link Builder} for the EntityLink, as expected by the entityLinkById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinkByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getEntityLinkByIdResponseBuilder();
	}

	/**
	 * Returns an entity link given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the entityLinkById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getEntityLinkByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "entityLinkById",
			InputParameter.newBindParameter("", "id", "queryEntityLinkByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method executes a partial query on the entityLinkByEntityIds query against the GraphQL server. That is, the
	 * query is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part
	 * of the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinkByEntityIds</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		EntityLink entityLinkByEntityIds = executor.entityLinkByEntityIdsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			fromEntityId, // A value for entityLinkByEntityIds's fromEntityId input parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public EntityLink entityLinkByEntityIdsWithBindValues(String queryResponseDef, Long fromEntityId, Long toEntityId,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.entityLinkByEntityIdsWithBindValues(queryResponseDef,
			fromEntityId, toEntityId, parameters));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method executes a partial query on the entityLinkByEntityIds query against the GraphQL server. That is, the
	 * query is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part
	 * of the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityLinkByEntityIds</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		EntityLink entityLinkByEntityIds = executor.entityLinkByEntityIds(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			fromEntityId, // A value for entityLinkByEntityIds's fromEntityId input parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param fromEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param toEntityId Parameter for the entityLinkByEntityIds field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public EntityLink entityLinkByEntityIds(String queryResponseDef, Long fromEntityId, long toEntityId,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.entityLinkByEntityIds(queryResponseDef, fromEntityId,
			toEntityId, paramsAndValues));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLink entityLinkByEntityIds =
	 * 			executor.entityLinkByEntityIdsWithBindValues(preparedRequest, fromEntityId, // A value for
	 * 																						// entityLinkByEntityIds's
	 * 																						// fromEntityId input
	 * 																						// parameter
	 * 				toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 				params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public EntityLink entityLinkByEntityIdsWithBindValues(ObjectResponse objectResponse, Long fromEntityId,
		Long toEntityId, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.entityLinkByEntityIdsWithBindValues(objectResponse,
			fromEntityId, toEntityId, parameters));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		EntityLink entityLinkByEntityIds = executor.entityLinkByEntityIds(preparedRequest, fromEntityId, // A
	 * 																											// value
	 * 																											// for
	 * 																											// entityLinkByEntityIds's
	 * 																											// fromEntityId
	 * 																											// input
	 * 																											// parameter
	 * 			toEntityId, // A value for entityLinkByEntityIds's toEntityId input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityLinkByEntityIds", graphQLTypeSimpleName = "EntityLink",
		javaClass = EntityLink.class)
	public EntityLink entityLinkByEntityIds(ObjectResponse objectResponse, Long fromEntityId, Long toEntityId,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.entityLinkByEntityIds(objectResponse, fromEntityId,
			toEntityId, paramsAndValues));
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * Get the {@link Builder} for the EntityLink, as expected by the entityLinkByEntityIds query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityLinkByEntityIdsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getEntityLinkByEntityIdsResponseBuilder();
	}

	/**
	 * Returns an entity link given its from- and to-entity identifiers.<br/>
	 * Get the {@link GraphQLRequest} for the entityLinkByEntityIds EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getEntityLinkByEntityIdsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "entityLinkByEntityIds",
			InputParameter.newBindParameter("", "fromEntityId", "queryEntityLinkByEntityIdsFromEntityId", MANDATORY,
				"ID", true, 0, false),
			InputParameter.newBindParameter("", "toEntityId", "queryEntityLinkByEntityIdsToEntityId", MANDATORY, "ID",
				true, 0, false));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method executes a partial query on the journals query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>journals</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		JournalPage journals = executor.journalsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for journals's filter input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public JournalPage journalsWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journalsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method executes a partial query on the journals query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>journals</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		JournalPage journals = executor.journals(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for journals's filter input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public JournalPage journals(String queryResponseDef, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journals(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		JournalPage journals = executor.journalsWithBindValues(preparedRequest, filter, // A value for journals's
	 * 																						// filter input parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public JournalPage journalsWithBindValues(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journalsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		JournalPage journals = executor.journals(preparedRequest, filter, // A value for journals's filter input
	 * 																			// parameter
	 * 			pageSort, // A value for journals's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journals", graphQLTypeSimpleName = "JournalPage", javaClass = JournalPage.class)
	public JournalPage journals(ObjectResponse objectResponse, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journals(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * Get the {@link Builder} for the JournalPage, as expected by the journals query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getJournalsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getJournalsResponseBuilder();
	}

	/**
	 * Returns a paged list of journals.<br/>
	 * Get the {@link GraphQLRequest} for the journals EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getJournalsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "journals",
			InputParameter.newBindParameter("", "filter", "queryJournalsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryJournalsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method executes a partial query on the journalById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>journalById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Journal journalById = executor.journalByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for journalById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Journal journalByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journalByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method executes a partial query on the journalById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>journalById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Journal journalById = executor.journalById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for journalById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the journalById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Journal journalById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.journalById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Journal journalById = executor.journalByIdWithBindValues(preparedRequest, id, // A value for
	 * 																						// journalById's id input
	 * 																						// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Journal journalByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.journalByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Journal journalById = executor.journalById(preparedRequest, id, // A value for journalById's id input
	 * 																		// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "journalById", graphQLTypeSimpleName = "Journal", javaClass = Journal.class)
	public Journal journalById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.journalById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * Get the {@link Builder} for the Journal, as expected by the journalById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getJournalByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getJournalByIdResponseBuilder();
	}

	/**
	 * Returns a journal given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the journalById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getJournalByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "journalById",
			InputParameter.newBindParameter("", "id", "queryJournalByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method executes a partial query on the log query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the <code>log</code>
	 * of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please take a look at the
	 * StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		LogPage log = executor.logWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public LogPage logWithBindValues(String queryResponseDef, LogQueryFilter filter, PageableInput pageSort,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.logWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method executes a partial query on the log query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the <code>log</code>
	 * of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please take a look at the
	 * StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		LogPage log = executor.log(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public LogPage log(String queryResponseDef, LogQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.log(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		LogPage log = executor.logWithBindValues(preparedRequest, filter, // A value for log's filter input
	 * 																			// parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public LogPage logWithBindValues(ObjectResponse objectResponse, LogQueryFilter filter, PageableInput pageSort,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.logWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		LogPage log = executor.log(preparedRequest, filter, // A value for log's filter input parameter
	 * 			pageSort, // A value for log's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class)
	public LogPage log(ObjectResponse objectResponse, LogQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.log(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * Get the {@link Builder} for the LogPage, as expected by the log query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getLogResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getLogResponseBuilder();
	}

	/**
	 * Returns a paged list of log entries.<br/>
	 * Get the {@link GraphQLRequest} for the log EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getLogGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "log",
			InputParameter.newBindParameter("", "filter", "queryLogFilter", OPTIONAL, "LogQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryLogPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method executes a partial query on the persons query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>persons</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		PersonPage persons = executor.personsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for persons's filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public PersonPage personsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.personsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method executes a partial query on the persons query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>persons</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		PersonPage persons = executor.persons(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for persons's filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public PersonPage persons(String queryResponseDef, LinkableEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.persons(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PersonPage persons = executor.personsWithBindValues(preparedRequest, filter, // A value for persons's
	 * 																						// filter input parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public PersonPage personsWithBindValues(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.personsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PersonPage persons = executor.persons(preparedRequest, filter, // A value for persons's filter input
	 * 																		// parameter
	 * 			pageSort, // A value for persons's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "persons", graphQLTypeSimpleName = "PersonPage", javaClass = PersonPage.class)
	public PersonPage persons(ObjectResponse objectResponse, LinkableEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.persons(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * Get the {@link Builder} for the PersonPage, as expected by the persons query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPersonsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPersonsResponseBuilder();
	}

	/**
	 * Returns a paged list of persons.<br/>
	 * Get the {@link GraphQLRequest} for the persons EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPersonsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "persons",
			InputParameter.newBindParameter("", "filter", "queryPersonsFilter", OPTIONAL, "LinkableEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPersonsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method executes a partial query on the personById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>personById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Person personById = executor.personByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for personById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Person personByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.personByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method executes a partial query on the personById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>personById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Person personById = executor.personById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for personById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the personById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Person personById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.personById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Person personById = executor.personByIdWithBindValues(preparedRequest, id, // A value for personById's id
	 * 																					// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Person personByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.personByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Person personById = executor.personById(preparedRequest, id, // A value for personById's id input
	 * 																		// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "personById", graphQLTypeSimpleName = "Person", javaClass = Person.class)
	public Person personById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.personById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * Get the {@link Builder} for the Person, as expected by the personById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPersonByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPersonByIdResponseBuilder();
	}

	/**
	 * Returns a person given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the personById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPersonByIdGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "personById",
			InputParameter.newBindParameter("", "id", "queryPersonByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method executes a partial query on the publications query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publications</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		PublicationPage publications = executor.publicationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publications's filter input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public PublicationPage publicationsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method executes a partial query on the publications query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publications</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		PublicationPage publications = executor.publications(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publications's filter input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public PublicationPage publications(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publications(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PublicationPage publications = executor.publicationsWithBindValues(preparedRequest, filter, // A value
	 * 																									// for
	 * 																									// publications's
	 * 																									// filter
	 * 																									// input
	 * 																									// parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public PublicationPage publicationsWithBindValues(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PublicationPage publications = executor.publications(preparedRequest, filter, // A value for
	 * 																						// publications's filter
	 * 																						// input parameter
	 * 			pageSort, // A value for publications's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publications", graphQLTypeSimpleName = "PublicationPage",
		javaClass = PublicationPage.class)
	public PublicationPage publications(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publications(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * Get the {@link Builder} for the PublicationPage, as expected by the publications query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublicationsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPublicationsResponseBuilder();
	}

	/**
	 * Returns a paged list of publications.<br/>
	 * Get the {@link GraphQLRequest} for the publications EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPublicationsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "publications",
			InputParameter.newBindParameter("", "filter", "queryPublicationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublicationsPageSort", OPTIONAL, "PageableInput",
				false, 0, false));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method executes a partial query on the publicationById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publicationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Publication publicationById = executor.publicationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publicationById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Publication publicationByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method executes a partial query on the publicationById query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publicationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Publication publicationById = executor.publicationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publicationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publicationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Publication publicationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Publication publicationById = executor.publicationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																									// publicationById's
	 * 																									// id input
	 * 																									// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Publication publicationByIdWithBindValues(ObjectResponse objectResponse, Long id,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Publication publicationById = executor.publicationById(preparedRequest, id, // A value for
	 * 																					// publicationById's id input
	 * 																					// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publicationById", graphQLTypeSimpleName = "Publication",
		javaClass = Publication.class)
	public Publication publicationById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publicationById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * Get the {@link Builder} for the Publication, as expected by the publicationById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublicationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPublicationByIdResponseBuilder();
	}

	/**
	 * Returns a publication given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the publicationById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPublicationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "publicationById",
			InputParameter.newBindParameter("", "id", "queryPublicationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method executes a partial query on the publishers query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publishers</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		PublisherPage publishers = executor.publishersWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publishers's filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public PublisherPage publishersWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publishersWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method executes a partial query on the publishers query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publishers</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		PublisherPage publishers = executor.publishers(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for publishers's filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public PublisherPage publishers(String queryResponseDef, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publishers(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PublisherPage publishers = executor.publishersWithBindValues(preparedRequest, filter, // A value for
	 * 																								// publishers's
	 * 																								// filter input
	 * 																								// parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public PublisherPage publishersWithBindValues(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publishersWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		PublisherPage publishers = executor.publishers(preparedRequest, filter, // A value for publishers's
	 * 																				// filter input parameter
	 * 			pageSort, // A value for publishers's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publishers", graphQLTypeSimpleName = "PublisherPage",
		javaClass = PublisherPage.class)
	public PublisherPage publishers(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publishers(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * Get the {@link Builder} for the PublisherPage, as expected by the publishers query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublishersResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPublishersResponseBuilder();
	}

	/**
	 * Returns a paged list of publishers.<br/>
	 * Get the {@link GraphQLRequest} for the publishers EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPublishersGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "publishers",
			InputParameter.newBindParameter("", "filter", "queryPublishersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryPublishersPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method executes a partial query on the publisherById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publisherById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Publisher publisherById = executor.publisherByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publisherById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Publisher publisherByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publisherByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method executes a partial query on the publisherById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>publisherById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Publisher publisherById = executor.publisherById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for publisherById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the publisherById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Publisher publisherById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publisherById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Publisher publisherById = executor.publisherByIdWithBindValues(preparedRequest, id, // A value for
	 * 																							// publisherById's id
	 * 																							// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Publisher publisherByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.publisherByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Publisher publisherById = executor.publisherById(preparedRequest, id, // A value for publisherById's id
	 * 																				// input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "publisherById", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class)
	public Publisher publisherById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.publisherById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * Get the {@link Builder} for the Publisher, as expected by the publisherById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getPublisherByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getPublisherByIdResponseBuilder();
	}

	/**
	 * Returns a publisher given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the publisherById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getPublisherByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "publisherById",
			InputParameter.newBindParameter("", "id", "queryPublisherByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method executes a partial query on the quotations query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>quotations</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		QuotationPage quotations = executor.quotationsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for quotations's filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public QuotationPage quotationsWithBindValues(String queryResponseDef, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotationsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method executes a partial query on the quotations query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>quotations</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		QuotationPage quotations = executor.quotations(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for quotations's filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public QuotationPage quotations(String queryResponseDef, LinkableEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotations(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		QuotationPage quotations = executor.quotationsWithBindValues(preparedRequest, filter, // A value for
	 * 																								// quotations's
	 * 																								// filter input
	 * 																								// parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public QuotationPage quotationsWithBindValues(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotationsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		QuotationPage quotations = executor.quotations(preparedRequest, filter, // A value for quotations's
	 * 																				// filter input parameter
	 * 			pageSort, // A value for quotations's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotations", graphQLTypeSimpleName = "QuotationPage",
		javaClass = QuotationPage.class)
	public QuotationPage quotations(ObjectResponse objectResponse, LinkableEntityQueryFilter filter,
		PageableInput pageSort, Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotations(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * Get the {@link Builder} for the QuotationPage, as expected by the quotations query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getQuotationsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getQuotationsResponseBuilder();
	}

	/**
	 * Returns a paged list of quotations.<br/>
	 * Get the {@link GraphQLRequest} for the quotations EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getQuotationsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "quotations",
			InputParameter.newBindParameter("", "filter", "queryQuotationsFilter", OPTIONAL,
				"LinkableEntityQueryFilter", false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryQuotationsPageSort", OPTIONAL, "PageableInput", false,
				0, false));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method executes a partial query on the quotationById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>quotationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Quotation quotationById = executor.quotationByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for quotationById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Quotation quotationByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotationByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method executes a partial query on the quotationById query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>quotationById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Quotation quotationById = executor.quotationById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for quotationById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the quotationById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Quotation quotationById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotationById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Quotation quotationById = executor.quotationByIdWithBindValues(preparedRequest, id, // A value for
	 * 																							// quotationById's id
	 * 																							// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Quotation quotationByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.quotationByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Quotation quotationById = executor.quotationById(preparedRequest, id, // A value for quotationById's id
	 * 																				// input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "quotationById", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class)
	public Quotation quotationById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.quotationById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * Get the {@link Builder} for the Quotation, as expected by the quotationById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getQuotationByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getQuotationByIdResponseBuilder();
	}

	/**
	 * Returns a quotation given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the quotationById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getQuotationByIdGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "quotationById",
			InputParameter.newBindParameter("", "id", "queryQuotationByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method executes a partial query on the topics query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topics</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		TopicPage topics = executor.topicsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for topics's filter input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public TopicPage topicsWithBindValues(String queryResponseDef, TopicQueryFilter filter, PageableInput pageSort,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method executes a partial query on the topics query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topics</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		TopicPage topics = executor.topics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for topics's filter input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public TopicPage topics(String queryResponseDef, TopicQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topics(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		TopicPage topics = executor.topicsWithBindValues(preparedRequest, filter, // A value for topics's filter
	 * 																					// input parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public TopicPage topicsWithBindValues(ObjectResponse objectResponse, TopicQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		TopicPage topics = executor.topics(preparedRequest, filter, // A value for topics's filter input
	 * 																	// parameter
	 * 			pageSort, // A value for topics's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "TopicPage", javaClass = TopicPage.class)
	public TopicPage topics(ObjectResponse objectResponse, TopicQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topics(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * Get the {@link Builder} for the TopicPage, as expected by the topics query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getTopicsResponseBuilder();
	}

	/**
	 * Returns a paged list of topics or sub-topics.<br/>
	 * Get the {@link GraphQLRequest} for the topics EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getTopicsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "topics",
			InputParameter.newBindParameter("", "filter", "queryTopicsFilter", OPTIONAL, "TopicQueryFilter", false, 0,
				false),
			InputParameter.newBindParameter("", "pageSort", "queryTopicsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method executes a partial query on the topicById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topicById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Topic topicById = executor.topicByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for topicById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Topic topicByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method executes a partial query on the topicById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topicById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Topic topicById = executor.topicById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for topicById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the topicById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Topic topicById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.topicById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Topic topicById = executor.topicByIdWithBindValues(preparedRequest, id, // A value for topicById's id
	 * 																				// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Topic topicByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Topic topicById = executor.topicById(preparedRequest, id, // A value for topicById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicById", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public Topic topicById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.topicById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * Get the {@link Builder} for the Topic, as expected by the topicById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getTopicByIdResponseBuilder();
	}

	/**
	 * Returns a topic given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the topicById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getTopicByIdGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "topicById",
			InputParameter.newBindParameter("", "id", "queryTopicByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method executes a partial query on the users query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>users</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		UserPage users = executor.usersWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for users's filter input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	public UserPage usersWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.usersWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method executes a partial query on the users query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>users</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		UserPage users = executor.users(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for users's filter input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	public UserPage users(String queryResponseDef, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.users(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		UserPage users = executor.usersWithBindValues(preparedRequest, filter, // A value for users's filter
	 * 																				// input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	public UserPage usersWithBindValues(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.usersWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		UserPage users = executor.users(preparedRequest, filter, // A value for users's filter input parameter
	 * 			pageSort, // A value for users's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "users", graphQLTypeSimpleName = "UserPage", javaClass = UserPage.class)
	public UserPage users(ObjectResponse objectResponse, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.users(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of users.<br/>
	 * Get the {@link Builder} for the UserPage, as expected by the users query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUsersResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getUsersResponseBuilder();
	}

	/**
	 * Returns a paged list of users.<br/>
	 * Get the {@link GraphQLRequest} for the users EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getUsersGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "users",
			InputParameter.newBindParameter("", "filter", "queryUsersFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryUsersPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method executes a partial query on the userById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>userById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		User userById = executor.userByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for userById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method executes a partial query on the userById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>userById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		User userById = executor.userById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for userById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the userById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.userById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User userById = executor.userByIdWithBindValues(preparedRequest, id, // A value for userById's id input
	 * 																				// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User userById = executor.userById(preparedRequest, id, // A value for userById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userById", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.userById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * Get the {@link Builder} for the User, as expected by the userById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUserByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getUserByIdResponseBuilder();
	}

	/**
	 * Returns a user given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the userById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getUserByIdGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "userById",
			InputParameter.newBindParameter("", "id", "queryUserByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method executes a partial query on the userByUsername query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>userByUsername</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		User userByUsername = executor.userByUsernameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for userByUsername's username input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByUsernameWithBindValues(String queryResponseDef, String username, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByUsernameWithBindValues(queryResponseDef, username, parameters));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method executes a partial query on the userByUsername query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>userByUsername</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		User userByUsername = executor.userByUsername(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			username, // A value for userByUsername's username input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param username Parameter for the userByUsername field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByUsername(String queryResponseDef, String username, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByUsername(queryResponseDef, username, paramsAndValues));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User userByUsername = executor.userByUsernameWithBindValues(preparedRequest, username, // A value for
	 * 			// userByUsername's username
	 * 			// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByUsernameWithBindValues(ObjectResponse objectResponse, String username,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByUsernameWithBindValues(objectResponse, username, parameters));
	}

	/**
	 * Returns a user given its username.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User userByUsername = executor.userByUsername(preparedRequest, username, // A value for userByUsername's
	 * 																					// username input
	 * 			// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "userByUsername", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User userByUsername(ObjectResponse objectResponse, String username, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.userByUsername(objectResponse, username, paramsAndValues));
	}

	/**
	 * Returns a user given its username.<br/>
	 * Get the {@link Builder} for the User, as expected by the userByUsername query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getUserByUsernameResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getUserByUsernameResponseBuilder();
	}

	/**
	 * Returns a user given its username.<br/>
	 * Get the {@link GraphQLRequest} for the userByUsername EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getUserByUsernameGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "userByUsername",
			InputParameter.newBindParameter("", "username", "queryUserByUsernameUsername", MANDATORY, "String", true, 0,
				false));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method executes a partial query on the currentUser query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>currentUser</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		User currentUser = executor.currentUserWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User currentUserWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.currentUserWithBindValues(queryResponseDef, parameters));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method executes a partial query on the currentUser query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>currentUser</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		User currentUser = executor.currentUser(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User currentUser(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.currentUser(queryResponseDef, paramsAndValues));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User currentUser = executor.currentUserWithBindValues(preparedRequest, username, // A value for
	 * 			// currentUser's username
	 * 			// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User currentUserWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.currentUserWithBindValues(objectResponse, parameters));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		User currentUser = executor.currentUser(preparedRequest,
	 * 			// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "currentUser", graphQLTypeSimpleName = "User", javaClass = User.class)
	public User currentUser(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.currentUser(objectResponse, paramsAndValues));
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * Get the {@link Builder} for the User, as expected by the currentUser query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getCurrentUserResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getCurrentUserResponseBuilder();
	}

	/**
	 * Returns the currently logged-in user.<br/>
	 * Get the {@link GraphQLRequest} for the currentUser EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getCurrentUserGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "currentUser");
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method executes a partial query on the groups query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groups</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		GroupPage groups = executor.groupsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for groups's filter input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	public GroupPage groupsWithBindValues(String queryResponseDef, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupsWithBindValues(queryResponseDef, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method executes a partial query on the groups query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groups</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		GroupPage groups = executor.groups(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for groups's filter input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Filters results.
	 * @param pageSort Sorts and/or paginates results.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	public GroupPage groups(String queryResponseDef, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groups(queryResponseDef, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		GroupPage groups = executor.groupsWithBindValues(preparedRequest, filter, // A value for groups's filter
	 * 																					// input parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	public GroupPage groupsWithBindValues(ObjectResponse objectResponse, TrackedEntityQueryFilter filter,
		PageableInput pageSort, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupsWithBindValues(objectResponse, filter, pageSort, parameters));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		GroupPage groups = executor.groups(preparedRequest, filter, // A value for groups's filter input
	 * 																	// parameter
	 * 			pageSort, // A value for groups's pageSort input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groups", graphQLTypeSimpleName = "GroupPage", javaClass = GroupPage.class)
	public GroupPage groups(ObjectResponse objectResponse, TrackedEntityQueryFilter filter, PageableInput pageSort,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groups(objectResponse, filter, pageSort, paramsAndValues));
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * Get the {@link Builder} for the GroupPage, as expected by the groups query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getGroupsResponseBuilder();
	}

	/**
	 * Returns a paged list of groups.<br/>
	 * Get the {@link GraphQLRequest} for the groups EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getGroupsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "groups",
			InputParameter.newBindParameter("", "filter", "queryGroupsFilter", OPTIONAL, "TrackedEntityQueryFilter",
				false, 0, false),
			InputParameter.newBindParameter("", "pageSort", "queryGroupsPageSort", OPTIONAL, "PageableInput", false, 0,
				false));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method executes a partial query on the groupById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groupById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Group groupById = executor.groupByIdWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for groupById's id input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByIdWithBindValues(String queryResponseDef, Long id, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByIdWithBindValues(queryResponseDef, id, parameters));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method executes a partial query on the groupById query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groupById</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Group groupById = executor.groupById(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			id, // A value for groupById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param id Parameter for the groupById field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupById(String queryResponseDef, Long id, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.groupById(queryResponseDef, id, paramsAndValues));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Group groupById = executor.groupByIdWithBindValues(preparedRequest, id, // A value for groupById's id
	 * 																				// input
	 * 																				// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByIdWithBindValues(ObjectResponse objectResponse, Long id, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByIdWithBindValues(objectResponse, id, parameters));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Group groupById = executor.groupById(preparedRequest, id, // A value for groupById's id input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupById", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupById(ObjectResponse objectResponse, Long id, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.groupById(objectResponse, id, paramsAndValues));
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * Get the {@link Builder} for the Group, as expected by the groupById query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupByIdResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getGroupByIdResponseBuilder();
	}

	/**
	 * Returns a group given its identifier.<br/>
	 * Get the {@link GraphQLRequest} for the groupById EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getGroupByIdGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "groupById",
			InputParameter.newBindParameter("", "id", "queryGroupByIdId", MANDATORY, "ID", true, 0, false));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method executes a partial query on the groupByGroupname query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groupByGroupname</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		Group groupByGroupname = executor.groupByGroupnameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			groupname, // A value for groupByGroupname's groupname input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByGroupnameWithBindValues(String queryResponseDef, String groupname,
		Map<String, Object> parameters) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByGroupnameWithBindValues(queryResponseDef, groupname, parameters));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method executes a partial query on the groupByGroupname query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>groupByGroupname</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		Group groupByGroupname = executor.groupByGroupname(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			groupname, // A value for groupByGroupname's groupname input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param groupname Parameter for the groupByGroupname field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByGroupname(String queryResponseDef, String groupname, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByGroupname(queryResponseDef, groupname, paramsAndValues));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Group groupByGroupname = executor.groupByGroupnameWithBindValues(preparedRequest, groupname, // A value
	 * 																										// for
	 * 			// groupByGroupname's groupname
	 * 			// input parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByGroupnameWithBindValues(ObjectResponse objectResponse, String groupname,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByGroupnameWithBindValues(objectResponse, groupname, parameters));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		Group groupByGroupname = executor.groupByGroupname(preparedRequest, groupname, // A value for
	 * 																						// groupByGroupname's
	 * 																						// groupname input
	 * 			// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "groupByGroupname", graphQLTypeSimpleName = "Group", javaClass = Group.class)
	public Group groupByGroupname(ObjectResponse objectResponse, String groupname, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.groupByGroupname(objectResponse, groupname, paramsAndValues));
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * Get the {@link Builder} for the Group, as expected by the groupByGroupname query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getGroupByGroupnameResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getGroupByGroupnameResponseBuilder();
	}

	/**
	 * Returns a group given its groupname.<br/>
	 * Get the {@link GraphQLRequest} for the groupByGroupname EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getGroupByGroupnameGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "groupByGroupname",
			InputParameter.newBindParameter("", "groupname", "queryGroupByGroupnameGroupname", MANDATORY, "String",
				true, 0, false));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method executes a partial query on the entityStatistics query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityStatistics</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		List<EntityStatistics> entityStatistics = executor.entityStatisticsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityStatistics's filter input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public List<EntityStatistics> entityStatisticsWithBindValues(String queryResponseDef, StatisticsQueryFilter filter,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityStatisticsWithBindValues(queryResponseDef, filter, parameters));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method executes a partial query on the entityStatistics query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>entityStatistics</code> of the Query query type. It can be something like "{ id name }", or "" for a
	 * scalar. Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		List<EntityStatistics> entityStatistics = executor.entityStatistics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			filter, // A value for entityStatistics's filter input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public List<EntityStatistics> entityStatistics(String queryResponseDef, StatisticsQueryFilter filter,
		Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityStatistics(queryResponseDef, filter, paramsAndValues));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		List<EntityStatistics> entityStatistics =
	 * 			executor.entityStatisticsWithBindValues(preparedRequest, filter, // A value for entityStatistics's
	 * 																				// filter input parameter
	 * 				params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public List<EntityStatistics> entityStatisticsWithBindValues(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityStatisticsWithBindValues(objectResponse, filter, parameters));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		List<EntityStatistics> entityStatistics = executor.entityStatistics(preparedRequest, filter, // A value
	 * 																										// for
	 * 																										// entityStatistics's
	 * 																										// filter
	 * 																										// input
	 * 																										// parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "entityStatistics", graphQLTypeSimpleName = "EntityStatistics",
		javaClass = EntityStatistics.class)
	public List<EntityStatistics> entityStatistics(ObjectResponse objectResponse, StatisticsQueryFilter filter,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.entityStatistics(objectResponse, filter, paramsAndValues));
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * Get the {@link com.graphql_java_generator.client.request.Builder} for the EntityStatistics, as expected by the
	 * entityStatistics query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getEntityStatisticsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getEntityStatisticsResponseBuilder();
	}

	/**
	 * Returns statistics on the specified entity kinds.<br/>
	 * Get the {@link GraphQLRequest} for the entityStatistics EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getEntityStatisticsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "entityStatistics",
			InputParameter.newBindParameter("", "filter", "queryEntityStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method executes a partial query on the topicStatistics query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topicStatistics</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		List<TopicStatistics> topicStatistics = executor.topicStatisticsWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			topicFilter, // A value for topicStatistics's topicFilter input parameter
	 * 			entityFilter, // A value for topicStatistics's entityFilter input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public List<TopicStatistics> topicStatisticsWithBindValues(String queryResponseDef, StatisticsQueryFilter filter,
		Map<String, Object> parameters) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicStatisticsWithBindValues(queryResponseDef, filter, parameters));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method executes a partial query on the topicStatistics query against the GraphQL server. That is, the query
	 * is one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of
	 * the query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>topicStatistics</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		List<TopicStatistics> topicStatistics = executor.topicStatistics(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			topicFilter, // A value for topicStatistics's topicFilter input parameter
	 * 			entityFilter, // A value for topicStatistics's entityFilter input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param filter Selects the entities to include.
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public List<TopicStatistics> topicStatistics(String queryResponseDef, StatisticsQueryFilter filter,
		Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicStatistics(queryResponseDef, filter, paramsAndValues));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		List<TopicStatistics> topicStatistics =
	 * 			executor.topicStatisticsWithBindValues(preparedRequest, entityFilter, // A value for
	 * 																					// topicStatistics's
	 * 																					// entityFilter input
	 * 																					// parameter
	 * 				params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public List<TopicStatistics> topicStatisticsWithBindValues(ObjectResponse objectResponse,
		StatisticsQueryFilter filter, Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicStatisticsWithBindValues(objectResponse, filter, parameters));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		List<TopicStatistics> topicStatistics = executor.topicStatistics(preparedRequest, topicFilter, // A value
	 * 																										// for
	 * 																										// topicStatistics's
	 * 																										// topicFilter
	 * 																										// input
	 * 																										// parameter
	 * 			entityFilter, // A value for topicStatistics's entityFilter input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "topicStatistics", graphQLTypeSimpleName = "TopicStatistics",
		javaClass = TopicStatistics.class)
	public List<TopicStatistics> topicStatistics(ObjectResponse objectResponse, StatisticsQueryFilter filter,
		Object... paramsAndValues) throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.topicStatistics(objectResponse, filter, paramsAndValues));
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * Get the {@link Builder} for the TopicStatistics, as expected by the topicStatistics query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder getTopicStatisticsResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.getTopicStatisticsResponseBuilder();
	}

	/**
	 * Returns statistics on entities linked to the specified topic(s).<br/>
	 * Get the {@link GraphQLRequest} for the topicStatistics EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest getTopicStatisticsGraphQLRequest(String partialRequest)
		throws GraphQLRequestPreparationException {

		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "topicStatistics",
			InputParameter.newBindParameter("", "filter", "queryTopicStatisticsFilter", InputParameterType.OPTIONAL,
				"StatisticsQueryFilter", false, 0, false));
	}

	/**
	 * This method executes a partial query on the __schema query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__schema</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		__Schema __schema = executor.__schemaWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public __Schema __schemaWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.__schemaWithBindValues(queryResponseDef, parameters));
	}

	/**
	 * This method executes a partial query on the __schema query against the GraphQL server. That is, the query is one
	 * of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__schema</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		__Schema __schema = executor.__schema(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public __Schema __schema(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__schema(queryResponseDef, paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		__Schema __schema = executor.__schemaWithBindValues(preparedRequest, params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public __Schema __schemaWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__schemaWithBindValues(objectResponse, parameters));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		__Schema __schema = executor.__schema(preparedRequest, "param", paramValue, // param is optional, as it
	 * 																					// is marked by a "?" in the
	 * 																					// request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public __Schema __schema(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__schema(objectResponse, paramsAndValues));
	}

	/**
	 * Get the {@link Builder} for the __Schema, as expected by the __schema query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__schemaResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.get__schemaResponseBuilder();
	}

	/**
	 * Get the {@link GraphQLRequest} for the __schema EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest get__schemaGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "__schema");
	}

	/**
	 * This method executes a partial query on the __type query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__type</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		__Type __type = executor.__typeWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			name, // A value for __type's name input parameter
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public __Type __typeWithBindValues(String queryResponseDef, String name, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.__typeWithBindValues(queryResponseDef, name, parameters));
	}

	/**
	 * This method executes a partial query on the __type query against the GraphQL server. That is, the query is one of
	 * the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the query
	 * that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__type</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar. Please
	 * take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		__Type __type = executor.__type(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			name, // A value for __type's name input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param name Parameter for the __type field of Query, as defined in the GraphQL schema
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public __Type __type(String queryResponseDef, String name, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__type(queryResponseDef, name, paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		__Type __type = executor.__typeWithBindValues(preparedRequest, name, // A value for __type's name input
	 * 																				// parameter
	 * 			params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public __Type __typeWithBindValues(ObjectResponse objectResponse, String name, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.__typeWithBindValues(objectResponse, name, parameters));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		__Type __type = executor.__type(preparedRequest, name, // A value for __type's name input parameter
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public __Type __type(ObjectResponse objectResponse, String name, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__type(objectResponse, name, paramsAndValues));
	}

	/**
	 * Get the {@link Builder} for the __Type, as expected by the __type query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__typeResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.get__typeResponseBuilder();
	}

	/**
	 * Get the {@link GraphQLRequest} for the __type EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest get__typeGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "__type",
			InputParameter.newBindParameter("", "name", "query__typeName", MANDATORY, "String", true, 0, false));
	}

	/**
	 * This method executes a partial query on the __typename query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__typename</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
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
	 * 		String __typename = executor.__typenameWithBindValues(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			params);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public String __typenameWithBindValues(String queryResponseDef, Map<String, Object> parameters)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.__typenameWithBindValues(queryResponseDef, parameters));
	}

	/**
	 * This method executes a partial query on the __typename query against the GraphQL server. That is, the query is
	 * one of the field of the Query type defined in the GraphQL schema. The queryResponseDef contains the part of the
	 * query that follows the field name.<br/>
	 * It offers a logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method takes care of writing the query name, and the parameter(s) for the query. The given queryResponseDef
	 * describes the format of the response of the server response, that is the expected fields of the
	 * <code>__typename</code> of the Query query type. It can be something like "{ id name }", or "" for a scalar.
	 * Please take a look at the StarWars, Forum and other samples for more complex queries.<br/>
	 * Here is a sample on how to use it:
	 * 
	 * <PRE>
	 * &#64;Component // This class must be a spring component
	 * public class MyClass {
	 * 
	 * 	@Autowired
	 * 	QueryExecutor executor;
	 * 
	 * 	void myMethod() {
	 * 		String __typename = executor.__typename(
	 * 			"{subfield1 @aDirectiveToDemonstrateBindVariables(if: &skip, param: ?param) subfield2 {id name}}",
	 * 			"param", paramValue, // param is optional, as it is marked by a "?" in the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
	 * 	}
	 * 
	 * }
	 * </PRE>
	 * 
	 * @param queryResponseDef The response definition of the query, in the native GraphQL format (see here above)
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestPreparationException When an error occurs during the request preparation, typically when
	 * building the {@link ObjectResponse}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public String __typename(String queryResponseDef, Object... paramsAndValues)
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__typename(queryResponseDef, paramsAndValues));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		String __typename = executor.__typenameWithBindValues(preparedRequest, params);
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
	 * @param parameters The list of values, for the bind variables declared in the request you defined. If there is no
	 * bind variable in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public String __typenameWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(
			this.queryReactiveExecutor.__typenameWithBindValues(objectResponse, parameters));
	}

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).<br/>
	 * This method is valid for queries/mutations/subscriptions which don't have bind variables, as there is no
	 * <I>parameters</I> argument to pass the list of values.<br/>
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
	 * 		String __typename = executor.__typename(preparedRequest, "param", paramValue, // param is optional, as it
	 * 																						// is marked by a "?" in
	 * 																						// the request
	 * 			"skip", Boolean.FALSE // skip is mandatory, as it is marked by a "&" in the request
	 * 		);
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
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<br/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public String __typename(ObjectResponse objectResponse, Object... paramsAndValues)
		throws GraphQLRequestExecutionException {

		return getValueFromMonoOptional(this.queryReactiveExecutor.__typename(objectResponse, paramsAndValues));
	}

	/**
	 * Get the {@link Builder} for the String, as expected by the __typename query.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder get__typenameResponseBuilder() throws GraphQLRequestPreparationException {
		return this.queryReactiveExecutor.get__typenameResponseBuilder();
	}

	/**
	 * Get the {@link GraphQLRequest} for the __typename EXECUTOR, created with the given Partial request.
	 * @param partialRequest The Partial GraphQL request, as explained in the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">plugin client
	 * documentation</A>
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRequest get__typenameGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return new GraphQLRequest(this.graphQlClient, partialRequest, RequestType.query, "__typename");
	}

	/**
	 * Retrieves the value returned by the given Mono.
	 * @param <T>
	 * @param mono
	 * @param clazz
	 * @return
	 * @throws GraphQLRequestExecutionException A {@link GraphQLRequestExecutionException} is thrown, when a
	 * {@link GraphQLRequestExecutionUncheckedException} is thrown while processing the Mono.
	 */
	private static <T> T getValueFromMono(Mono<T> mono) throws GraphQLRequestExecutionException {
		try {
			return mono.block();
		} catch (GraphQLRequestExecutionUncheckedException e) {
			throw e.getGraphQLRequestExecutionException();
		}
	}

	/**
	 * Retrieves the value returned by the given Mono. The values are {@link Optional}, and the returned is either null
	 * or the non empty value
	 * @param <T>
	 * @param mono
	 * @param clazz
	 * @return
	 * @throws GraphQLRequestExecutionException A {@link GraphQLRequestExecutionException} is thrown, when a
	 * {@link GraphQLRequestExecutionUncheckedException} is thrown while processing the Mono.
	 */
	private static <T> T getValueFromMonoOptional(Mono<Optional<T>> mono) throws GraphQLRequestExecutionException {
		try {
			Optional<T> optional = mono.block();
			return (optional.isPresent()) ? optional.get() : null;
		} catch (GraphQLRequestExecutionUncheckedException e) {
			throw e.getGraphQLRequestExecutionException();
		}
	}

}
