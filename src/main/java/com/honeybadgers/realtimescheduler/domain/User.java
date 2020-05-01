package com.honeybadgers.realtimescheduler.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.io.Serializable;

// together with implements Serializable, used by redis: https://www.baeldung.com/spring-data-redis-tutorial
@Entity
@Table(name = "\"user\"")
@RedisHash("User")
@Getter
@Setter
public class User implements Serializable {

    @Id
    @GeneratedValue(generator="randomId")
    @GenericGenerator(name="randomId", strategy="com.honeybadgers.realtimescheduler.domain.RandomIdGenerator")
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String role;

    // Annotation for preventing the disclosure of age to api endpoint
    //@JsonIgnore
    // Annotation for createUser to enforce an age o 1+
    @Positive
    private Integer age;
}
