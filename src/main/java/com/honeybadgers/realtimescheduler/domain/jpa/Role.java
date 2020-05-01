package com.honeybadgers.realtimescheduler.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(generator="randomId")
    @GenericGenerator(name="randomId", strategy="com.honeybadgers.realtimescheduler.domain.RandomIdGenerator")
    private String id;

    @NotEmpty
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roleSet")
    private Set<Task> taskSet;
}
