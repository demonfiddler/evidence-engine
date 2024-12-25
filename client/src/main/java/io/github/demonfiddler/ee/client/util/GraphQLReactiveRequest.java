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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.GraphQlClient;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLRequestObject;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.request.QueryField;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

import io.github.demonfiddler.ee.client.Mutation;
// Utility classes are generated in the util subpackage. We need to import the ${object.classSimpleName} from the 'main' package
import io.github.demonfiddler.ee.client.Query;
import reactor.core.publisher.Mono;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
public class GraphQLReactiveRequest extends ObjectResponse {

	/** Logger for this class */
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(GraphQLRequest.class);

	final GraphqlUtils graphqlUtils = new GraphqlUtils();
	final GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();

	// This initialization must occur before the execution of the constructors, in order to properly parse the GraphQL
	// request
	static {
		CustomScalarRegistryInitializer.initCustomScalarRegistry();
		DirectiveRegistryInitializer.initDirectiveRegistry();
	}

	/**
	 * Creates the GraphQL request, in reactive mode, for a full request. It will:
	 * <UL>
	 * <LI>Read the query and/or the mutation</LI>
	 * <LI>Read all fragment definitions</LI>
	 * <LI>For all non scalar field, subfields (and so on recursively), if they are empty (that is the query doesn't
	 * define the requested fields of a non scalar field, then all its scalar fields are added)</LI>
	 * <LI>Add the introspection __typename field to all scalar field list, if it doesnt't already exist. This is
	 * necessary to allow proper deserialization of interfaces and unions.</LI>
	 * </UL>
	 * <BR/>
	 * This method will 'guess' the {@link GraphQlClient} to use, in order to execute the client. This means that,
	 * <u>when using a GraphQL client that can attack several servers, you must use the relevant GraphQLRequest
	 * class</u>, that is: the one generated in the relevant folder, along with the other classes for this schema.
	 * @param schema value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only
	 * one schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param graphQLRequest The GraphQL request, in text format, as defined in the GraphQL specifications, and as it
	 * can be used in GraphiQL. Please read the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc page</A> for
	 * more information, including hints and limitations.
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest(String graphQLRequest) throws GraphQLRequestPreparationException {
		super(null, "", graphQLRequest); //$NON-NLS-1$
	}

	/**
	 * Create the instance for the given GraphQL request, in reactive mode, for a partial request or a full
	 * request.<BR/>
	 * <B><U>Important note:</U></B> this constructor <B>SHOULD NOT</B> be used only by the code generated by the
	 * plugin, not by external applications. Its signature may change in the future. To prepare Partial Requests,
	 * application code <B>SHOULD</B> call the getXxxxGraphQLRequests methods, that are generated in the
	 * query/mutation/subscription java classes.
	 * @param graphQlClient The {@link GraphQlClient} that is responsible for the actual execution of the request
	 * @param graphQLRequest The <B>partial</B> GraphQL request, in text format. Writing partial request allows use to
	 * execute a query/mutation/subscription, and only define what's expected as a response for this
	 * query/mutation/subscription. You can send the parameters for this query/mutation/subscription as parameter of the
	 * java method, without dealing with bind variable in the GraphQL query. Please read the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc page</A> for
	 * more information, including hints and limitations.
	 * @param requestType The information whether this queryName is actually a query, a mutation or a subscription
	 * @param fieldName The name of the query, mutation or subscription, for instance "createHuman", in the GraphQL
	 * request "mutation {createHuman (...) { ...}}".
	 * @param inputParams The list of input parameters for this query/mutation/subscription
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLReactiveRequest(GraphQlClient graphQlClient, String graphQLRequest, RequestType requestType,
		String fieldName, InputParameter... inputParams) throws GraphQLRequestPreparationException {
		super(graphQlClient, "", graphQLRequest, requestType, fieldName, inputParams); //$NON-NLS-1$
	}

	/**
	 * This method executes the current GraphQL request as a full query request. It offers a logging of the call (if in
	 * debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * Here is a sample (and please have a look to the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">graphql-java-generator
	 * website</A> for more information):
	 * 
	 * <PRE>
	 * GraphQLRequest request;
	 * 
	 * public void setup() {
	 *   GraphQLRequest.setStaticConfiguration(...);
	 *   // Preparation of the query
	 *   request = myQueryType.getResponseBuilder()
	 * 			.withQueryResponseDef("query{hero(param:?heroParam) @include(if:true) {id name @skip(if: ?skip) appearsIn friends {id name}}}").build();
	 * }
	 * 
	 * public void doTheJob() {
	 *   ..
	 *   Map<String, Object> params = new HashMap<>();
	 *   params.put("heroParam", heroParamValue);
	 *   params.put("skip", Boolean.FALSE);
	 *   // This will set the value sinceValue to the sinceParam field parameter
	 *   Mono<Query> mono = request.execQuery(params);
	 *   Query response = mono.block();
	 *   ...
	 * }
	 * </PRE>
	 * 
	 * @param parameters The list of values, for the bind variables defined in the query. If there is no bind variable
	 * in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> execQuery(Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		logExecution(RequestType.query, "Query", parameters);
		return execReactive(Query.class, parameters);
	}

	/**
	 * This method executes the current GraphQL request as a full query request. It offers a logging of the call (if in
	 * debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * Here is a sample (and please have a look to the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">graphql-java-generator
	 * website</A> for more information):
	 * 
	 * <PRE>
	 * GraphQLRequest request;
	 * 
	 * public void setup() {
	 *   GraphQLRequest.setStaticConfiguration(...);
	 *   // Preparation of the query
	 *   request = new GraphQLRequest("query{hero(param:?heroParam) @include(if:true) {id name @skip(if: ?skip) appearsIn friends {id name}}}").build();
	 * }
	 * 
	 * public void doTheJob() {
	 *   ..
	 *   // This will set the value sinceValue to the sinceParam field parameter
	 *   Mono<Query> mono = request.execQuery("heroParam", heroParamValue, "skip", Boolean.FALSE);
	 *   Query response = mono.block();
	 *   ...
	 * }
	 * </PRE>
	 * 
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Query> execQuery(Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return execReactive(Query.class, this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method executes the current GraphQL request as a full mutation request. It offers a logging of the call (if
	 * in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * Here is a sample (and please have a look to the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">graphql-java-generator
	 * website</A> for more information):
	 * 
	 * <PRE>
	 * GraphQLRequest request;
	 * 
	 * public void setup() {
	 *   GraphQLRequest.setStaticConfiguration(...);
	 *   // Preparation of the query
	 *   request = myQueryType.getResponseBuilder()
	 *   		.withQueryResponseDef("mutation{hero(param:?heroParam) @include(if:true) {id name @skip(if: ?skip) appearsIn friends {id name}}}").build();
	 * }
	 * 
	 * public void doTheJob() {
	 *   ..
	 *   Map<String, Object> params = new HashMap<>();
	 *   params.put("heroParam", heroParamValue);
	 *   params.put("skip", Boolean.FALSE);
	 *   // This will set the value sinceValue to the sinceParam field parameter
	 *   Mono<Mutation> mono = request.execMutation(params);
	 *   Mutation response = mono.block();
	 * ...
	 * }
	 * </PRE>
	 * 
	 * @param parameters The list of values, for the bind variables defined in the query. If there is no bind variable
	 * in the defined Query, this argument may be null or an empty {@link Map}
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Mutation> execMutation(Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		logExecution(RequestType.mutation, "Mutation", parameters);
		return execReactive(Mutation.class, parameters);
	}

	/**
	 * This method executes the current GraphQL request as a full mutation request. It offers a logging of the call (if
	 * in debug mode), or of the call and its parameters (if in trace mode).<BR/>
	 * Here is a sample (and please have a look to the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">graphql-java-generator
	 * website</A> for more information):
	 * 
	 * <PRE>
	 * GraphQLRequest request;
	 * 
	 * public void setup() {
	 *   GraphQLRequest.setStaticConfiguration(...);
	 *   // Preparation of the query
	 *   request = new GraphQLRequest("mutation{hero(param:?heroParam) @include(if:true) {id name @skip(if: ?skip) appearsIn friends {id name}}}").build();
	 * }
	 * 
	 * public void doTheJob() {
	 *   ..
	 *   // This will set the value sinceValue to the sinceParam field parameter
	 *   Mono<Mutation> mono = request.execMutation("heroParam", heroParamValue, "skip", Boolean.FALSE);
	 *   Mutation response = mono.block();
	 *   ...
	 * }
	 * </PRE>
	 * 
	 * @param paramsAndValues This parameter contains all the name and values for the Bind Variables defined in the
	 * objectResponse parameter, that must be sent to the server. Optional parameter may not have a value. They will be
	 * ignored and not sent to the server. Mandatory parameter must be provided in this argument.<BR/>
	 * This parameter contains an even number of parameters: it must be a series of name and values : (paramName1,
	 * paramValue1, paramName2, paramValue2...)
	 * @throws GraphQLRequestExecutionException When an error occurs during the request execution, typically a network
	 * error, an error from the GraphQL server or if the server response can't be parsed
	 */
	public Mono<Mutation> execMutation(Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return execReactive(Mutation.class, this.graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
	}

	/**
	 * This method returns the package name, where the GraphQL generated classes are. It's used to load the class
	 * definition, and get the GraphQL metadata coming from the GraphQL schema.
	 * @return
	 */
	@Override
	protected String getGraphQLClassesPackageName() {
		return "io.github.demonfiddler.ee.client"; //$NON-NLS-1$
	}

	@Override
	public QueryField getQueryContext() throws GraphQLRequestPreparationException {
		return new QueryField(QueryRootResponse.class, "query"); //$NON-NLS-1$
	}

	@Override
	public QueryField getMutationContext() throws GraphQLRequestPreparationException {
		return new QueryField(MutationRootResponse.class, "mutation"); //$NON-NLS-1$
	}

	@Override
	public QueryField getSubscriptionContext() throws GraphQLRequestPreparationException {
		// No subscription in this GraphQL schema
		return null;
	}

	@Override
	public Class<? extends GraphQLRequestObject> getSubscriptionClass() {
		return null;
	}
}
