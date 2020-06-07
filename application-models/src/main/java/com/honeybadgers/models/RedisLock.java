package com.honeybadgers.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@RedisHash("RedisLock")
public class RedisLock implements Serializable {

    String id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime resume_date;

    int capacity = 0;

    @Override
    public String toString() {
        return "RedisLock{" +
                "id='" + id + '\'' +
                ", resume_date=" + resume_date +
                ", capacity=" + capacity +
                '}';
    }
}
