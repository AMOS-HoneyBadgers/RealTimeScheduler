package com.honeybadgers.realtimescheduler.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Table(name = "tasks")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Max(value = 999)
    @Min(value = 0)
    @Column(nullable = false)
    private int priority;

    private Timestamp earliestStart;

    private Timestamp latestStart;

    @Min(value = 1)
    private int workingDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_flag", nullable = false)
    private TypeFlagEnum typeFlagEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeEnum modeEnum;

    private int maxFailures;

    private Integer indexNumber;

    private Boolean force;

    private Integer parallelismDegree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, String> metaData;

    public Task(String id, int priority, Timestamp latestStart, Timestamp earliestStart, int workingDays, TypeFlagEnum typeFlagEnum, ModeEnum modeEnum, int maxFailures, Integer indexNumber, Boolean force, Map<String, String> metaData) {
        this.id = id;
        this.priority = priority;
        this.latestStart = latestStart;
        this.earliestStart = earliestStart;
        this.workingDays = workingDays;
        this.typeFlagEnum = typeFlagEnum;
        this.modeEnum = modeEnum;
        this.maxFailures = maxFailures;
        this.indexNumber = indexNumber;
        this.force = force;
        this.metaData = metaData;
    }

    public Task(String id, int priority, Timestamp latestStart, Timestamp earliestStart, int workingDays, TypeFlagEnum typeFlagEnum, ModeEnum modeEnum, int maxFailures, Integer parallelismDegree, Map<String, String> metaData) {
        this.id = id;
        this.priority = priority;
        this.latestStart = latestStart;
        this.earliestStart = earliestStart;
        this.workingDays = workingDays;
        this.typeFlagEnum = typeFlagEnum;
        this.modeEnum = modeEnum;
        this.maxFailures = maxFailures;
        this.parallelismDegree = parallelismDegree;
        this.metaData = metaData;
    }

    @PrePersist
    void checkModeParameters() {
        if(this.modeEnum == ModeEnum.Parallel) {
            assert this.parallelismDegree != null;
        } else if(this.modeEnum == ModeEnum.Sequential) {
            assert this.indexNumber != null;
            if(this.force == null)
                this.force = false;
        }
    }
}
