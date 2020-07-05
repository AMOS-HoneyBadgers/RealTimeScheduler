package com.honeybadgers.groupapi.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * GroupModel
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T17:28:57.438+02:00[Europe/Berlin]")

public class GroupModel   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("parent_id")
  private String parentId;

  @JsonProperty("priority")
  private Integer priority;

  @JsonProperty("deadline")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime deadline;

  @JsonProperty("active_times")
  @Valid
  private List<GroupModelActiveTimes> activeTimes = null;

  @JsonProperty("working_days")
  @Valid
  private List<Boolean> workingDays = null;

  /**
   * Type of this task (relevant for scheduling)
   */
  public enum TypeFlagEnum {
    BATCH("batch"),
    
    REALTIME("realtime");

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

  @JsonProperty("type_flag")
  private TypeFlagEnum typeFlag = TypeFlagEnum.BATCH;

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
  private ModeEnum mode = ModeEnum.PARALLEL;

  @JsonProperty("last_index_number")
  private Integer lastIndexNumber;

  @JsonProperty("parallelism_degree")
  private Integer parallelismDegree = 1;

  public GroupModel id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Primary id of task object
   * @return id
  */
  @ApiModelProperty(required = true, value = "Primary id of task object")
  @NotNull

@Size(max=128) 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GroupModel parentId(String parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * Id of parent group object
   * @return parentId
  */
  @ApiModelProperty(value = "Id of parent group object")

@Size(max=128) 
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public GroupModel priority(Integer priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Default priority of all tasks of this group
   * minimum: 0
   * maximum: 9999
   * @return priority
  */
  @ApiModelProperty(value = "Default priority of all tasks of this group")

@Min(0) @Max(9999) 
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public GroupModel deadline(OffsetDateTime deadline) {
    this.deadline = deadline;
    return this;
  }

  /**
   * Default deadline for all tasks of this group
   * @return deadline
  */
  @ApiModelProperty(value = "Default deadline for all tasks of this group")

  @Valid

  public OffsetDateTime getDeadline() {
    return deadline;
  }

  public void setDeadline(OffsetDateTime deadline) {
    this.deadline = deadline;
  }

  public GroupModel activeTimes(List<GroupModelActiveTimes> activeTimes) {
    this.activeTimes = activeTimes;
    return this;
  }

  public GroupModel addActiveTimesItem(GroupModelActiveTimes activeTimesItem) {
    if (this.activeTimes == null) {
      this.activeTimes = new ArrayList<>();
    }
    this.activeTimes.add(activeTimesItem);
    return this;
  }

  /**
   * Array containing time frames, in which tasks are allowed to be dispatched
   * @return activeTimes
  */
  @ApiModelProperty(value = "Array containing time frames, in which tasks are allowed to be dispatched")


  public List<GroupModelActiveTimes> getActiveTimes() {
    return activeTimes;
  }

  public void setActiveTimes(List<GroupModelActiveTimes> activeTimes) {
    this.activeTimes = activeTimes;
  }

  public GroupModel workingDays(List<Boolean> workingDays) {
    this.workingDays = workingDays;
    return this;
  }

  public GroupModel addWorkingDaysItem(Boolean workingDaysItem) {
    if (this.workingDays == null) {
      this.workingDays = new ArrayList<>();
    }
    this.workingDays.add(workingDaysItem);
    return this;
  }

  /**
   * Default working_days for tasks of this group
   * @return workingDays
  */
  @ApiModelProperty(value = "Default working_days for tasks of this group")

@Size(min=7,max=7) 
  public List<Boolean> getWorkingDays() {
    return workingDays;
  }

  public void setWorkingDays(List<Boolean> workingDays) {
    this.workingDays = workingDays;
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

  public GroupModel lastIndexNumber(Integer lastIndexNumber) {
    this.lastIndexNumber = lastIndexNumber;
    return this;
  }

  /**
   * Index number of last dispatched task of this group
   * minimum: 0
   * @return lastIndexNumber
  */
  @ApiModelProperty(value = "Index number of last dispatched task of this group")

@Min(0)
  public Integer getLastIndexNumber() {
    return lastIndexNumber;
  }

  public void setLastIndexNumber(Integer lastIndexNumber) {
    this.lastIndexNumber = lastIndexNumber;
  }

  public GroupModel parallelismDegree(Integer parallelismDegree) {
    this.parallelismDegree = parallelismDegree;
    return this;
  }

  /**
   * [only with mode = parallel] Number of tasks allowed to be executed at the same time
   * minimum: 1
   * @return parallelismDegree
  */
  @ApiModelProperty(value = "[only with mode = parallel] Number of tasks allowed to be executed at the same time")

@Min(1)
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
        Objects.equals(this.deadline, groupModel.deadline) &&
        Objects.equals(this.activeTimes, groupModel.activeTimes) &&
        Objects.equals(this.workingDays, groupModel.workingDays) &&
        Objects.equals(this.typeFlag, groupModel.typeFlag) &&
        Objects.equals(this.mode, groupModel.mode) &&
        Objects.equals(this.lastIndexNumber, groupModel.lastIndexNumber) &&
        Objects.equals(this.parallelismDegree, groupModel.parallelismDegree);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parentId, priority, deadline, activeTimes, workingDays, typeFlag, mode, lastIndexNumber, parallelismDegree);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupModel {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    deadline: ").append(toIndentedString(deadline)).append("\n");
    sb.append("    activeTimes: ").append(toIndentedString(activeTimes)).append("\n");
    sb.append("    workingDays: ").append(toIndentedString(workingDays)).append("\n");
    sb.append("    typeFlag: ").append(toIndentedString(typeFlag)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    lastIndexNumber: ").append(toIndentedString(lastIndexNumber)).append("\n");
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

