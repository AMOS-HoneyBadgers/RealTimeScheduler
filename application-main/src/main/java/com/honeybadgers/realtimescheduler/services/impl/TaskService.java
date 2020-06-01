package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TaskService implements ITaskService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @Autowired
    TaskRedisRepository taskRedisRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskPostgresRepository.findAll();
    }

    @Override
    public void uploadTask(Task task) {
        taskPostgresRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        this.taskPostgresRepository.deleteById(id);
    }

    @Override
    public RedisTask calculatePriority(Task task) {
        RedisTask redisTask = new RedisTask();
        redisTask.setId(task.getId());
        redisTask.setPriority((int) (Math.random() * ((10000 - 1000) + 1)));
        return redisTask;
    }

    @Override
    public void scheduleTask(RedisTask redisTask) {
        taskRedisRepository.save(redisTask);
        //Optional<RedisTask> redisTask1 = taskRedisRepository.findById(redisTask.getId());
        //System.out.println("Task-id: " + redisTask1.get().getId() + ", priority: " + redisTask1.get().getPriority());
    }
}
