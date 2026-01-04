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

import java.util.Objects;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;

/**
 * A message about the imported record.
 */
@Schema(name = "ImportMessage", description = "A message about the imported record.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
  date = "2025-11-20T15:02:54.785156700Z[Europe/London]", comments = "Generator version: 7.17.0")
public class ImportMessage {

  /**
   * The severity/importance of the message.
   */
  public enum SeverityEnum {

    INFO("info"), //
    WARNING("warning"), //
    ERROR("error"), //
    FATAL("fatal");

    private final String value;

    SeverityEnum(String value) {
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
    public static SeverityEnum fromValue(String value) {
      for (SeverityEnum b : SeverityEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

  }

  private @Nullable Integer lineNum;
  private @Nullable SeverityEnum severity;
  private @Nullable String text;

  public ImportMessage lineNum(@Nullable Integer lineNum) {
    this.lineNum = lineNum;
    return this;
  }

  /**
   * The line number in the source to which the message relates.
   * @return lineNum
   */
  @Schema(name = "lineNum", description = "The line number in the source to which the message relates.",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lineNum")
  public @Nullable Integer getLineNum() {
    return lineNum;
  }

  public void setLineNum(@Nullable Integer lineNum) {
    this.lineNum = lineNum;
  }

  public ImportMessage severity(@Nullable SeverityEnum severity) {
    this.severity = severity;
    return this;
  }

  /**
   * The severity/importance of the message.
   * @return severity
   */
  @Schema(name = "severity", description = "The severity/importance of the message.",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("severity")
  public @Nullable SeverityEnum getSeverity() {
    return severity;
  }

  public void setSeverity(@Nullable SeverityEnum severity) {
    this.severity = severity;
  }

  public ImportMessage text(@Nullable String text) {
    this.text = text;
    return this;
  }

  /**
   * The message text.
   * @return text
   */
  @Schema(name = "text", description = "The message text.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public @Nullable String getText() {
    return text;
  }

  public void setText(@Nullable String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ImportMessage importMessage = (ImportMessage)o;
    return Objects.equals(this.lineNum, importMessage.lineNum) && //
        Objects.equals(this.severity, importMessage.severity) && //
        Objects.equals(this.text, importMessage.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lineNum, severity, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImportMessage {\n");
    sb.append("    lineNum: ").append(toIndentedString(lineNum)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
