package com.honeybadgers.models.model;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
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
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "\"group\"")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @Column(name="id", unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Group parentGroup;

    @Max(value = 999)
    @Min(value = 0)
    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "deadline")
    private Timestamp deadline;

    @Type(type = "jsonb")
    @Column(name = "active_times", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<ActiveTimes> activeTimeFrames;

    // hibernate does not support boolean[] not even using hibernate-types-52
    @Type(type = "int-array")
    @Column(name = "working_days", columnDefinition = "integer[]")
    private int[] workingDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_flag", nullable = false)
    private TypeFlagEnum typeFlagEnum = TypeFlagEnum.Batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeEnum modeEnum = ModeEnum.Parallel;

    @Min(value = 0)
    @Column(name = "last_index_number")
    private Integer lastIndexNumber;

    @Min(value = 1)
    @Column(name = "parallelism_degree")
    private Integer parallelismDegree;

    @Column(name = "current_parallelism_degree", nullable = false)
    private Integer currentParallelismDegree = 0;


    @PrePersist
    void checkModeParameters() {
        if(this.modeEnum == ModeEnum.Sequential && this.lastIndexNumber == null) {
            this.lastIndexNumber = 0;
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", parentGroup=" + parentGroup +
                ", priority=" + priority +
                ", deadline=" + deadline +
                ", activeTimeFrames=" + activeTimeFrames +
                ", workingDays=" + Arrays.toString(workingDays) +
                ", typeFlagEnum=" + typeFlagEnum +
                ", modeEnum=" + modeEnum +
                ", lastIndexNumber=" + lastIndexNumber +
                ", parallelismDegree=" + parallelismDegree +
                ", currentParallelismDegree=" + currentParallelismDegree +
                '}';
    }
}
