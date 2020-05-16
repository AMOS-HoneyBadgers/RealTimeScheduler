package com.honeybadgers.groupapi.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * GroupModel
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T15:34:10.745+02:00[Europe/Berlin]")

public class GroupModel   {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("parentId")
  private UUID parentId;

  @JsonProperty("priority")
  private Integer priority;

  /**
   * Type of this task (relevant for scheduling)
   */
  public enum TypeFlagEnum {
    BATCH("batch"),
    
    USER("user");

    private String value;

    TypeFlagEnum(String value) {
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
    public static TypeFlagEnum fromValue(String value) {
      for (TypeFlagEnum b : TypeFlagEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("typeFlag")
  private TypeFlagEnum typeFlag;

  @JsonProperty("paused")
  private Boolean paused = false;

  @JsonProperty("maxFailures")
  private Integer maxFailures;

  /**
   * Mode of tasks of this group
   */
  public enum ModeEnum {
    SEQUENTIAL("sequential"),
    
    PARALLEL("parallel");

    private String value;

    ModeEnum(String value) {
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
    public static ModeEnum fromValue(String value) {
      for (ModeEnum b : ModeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("mode")
  private ModeEnum mode;

  @JsonProperty("indexNumber")
  private Integer indexNumber;

  @JsonProperty("force")
  private Boolean force;

  @JsonProperty("parallelismDegree")
  private Integer parallelismDegree;

  public GroupModel id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Primary id of task object
   * @return id
  */
  @ApiModelProperty(required = true, value = "Primary id of task object")
  @NotNull

  @Valid

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public GroupModel parentId(UUID parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * Id of parent group object
   * @return parentId
  */
  @ApiModelProperty(value = "Id of parent group object")

  @Valid

  public UUID getParentId() {
    return parentId;
  }

  public void setParentId(UUID parentId) {
    this.parentId = parentId;
  }

  public GroupModel priority(Integer priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Priority of this task for scheduling
   * minimum: 0
   * maximum: 999
   * @return priority
  */
  @ApiModelProperty(value = "Priority of this task for scheduling")

@Min(0) @Max(999) 
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public GroupModel typeFlag(TypeFlagEnum typeFlag) {
    this.typeFlag = typeFlag;
    return this;
  }

  /**
   * Type of this task (relevant for scheduling)
   * @return typeFlag
  */
  @ApiModelProperty(value = "Type of this task (relevant for scheduling)")


  public TypeFlagEnum getTypeFlag() {
    return typeFlag;
  }

  public void setTypeFlag(TypeFlagEnum typeFlag) {
    this.typeFlag = typeFlag;
  }

  public GroupModel paused(Boolean paused) {
    this.paused = paused;
    return this;
  }

  /**
   * Boolean, which identifies, whereas this group (and all of its tasks) are currently paused
   * @return paused
  */
  @ApiModelProperty(value = "Boolean, which identifies, whereas this group (and all of its tasks) are currently paused")


  public Boolean getPaused() {
    return paused;
  }

  public void setPaused(Boolean paused) {
    this.paused = paused;
  }

  public GroupModel maxFailures(Integer maxFailures) {
    this.maxFailures = maxFailures;
    return this;
  }

  /**
   * Maximum number of failures this task is allowed to fail
   * minimum: 0
   * @return maxFailures
  */
  @ApiModelProperty(value = "Maximum number of failures this task is allowed to fail")

@Min(0)
  public Integer getMaxFailures() {
    return maxFailures;
  }

  public void setMaxFailures(Integer maxFailures) {
    this.maxFailures = maxFailures;
  }

  public GroupModel mode(ModeEnum mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Mode of tasks of this group
   * @return mode
  */
  @ApiModelProperty(value = "Mode of tasks of this group")


  public ModeEnum getMode() {
    return mode;
  }

  public void setMode(ModeEnum mode) {
    this.mode = mode;
  }

  public GroupModel indexNumber(Integer indexNumber) {
    this.indexNumber = indexNumber;
    return this;
  }

  /**
   * [only with mode = sequential] Index number of task in corresponding sequence
   * minimum: 0
   * @return indexNumber
  */
  @ApiModelProperty(value = "[only with mode = sequential] Index number of task in corresponding sequence")

@Min(0)
  public Integer getIndexNumber() {
    return indexNumber;
  }

  public void setIndexNumber(Integer indexNumber) {
    this.indexNumber = indexNumber;
  }

  public GroupModel force(Boolean force) {
    this.force = force;
    return this;
  }

  /**
   * [only with mode = sequential] If set to true: ignore indexNumber and do not wait on previous tasks
   * @return force
  */
  @ApiModelProperty(value = "[only with mode = sequential] If set to true: ignore indexNumber and do not wait on previous tasks")


  public Boolean getForce() {
    return force;
  }

  public void setForce(Boolean force) {
    this.force = force;
  }

  public GroupModel parallelismDegree(Integer parallelismDegree) {
    this.parallelismDegree = parallelismDegree;
    return this;
  }

  /**
   * [only with mode = parallel] Number of tasks allowed to be executed at the same time
   * @return parallelismDegree
  */
  @ApiModelProperty(value = "[only with mode = parallel] Number of tasks allowed to be executed at the same time")


  public Integer getParallelismDegree() {
    return parallelismDegree;
  }

  public void setParallelismDegree(Integer parallelismDegree) {
    this.parallelismDegree = parallelismDegree;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupModel groupModel = (GroupModel) o;
    return Objects.equals(this.id, groupModel.id) &&
        Objects.equals(this.parentId, groupModel.parentId) &&
        Objects.equals(this.priority, groupModel.priority) &&
        Objects.equals(this.typeFlag, groupModel.typeFlag) &&
        Objects.equals(this.paused, groupModel.paused) &&
        Objects.equals(this.maxFailures, groupModel.maxFailures) &&
        Objects.equals(this.mode, groupModel.mode) &&
        Objects.equals(this.indexNumber, groupModel.indexNumber) &&
        Objects.equals(this.force, groupModel.force) &&
        Objects.equals(this.parallelismDegree, groupModel.parallelismDegree);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parentId, priority, typeFlag, paused, maxFailures, mode, indexNumber, force, parallelismDegree);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupModel {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    typeFlag: ").append(toIndentedString(typeFlag)).append("\n");
    sb.append("    paused: ").append(toIndentedString(paused)).append("\n");
    sb.append("    maxFailures: ").append(toIndentedString(maxFailures)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    indexNumber: ").append(toIndentedString(indexNumber)).append("\n");
    sb.append("    force: ").append(toIndentedString(force)).append("\n");
    sb.append("    parallelismDegree: ").append(toIndentedString(parallelismDegree)).append("\n");
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

