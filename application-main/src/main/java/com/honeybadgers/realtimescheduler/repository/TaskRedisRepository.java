package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.RedisTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRedisRepository extends CrudRepository<RedisTask, String> {
}
