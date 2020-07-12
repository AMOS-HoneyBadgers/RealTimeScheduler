package com.honeybadgers.models.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"lock\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DispatchFlag {
    @Id
    @Column(name="id", unique = true, nullable = false)
    private String id;

    @Type(type = "boolean")
    @Column(name = "is_dispatched", columnDefinition = "boolean")
    private boolean is_dispatched;
}
