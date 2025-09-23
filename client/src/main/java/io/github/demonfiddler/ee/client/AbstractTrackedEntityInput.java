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

public abstract class AbstractTrackedEntityInput extends AbstractBaseEntityInput {

	// Commented out, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	// /**
	//  * A five-star rating for the entity, interpretation depends on entity kind.
	//  */
	// @JsonProperty("rating")
	// @GraphQLScalar( fieldName = "rating", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	// Integer rating;

	// Commented out, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	// /**
	//   * A five-star rating for the link, indicative of significancce, credibility, evidence base, etc.
 	//  */
	// @JsonProperty("rating")
	// public void setRating(java.lang.Integer rating) {
	// 	this.rating = rating;
	// }

	// Commented out, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	// /**
	//  * A five-star rating for the link, indicative of significancce, credibility, evidence base, etc.
	//  */
	// @JsonProperty("rating")
	// public Integer getRating() {
	// 	return this.rating;
	// }
		
	// Abstract, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	public abstract void setRating(Integer rating);
	public abstract Integer getRating();

    /**
     * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
     * {@link #builder()}
     */
    @SuppressWarnings("unchecked")
    abstract static class Builder<B extends Builder<B, I>, I extends AbstractTrackedEntityInput>
        extends AbstractBaseEntityInput.Builder<B, I> {

        private Integer rating;

        /**
         * The entity identifier, required if updating an existing record.
         */
        public final B withRating(Integer ratingParam) {
            this.rating = ratingParam;
            return (B)this;
        }

        I build(I input) {
            super.build(input);
            input.setRating(rating);
            return input;
        }

        public abstract I build();

    }
}
