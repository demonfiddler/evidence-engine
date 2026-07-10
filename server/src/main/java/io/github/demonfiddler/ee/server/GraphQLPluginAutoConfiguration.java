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

package io.github.demonfiddler.ee.server;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.BatchLoaderRegistry;

import com.graphql_java_generator.server.util.GraphqlServerUtils;

import io.github.demonfiddler.ee.server.controller.AuthPayloadController;
import io.github.demonfiddler.ee.server.controller.ClaimController;
import io.github.demonfiddler.ee.server.controller.ClaimPageController;
import io.github.demonfiddler.ee.server.controller.CommentController;
import io.github.demonfiddler.ee.server.controller.CommentPageController;
import io.github.demonfiddler.ee.server.controller.DeclarationController;
import io.github.demonfiddler.ee.server.controller.DeclarationPageController;
import io.github.demonfiddler.ee.server.controller.EntityAuditController;
import io.github.demonfiddler.ee.server.controller.EntityLinkController;
import io.github.demonfiddler.ee.server.controller.EntityLinkPageController;
import io.github.demonfiddler.ee.server.controller.EntityStatisticsController;
import io.github.demonfiddler.ee.server.controller.FieldAuditController;
import io.github.demonfiddler.ee.server.controller.FieldGroupAuditEntryController;
import io.github.demonfiddler.ee.server.controller.GroupController;
import io.github.demonfiddler.ee.server.controller.GroupPageController;
import io.github.demonfiddler.ee.server.controller.IBaseEntityController;
import io.github.demonfiddler.ee.server.controller.ILinkableEntityController;
import io.github.demonfiddler.ee.server.controller.IPageController;
import io.github.demonfiddler.ee.server.controller.ITrackedEntityController;
import io.github.demonfiddler.ee.server.controller.JournalController;
import io.github.demonfiddler.ee.server.controller.JournalPageController;
import io.github.demonfiddler.ee.server.controller.LinkAuditController;
import io.github.demonfiddler.ee.server.controller.LinkAuditEntryController;
import io.github.demonfiddler.ee.server.controller.LinkGroupAuditEntryController;
import io.github.demonfiddler.ee.server.controller.LogController;
import io.github.demonfiddler.ee.server.controller.LogPageController;
import io.github.demonfiddler.ee.server.controller.MutationController;
import io.github.demonfiddler.ee.server.controller.PersonController;
import io.github.demonfiddler.ee.server.controller.PersonPageController;
import io.github.demonfiddler.ee.server.controller.PublicationController;
import io.github.demonfiddler.ee.server.controller.PublicationPageController;
import io.github.demonfiddler.ee.server.controller.PublisherController;
import io.github.demonfiddler.ee.server.controller.PublisherPageController;
import io.github.demonfiddler.ee.server.controller.QueryController;
import io.github.demonfiddler.ee.server.controller.QuotationController;
import io.github.demonfiddler.ee.server.controller.QuotationPageController;
import io.github.demonfiddler.ee.server.controller.TopicController;
import io.github.demonfiddler.ee.server.controller.TopicPageController;
import io.github.demonfiddler.ee.server.controller.TopicStatisticsController;
import io.github.demonfiddler.ee.server.controller.UserController;
import io.github.demonfiddler.ee.server.controller.UserPageController;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateAuthPayload;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateClaim;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateClaimPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateComment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateCommentPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateDeclaration;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateDeclarationPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateEntityAudit;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateEntityLink;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateEntityLinkPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateEntityStatistics;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateFieldAudit;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateFieldGroupAuditEntry;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateGroup;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateGroupPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateIBaseEntity;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateILinkableEntity;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateIPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateITrackedEntity;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateJournal;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateJournalPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLinkAudit;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLinkAuditEntry;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLinkGroupAuditEntry;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLog;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLogPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateMutation;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePerson;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePersonPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublication;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublicationPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublisher;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublisherPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuery;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuotation;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuotationPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopic;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopicPage;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopicStatistics;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateUser;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateUserPage;
import io.github.demonfiddler.ee.server.model.AbstractLinkableEntity;
import io.github.demonfiddler.ee.server.model.AbstractTrackedEntity;
import io.github.demonfiddler.ee.server.model.IBaseEntity;

/**
 * This Spring autoconfiguration class is used to declare default beans, that can then be overridden, thanks to the
 * <code>@Primary</code> Spring annotation.
 */
@AutoConfiguration
public class GraphQLPluginAutoConfiguration {

	/**
	 * Default declaration of the Spring controller for the entity <code>Claim</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>ClaimController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "claimController")
	ClaimController claimController(BatchLoaderRegistry registry, DataFetchersDelegateClaim dataFetchersDelegateClaim,
		GraphqlServerUtils graphqlServerUtils) {

		return new ClaimController(registry, dataFetchersDelegateClaim, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>ClaimPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>ClaimPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "claimPageController")
	ClaimPageController claimPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateClaimPage dataFetchersDelegateClaimPage, GraphqlServerUtils graphqlServerUtils) {

		return new ClaimPageController(registry, dataFetchersDelegateClaimPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>Comment</code>. This default spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> spring
	 * annotation.<br/>
	 * The <code>CommentController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "commentController")
	CommentController commentController(BatchLoaderRegistry registry,
		DataFetchersDelegateComment dataFetchersDelegateComment, GraphqlServerUtils graphqlServerUtils) {

		return new CommentController(registry, dataFetchersDelegateComment, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>CommentPage</code>. This default spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> spring
	 * annotation.<br/>
	 * The <code>CommentPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "commentPageController")
	CommentPageController commentPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateCommentPage dataFetchersDelegateCommentPage, GraphqlServerUtils graphqlServerUtils) {

		return new CommentPageController(registry, dataFetchersDelegateCommentPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Declaration</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>DeclarationController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "declarationController")
	DeclarationController declarationController(BatchLoaderRegistry registry,
		DataFetchersDelegateDeclaration dataFetchersDelegateDeclaration, GraphqlServerUtils graphqlServerUtils) {

		return new DeclarationController(registry, dataFetchersDelegateDeclaration, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Declaration</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>DeclarationPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "declarationPageController")
	DeclarationPageController declarationPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateDeclarationPage dataFetchersDelegateDeclarationPage,
		GraphqlServerUtils graphqlServerUtils) {

		return new DeclarationPageController(registry, dataFetchersDelegateDeclarationPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>EntityLink</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>EntityLinkController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "entityLinkController")
	EntityLinkController entityLinkController(BatchLoaderRegistry registry,
		DataFetchersDelegateEntityLink dataFetchersDelegateEntityLink, GraphqlServerUtils graphqlServerUtils) {

		return new EntityLinkController(registry, dataFetchersDelegateEntityLink, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>EntityLinkPage</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>EntityLinkPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "entityLinkPageController")
	EntityLinkPageController entityLinkPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateEntityLinkPage dataFetchersDelegateEntityLinkPage, GraphqlServerUtils graphqlServerUtils) {

		return new EntityLinkPageController(registry, dataFetchersDelegateEntityLinkPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Journal</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>JournalController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "journalController")
	JournalController journalController(BatchLoaderRegistry registry,
		DataFetchersDelegateJournal dataFetchersDelegateJournal, GraphqlServerUtils graphqlServerUtils) {

		return new JournalController(registry, dataFetchersDelegateJournal, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>JournalPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>JournalPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "journalPageController")
	JournalPageController journalPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateJournalPage dataFetchersDelegateJournalPage, GraphqlServerUtils graphqlServerUtils) {

		return new JournalPageController(registry, dataFetchersDelegateJournalPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>EntityAudit</code>. This default spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the @Primary spring annotation.<br/>
	 * The <code>EntityAuditController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "entityAuditController")
	EntityAuditController entityAuditController(BatchLoaderRegistry registry,
		DataFetchersDelegateEntityAudit dataFetchersDelegateEntityAudit, GraphqlServerUtils graphqlServerUtils) {

		return new EntityAuditController(dataFetchersDelegateEntityAudit, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>LinkAudit</code>. This default spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the @Primary spring annotation.<br/>
	 * The <code>LinkAuditController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "linkAuditController")
	LinkAuditController linkAuditController(BatchLoaderRegistry registry,
		DataFetchersDelegateLinkAudit dataFetchersDelegateLinkAudit, GraphqlServerUtils graphqlServerUtils) {

		return new LinkAuditController(dataFetchersDelegateLinkAudit, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>LinkAuditEntry</code>. This default spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the @Primary spring annotation.<br/>
	 * The <code>LinkAuditEntryController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "linkAuditEntryController")
	LinkAuditEntryController linkAuditEntryController(BatchLoaderRegistry registry,
		DataFetchersDelegateLinkAuditEntry dataFetchersDelegateLinkAuditEntry, GraphqlServerUtils graphqlServerUtils) {

		return new LinkAuditEntryController(dataFetchersDelegateLinkAuditEntry, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>LinkGroupAuditEntry</code>. This default spring
	 * can be overridden by declaring a Spring Bean of same type and name, that has the @Primary spring annotation.<br/>
	 * The <code>LinkGroupAuditEntryController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "linkGroupAuditEntryController")
	LinkGroupAuditEntryController linkGroupAuditEntryController(BatchLoaderRegistry registry,
		DataFetchersDelegateLinkGroupAuditEntry dataFetchersDelegateLinkGroupAuditEntry,
		GraphqlServerUtils graphqlServerUtils) {

		return new LinkGroupAuditEntryController(dataFetchersDelegateLinkGroupAuditEntry, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>FieldAudit</code>. This default spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the @Primary spring annotation.<br/>
	 * The <code>FieldAuditController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "fieldAuditController")
	FieldAuditController fieldAuditController(BatchLoaderRegistry registry,
		DataFetchersDelegateFieldAudit dataFetchersDelegateFieldAudit, GraphqlServerUtils graphqlServerUtils) {

		return new FieldAuditController(dataFetchersDelegateFieldAudit, graphqlServerUtils);
	}

	/**
	 * Default declaration of the spring controller for the entity <code>FieldGroupAuditEntry</code>. This default
	 * spring can be overridden by declaring a Spring Bean of same type and name, that has the @Primary spring
	 * annotation.<br/>
	 * The <code>FieldGroupAuditEntryController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "fieldGroupAuditEntryController")
	FieldGroupAuditEntryController fieldGroupAuditEntryController(BatchLoaderRegistry registry,
		DataFetchersDelegateFieldGroupAuditEntry dataFetchersDelegateFieldGroupAuditEntry,
		GraphqlServerUtils graphqlServerUtils) {

		return new FieldGroupAuditEntryController(dataFetchersDelegateFieldGroupAuditEntry, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Log</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>LogController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "logController")
	LogController logController(BatchLoaderRegistry registry, DataFetchersDelegateLog dataFetchersDelegateLog,
		GraphqlServerUtils graphqlServerUtils) {

		return new LogController(registry, dataFetchersDelegateLog, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>LogPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>LogPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "logPageController")
	LogPageController logPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateLogPage dataFetchersDelegateLogPage, GraphqlServerUtils graphqlServerUtils) {

		return new LogPageController(registry, dataFetchersDelegateLogPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Person</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PersonController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "personController")
	PersonController personController(BatchLoaderRegistry registry,
		DataFetchersDelegatePerson dataFetchersDelegatePerson, GraphqlServerUtils graphqlServerUtils) {

		return new PersonController(registry, dataFetchersDelegatePerson, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>PersonPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PersonPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "personPageController")
	PersonPageController personPageController(BatchLoaderRegistry registry,
		DataFetchersDelegatePersonPage dataFetchersDelegatePersonPage, GraphqlServerUtils graphqlServerUtils) {

		return new PersonPageController(registry, dataFetchersDelegatePersonPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Publication</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PublicationController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publicationController")
	PublicationController publicationController(BatchLoaderRegistry registry,
		DataFetchersDelegatePublication dataFetchersDelegatePublication, GraphqlServerUtils graphqlServerUtils) {

		return new PublicationController(registry, dataFetchersDelegatePublication, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>PublicationPage</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PublicationPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publicationPageController")
	PublicationPageController publicationPageController(BatchLoaderRegistry registry,
		DataFetchersDelegatePublicationPage dataFetchersDelegatePublicationPage,
		GraphqlServerUtils graphqlServerUtils) {

		return new PublicationPageController(registry, dataFetchersDelegatePublicationPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Publisher</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PublisherController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publisherController")
	PublisherController publisherController(BatchLoaderRegistry registry,
		DataFetchersDelegatePublisher dataFetchersDelegatePublisher, GraphqlServerUtils graphqlServerUtils) {

		return new PublisherController(registry, dataFetchersDelegatePublisher, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>PublisherPage</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>PublisherPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publisherPageController")
	PublisherPageController publisherPageController(BatchLoaderRegistry registry,
		DataFetchersDelegatePublisherPage dataFetchersDelegatePublisherPage, GraphqlServerUtils graphqlServerUtils) {

		return new PublisherPageController(registry, dataFetchersDelegatePublisherPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Quotation</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>QuotationController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "quotationController")
	QuotationController quotationController(BatchLoaderRegistry registry,
		DataFetchersDelegateQuotation dataFetchersDelegateQuotation, GraphqlServerUtils graphqlServerUtils) {

		return new QuotationController(registry, dataFetchersDelegateQuotation, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>QuotationPage</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>QuotationPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "quotationPageController")
	QuotationPageController quotationPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateQuotationPage dataFetchersDelegateQuotationPage, GraphqlServerUtils graphqlServerUtils) {

		return new QuotationPageController(registry, dataFetchersDelegateQuotationPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>EntityStatistics</code>. This default Spring
	 * can be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>EntityStatisticsController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "entityStatisticsController")
	EntityStatisticsController entityStatisticsController(BatchLoaderRegistry registry,
		DataFetchersDelegateEntityStatistics dataFetchersDelegateEntityStatistics,
		GraphqlServerUtils graphqlServerUtils) {

		return new EntityStatisticsController(dataFetchersDelegateEntityStatistics, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>TopicStatistics</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>TopicStatisticsController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicStatisticsController")
	TopicStatisticsController topicStatisticsController(BatchLoaderRegistry registry,
		DataFetchersDelegateTopicStatistics dataFetchersDelegateTopicStatistics,
		GraphqlServerUtils graphqlServerUtils) {

		return new TopicStatisticsController(dataFetchersDelegateTopicStatistics, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Topic</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>TopicController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicController")
	TopicController topicController(BatchLoaderRegistry registry, DataFetchersDelegateTopic dataFetchersDelegateTopic) {
		return new TopicController(registry, dataFetchersDelegateTopic);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>TopicPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>TopicPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicPageController")
	TopicPageController topicPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateTopicPage dataFetchersDelegateTopicPage, GraphqlServerUtils graphqlServerUtils) {

		return new TopicPageController(registry, dataFetchersDelegateTopicPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>User</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>UserController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "userController")
	UserController userController(BatchLoaderRegistry registry, DataFetchersDelegateUser dataFetchersDelegateUser,
		GraphqlServerUtils graphqlServerUtils) {

		return new UserController(registry, dataFetchersDelegateUser, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>UserPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>UserPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "userPageController")
	UserPageController userPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateUserPage dataFetchersDelegateUserPage, GraphqlServerUtils graphqlServerUtils) {

		return new UserPageController(registry, dataFetchersDelegateUserPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Group</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>GroupController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "groupController")
	GroupController groupController(BatchLoaderRegistry registry, DataFetchersDelegateGroup dataFetchersDelegateGroup,
		GraphqlServerUtils graphqlServerUtils) {

		return new GroupController(registry, dataFetchersDelegateGroup, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>GroupPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>GroupPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "groupPageController")
	GroupPageController groupPageController(BatchLoaderRegistry registry,
		DataFetchersDelegateGroupPage dataFetchersDelegateGroupPage, GraphqlServerUtils graphqlServerUtils) {

		return new GroupPageController(registry, dataFetchersDelegateGroupPage, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>AuthPayload</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>AuthPayloadController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "authPayloadController")
	AuthPayloadController authPayloadController(BatchLoaderRegistry registry,
		DataFetchersDelegateAuthPayload dataFetchersDelegateAuthPayload, GraphqlServerUtils graphqlServerUtils) {

		return new AuthPayloadController(dataFetchersDelegateAuthPayload, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Query</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>QueryController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "queryController")
	QueryController queryController(DataFetchersDelegateQuery dataFetchersDelegateQuery, GraphqlServerUtils graphqlServerUtils) {
		return new QueryController(dataFetchersDelegateQuery, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>Mutation</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>MutationController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "mutationController")
	MutationController mutationController(DataFetchersDelegateMutation dataFetchersDelegateMutation,
		GraphqlServerUtils graphqlServerUtils) {

		return new MutationController(dataFetchersDelegateMutation, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>IBaseEntity</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>IBaseEntityController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iBaseEntityController")
	IBaseEntityController iBaseEntityController(BatchLoaderRegistry registry,
		DataFetchersDelegateIBaseEntity<IBaseEntity> dataFetchersDelegateIBaseEntity,
		GraphqlServerUtils graphqlServerUtils) {

		return new IBaseEntityController(registry, dataFetchersDelegateIBaseEntity, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>ITrackedEntity</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>ITrackedEntityController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iTrackedEntityController")
	ITrackedEntityController iTrackedEntityController(
		DataFetchersDelegateITrackedEntity<AbstractTrackedEntity> dataFetchersDelegateITrackedEntity,
		GraphqlServerUtils graphqlServerUtils) {

		return new ITrackedEntityController(dataFetchersDelegateITrackedEntity, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>ILinkableEntity</code>. This default Spring can
	 * be overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>ILinkableEntityController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iLinkableEntityController")
	ILinkableEntityController iLinkableEntityController(
		DataFetchersDelegateILinkableEntity<AbstractLinkableEntity> dataFetchersDelegateILinkableEntity,
		GraphqlServerUtils graphqlServerUtils) {

		return new ILinkableEntityController(dataFetchersDelegateILinkableEntity, graphqlServerUtils);
	}

	/**
	 * Default declaration of the Spring controller for the entity <code>IPage</code>. This default Spring can be
	 * overridden by declaring a Spring Bean of same type and name, that has the <code>@Primary</code> Spring
	 * annotation.<br/>
	 * The <code>IPageController</code> bean must be a valid bean that can be discovered by the
	 * <code>AnnotatedControllerConfigurer</code> Spring configurer, for this configurer to work. But it must not be
	 * discovered. So it is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iPageController")
	IPageController iPageController(BatchLoaderRegistry registry, DataFetchersDelegateIPage dataFetchersDelegateIPage,
		GraphqlServerUtils graphqlServerUtils) {

		return new IPageController(registry, dataFetchersDelegateIPage, graphqlServerUtils);
	}

}
