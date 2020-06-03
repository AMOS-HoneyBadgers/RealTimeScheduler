package com.honeybadgers.realtimescheduler.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("RedisTask")
public class RedisTask implements Serializable {

    private String id;
    private long priority;
}
