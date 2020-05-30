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
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskRestModel {

    private String id;

    private String groupId;

    private int priority;

    private long earliestStart;

    private long latestStart;

    private int workingDays;

    private String typeFlagEnum;

    private String modeEnum;

    private int maxFailures;

    private Integer indexNumber;

    private Boolean force;

    private Integer parallelismDegree;

    private Map<String, String> metaData;

}
