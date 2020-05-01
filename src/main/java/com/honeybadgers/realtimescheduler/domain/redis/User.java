package com.honeybadgers.realtimescheduler.domain.redis;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

    private String name;

    private String role;

    private Integer age;
}
