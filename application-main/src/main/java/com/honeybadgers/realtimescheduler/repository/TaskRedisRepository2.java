package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.RedisTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
@Slf4j
public class TaskRedisRepository2 implements CrudRepository<RedisTask, String> {

    private static final String KEY = "REDISTASK";

    @Autowired
    //@Qualifier("prioRedisTemplate")
    RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, RedisTask> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public <S extends RedisTask> S save(S s) {
        hashOperations.put(KEY, s.getId(), s);
        return s;
    }

    @Override
    public <S extends RedisTask> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new RuntimeException();
    }

    @Override
    public Optional<RedisTask> findById(String s) {
        RedisTask task = hashOperations.get(KEY,s);
        log.warn("REPRO2.find: TASK RETURN: " + task);
        if(task != null)
            log.warn("REPRO2.find: TASK STRING: " + task.toString());
        if(task == null)
            return Optional.empty();
        return Optional.of(task);
    }

    @Override
    public boolean existsById(String s) {
        throw new RuntimeException();
    }

    @Override
    public Iterable<RedisTask> findAll() {
        throw new RuntimeException();
    }

    @Override
    public Iterable<RedisTask> findAllById(Iterable<String> iterable) {
        throw new RuntimeException();
    }

    @Override
    public long count() {
        throw new RuntimeException();
    }

    @Override
    public void deleteById(String s) {
        throw new RuntimeException();
    }

    @Override
    public void delete(RedisTask redisTask) {
        throw new RuntimeException();
    }

    @Override
    public void deleteAll(Iterable<? extends RedisTask> iterable) {
        throw new RuntimeException();
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException();
    }
}
