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

package io.github.demonfiddler.ee.client.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.EntityKind;
import io.github.demonfiddler.ee.client.IPage;
import io.github.demonfiddler.ee.client.PageableInput;
import io.github.demonfiddler.ee.client.StatusKind;
import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

abstract class AbstractClientRunner implements CommandLineRunner {

    /**
     * Executes a GraphQL query.
     */
    @FunctionalInterface
    static interface QueryFunction<F, P extends IPage> {

        P execute(F filter, PageableInput pageSort)
                throws GraphQLRequestPreparationException, GraphQLRequestExecutionException;

    }

    // N.B. Keep the initial value up to date with the number of AbstractClientRunner subclasses.
    private static final AtomicInteger runnerCount = new AtomicInteger(4);
    protected static final String OPT_DRY_RUN = "dry-run";
    protected static final String OPT_ENDPOINT = "endpoint";
    protected static final String OPT_FORCE = "force";
    protected static final String OPT_LINK = "link";
    protected static final String OPT_LOAD = "load";
    protected static final String OPT_PAGE_SIZE = "page-size";
    protected static final String OPT_PASSWORD = "password";
    protected static final String OPT_PUBLISH = "publish";
    protected static final String OPT_RECURSIVE = "recursive";
    protected static final String OPT_STATUS = "status";
    protected static final String OPT_SPRING_PROFILES = "spring.profiles.active";
    protected static final String OPT_TOPIC_ID = "topic-id";
    protected static final String OPT_UPDATE_PUBLICATIONS = "update-publications";
    protected static final String OPT_USERNAME = "username";
    protected static final Options OPTIONS;

    static {
        OPTIONS = new Options() //
            .addOption("d", OPT_DRY_RUN, false, "Dry run - no database updates")
            .addOption("E", OPT_FORCE, true, "GraphQL endpoint") //
            .addOption("f", OPT_FORCE, false, "Force update") //
            .addOption("h", OPT_PUBLISH, true, "Publish records of the specified types") //
            .addOption("k", OPT_LINK, true, "Link Publications (PUB) / Quotations (QUO) to Topics and author/quotee Persons (PER) to Claims & Topics") //
            .addOption("l", OPT_LOAD, true, "Load data from CSV file") //
            .addOption("p", OPT_PAGE_SIZE, true, "Number of items to read per page (default 100)") //
            .addOption("P", OPT_PASSWORD, true, "The password with which to login") //
            .addOption(null, OPT_SPRING_PROFILES, true, "Active Spring profiles") //
            .addOption("s", OPT_STATUS, true, "Filter on status") //
            .addOption("r", OPT_RECURSIVE, false, "Include records linked to sub-topics") //
            .addOption("t", OPT_TOPIC_ID, true, "Link imported records to specified topic OR filter on topic ID")
            .addOption("u", OPT_UPDATE_PUBLICATIONS, false, "Update Publications with journal.publisher")
            .addOption("U", OPT_USERNAME, true, "Username to login as")
            ;
    }

    private final ConfigurableApplicationContext context;
    final Authenticator authenticator;
    final QueryExecutor queryExecutor;
    final MutationExecutor mutationExecutor;
    CommandLine cmdline;
    boolean dryRun;
    boolean force;
    int pageSize;
    Boolean recursive;
    Long topicId;

    AbstractClientRunner(ConfigurableApplicationContext context, Authenticator authenticator,
        QueryExecutor queryExecutor, MutationExecutor mutationExecutor) {

        this.context = context;
        this.authenticator = authenticator;
        this.queryExecutor = queryExecutor;
        this.mutationExecutor = mutationExecutor;
    }

    @Override
    public final void run(String... args) throws Exception {
        try {
            getLogger().trace("Starting {}, runnerCount={}", getClass().getSimpleName(), runnerCount.get());

            CommandLineParser parser = new DefaultParser();
            this.cmdline = parser.parse(OPTIONS, args);
            this.dryRun = cmdline.hasOption(OPT_DRY_RUN);
            this.force = cmdline.hasOption(OPT_FORCE);
            this.pageSize = cmdline.hasOption(OPT_PAGE_SIZE) ? Integer.valueOf(cmdline.getOptionValue(OPT_PAGE_SIZE)) : 100;
            this.recursive = cmdline.hasOption(OPT_RECURSIVE) ? Boolean.TRUE : null;
            this.topicId = cmdline.hasOption(OPT_TOPIC_ID) ? Long.valueOf(cmdline.getOptionValue(OPT_TOPIC_ID)) : null;

            boolean loggedIn;
            String endpoint = cmdline.getOptionValue(OPT_ENDPOINT);
            String username = cmdline.getOptionValue(OPT_USERNAME);
            String password = cmdline.getOptionValue(OPT_PASSWORD);
            if (endpoint != null || username != null || password != null) {
                if (endpoint == null || username == null || password == null)
                    throw new BadCredentialsException("endpoint, username and password must all be specified");
                loggedIn = authenticator.login(endpoint, username, password);
            } else {
                loggedIn = authenticator.login();
            }
            if (!loggedIn)
                throw new BadCredentialsException("Authentication failed");

            doRun();
        } catch (Exception e) {
            getLogger().error(getClass().getSimpleName() + " threw an exception", e);
        } finally {
            getLogger().trace("Finishing {}, runnerCount={}", getClass().getSimpleName(), runnerCount.get());

            authenticator.logout();

            // If this is the last runner, terminate the process.
            if (runnerCount.decrementAndGet() == 0) {
                SpringApplication.exit(context);
                if (!context.isClosed())
                    context.close();
            }
        }
    }

    abstract Logger getLogger();
    abstract void doRun() throws Exception;

    <P extends IPage, F, T> P readPaged(Class<P> pageClass, F filter, QueryFunction<F, P> query)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        try {
            Method getContent = pageClass.getDeclaredMethod("getContent");
            Method setContent = pageClass.getDeclaredMethod("setContent", List.class);

            PageableInput pageSort = PageableInput.builder().withPageSize(pageSize).build();
            List<T> resultContent = new ArrayList<>();
            P page;
            int pageNum = 0;
            do {
                pageSort.setPageNumber(pageNum++);
                page = query.execute(filter, pageSort);
                @SuppressWarnings("unchecked")
                List<T> content = (List<T>) getContent.invoke(page);
                resultContent.addAll(content);

                getLogger().trace("Read {} #{} with {} items, total {}", pageClass.getSimpleName(), pageNum,
                    content.size(), resultContent.size());
            } while (page.getHasNext());

            P result = pageClass.getConstructor().newInstance();
            result.setIsEmpty(resultContent.isEmpty());
            result.setHasContent(resultContent.size() != 0);
            result.setHasPrevious(false);
            result.setHasNext(false);
            result.setIsFirst(true);
            result.setIsLast(true);
            result.setNumber(0);
            result.setSize(0);
            result.setNumberOfElements(resultContent.size());
            result.setTotalElements((long) resultContent.size());
            result.setTotalPages(1);
            setContent.invoke(result, resultContent);
            return result;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Reflection failed", e);
        }
    }

    List<EntityKind> parseEntityKinds(String option) {
        StringTokenizer tok = new StringTokenizer(cmdline.getOptionValue(option), ",");
        List<EntityKind> entityKinds = new ArrayList<>(tok.countTokens());
        while (tok.hasMoreTokens())
            entityKinds.add(EntityKind.valueOf(tok.nextToken()));
        return entityKinds;
    }

    List<StatusKind> parseStatus() {
        List<StatusKind> status = null;
        if (cmdline.hasOption(OPT_STATUS)) {
            StringTokenizer tok = new StringTokenizer(cmdline.getOptionValue(OPT_STATUS), ",");
            if (tok.hasMoreTokens()) {
                status = new ArrayList<>(tok.countTokens());
                while (tok.hasMoreTokens())
                    status.add(StatusKind.valueOf(tok.nextToken()));
            }
        }
        return status;
    }

}
