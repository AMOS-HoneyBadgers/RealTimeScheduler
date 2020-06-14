package com.honeybadgers.cleaner.repository;

import com.honeybadgers.models.RedisLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LockRepository extends CrudRepository<RedisLock, String> {

}
