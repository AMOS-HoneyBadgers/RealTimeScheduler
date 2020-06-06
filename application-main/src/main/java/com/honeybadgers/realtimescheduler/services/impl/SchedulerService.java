package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SchedulerService implements ISchedulerService {

    static final Logger logger = LogManager.getLogger(SchedulerService.class);

    @Autowired
    TaskRedisRepository taskRedisRepository;

    @Autowired
    LockRedisRepository lockRedisRepository;

    @Autowired
    ITaskService taskService;

    @Autowired
    ICommunication sender;


    @Override
    public RedisTask createRedisTask(String taskId){
        RedisTask redisTask = new RedisTask();
        redisTask.setId(taskId);
        return redisTask;
    }

    @Override
    public List<RedisTask> getAllRedisTasksAndSort(){
        Iterable<RedisTask> redisTasks = taskRedisRepository.findAll();
        List<RedisTask> sortedList = new ArrayList<RedisTask>();
        redisTasks.forEach(sortedList::add);
        Collections.sort(sortedList, (o1, o2) -> o1.getPriority() > o2.getPriority() ? -1 : (o1.getPriority() < o2.getPriority()) ? 1 : 0);

        return sortedList;
    }

    @Override
    public boolean checkTaskOnLocked(String taskId) {
        String lock = lockRedisRepository.findById(taskId).orElse(null);
        return lock != null;
    }

    @Override
    public void scheduleTask(String taskId) {
        // TODO Transaction
        RedisTask redisTask = taskRedisRepository.findById(taskId).orElse(null);

        if(redisTask == null){
            redisTask = createRedisTask(taskId);
        }
        Task task = taskService.getTaskById(taskId).orElse(null);
        if(task == null)
            throw new RuntimeException("task could not be found in database");

        redisTask.setPriority(taskService.calculatePriority(task));
        taskRedisRepository.save(redisTask);

        List<RedisTask> tasks = this.getAllRedisTasksAndSort();

        // TODO ASK DATEV WIE SCHNELL DIE ABGEARBEITET WERDEN
        // TODO locks, activeTimes, workingDays, ...
        try {
            for(int i = 0; i < 100; i++) {
                sender.sendTaskToDispatcher(tasks.get(i).getId());
            }
        } catch(IndexOutOfBoundsException e) {
            logger.info("passt scho" + e.getMessage());
        }


        logger.info("Task-id: " + redisTask.getId() + ", priority: " + redisTask.getPriority());
    }
}
