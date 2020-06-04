package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository2;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class TaskService implements ITaskService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @Autowired
    TaskRedisRepository2 taskRedisRepository2;

    @Autowired
    TaskRedisRepository taskRedisRepository;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-modifier}")
    double deadlineModifier;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-bonus-base-prio-dependant}")
    boolean deadlineBaseDependant;

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
    public long calculatePriority(Task task) {
        double finalPriority = 0;
        finalPriority = task.getPriority();
        Timestamp deadline = task.getDeadline();
        if(deadline != null){
            Date currentTime = new Date(System.currentTimeMillis());
            //timeDiff in Minuten umrechnen, da sonst Differenz zu klein (finalPriority ändert zu wenig)
            double timeDiff = Math.abs(deadline.getTime() - currentTime.getTime()) / (1000.0 * 60.0);
            log.info("timediff: " + timeDiff);
            if(deadlineBaseDependant) {
                // the higher the base-prio, the higher it will be increased by same timediff
                finalPriority += ((deadlineModifier * finalPriority) / timeDiff);
            } else {
                finalPriority += (deadlineModifier/timeDiff);
            }
        }
        return Math.round(finalPriority);
    }

    private RedisTask createRedisTask(String taskId){
        RedisTask redisTask = new RedisTask();
        redisTask.setId(taskId);
        return redisTask;
    }

    @Override
    public void scheduleTask(Task task) {
        /*RedisTask redisTask = taskRedisRepository.findById(task.getId()).orElse(null);
        if(redisTask == null){
            redisTask = createRedisTask(task.getId());
        }
        redisTask.setPriority(calculatePriority(task));
        taskRedisRepository.save(redisTask);
        log.info("Task-id: " + redisTask.getId() + ", priority: " + redisTask.getPriority());*/

        RedisTask redisTask = createRedisTask(task.getId());
        redisTask.setPriority(calculatePriority(task));
        taskRedisRepository2.save(redisTask);
        RedisTask opt = taskRedisRepository2.findById(redisTask.getId()).orElse(null);
        if(opt == null)
            throw new RuntimeException("could not find redistask id");

        log.info("###############################--------------------!!!!!!!!+++++++++++++++?????????=========== taskid: " + opt.getId());
    }
    public List<RedisTask> getAllRedisTasks(){
       Iterable<RedisTask> redisTasks = taskRedisRepository.findAll();
       List<RedisTask> sortedList = new ArrayList<RedisTask>();
       redisTasks.forEach(sortedList::add);
       Collections.sort(sortedList, (o1, o2) -> o1.getPriority() > o2.getPriority() ? -1 : (o1.getPriority() < o2.getPriority()) ? 1 : 0);
       return sortedList;
    }
}
