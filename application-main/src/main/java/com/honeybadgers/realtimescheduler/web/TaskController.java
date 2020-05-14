package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.services.PostgresExampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/task")
@Profile({"postgre"})               // only initialize if one of the given profiles is active
@Slf4j
public class TaskController {

    @Autowired
    PostgresExampleService postgresExampleService;

    /*@GetMapping("/")
    public List<Task> getAllTasks() {
        return postgresExampleService.getAllTasks();
    }*/
}
