package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.domain.RandomIdGenerator;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


// TODO: https://www.baeldung.com/spring-data-redis-tutorial
// https://www.youtube.com/watch?v=Hbt56gFj998

@Repository
public interface TaskRedisRepository extends CrudRepository<Task, String> {

    /*public static final String TASKS_KEY = "tasks";

    private final RandomIdGenerator idGenerator;
    private final HashOperations<String, String, Task> hashOps;

    public TaskRedisRepository(RedisTemplate<String, Task> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
        this.idGenerator = new RandomIdGenerator();
    }

    @Override
    public <S extends Task> S save(S task) {
        if (task.getId() == null) {
            task.setId(idGenerator.generateId());
        }

        hashOps.put(TASKS_KEY, task.getId(), task);

        return task;
    }

    @Override
    public <S extends Task> Iterable<S> saveAll(Iterable<S> tasks) {
        List<S> result = new ArrayList<>();

        for (S entity : tasks) {
            save(entity);
            result.add(entity);
        }

        return result;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(hashOps.get(TASKS_KEY, id));
    }

    @Override
    public boolean existsById(String id) {
        return hashOps.hasKey(TASKS_KEY, id);
    }

    @Override
    public Iterable<Task> findAll() {
        return hashOps.values(TASKS_KEY);
    }

    @Override
    public Iterable<Task> findAllById(Iterable<String> ids) {
        return hashOps.multiGet(TASKS_KEY, convertIterableToList(ids));
    }

    @Override
    public long count() {
        return hashOps.keys(TASKS_KEY).size();
    }

    @Override
    public void deleteById(String id) {
        hashOps.delete(TASKS_KEY, id);
    }

    @Override
    public void delete(Task task) {
        hashOps.delete(TASKS_KEY, task.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends Task> tasks) {
        for (Task task : tasks) {
            delete(task);
        }
    }

    @Override
    public void deleteAll() {
        Set<String> ids = hashOps.keys(TASKS_KEY);
        for (String id : ids) {
            deleteById(id);
        }
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T object : iterable) {
            list.add(object);
        }
        return list;
    }*/
}

