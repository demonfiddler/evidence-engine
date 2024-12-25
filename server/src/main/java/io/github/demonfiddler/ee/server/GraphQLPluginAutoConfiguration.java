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

package io.github.demonfiddler.ee.server;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.BatchLoaderRegistry;

import io.github.demonfiddler.ee.server.controller.ClaimController;
import io.github.demonfiddler.ee.server.controller.ClaimPageController;
import io.github.demonfiddler.ee.server.controller.DeclarationController;
import io.github.demonfiddler.ee.server.controller.DeclarationPageController;
import io.github.demonfiddler.ee.server.controller.IBaseEntityController;
import io.github.demonfiddler.ee.server.controller.ITopicalEntityController;
import io.github.demonfiddler.ee.server.controller.ITrackedEntityController;
import io.github.demonfiddler.ee.server.controller.JournalController;
import io.github.demonfiddler.ee.server.controller.JournalPageController;
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
import io.github.demonfiddler.ee.server.controller.TopicRefController;
import io.github.demonfiddler.ee.server.controller.TopicRefPageController;
import io.github.demonfiddler.ee.server.controller.UserController;
import io.github.demonfiddler.ee.server.controller.UserPageController;

/**
 * This Spring autoconfiguration class is used to declare default beans, that can then be overridden, thanks to the
 * &amp;Primary spring annotation.
 */
@AutoConfiguration
public class GraphQLPluginAutoConfiguration {

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.ClaimController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "claimController")
	ClaimController claimController(BatchLoaderRegistry registry) {
		return new ClaimController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.ClaimPageController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "claimPageController")
	ClaimPageController claimPageController(BatchLoaderRegistry registry) {
		return new ClaimPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.DeclarationController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "declarationController")
	DeclarationController declarationController(BatchLoaderRegistry registry) {
		return new DeclarationController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.DeclarationPageController</code> bean must be a valid bean that
	 * can be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to
	 * work. But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "declarationPageController")
	DeclarationPageController declarationPageController(BatchLoaderRegistry registry) {
		return new DeclarationPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.JournalController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "journalController")
	JournalController journalController(BatchLoaderRegistry registry) {
		return new JournalController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.JournalPageController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "journalPageController")
	JournalPageController journalPageController(BatchLoaderRegistry registry) {
		return new JournalPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.LogController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "logController")
	LogController logController(BatchLoaderRegistry registry) {
		return new LogController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.LogPageController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "logPageController")
	LogPageController logPageController(BatchLoaderRegistry registry) {
		return new LogPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PersonController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "personController")
	PersonController personController(BatchLoaderRegistry registry) {
		return new PersonController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PersonPageController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "personPageController")
	PersonPageController personPageController(BatchLoaderRegistry registry) {
		return new PersonPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PublicationController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publicationController")
	PublicationController publicationController(BatchLoaderRegistry registry) {
		return new PublicationController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PublicationPageController</code> bean must be a valid bean that
	 * can be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to
	 * work. But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publicationPageController")
	PublicationPageController publicationPageController(BatchLoaderRegistry registry) {
		return new PublicationPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PublisherController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publisherController")
	PublisherController publisherController(BatchLoaderRegistry registry) {
		return new PublisherController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.PublisherPageController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "publisherPageController")
	PublisherPageController publisherPageController(BatchLoaderRegistry registry) {
		return new PublisherPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.QuotationController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "quotationController")
	QuotationController quotationController(BatchLoaderRegistry registry) {
		return new QuotationController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.QuotationPageController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "quotationPageController")
	QuotationPageController quotationPageController(BatchLoaderRegistry registry) {
		return new QuotationPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.TopicController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicController")
	TopicController topicController(BatchLoaderRegistry registry) {
		return new TopicController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.TopicPageController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicPageController")
	TopicPageController topicPageController(BatchLoaderRegistry registry) {
		return new TopicPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.TopicRefController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicRefController")
	TopicRefController topicRefController(BatchLoaderRegistry registry) {
		return new TopicRefController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.TopicRefPageController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "topicRefPageController")
	TopicRefPageController topicRefPageController(BatchLoaderRegistry registry) {
		return new TopicRefPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.UserController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "userController")
	UserController userController(BatchLoaderRegistry registry) {
		return new UserController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.UserPageController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "userPageController")
	UserPageController userPageController(BatchLoaderRegistry registry) {
		return new UserPageController(registry);
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.QueryController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "queryController")
	QueryController queryController(BatchLoaderRegistry registry) {
		return new QueryController();
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.MutationController</code> bean must be a valid bean that can be
	 * discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But
	 * it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "mutationController")
	MutationController mutationController(BatchLoaderRegistry registry) {
		return new MutationController();
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.ITrackedEntityController</code> bean must be a valid bean that
	 * can be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to
	 * work. But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iTrackedEntityController")
	ITrackedEntityController iTrackedEntityController(BatchLoaderRegistry registry) {
		return new ITrackedEntityController();
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.ITopicalEntityController</code> bean must be a valid bean that
	 * can be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to
	 * work. But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iTopicalEntityController")
	ITopicalEntityController iTopicalEntityController(BatchLoaderRegistry registry) {
		return new ITopicalEntityController();
	}

	/**
	 * Default declaration of the spring controller for the entity
	 * <code>${dataFetcherDelegate.type.classSimpleName}</code>. This default spring can be overridden by declaring a
	 * Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>io.github.demonfiddler.ee.server.util.IBaseEntityController</code> bean must be a valid bean that can
	 * be discovered by the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work.
	 * But it must not be discovered. So it is excluded in the {@link EvidenceEngineServer} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "iBaseEntityController")
	IBaseEntityController iBaseEntityController(BatchLoaderRegistry registry) {
		return new IBaseEntityController(registry);
	}

}
