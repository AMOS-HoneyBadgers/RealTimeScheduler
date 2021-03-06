package com.honeybadgers.models.model.jpa;

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
import java.util.Map;

@Entity
@Table(name = "task")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Max(value = 9999)
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
    // -> boolean as int
    @Type(type = "int-array")
    @Column(name = "working_days", columnDefinition = "integer[]")
    private int[] workingDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatusEnum status = TaskStatusEnum.Waiting;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_flag", nullable = false)
    private TypeFlagEnum typeFlagEnum = TypeFlagEnum.Batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeEnum modeEnum = ModeEnum.Parallel;

    @Min(value = 0)
    @Column(name = "retries")
    private int retries = 0;

    @Column(name = "force", nullable = false)
    private boolean force = false;

    @Min(value = 1)
    @Column(name = "index_number")
    private Integer indexNumber;

    @Type(type = "jsonb")
    @Column(name = "meta_data", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, String> metaData;

    @Column(name = "total_priority")
    private Long totalPriority;

    @Type(type = "jsonb")
    @Column(name = "history", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<History> history;


    @PrePersist
    void checkModeParameters() {
        if(this.modeEnum == ModeEnum.Sequential) {
            assert this.indexNumber != null;
        } else if(this.modeEnum == ModeEnum.Parallel) {
            assert this.group.getParallelismDegree() != null;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", group=" + group +
                ", priority=" + priority +
                ", deadline=" + deadline +
                ", activeTimeFrames=" + activeTimeFrames +
                ", workingDays=" + Arrays.toString(workingDays) +
                ", status=" + status +
                ", typeFlagEnum=" + typeFlagEnum +
                ", modeEnum=" + modeEnum +
                ", retries=" + retries +
                ", force=" + force +
                ", indexNumber=" + indexNumber +
                ", metaData=" + metaData +
                ", totalPriority=" + totalPriority +
                ", history=" + history +
                '}';
    }
}
