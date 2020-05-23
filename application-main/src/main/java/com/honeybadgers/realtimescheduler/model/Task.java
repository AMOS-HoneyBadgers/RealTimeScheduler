package com.honeybadgers.realtimescheduler.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    @Column(name="id", unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "groupid")
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
    @Column(name = "typeFlag", nullable = false)
    private TypeFlagEnum typeFlagEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeEnum modeEnum;

    private int maxFailures;

    private Integer indexNumber;

    private Boolean force;

    private Integer parallelismDegree;

    @Type(type = "jsonb")
    @Column(name = "metadata", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, String> metaData;


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
