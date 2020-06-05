package com.honeybadgers.managementapi.repository;

import com.honeybadgers.models.RedisTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StateRepository implements CrudRepository<RedisTask, String> {

    @Override
    public <S extends RedisTask> S save(S s) {
        return null;
    }

    @Override
    public <S extends RedisTask> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<RedisTask> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<RedisTask> findAll() {
        return null;
    }

    @Override
    public Iterable<RedisTask> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(RedisTask redisTask) {

    }

    @Override
    public void deleteAll(Iterable<? extends RedisTask> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}
