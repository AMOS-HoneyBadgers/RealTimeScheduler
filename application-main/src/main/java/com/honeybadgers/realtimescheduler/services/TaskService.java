package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    }
}
