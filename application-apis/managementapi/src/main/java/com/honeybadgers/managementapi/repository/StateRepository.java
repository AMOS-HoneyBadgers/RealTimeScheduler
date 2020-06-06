package com.honeybadgers.managementapi.repository;

import com.honeybadgers.models.RedisTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends CrudRepository<String, String> {

}
