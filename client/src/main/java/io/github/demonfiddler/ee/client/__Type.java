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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__Type")
@JsonInclude(Include.NON_NULL)
public class __Type extends AbstractGraphQLEntity {

	public __Type() {
	}

	@JsonProperty("kind")
	@GraphQLScalar(fieldName = "kind", graphQLTypeSimpleName = "__TypeKind", javaClass = __TypeKind.class,
		listDepth = 0)
	__TypeKind kind;

	@JsonProperty("name")
	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String name;

	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("fields")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__Field.class)
	@GraphQLInputParameters(names = { "includeDeprecated" }, types = { "Boolean" }, mandatories = { false },
		listDepths = { 0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "fields", graphQLTypeSimpleName = "__Field", javaClass = __Field.class, listDepth = 1)
	List<__Field> fields;

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("interfaces")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__Type.class)
	@GraphQLNonScalar(fieldName = "interfaces", graphQLTypeSimpleName = "__Type", javaClass = __Type.class,
		listDepth = 1)
	List<__Type> interfaces;

	// must be non-null for INTERFACE and UNION, otherwise null.
	@JsonProperty("possibleTypes")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__Type.class)
	@GraphQLNonScalar(fieldName = "possibleTypes", graphQLTypeSimpleName = "__Type", javaClass = __Type.class,
		listDepth = 1)
	List<__Type> possibleTypes;

	// must be non-null for ENUM, otherwise null.
	@JsonProperty("enumValues")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__EnumValue.class)
	@GraphQLInputParameters(names = { "includeDeprecated" }, types = { "Boolean" }, mandatories = { false },
		listDepths = { 0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "enumValues", graphQLTypeSimpleName = "__EnumValue", javaClass = __EnumValue.class,
		listDepth = 1)
	List<__EnumValue> enumValues;

	// must be non-null for INPUT_OBJECT, otherwise null.
	@JsonProperty("inputFields")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__InputValue.class)
	@GraphQLInputParameters(names = { "includeDeprecated" }, types = { "Boolean" }, mandatories = { false },
		listDepths = { 0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "inputFields", graphQLTypeSimpleName = "__InputValue", javaClass = __InputValue.class,
		listDepth = 1)
	List<__InputValue> inputFields;

	// must be non-null for NON_NULL and LIST, otherwise null.
	@JsonProperty("ofType")
	@GraphQLNonScalar(fieldName = "ofType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class, listDepth = 0)
	__Type ofType;

	// may be non-null for custom SCALAR, otherwise null.
	@JsonProperty("specifiedByURL")
	@GraphQLScalar(fieldName = "specifiedByURL", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String specifiedByURL;

	@JsonProperty("kind")
	public void setKind(__TypeKind kind) {
		this.kind = kind;
	}

	@JsonProperty("kind")
	public __TypeKind getKind() {
		return this.kind;
	}

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

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("fields")
	public void setFields(List<__Field> fields) {
		this.fields = fields;
	}

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("fields")
	public List<__Field> getFields() {
		return this.fields;
	}

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("interfaces")
	public void setInterfaces(List<__Type> interfaces) {
		this.interfaces = interfaces;
	}

	// must be non-null for OBJECT and INTERFACE, otherwise null.
	@JsonProperty("interfaces")
	public List<__Type> getInterfaces() {
		return this.interfaces;
	}

	// must be non-null for INTERFACE and UNION, otherwise null.
	@JsonProperty("possibleTypes")
	public void setPossibleTypes(List<__Type> possibleTypes) {
		this.possibleTypes = possibleTypes;
	}

	// must be non-null for INTERFACE and UNION, otherwise null.
	@JsonProperty("possibleTypes")
	public List<__Type> getPossibleTypes() {
		return this.possibleTypes;
	}

	// must be non-null for ENUM, otherwise null.
	@JsonProperty("enumValues")
	public void setEnumValues(List<__EnumValue> enumValues) {
		this.enumValues = enumValues;
	}

	// must be non-null for ENUM, otherwise null.
	@JsonProperty("enumValues")
	public List<__EnumValue> getEnumValues() {
		return this.enumValues;
	}

	// must be non-null for INPUT_OBJECT, otherwise null.
	@JsonProperty("inputFields")
	public void setInputFields(List<__InputValue> inputFields) {
		this.inputFields = inputFields;
	}

	// must be non-null for INPUT_OBJECT, otherwise null.
	@JsonProperty("inputFields")
	public List<__InputValue> getInputFields() {
		return this.inputFields;
	}

	// must be non-null for NON_NULL and LIST, otherwise null.
	@JsonProperty("ofType")
	public void setOfType(__Type ofType) {
		this.ofType = ofType;
	}

	// must be non-null for NON_NULL and LIST, otherwise null.
	@JsonProperty("ofType")
	public __Type getOfType() {
		return this.ofType;
	}

	// may be non-null for custom SCALAR, otherwise null.
	@JsonProperty("specifiedByURL")
	public void setSpecifiedByURL(String specifiedByURL) {
		this.specifiedByURL = specifiedByURL;
	}

	// may be non-null for custom SCALAR, otherwise null.
	@JsonProperty("specifiedByURL")
	public String getSpecifiedByURL() {
		return this.specifiedByURL;
	}

	public String toString() {
		return "__Type {" //
			+ "kind: " + this.kind //
			+ ", " //
			+ "name: " + this.name //
			+ ", " //
			+ "description: " + this.description //
			+ ", " //
			+ "fields: " + this.fields //
			+ ", " //
			+ "interfaces: " + this.interfaces //
			+ ", " //
			+ "possibleTypes: " + this.possibleTypes //
			+ ", " //
			+ "enumValues: " + this.enumValues //
			+ ", " //
			+ "inputFields: " + this.inputFields //
			+ ", " //
			+ "ofType: " + this.ofType //
			+ ", " //
			+ "specifiedByURL: " + this.specifiedByURL //
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
	public static class Builder extends AbstractGraphQLEntity.Builder<Builder, __Type> {

		private __TypeKind kind;
		private String name;
		private String description;
		private List<__Field> fields;
		private List<__Type> interfaces;
		private List<__Type> possibleTypes;
		private List<__EnumValue> enumValues;
		private List<__InputValue> inputFields;
		private __Type ofType;
		private String specifiedByURL;

		public Builder withKind(__TypeKind kindParam) {
			this.kind = kindParam;
			return this;
		}

		public Builder withName(String nameParam) {
			this.name = nameParam;
			return this;
		}

		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		// must be non-null for OBJECT and INTERFACE, otherwise null.
		public Builder withFields(List<__Field> fieldsParam) {
			this.fields = fieldsParam;
			return this;
		}

		// must be non-null for OBJECT and INTERFACE, otherwise null.
		public Builder withInterfaces(List<__Type> interfacesParam) {
			this.interfaces = interfacesParam;
			return this;
		}

		// must be non-null for INTERFACE and UNION, otherwise null.
		public Builder withPossibleTypes(List<__Type> possibleTypesParam) {
			this.possibleTypes = possibleTypesParam;
			return this;
		}

		// must be non-null for ENUM, otherwise null.
		public Builder withEnumValues(List<__EnumValue> enumValuesParam) {
			this.enumValues = enumValuesParam;
			return this;
		}

		// must be non-null for INPUT_OBJECT, otherwise null.
		public Builder withInputFields(List<__InputValue> inputFieldsParam) {
			this.inputFields = inputFieldsParam;
			return this;
		}

		// must be non-null for NON_NULL and LIST, otherwise null.
		public Builder withOfType(__Type ofTypeParam) {
			this.ofType = ofTypeParam;
			return this;
		}

		// may be non-null for custom SCALAR, otherwise null.
		public Builder withSpecifiedByURL(String specifiedByURLParam) {
			this.specifiedByURL = specifiedByURLParam;
			return this;
		}

		public __Type build() {
			__Type _object = build(new __Type());
			_object.setKind(this.kind);
			_object.setName(this.name);
			_object.setDescription(this.description);
			_object.setFields(this.fields);
			_object.setInterfaces(this.interfaces);
			_object.setPossibleTypes(this.possibleTypes);
			_object.setEnumValues(this.enumValues);
			_object.setInputFields(this.inputFields);
			_object.setOfType(this.ofType);
			_object.setSpecifiedByURL(this.specifiedByURL);
			return _object;
		}

		@Override
		String getTypeName() {
			return "__Type";
		}

	}

}
