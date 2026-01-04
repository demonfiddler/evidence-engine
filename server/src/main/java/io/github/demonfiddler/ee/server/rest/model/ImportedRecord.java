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

package io.github.demonfiddler.ee.server.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

/**
 * Summarises an imported record.
 */
@Schema(name = "ImportedRecord", description = "Summarises an imported record.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-11-19T12:59:29.359836300Z[Europe/London]", comments = "Generator version: 7.17.0")
public class ImportedRecord {

    private @Nullable Long id;
    private @Nullable String label;
    private @Nullable ResultEnum result;
    @Valid
    private List<@Valid ImportMessage> messages = new ArrayList<>();

    /**
     * Describes the outcome of the import operation.
     */
    public enum ResultEnum {

        IMPORTED("imported"), //
        DUPLICATE("duplicate"), //
        ERROR("error");

        private final String value;

        ResultEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static ResultEnum fromValue(String value) {
            for (ResultEnum b : ResultEnum.values()) {
                if (b.value.equals(value))
                    return b;
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

    }

    public ImportedRecord id(@Nullable Long id) {
        this.id = id;
        return this;
    }

    /**
     * A unique object identifier.
     * @return id
     */
    @Schema(name = "id", description = "A unique object identifier.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public @Nullable Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public ImportedRecord label(@Nullable String label) {
        this.label = label;
        return this;
    }

    /**
     * A textual label to identify the item.
     * @return label
     */
    @Schema(name = "label", description = "A textual label to identify the item.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("label")
    public @Nullable String getLabel() {
        return label;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    public ImportedRecord result(@Nullable ResultEnum result) {
        this.result = result;
        return this;
    }

    /**
     * Describes the outcome of the import operation.
     * @return result
     */
    @Schema(name = "result", description = "Describes the outcome of the import operation.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("result")
    public @Nullable ResultEnum getResult() {
        return result;
    }

    public void setResult(@Nullable ResultEnum result) {
        this.result = result;
    }

    public ImportedRecord messages(List<@Valid ImportMessage> messages) {
        this.messages = messages;
        return this;
    }

    public ImportedRecord addMessagesItem(ImportMessage messagesItem) {
        if (this.messages == null)
            this.messages = new ArrayList<>();
        this.messages.add(messagesItem);
        return this;
    }

    /**
     * Message relating to the imported record.
     * @return messages
     */
    @Valid
    @Schema(name = "messages", description = "Message relating to the imported record.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("messages")
    public List<@Valid ImportMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<@Valid ImportMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ImportedRecord importedRecord = (ImportedRecord)o;
        return Objects.equals(this.id, importedRecord.id) && Objects.equals(this.label, importedRecord.label)
            && Objects.equals(this.result, importedRecord.result)
            && Objects.equals(this.messages, importedRecord.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, result, messages);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ImportedRecord {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    label: ").append(toIndentedString(label)).append("\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
        sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

}
