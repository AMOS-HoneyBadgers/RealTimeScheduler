package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.models.*;
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

    //@Autowired
    //ICommunication sender;

    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) {

        Task task = new Task();
        task.setPriority(Integer.parseInt(priority));
        task.setId(UUID.randomUUID().toString());
        task.setDeadline(new Timestamp(System.currentTimeMillis()+100000));


        taskService.scheduleTask(task);
        //sender.sendTaskToDispatcher(task.getId());
        return ResponseEntity.ok().build();
    }
    /*@GetMapping("/getAllRedisTasks")
    public ResponseEntity<?> getAllRedisTasks(){
        System.out.println("instanzcheck");
        return ResponseEntity.ok(taskService.getAllRedisTasks());
    }*/

    @PostMapping("/testTaskQueue/{task}")
    public ResponseEntity<?> tasksQueue(@PathVariable(value = "task") final String task){
        //sender.sendTaskToTasksQueue(task);
        return ResponseEntity.ok("sent task " + task);
    }
}
