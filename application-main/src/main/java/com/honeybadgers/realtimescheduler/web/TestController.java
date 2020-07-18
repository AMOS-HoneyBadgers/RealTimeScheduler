package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.GroupAncestorModel;
import com.honeybadgers.postgre.repository.GroupAncestorRepository;
import com.honeybadgers.postgre.repository.PausedRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    PausedRepository pausedRepository;


    @Autowired
    TaskRepository taskRepository;

    @Autowired
    GroupAncestorRepository groupAncestorRepository;


    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) {
        //sender.sendTaskToDispatcher("assassasa");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/testTaskQueue/{task}")
    public ResponseEntity<?> tasksQueue(@PathVariable(value = "task") final String task) {
        sender.sendTaskToTasksQueue(task);
        return ResponseEntity.ok("sent task " + task);
    }

    @PostMapping("/test/ancestor/{groupId}")
    public ResponseEntity<?> testAncestorQuery(@PathVariable(value = "groupId") final String groupId) {
        log.warn("################## BEFORE QUERY");

        GroupAncestorModel ancestorModel = groupAncestorRepository.getAllAncestorIdsFromGroup(groupId).orElse(null);
        if (ancestorModel == null) {
            log.info("QUERY RETURNED NULL -> FAILURE/GROUPID INVALID");
            return ResponseEntity.badRequest().body("QUERY RETURNED NULL -> FAILURE/GROUPID INVALID");
        }

        log.info("################## QUERY RET: " + ancestorModel.toString());

        return ResponseEntity.ok(ancestorModel);
    }
}
