package com.honeybadgers.realtimescheduler.repository.redis;

import com.honeybadgers.realtimescheduler.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// TODO: https://www.baeldung.com/spring-data-redis-tutorial
// https://www.youtube.com/watch?v=Hbt56gFj998

@Repository
@Profile({"redis"})
public interface UserRedisRepository extends CrudRepository<User, String> {
}

