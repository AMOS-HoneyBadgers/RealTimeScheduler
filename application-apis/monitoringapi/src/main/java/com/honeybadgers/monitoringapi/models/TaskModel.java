package com.honeybadgers.monitoringapi.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.honeybadgers.monitoringapi.models.TaskModelMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TaskModel
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:26.611+02:00[Europe/Berlin]")

public class TaskModel   {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("groupId")
  private UUID groupId;

  @JsonProperty("priority")
  private Integer priority;

  @JsonProperty("earliestStart")
  private String earliestStart;

  @JsonProperty("latestStart")
  private String latestStart;

  @JsonProperty("workingDays")
  private Integer workingDays;

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

  @JsonProperty("maxFailures")
  private Integer maxFailures;

  /**
   * Mode of task
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

  @JsonProperty("meta")
  @Valid
  private List<TaskModelMeta> meta = null;

  public TaskModel id(UUID id) {
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

  public TaskModel groupId(UUID groupId) {
    this.groupId = groupId;
    return this;
  }

  /**
   * Id of group this task is assigned to
   * @return groupId
  */
  @ApiModelProperty(required = true, value = "Id of group this task is assigned to")
  @NotNull

  @Valid

  public UUID getGroupId() {
    return groupId;
  }

  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  public TaskModel priority(Integer priority) {
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

  public TaskModel earliestStart(String earliestStart) {
    this.earliestStart = earliestStart;
    return this;
  }

  /**
   * Time, by which this task is allowed to be dispatched at earliest
   * @return earliestStart
  */
  @ApiModelProperty(value = "Time, by which this task is allowed to be dispatched at earliest")


  public String getEarliestStart() {
    return earliestStart;
  }

  public void setEarliestStart(String earliestStart) {
    this.earliestStart = earliestStart;
  }

  public TaskModel latestStart(String latestStart) {
    this.latestStart = latestStart;
    return this;
  }

  /**
   * Time, by which this task has to be dispatched at latest
   * @return latestStart
  */
  @ApiModelProperty(value = "Time, by which this task has to be dispatched at latest")


  public String getLatestStart() {
    return latestStart;
  }

  public void setLatestStart(String latestStart) {
    this.latestStart = latestStart;
  }

  public TaskModel workingDays(Integer workingDays) {
    this.workingDays = workingDays;
    return this;
  }

  /**
   * Days for processing
   * minimum: 1
   * @return workingDays
  */
  @ApiModelProperty(value = "Days for processing")

@Min(1)
  public Integer getWorkingDays() {
    return workingDays;
  }

  public void setWorkingDays(Integer workingDays) {
    this.workingDays = workingDays;
  }

  public TaskModel typeFlag(TypeFlagEnum typeFlag) {
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

  public TaskModel maxFailures(Integer maxFailures) {
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

  public TaskModel mode(ModeEnum mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Mode of task
   * @return mode
  */
  @ApiModelProperty(value = "Mode of task")


  public ModeEnum getMode() {
    return mode;
  }

  public void setMode(ModeEnum mode) {
    this.mode = mode;
  }

  public TaskModel indexNumber(Integer indexNumber) {
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

  public TaskModel force(Boolean force) {
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

  public TaskModel parallelismDegree(Integer parallelismDegree) {
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

  public TaskModel meta(List<TaskModelMeta> meta) {
    this.meta = meta;
    return this;
  }

  public TaskModel addMetaItem(TaskModelMeta metaItem) {
    if (this.meta == null) {
      this.meta = new ArrayList<>();
    }
    this.meta.add(metaItem);
    return this;
  }

  /**
   * Key-value object for meta-data
   * @return meta
  */
  @ApiModelProperty(value = "Key-value object for meta-data")

  @Valid

  public List<TaskModelMeta> getMeta() {
    return meta;
  }

  public void setMeta(List<TaskModelMeta> meta) {
    this.meta = meta;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaskModel taskModel = (TaskModel) o;
    return Objects.equals(this.id, taskModel.id) &&
        Objects.equals(this.groupId, taskModel.groupId) &&
        Objects.equals(this.priority, taskModel.priority) &&
        Objects.equals(this.earliestStart, taskModel.earliestStart) &&
        Objects.equals(this.latestStart, taskModel.latestStart) &&
        Objects.equals(this.workingDays, taskModel.workingDays) &&
        Objects.equals(this.typeFlag, taskModel.typeFlag) &&
        Objects.equals(this.maxFailures, taskModel.maxFailures) &&
        Objects.equals(this.mode, taskModel.mode) &&
        Objects.equals(this.indexNumber, taskModel.indexNumber) &&
        Objects.equals(this.force, taskModel.force) &&
        Objects.equals(this.parallelismDegree, taskModel.parallelismDegree) &&
        Objects.equals(this.meta, taskModel.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, groupId, priority, earliestStart, latestStart, workingDays, typeFlag, maxFailures, mode, indexNumber, force, parallelismDegree, meta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaskModel {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    earliestStart: ").append(toIndentedString(earliestStart)).append("\n");
    sb.append("    latestStart: ").append(toIndentedString(latestStart)).append("\n");
    sb.append("    workingDays: ").append(toIndentedString(workingDays)).append("\n");
    sb.append("    typeFlag: ").append(toIndentedString(typeFlag)).append("\n");
    sb.append("    maxFailures: ").append(toIndentedString(maxFailures)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    indexNumber: ").append(toIndentedString(indexNumber)).append("\n");
    sb.append("    force: ").append(toIndentedString(force)).append("\n");
    sb.append("    parallelismDegree: ").append(toIndentedString(parallelismDegree)).append("\n");
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
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

