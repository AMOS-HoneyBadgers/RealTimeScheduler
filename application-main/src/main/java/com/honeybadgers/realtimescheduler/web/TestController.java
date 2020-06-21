package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.RedisLock;
import com.honeybadgers.models.model.RedisTask;
import com.honeybadgers.models.model.GroupAncestorModel;
import com.honeybadgers.realtimescheduler.repository.GroupAncestorRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import com.honeybadgers.redis.config.RedisApplicationProperties;
import com.honeybadgers.redis.repository.LockRedisRepository;
import com.honeybadgers.redis.repository.TaskRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/data")        // only initialize if one of the given profiles is active
@Slf4j
@EntityScan(basePackages = {"com.honeybadgers.models", "com.honeybadgers.realtimescheduler.model"})
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

    @Autowired
    GroupAncestorRepository groupAncestorRepository;

    @Autowired
    RedisApplicationProperties redisApplicationProperties;


    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) {
        sender.sendTaskToDispatcher("assassasa");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/testTaskQueue/{task}")
    public ResponseEntity<?> tasksQueue(@PathVariable(value = "task") final String task){
        sender.sendTaskToTasksQueue(task);
        return ResponseEntity.ok("sent task " + task);
    }

    @PostMapping("/test/lock")
    public ResponseEntity<?> testLockRedis() {

        log.info("########################## REDIS PROP: " + redisApplicationProperties.toString());

        RedisLock lock = new RedisLock();
        lock.setId("GROUP:TestGroup9");

        log.warn("######################### TRYING TO FIND " + lock.toString());

        RedisLock get = lockRedisRepository.findById(lock.getId()).orElse(null);
        if(get == null)
            log.warn("######################### FAILURE!!");
        else
            log.warn("######################### SUCCESS: " + get.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/lock2")
    public ResponseEntity<?> testLockRedis2() {

        RedisLock lock = new RedisLock();
        lock.setId("GROUP:TestGroup12");
        //lock.setResume_date(LocalDateTime.now());
        lockRedisRepository.save(lock);

        log.warn("######################### SAVED " + lock.toString());

        RedisLock get = lockRedisRepository.findById(lock.getId()).orElse(null);
        if(get == null)
            log.warn("######################### FAILURE!!");
        else
            log.warn("######################### SUCCESS: " + get.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/schaub")
    public ResponseEntity<?> testPrioRedis() {
        log.warn("################## BEFORE GETALL");
        List<RedisTask> getAll = schedulerService.getAllRedisTasksAndSort();
        log.info("################## getAll: " + (getAll != null ? getAll.size() : null));
        Iterable<RedisTask> tasks = taskRedisRepository.findAll();
        List<RedisTask> tasksList = new ArrayList<>();
        tasks.forEach(tasksList::add);
        log.info("################## findAll: " + (tasksList != null ? tasksList.size() : null));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/ancestor/{groupId}")
    public ResponseEntity<?> testAncestorQuery(@PathVariable(value = "groupId") final String groupId) {
        log.warn("################## BEFORE QUERY");

        GroupAncestorModel ancestorModel = groupAncestorRepository.getAllAncestorIdsFromGroup(groupId).orElse(null);
        if(ancestorModel == null) {
            log.info("QUERY RETURNED NULL -> FAILURE/GROUPID INVALID");
            return ResponseEntity.badRequest().body("QUERY RETURNED NULL -> FAILURE/GROUPID INVALID");
        }

        log.info("################## QUERY RET: " + ancestorModel.toString());

        return ResponseEntity.ok(ancestorModel);
    }

    @PostMapping("/test/capacity")
    public ResponseEntity<?> testRedisCapacity() {

        RedisLock capacity = lockRedisRepository.findById("DISPATCHER_CAPACITY_ID_42").orElse(null);
        if(capacity == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
