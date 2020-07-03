package com.honeybadgers.managementapi.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DateTimeBody
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T17:30:15.667+02:00[Europe/Berlin]")

public class DateTimeBody   {
  @JsonProperty("resume_date_time")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime resumeDateTime;

  public DateTimeBody resumeDateTime(OffsetDateTime resumeDateTime) {
    this.resumeDateTime = resumeDateTime;
    return this;
  }

  /**
   * DateTime, when to resume after being stopped
   * @return resumeDateTime
  */
  @ApiModelProperty(required = true, value = "DateTime, when to resume after being stopped")
  @NotNull

  @Valid

  public OffsetDateTime getResumeDateTime() {
    return resumeDateTime;
  }

  public void setResumeDateTime(OffsetDateTime resumeDateTime) {
    this.resumeDateTime = resumeDateTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DateTimeBody dateTimeBody = (DateTimeBody) o;
    return Objects.equals(this.resumeDateTime, dateTimeBody.resumeDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resumeDateTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DateTimeBody {\n");
    
    sb.append("    resumeDateTime: ").append(toIndentedString(resumeDateTime)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

