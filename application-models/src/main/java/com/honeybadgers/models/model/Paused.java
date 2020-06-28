package com.honeybadgers.models.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "paused")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Paused {

    @Id
    @Column(name="id", unique = true, nullable = false)
    private String id;

    @Column(name = "resume_date")
    private Timestamp resumeDate;
}
