package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.*;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository1;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository2;
import com.honeybadgers.realtimescheduler.services.IGroupService;
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
    IGroupService groupService;

    @Autowired
    ICommunication sender;

    @Autowired
    TaskRedisRepository1 taskRedisRepository1;

    @Autowired
    TaskRedisRepository2 taskRedisRepository2;

    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) {

        Task task = new Task();
        task.setPriority(Integer.parseInt(priority));
        task.setId(UUID.randomUUID().toString());
        task.setDeadline(new Timestamp(System.currentTimeMillis()+100000));


        taskService.scheduleTask(task.getId());
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
        task.setPriority(200);

        taskRedisRepository1.save(task);
        log.warn("REPRO 1 SAVE!");
        RedisTask task2 = taskRedisRepository2.findById(task.getId()).orElse(null);
        if(task2 != null) {
            log.warn("REPRO 2 FOUND TASK -> FAILURE!!!!!");
            log.warn("TASK 1: " + task.toString() + "     TASK 2: " + task2.toString());
        } else
            log.warn("REPRO 2 DID NOT FIND TASK -> SUCCESS!!!!!");


        RedisTask task3 = new RedisTask();
        task3.setId(UUID.randomUUID().toString());
        task3.setPriority(200);
        taskRedisRepository2.save(task3);
        log.warn("REPRO 2 SAVE!");
        RedisTask task4 = taskRedisRepository1.findById(task3.getId()).orElse(null);
        if(task4 != null) {
            log.warn("REPRO 2 FOUND TASK -> FAILURE!!!!!");
            log.warn("TASK 3: " + task3.toString() + "     TASK 4: " + task4.toString());
        } else
            log.warn("REPRO 2 DID NOT FIND TASK -> SUCCESS!!!!!");


        log.warn("TEST FIND REPRO1 TASK1");
        RedisTask task5 = taskRedisRepository1.findById(task.getId()).orElse(null);
        if(task5 != null) {
            log.warn("REPRO 1 FOUND TASK -> SUCCESS!!!!!");
            log.warn("TASK 1: " + task.toString() + "     TASK 5: " + task5.toString());
        } else
            log.warn("REPRO 1 DID NOT FIND TASK -> FAILURE!!!!!");


        log.warn("TEST FIND REPRO2 TASK3");
        RedisTask task6 = taskRedisRepository2.findById(task3.getId()).orElse(null);
        if(task6 != null) {
            log.warn("REPRO 2 FOUND TASK -> SUCCESS!!!!!");
            log.warn("TASK 3: " + task3.toString() + "     TASK 6: " + task6.toString());
        } else
            log.warn("REPRO 2 DID NOT FIND TASK -> FAILURE!!!!!");


        return ResponseEntity.ok().build();
    }
}
