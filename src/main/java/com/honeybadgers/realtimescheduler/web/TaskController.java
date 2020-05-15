package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/tasks")        // only initialize if one of the given profiles is active
@Slf4j
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @PostMapping("/post")
    public ResponseEntity<?> uploadTask(@Valid @RequestBody Task task) {
        if(this.taskService.uploadTask(task))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }
}
