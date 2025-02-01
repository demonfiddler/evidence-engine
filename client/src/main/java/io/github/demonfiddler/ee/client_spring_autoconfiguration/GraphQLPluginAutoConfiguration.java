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

package io.github.demonfiddler.ee.client_spring_autoconfiguration;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This Spring {@link AutoConfiguration} class defines the default Spring Beans for this GraphQL schema.
 * 
 * @author etienne-sf
 */
@AutoConfiguration
public class GraphQLPluginAutoConfiguration {

	private static Logger logger = LoggerFactory.getLogger(GraphQLPluginAutoConfiguration.class);

	// Creating this bean makes sure that its static field is set. This is mandatory for some part of the code that must
	// be kept, to allow compliance with existing projects.
	@Autowired
	SpringContextBean springContextBean;

	@Value(value = "${graphql.endpoint.url}")
	private String graphqlEndpointUrl;

	@Autowired
	ApplicationContext applicationContext;
	
	final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * This beans defines the GraphQL endpoint for the current GraphQL schema, as a {@link String}. The <I>application.properties</I> 
	 * must define the GraphQL URL endpoint in the <I>graphql.endpoint.url</I> property.
	 * 
	 * 
	 * @return Returns the value of the <I>graphql.endpoint.url</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	@ConditionalOnMissingBean(name = "graphqlEndpoint")
	String graphqlEndpoint() {
		return graphqlEndpointUrl;
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.<BR/>
	 * This bean is only created if no such bean already exists
	 */
	@Bean
	@ConditionalOnMissingBean(name = "webClient")
	public WebClient webClient(String graphqlEndpoint) {
		logger.debug("Creating default webClient (from the GraphQLSpringAutoConfiguration class) for graphqlEndpoint [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		return WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint))
				.build();
	}

	@Bean
	@ConditionalOnMissingBean(name = "httpGraphQlClient")
	GraphQlClient httpGraphQlClient() {
		logger.debug("Creating default httpGraphQlClient (from the GraphQLSpringAutoConfiguration class) [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		// The usual way to autowire other beans is to define them as parameters of the bean definition methods. But this doesn't
		// seem to work when several beans of the same type exist, and one is defined as "@Primary". 
		// So we retrieve "manually" the needed bean from its name:
		WebClient webClient = (WebClient) applicationContext.getBean("webClient");
		return HttpGraphQlClient.builder(webClient).build();
	}

 	@Bean
	@ConditionalOnMissingBean(name = "webSocketGraphQlClient")
	GraphQlClient webSocketGraphQlClient() {
		logger.debug("Creating default webSocketGraphQlClient (from the GraphQLSpringAutoConfiguration class) [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		WebSocketClient client = new ReactorNettyWebSocketClient();
		return WebSocketGraphQlClient.builder(graphqlEndpointUrl, client).build();
	}

	@Bean
	@ConditionalOnMissingBean(name = "graphqlClientUtils")
	GraphqlClientUtils graphqlClientUtils() {
		return GraphqlClientUtils.graphqlClientUtils;
	}

	@Bean
	@ConditionalOnMissingBean(name = "graphqlUtils")
	GraphqlUtils graphqlUtils() {
		return GraphqlUtils.graphqlUtils;
	}

}
