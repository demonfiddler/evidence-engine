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

package io.github.demonfiddler.ee.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;

/**
 * An abstract base class for {@code ILinkableEntity} implementations.
 */
public abstract class AbstractLinkableEntity extends AbstractTrackedEntity implements ILinkableEntity {

    /**
     * Outbound links for which the receiver is the 'linked-from' entity
     */
    @JsonProperty("fromEntityLinks")
    @GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "LinkableEntityQueryFilter", "PageableInput" },
        mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
    @GraphQLNonScalar(fieldName = "fromEntityLinks", graphQLTypeSimpleName = "EntityLinkPage",
        javaClass = EntityLinkPage.class, listDepth = 0)
    EntityLinkPage fromEntityLinks;

    /**
     * Inbound links for which the receiver is the 'linked-to' entity
     */
    @JsonProperty("toEntityLinks")
    @GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "LinkableEntityQueryFilter", "PageableInput" },
        mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
    @GraphQLNonScalar(fieldName = "toEntityLinks", graphQLTypeSimpleName = "EntityLinkPage",
        javaClass = EntityLinkPage.class, listDepth = 0)
    EntityLinkPage toEntityLinks;

    /**
     * Outbound links for which the receiver is the 'linked-from' entity
     */
    @Override
    @JsonIgnore
    public final void setFromEntityLinks(EntityLinkPage fromEntityLinks) {
        this.fromEntityLinks = fromEntityLinks;
    }

    /**
     * Outbound links for which the receiver is the 'linked-from' entity
     */
    @Override
    @JsonIgnore
    public final EntityLinkPage getFromEntityLinks() {
        return this.fromEntityLinks;
    }

    /**
     * Inbound links for which the receiver is the 'linked-to' entity
     */
    @Override
    @JsonIgnore
    public final void setToEntityLinks(EntityLinkPage toEntityLinks) {
        this.toEntityLinks = toEntityLinks;
    }

    /**
     * Inbound links for which the receiver is the 'linked-to' entity
     */
    @Override
    @JsonIgnore
    public final EntityLinkPage getToEntityLinks() {
        return this.toEntityLinks;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fromEntityLinks == null) ? 0 : fromEntityLinks.hashCode());
        result = prime * result + ((toEntityLinks == null) ? 0 : toEntityLinks.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        AbstractLinkableEntity other = (AbstractLinkableEntity)obj;
        if (fromEntityLinks == null) {
            if (other.fromEntityLinks != null)
                return false;
        } else if (!fromEntityLinks.equals(other.fromEntityLinks))
            return false;
        if (toEntityLinks == null) {
            if (other.toEntityLinks != null)
                return false;
        } else if (!toEntityLinks.equals(other.toEntityLinks))
            return false;
        return true;
    }

    /**
     * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
     * {@link #builder()}
     */
    @SuppressWarnings("unchecked")
    static abstract class Builder<B extends Builder<B, T>, T extends ILinkableEntity>
        extends AbstractTrackedEntity.Builder<B, T> {

        private EntityLinkPage fromEntityLinks;
        private EntityLinkPage toEntityLinks;

        /**
         * Outbound links for which the receiver is the 'linked-from' entity
         */
        public final B withFromEntityLinks(EntityLinkPage fromEntityLinksParam) {
            this.fromEntityLinks = fromEntityLinksParam;
            return (B)this;
        }

        /**
         * Inbound links for which the receiver is the 'linked-to' entity
         */
        public final B withToEntityLinks(EntityLinkPage toEntityLinksParam) {
            this.toEntityLinks = toEntityLinksParam;
            return (B)this;
        }

        T build(T _object) {
            super.build(_object);
            _object.setFromEntityLinks(this.fromEntityLinks);
            _object.setToEntityLinks(this.toEntityLinks);
            return _object;
        }

    }

}
