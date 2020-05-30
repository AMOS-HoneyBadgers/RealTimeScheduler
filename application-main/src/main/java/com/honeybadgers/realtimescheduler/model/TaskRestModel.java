package com.honeybadgers.realtimescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskRestModel {

    // TODO apply new changes of model

    private String id;

    private String groupId;

    private int priority;

    private Integer deadline;

    private List<ActiveTimes> activeTimes;

    private Integer[] workingDays;

    private String statusEnum;

    private String typeFlagEnum;

    private String modeEnum;

    private Boolean paused;

    private Integer retries;

    private Boolean force;

    private Integer indexNumber;

    private Map<String, String> metaData;

}
