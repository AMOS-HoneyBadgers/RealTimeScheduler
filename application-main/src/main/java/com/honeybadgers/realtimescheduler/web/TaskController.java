package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.job.TestJob1;
import com.honeybadgers.realtimescheduler.model.*;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.quartz.DateBuilder.futureDate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/data")        // only initialize if one of the given profiles is active
@Slf4j
public class TaskController {

    @Autowired
    ITaskService taskService;

    @Autowired
    IGroupService groupService;

    @Autowired
    Scheduler scheduler;

    @GetMapping("/task")
    public List<Task> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value="/task")
    public ResponseEntity<?> uploadTask(@Valid @RequestBody TaskRestModel task) {

        Task newTask = new Task();
        newTask.setId( task.getId() );
        newTask.setGroup(groupService.getGroupById(task.getGroupId()));
        newTask.setEarliestStart(new Timestamp(task.getEarliestStart()));
        newTask.setLatestStart(new Timestamp(task.getLatestStart()));
        newTask.setModeEnum(ModeEnum.getFromString( task.getModeEnum() ));
        newTask.setTypeFlagEnum( TypeFlagEnum.getFromString( task.getTypeFlagEnum() ) );
        newTask.setForce( task.getForce() );
        newTask.setIndexNumber( task.getIndexNumber() );
        newTask.setPriority( task.getPriority() );
        newTask.setWorkingDays( task.getWorkingDays() );
        newTask.setParallelismDegree( task.getParallelismDegree() );
        newTask.setMetaData( task.getMetaData() );
        newTask.setMaxFailures(task.getMaxFailures());

        this.taskService.uploadTask(newTask);
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
    public ResponseEntity<?> uploadGroup(@Valid @RequestBody GroupRestModel grp) {

        Group newGroup = new Group();
        newGroup.setId( grp.getId() );
        newGroup.setMaxFailures( grp.getMaxFailures() );
        newGroup.setModeEnum( ModeEnum.getFromString( grp.getModeEnum() ) );
        newGroup.setTypeFlagEnum( TypeFlagEnum.getFromString( grp.getTypeFlagEnum() ) );
        newGroup.setPriority( grp.getPriority() );
        newGroup.setParentGroup(groupService.getGroupById(grp.getParentGroupId()));


        this.groupService.uploadGroup(newGroup);
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
    public ResponseEntity<?> create(@PathVariable(value = "priority") final String priority) throws SchedulerException {

        Date startTime = futureDate(5, DateBuilder.IntervalUnit.SECOND);

        for (int i = 0; i < 10; i++) {
            JobDetail jd = JobBuilder.newJob(TestJob1.class)
                    .withIdentity(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                    .usingJobData("id", Integer.toString(i))
                    .storeDurably(true)
                    .build();

            Trigger tg = TriggerBuilder.newTrigger()
                    .withIdentity(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                    .startAt(startTime)
                    .withPriority(i)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .build();
            scheduler.scheduleJob(jd, tg);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/testrepeat")
    public ResponseEntity<?> createRepeatJob() throws SchedulerException {

        JobDetail jd = JobBuilder.newJob(TestJob1.class)
                .withIdentity(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .usingJobData("id", UUID.randomUUID().toString())
                .storeDurably(true)
                .build();

        Trigger tg = TriggerBuilder.newTrigger()
                .withIdentity(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .startNow()
                .withPriority(1)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatForever()
                        .withIntervalInSeconds(5))
                .build();

        scheduler.scheduleJob(jd, tg);

        return ResponseEntity.ok().build();
    }


}
