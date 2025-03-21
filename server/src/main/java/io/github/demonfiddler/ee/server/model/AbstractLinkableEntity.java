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

import com.graphql_java_generator.annotation.GraphQLNonScalar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Abstract base implementation for all {@code ILinkableEntity} classes.
 */
@Entity
@Table(name = "entity")
public abstract class AbstractLinkableEntity extends AbstractTrackedEntity implements ILinkableEntity {

	/**
	 * The entities 'linked-from' this entity.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "fromEntityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class, listDepth = 0)
	EntityLinkPage fromEntityLinks;

	/**
	 * The entities 'linked-to' this entity.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "toEntityLinks", graphQLTypeSimpleName = "EntityLinkPage",
		javaClass = EntityLinkPage.class, listDepth = 0)
	EntityLinkPage toEntityLinks;

	public EntityLinkPage getFromEntityLinks() {
		return fromEntityLinks;
	}

	public void setFromEntityLinks(EntityLinkPage fromEntityLinks) {
		this.fromEntityLinks = fromEntityLinks;
	}

	public EntityLinkPage getToEntityLinks() {
		return toEntityLinks;
	}

	public void setToEntityLinks(EntityLinkPage toEntityLinks) {
		this.toEntityLinks = toEntityLinks;
	}

	/**
	 * The Builder that helps building instances of this POJO.
	 */
	@SuppressWarnings("unchecked")
	abstract static class Builder<B extends Builder<B, T>, T extends ILinkableEntity>
		extends AbstractTrackedEntity.Builder<B, T> {

		private EntityLinkPage fromEntityLinks;
		private EntityLinkPage toEntityLinks;

		/**
		 * The entities linked from the receiver.
		 */
		public final B withFromEntityLinks(EntityLinkPage fromEntityLinksParam) {
			this.fromEntityLinks = fromEntityLinksParam;
			return (B)this;
		}

		/**
		 * The entities linked to the receiver.
		 */
		public final B withToEntityLinks(EntityLinkPage toEntityLinksParam) {
			this.fromEntityLinks = toEntityLinksParam;
			return (B)this;
		}

		@Override
		T build(T t) {
			super.build(t);
			t.setFromEntityLinks(this.fromEntityLinks);
			t.setToEntityLinks(this.toEntityLinks);
			return t;
		}

	}

}
