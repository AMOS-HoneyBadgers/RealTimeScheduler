package com.honeybadgers.realtimescheduler.domain.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "priority")
    private Integer taskPriority;

    private String name;

    @CreationTimestamp
    private Timestamp submittimestamp;
}
