package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRedisRepository extends CrudRepository<RedisTask, String> {
}
