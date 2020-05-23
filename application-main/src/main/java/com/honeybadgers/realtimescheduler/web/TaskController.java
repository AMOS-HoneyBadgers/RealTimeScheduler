package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.job.TestJob1;
import com.honeybadgers.realtimescheduler.model.Group;
import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.services.GroupService;
import com.honeybadgers.realtimescheduler.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/data")        // only initialize if one of the given profiles is active
@Slf4j
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    GroupService groupService;

    @Autowired
    Scheduler scheduler;

    @GetMapping("/task")
    public List<Task> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value="/task")
    public ResponseEntity<?> uploadTask(@Valid @RequestBody Task task) {
        this.taskService.uploadTask(task);
        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value="/task")
    public ResponseEntity<?> updateTask(@Valid @RequestBody Task task) {
        this.taskService.uploadTask(task);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value="/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable(value="id") final String id) {
        this.taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group")
    public List<Group> getAllGroups() {
        return this.groupService.getAllGroups();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value="/group")
    public ResponseEntity<?> uploadGroup(@Valid @RequestBody Group grp) {
        this.groupService.uploadGroup(grp);
        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value="/group")
    public ResponseEntity<?> updateGroup(@Valid @RequestBody Group grp) {
        this.groupService.uploadGroup(grp);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value="/group/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable(value="id") final String id) {
        this.groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/testCreate/{priority}")
    public ResponseEntity<?>create(@PathVariable(value="priority") final String priority) throws SchedulerException {
        String id = UUID.randomUUID().toString();
        JobDetail jd = JobBuilder.newJob(TestJob1.class)
                .withIdentity(id, "group1")
                .storeDurably(true)
                .build();

        Trigger tg = TriggerBuilder.newTrigger()
                .forJob(jd)
                .withIdentity(id, "group1")
                .startNow()
                .withPriority(Integer.parseInt(priority))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        scheduler.scheduleJob(jd, tg);

        return ResponseEntity.ok().build();
    }


}
