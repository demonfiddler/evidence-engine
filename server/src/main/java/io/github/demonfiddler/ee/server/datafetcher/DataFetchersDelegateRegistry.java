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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * This registry logs each DataFetchersDelegate spring component. It's used by POJO, when the handle data fetchers
 * members. For instance with this GraphQL type:
 * 
 * <PRE>
 * type Member {
 *     name(uppercase: Boolean): String
 * }
 * </PRE>
 * 
 * A <code>name(String)</code> data fetcher method is created in the generated Membe POJO. It needs to call the
 * <code>name(String)</code> of the Member DataFetchersDelegate.<br/>
 * Since version 2.5
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Component
public class DataFetchersDelegateRegistry {

	// This singleton is used by POJO to access to the
	// DataFetchersDelegateMyQueryType created by Spring
	public static DataFetchersDelegateRegistry dataFetchersDelegateRegistry;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	DataFetchersDelegateClaim dataFetchersDelegateClaim;
	@Autowired
	DataFetchersDelegateClaimPage dataFetchersDelegateClaimPage;
	@Autowired
	DataFetchersDelegateDeclaration dataFetchersDelegateDeclaration;
	@Autowired
	DataFetchersDelegateDeclarationPage dataFetchersDelegateDeclarationPage;
	@Autowired
	DataFetchersDelegateJournal dataFetchersDelegateJournal;
	@Autowired
	DataFetchersDelegateJournalPage dataFetchersDelegateJournalPage;
	@Autowired
	DataFetchersDelegateLog dataFetchersDelegateLog;
	@Autowired
	DataFetchersDelegateLogPage dataFetchersDelegateLogPage;
	@Autowired
	DataFetchersDelegatePerson dataFetchersDelegatePerson;
	@Autowired
	DataFetchersDelegatePersonPage dataFetchersDelegatePersonPage;
	@Autowired
	DataFetchersDelegatePublication dataFetchersDelegatePublication;
	@Autowired
	DataFetchersDelegatePublicationPage dataFetchersDelegatePublicationPage;
	@Autowired
	DataFetchersDelegatePublisher dataFetchersDelegatePublisher;
	@Autowired
	DataFetchersDelegatePublisherPage dataFetchersDelegatePublisherPage;
	@Autowired
	DataFetchersDelegateQuotation dataFetchersDelegateQuotation;
	@Autowired
	DataFetchersDelegateQuotationPage dataFetchersDelegateQuotationPage;
	@Autowired
	DataFetchersDelegateTopic dataFetchersDelegateTopic;
	@Autowired
	DataFetchersDelegateTopicPage dataFetchersDelegateTopicPage;
	@Autowired
	DataFetchersDelegateTopicRef dataFetchersDelegateTopicRef;
	@Autowired
	DataFetchersDelegateTopicRefPage dataFetchersDelegateTopicRefPage;
	@Autowired
	DataFetchersDelegateUser dataFetchersDelegateUser;
	@Autowired
	DataFetchersDelegateUserPage dataFetchersDelegateUserPage;
	@Autowired
	DataFetchersDelegateQuery dataFetchersDelegateQuery;
	@Autowired
	DataFetchersDelegateMutation dataFetchersDelegateMutation;
	@Autowired
	DataFetchersDelegateITrackedEntity dataFetchersDelegateITrackedEntity;
	@Autowired
	DataFetchersDelegateITopicalEntity dataFetchersDelegateITopicalEntity;
	@Autowired
	DataFetchersDelegateIBaseEntity dataFetchersDelegateIBaseEntity;

	public DataFetchersDelegateRegistry() {
		dataFetchersDelegateRegistry = this;
	}

	public DataFetchersDelegateClaim getDataFetchersDelegateClaim() {
		return this.dataFetchersDelegateClaim;
	}

	public DataFetchersDelegateClaimPage getDataFetchersDelegateClaimPage() {
		return this.dataFetchersDelegateClaimPage;
	}

	public DataFetchersDelegateDeclaration getDataFetchersDelegateDeclaration() {
		return this.dataFetchersDelegateDeclaration;
	}

	public DataFetchersDelegateDeclarationPage getDataFetchersDelegateDeclarationPage() {
		return this.dataFetchersDelegateDeclarationPage;
	}

	public DataFetchersDelegateJournal getDataFetchersDelegateJournal() {
		return this.dataFetchersDelegateJournal;
	}

	public DataFetchersDelegateJournalPage getDataFetchersDelegateJournalPage() {
		return this.dataFetchersDelegateJournalPage;
	}

	public DataFetchersDelegateLog getDataFetchersDelegateLog() {
		return this.dataFetchersDelegateLog;
	}

	public DataFetchersDelegateLogPage getDataFetchersDelegateLogPage() {
		return this.dataFetchersDelegateLogPage;
	}

	public DataFetchersDelegatePerson getDataFetchersDelegatePerson() {
		return this.dataFetchersDelegatePerson;
	}

	public DataFetchersDelegatePersonPage getDataFetchersDelegatePersonPage() {
		return this.dataFetchersDelegatePersonPage;
	}

	public DataFetchersDelegatePublication getDataFetchersDelegatePublication() {
		return this.dataFetchersDelegatePublication;
	}

	public DataFetchersDelegatePublicationPage getDataFetchersDelegatePublicationPage() {
		return this.dataFetchersDelegatePublicationPage;
	}

	public DataFetchersDelegatePublisher getDataFetchersDelegatePublisher() {
		return this.dataFetchersDelegatePublisher;
	}

	public DataFetchersDelegatePublisherPage getDataFetchersDelegatePublisherPage() {
		return this.dataFetchersDelegatePublisherPage;
	}

	public DataFetchersDelegateQuotation getDataFetchersDelegateQuotation() {
		return this.dataFetchersDelegateQuotation;
	}

	public DataFetchersDelegateQuotationPage getDataFetchersDelegateQuotationPage() {
		return this.dataFetchersDelegateQuotationPage;
	}

	public DataFetchersDelegateTopic getDataFetchersDelegateTopic() {
		return this.dataFetchersDelegateTopic;
	}

	public DataFetchersDelegateTopicPage getDataFetchersDelegateTopicPage() {
		return this.dataFetchersDelegateTopicPage;
	}

	public DataFetchersDelegateTopicRef getDataFetchersDelegateTopicRef() {
		return this.dataFetchersDelegateTopicRef;
	}

	public DataFetchersDelegateTopicRefPage getDataFetchersDelegateTopicRefPage() {
		return this.dataFetchersDelegateTopicRefPage;
	}

	public DataFetchersDelegateUser getDataFetchersDelegateUser() {
		return this.dataFetchersDelegateUser;
	}

	public DataFetchersDelegateUserPage getDataFetchersDelegateUserPage() {
		return this.dataFetchersDelegateUserPage;
	}

	public DataFetchersDelegateQuery getDataFetchersDelegateQuery() {
		return this.dataFetchersDelegateQuery;
	}

	public DataFetchersDelegateMutation getDataFetchersDelegateMutation() {
		return this.dataFetchersDelegateMutation;
	}

	public DataFetchersDelegateITrackedEntity getDataFetchersDelegateITrackedEntity() {
		return this.dataFetchersDelegateITrackedEntity;
	}

	public DataFetchersDelegateITopicalEntity getDataFetchersDelegateITopicalEntity() {
		return this.dataFetchersDelegateITopicalEntity;
	}

	public DataFetchersDelegateIBaseEntity getDataFetchersDelegateIBaseEntity() {
		return this.dataFetchersDelegateIBaseEntity;
	}

}
