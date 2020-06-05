package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.*;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/data")        // only initialize if one of the given profiles is active
@Slf4j
public class TestController {

    @Autowired
    ITaskService taskService;

    @Autowired
    ISchedulerService schedulerService;

    @Autowired
    IGroupService groupService;

    @Autowired
    ICommunication sender;

    @Autowired
    LockRedisRepository lockRedisRepository;

    @Autowired
    TaskRedisRepository taskRedisRepository;

    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) {

        Task task = new Task();
        task.setPriority(Integer.parseInt(priority));
        task.setId(UUID.randomUUID().toString());
        task.setDeadline(new Timestamp(System.currentTimeMillis()+100000));


        schedulerService.scheduleTask(task.getId());
        //sender.sendTaskToDispatcher(task.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/testTaskQueue/{task}")
    public ResponseEntity<?> tasksQueue(@PathVariable(value = "task") final String task){
        sender.sendTaskToTasksQueue(task);
        return ResponseEntity.ok("sent task " + task);
    }

    @PostMapping("/test/redis")
    public ResponseEntity<?> testRedis() {

        RedisTask task = new RedisTask();
        task.setId(UUID.randomUUID().toString());
        task.setPriority(2304);

        lockRedisRepository.save(task.getId());
        String getS = lockRedisRepository.findById(task.getId()).orElse(null);
        if(getS == null) {
            log.warn("###################### FAILURE IN LOCK!");
        } else {
            log.warn("###################### SUCCESS IN LOCK! " + task.getId() + " ACTUAL " + getS);
        }

        taskRedisRepository.save(task);
        RedisTask getT = taskRedisRepository.findById(task.getId()).orElse(null);
        if(getT == null) {
            log.warn("###################### FAILURE IN TASK!");
        } else {
            log.warn("###################### SUCCESS IN TASK! " + task.toString() + " ACTUAL " + getT.toString());
        }
        Iterable<RedisTask> tasks = taskRedisRepository.findAll();
        Iterable<String> strings = lockRedisRepository.findAll();

        tasks.forEach(redisTask -> log.warn("TASK: " + redisTask.toString()));
        strings.forEach(string -> log.warn("STRING: " + string));

        return ResponseEntity.ok().build();
    }
}
