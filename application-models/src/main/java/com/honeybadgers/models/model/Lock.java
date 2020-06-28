package com.honeybadgers.models.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lock")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lock {

    @Id
    @Column(name="id", unique = true, nullable = false)
    private String id;

    @Column(name = "is_dispatched", nullable = false)
    private boolean isDispatched = false;
}
