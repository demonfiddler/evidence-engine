/** Generated by the default template from graphql-java-generator */

package io.github.demonfiddler.ee.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__EnumValue")
@JsonInclude(Include.NON_NULL)
public class __EnumValue extends AbstractGraphQLEntity {

	public __EnumValue() {
	}

	@JsonProperty("name")
	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String name;

	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	@JsonProperty("isDeprecated")
	@GraphQLScalar(fieldName = "isDeprecated", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean isDeprecated;

	@JsonProperty("deprecationReason")
	@GraphQLScalar(fieldName = "deprecationReason", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String deprecationReason;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("name")
	public String getName() {
		return this.name;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("description")
	public String getDescription() {
		return this.description;
	}

	@JsonProperty("isDeprecated")
	public void setIsDeprecated(Boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	@JsonProperty("isDeprecated")
	public Boolean getIsDeprecated() {
		return this.isDeprecated;
	}

	@JsonProperty("deprecationReason")
	public void setDeprecationReason(String deprecationReason) {
		this.deprecationReason = deprecationReason;
	}

	@JsonProperty("deprecationReason")
	public String getDeprecationReason() {
		return this.deprecationReason;
	}

	public String toString() {
		return "__EnumValue {" //
			+ "name: " + this.name //
			+ ", " //
			+ "description: " + this.description //
			+ ", " //
			+ "isDeprecated: " + this.isDeprecated //
			+ ", " //
			+ "deprecationReason: " + this.deprecationReason //
			+ ", " //
			+ "__typename: " + this.__typename //
			+ "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractGraphQLEntity.Builder<Builder, __EnumValue>  {

		private String name;
		private String description;
		private Boolean isDeprecated;
		private String deprecationReason;

		public Builder withName(String nameParam) {
			this.name = nameParam;
			return this;
		}

		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		public Builder withIsDeprecated(Boolean isDeprecatedParam) {
			this.isDeprecated = isDeprecatedParam;
			return this;
		}

		public Builder withDeprecationReason(String deprecationReasonParam) {
			this.deprecationReason = deprecationReasonParam;
			return this;
		}

		public __EnumValue build() {
			__EnumValue _object = build(new __EnumValue());
			_object.setName(this.name);
			_object.setDescription(this.description);
			_object.setIsDeprecated(this.isDeprecated);
			_object.setDeprecationReason(this.deprecationReason);
			return _object;
		}

		@Override
		String getTypeName() {
			return "__EnumValue";
		}

	}

}
