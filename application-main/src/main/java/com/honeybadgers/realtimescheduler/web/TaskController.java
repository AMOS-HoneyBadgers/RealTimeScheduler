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
import java.util.Arrays;
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

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value = "/task")
    public ResponseEntity<?> uploadTask(@Valid @RequestBody TaskRestModel task) {

        Task newTask = new Task();
        newTask.setId(task.getId());
        newTask.setGroup(groupService.getGroupById(task.getGroupId()));
        newTask.setActiveTimeFrames(task.getActiveTimes());
        if(task.getWorkingDays() == null) {
            newTask.setWorkingDays(new int[]{1,1,1,1,1,1,1});
        } else {
            newTask.setWorkingDays(Arrays.stream(task.getWorkingDays()).mapToInt(value -> {
                if (value == null)
                    return 1;
                return value;
            }).toArray());
        }
        TaskStatusEnum statusEnum = TaskStatusEnum.getFromString(task.getStatusEnum());
        if(statusEnum != null)
            newTask.setStatus(statusEnum);
        ModeEnum modeEnum = ModeEnum.getFromString(task.getModeEnum());
        if (modeEnum != null)
            newTask.setModeEnum(modeEnum);
        TypeFlagEnum typeFlagEnum = TypeFlagEnum.getFromString(task.getTypeFlagEnum());
        if(typeFlagEnum != null)
            newTask.setTypeFlagEnum(typeFlagEnum);
        if(task.getForce() != null)
            newTask.setForce(task.getForce());
        newTask.setIndexNumber(task.getIndexNumber());
        newTask.setPriority(task.getPriority());
        if(task.getDeadline() != null)
            newTask.setDeadline(new Timestamp(task.getDeadline()));
        newTask.setMetaData(task.getMetaData());
        if(task.getRetries() != null)
            newTask.setRetries(task.getRetries());
        if(task.getPaused() != null)
            newTask.setPaused(task.getPaused());

        this.taskService.uploadTask(newTask);
        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value = "/task")
    public ResponseEntity<?> updateTask(@Valid @RequestBody Task task) {
        this.taskService.uploadTask(task);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable(value = "id") final String id) {
        this.taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group")
    public List<Group> getAllGroups() {
        return this.groupService.getAllGroups();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value = "/group")
    public ResponseEntity<?> uploadGroup(@Valid @RequestBody GroupRestModel grp) {

        Group newGroup = new Group();
        newGroup.setId(grp.getId());
        newGroup.setActiveTimeFrames(grp.getActiveTimes());
        if(grp.getWorkingDays() == null) {
            newGroup.setWorkingDays(new int[]{1,1,1,1,1,1,1});
        } else {
            newGroup.setWorkingDays(Arrays.stream(grp.getWorkingDays()).mapToInt(value -> {
                if (value == null)
                    return 1;
                return value;
            }).toArray());
        }
        ModeEnum modeEnum = ModeEnum.getFromString(grp.getModeEnum());
        if (modeEnum != null)
            newGroup.setModeEnum(modeEnum);
        TypeFlagEnum typeFlagEnum = TypeFlagEnum.getFromString(grp.getTypeFlagEnum());
        if(typeFlagEnum != null)
            newGroup.setTypeFlagEnum(typeFlagEnum);
        newGroup.setPriority(grp.getPriority());
        if(grp.getPaused() != null)
            newGroup.setPaused(grp.getPaused());
        newGroup.setParallelismDegree(grp.getParallelismDegree());
        newGroup.setLastIndexNumber(grp.getLastIndexNumber());
        if(grp.getParentGroupId() != null)
            newGroup.setParentGroup(groupService.getGroupById(grp.getParentGroupId()));

        this.groupService.uploadGroup(newGroup);
        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, value = "/group")
    public ResponseEntity<?> updateGroup(@Valid @RequestBody Group grp) {
        this.groupService.uploadGroup(grp);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/group/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable(value = "id") final String id) {
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

    @GetMapping("/testtask/{priority}")
    public ResponseEntity<?> createTestTask(@PathVariable(value = "priority") final String priority) throws SchedulerException {

        Task task = new Task();
        task.setPriority(Integer.parseInt(priority));
        this.taskService.calculatePriority(task);
        this.taskService.scheduleTask(Integer.parseInt(priority));


        return ResponseEntity.ok().build();
    }


}
