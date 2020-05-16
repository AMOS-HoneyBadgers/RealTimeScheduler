package com.honeybadgers.realtimescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @Column(name="id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name="name")
    private String name;

}
