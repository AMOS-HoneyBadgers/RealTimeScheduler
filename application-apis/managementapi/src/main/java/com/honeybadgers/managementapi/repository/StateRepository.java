package com.honeybadgers.managementapi.repository;

import com.honeybadgers.models.RedisLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<RedisLock, String> {

}
