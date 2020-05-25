package com.honeybadgers.realtimescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "groups")
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
    @Column(nullable = false)
    private int priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_flag", nullable = false)
    private TypeFlagEnum typeFlagEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeEnum modeEnum;

    private int maxFailures;

    private boolean paused = false;

}
