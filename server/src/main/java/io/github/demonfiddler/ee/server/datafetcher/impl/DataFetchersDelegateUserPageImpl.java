package io.github.demonfiddler.ee.server.datafetcher.impl;

import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateUserPage;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.model.UserPage;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@Component
public class DataFetchersDelegateUserPageImpl implements DataFetchersDelegateUserPage {

    @Resource
    private EntityUtils entityUtils;

    @Override
    public Flux<User> content(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
            List<UserPage> keys) {

        return entityUtils.getListValues(keys, UserPage::getContent);
    }

}
