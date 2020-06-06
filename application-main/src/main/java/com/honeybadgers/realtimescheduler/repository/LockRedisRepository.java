package com.honeybadgers.realtimescheduler.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
@Slf4j
public class LockRedisRepository implements CrudRepository<String, String> {

    private static final String KEY = "LOCK";

    @Autowired
    @Qualifier("lockRedisTemplate")
    RedisTemplate<String, String> redisTemplate;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public <S extends String> S save(S s) {
        hashOperations.put(KEY, s, s);
        return s;
    }

    @Override
    public <S extends String> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public Optional<String> findById(String s) {
        String task = hashOperations.get(KEY,s);
        if(task == null)
            return Optional.empty();
        return Optional.of(task);
    }

    @Override
    public boolean existsById(String s) {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public Iterable<String> findAll() {
        return hashOperations.entries(KEY).values();
    }

    @Override
    public Iterable<String> findAllById(Iterable<String> iterable) {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public long count() {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public void deleteById(String s) {
        hashOperations.delete(KEY, s);
    }

    @Override
    public void delete(String redisTask) {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public void deleteAll(Iterable<? extends String> iterable) {
        throw new RuntimeException("NotImplemented!");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("NotImplemented!");
    }
}
