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

package io.github.demonfiddler.ee.server.model;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

/**
 * A node in the topic hierarchy tree.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Entity
@GraphQLObjectType("Topic")
public class Topic implements IBaseEntity, ITrackedEntity {

	/**
	 * The unique topic identifier.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The entity status.
	 */
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String status;

	/**
	 * When the record was created.
	 */
	@GraphQLScalar(fieldName = "created", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime created;

	/**
	 * The user who created the record.
	 */
	@GraphQLNonScalar(fieldName = "createdByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_user_id", nullable = false)
	User createdByUser;

	/**
	 * When the record was last updated.
	 */
	@GraphQLScalar(fieldName = "updated", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime updated;

	/**
	 * The user who last updated the record.
	 */
	@GraphQLNonScalar(fieldName = "updatedByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "updated_by_user_id", nullable = true)
	User updatedByUser;

	/**
	 * Log of transactions involving the record.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class, listDepth = 0)
	LogPage log;

	/**
	 * The topic label for display in the user interface.
	 */
	@GraphQLScalar(fieldName = "label", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String label;

	/**
	 * The topic description.
	 */
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	/**
	 * The parent topic.
	 */
	@GraphQLNonScalar(fieldName = "parent", graphQLTypeSimpleName = "Topic", javaClass = Topic.class, listDepth = 0)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "parent_id", nullable = true)
	Topic parent;

	/**
	 * The sub-topics.
	 */
	@GraphQLNonScalar(fieldName = "children", graphQLTypeSimpleName = "Topic", javaClass = Topic.class, listDepth = 1)
	@OneToMany(mappedBy = "parent", /*cascade=CascadeType.ALL,*/ fetch = FetchType.LAZY)
	List<Topic> children = Collections.emptyList();

	/**
	 * Referenced entities.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "entities", graphQLTypeSimpleName = "ITopicalEntity",
		javaClass = ITopicalEntity.class, listDepth = 1)
	List<ITopicalEntity> entities;

	/**
	 * The unique topic identifier.
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique topic identifier.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * The entity status.
	 */
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * The entity status.
	 */
	@Override
	public String getStatus() {
		return this.status;
	}

	/**
	 * When the record was created.
	 */
	@Override
	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	/**
	 * When the record was created.
	 */
	@Override
	public OffsetDateTime getCreated() {
		return this.created;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	public User getCreatedByUser() {
		return this.createdByUser;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	public void setUpdated(OffsetDateTime updated) {
		this.updated = updated;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	public OffsetDateTime getUpdated() {
		return this.updated;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	public void setUpdatedByUser(User updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	public User getUpdatedByUser() {
		return this.updatedByUser;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	public void setLog(LogPage log) {
		this.log = log;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	public LogPage getLog() {
		return this.log;
	}

	/**
	 * The topic label for display in the user interface.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * The topic label for display in the user interface.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * The topic description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The topic description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * The parent topic.
	 */
	public void setParent(Topic parent) {
		this.parent = parent;
	}

	/**
	 * The parent topic.
	 */
	public Topic getParent() {
		return this.parent;
	}

	/**
	 * The sub-topics.
	 */
	public void setChildren(List<Topic> children) {
		this.children = children;
	}

	/**
	 * The sub-topics.
	 */
	public List<Topic> getChildren() {
		return this.children;
	}

	/**
	 * Referenced entities.
	 */
	public void setEntities(List<ITopicalEntity> entities) {
		this.entities = entities;
	}

	/**
	 * Referenced entities.
	 */
	public List<ITopicalEntity> getEntities() {
		return this.entities;
	}

	public String toString() {
		return "Topic {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "status: " + this.status //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "created: " + this.created //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "createdByUser: " + this.createdByUser //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "updated: " + this.updated //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "updatedByUser: " + this.updatedByUser //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "log: " + this.log //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "label: " + this.label //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "description: " + this.description //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "parent: " + this.parent //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "children: " + this.children //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entities: " + this.entities //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {

		private Long id;
		private String status;
		private OffsetDateTime created;
		private User createdByUser;
		private OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;
		private String label;
		private String description;
		private Topic parent;
		private List<Topic> children;
		private List<ITopicalEntity> entities;

		/**
		 * The unique topic identifier.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The entity status.
		 */
		public Builder withStatus(String statusParam) {
			this.status = statusParam;
			return this;
		}

		/**
		 * When the record was created.
		 */
		public Builder withCreated(OffsetDateTime createdParam) {
			this.created = createdParam;
			return this;
		}

		/**
		 * The user who created the record.
		 */
		public Builder withCreatedByUser(User createdByUserParam) {
			this.createdByUser = createdByUserParam;
			return this;
		}

		/**
		 * When the record was last updated.
		 */
		public Builder withUpdated(OffsetDateTime updatedParam) {
			this.updated = updatedParam;
			return this;
		}

		/**
		 * The user who last updated the record.
		 */
		public Builder withUpdatedByUser(User updatedByUserParam) {
			this.updatedByUser = updatedByUserParam;
			return this;
		}

		/**
		 * Log of transactions involving the record.
		 */
		public Builder withLog(LogPage logParam) {
			this.log = logParam;
			return this;
		}

		/**
		 * The topic label for display in the user interface.
		 */
		public Builder withLabel(String labelParam) {
			this.label = labelParam;
			return this;
		}

		/**
		 * The topic description.
		 */
		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		/**
		 * The parent topic.
		 */
		public Builder withParent(Topic parentParam) {
			this.parent = parentParam;
			return this;
		}

		/**
		 * The sub-topics.
		 */
		public Builder withChildren(List<Topic> childrenParam) {
			this.children = childrenParam;
			return this;
		}

		/**
		 * Referenced entities.
		 */
		public Builder withEntities(List<ITopicalEntity> entitiesParam) {
			this.entities = entitiesParam;
			return this;
		}

		public Topic build() {
			Topic _object = new Topic();
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setLabel(this.label);
			_object.setDescription(this.description);
			_object.setParent(this.parent);
			_object.setChildren(this.children);
			_object.setEntities(this.entities);
			return _object;
		}

	}

}
