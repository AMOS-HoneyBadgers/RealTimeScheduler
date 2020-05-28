package com.honeybadgers.realtimescheduler.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupRestModel {

    private String id;

    private String parentGroupId;

    private int priority;

    private List<ActiveTimes> activeTimes;

    private Integer[] workingDays;

    private String typeFlagEnum;

    private String modeEnum;

    private Boolean paused;

    private Integer lastIndexNumber;

    private Integer parallelismDegree;

}
