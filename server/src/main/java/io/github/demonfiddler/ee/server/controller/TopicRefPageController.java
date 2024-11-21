/** Generated by the default template from graphql-java-generator */

package io.github.demonfiddler.ee.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Controller
@SchemaMapping(typeName = "TopicRefPage")
@SuppressWarnings("unused")
public class TopicRefPageController {

	@Autowired
	protected DataFetchersDelegateTopicRefPage dataFetchersDelegateTopicRefPage;

	@Autowired
	protected GraphqlServerUtils graphqlServerUtils;

	public TopicRefPageController(BatchLoaderRegistry registry) {
		// Registering the data loaders is useless if the @BatchMapping is used. But we need it here, for backward
		// compatibility with code developed against the previous plugin versions
		registry.forTypePair(Long.class, TopicRefPage.class).registerMappedBatchLoader((keysSet, env) -> {
			List<Long> keys = new ArrayList<>(keysSet.size());
			keys.addAll(keysSet);
			return Mono.fromCallable(() -> {
				Map<Long, TopicRefPage> map = new HashMap<>();
				// Values are returned in the same order as the keys list
				List<TopicRefPage> values = this.dataFetchersDelegateTopicRefPage.batchLoader(keys, env);
				for (int i = 0; i < keys.size(); i++) {
					map.put(keys.get(i), values.get(i));
				}
				return map;
			});
		});
	}

	/**
	 * This methods loads the data for ${dataFetcher.graphQLType}.content. It is generated as the
	 * <code>generateBatchMappingDataFetchers</code> plugin parameter is true. <br/>
	 * @param batchLoaderEnvironment The environement for this batch loaded. You can extract the GraphQLContext from
	 * this parameter.
	 * @param graphQLContext
	 * @param keys The objects for which the value for the content field must be retrieved.
	 * @return This method returns <code>${dataFetcher.batchMappingReturnType.value}</code>, as defined by the
	 * <code>batchMappingDataFetcherReturnType</code> plugin parameter. <br/>
	 * Please look at the spring-graphql annotation for a documentation on how to return the proper values
	 */
	@BatchMapping(field = "content")
	public Flux<TopicRef> content(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
		List<TopicRefPage> keys) {

		return this.dataFetchersDelegateTopicRefPage.content(batchLoaderEnvironment, graphQLContext, keys);
	}

}