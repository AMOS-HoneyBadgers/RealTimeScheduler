package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.jpa.TaskPostgresRepository;
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
@Profile({"postgre"})
@Slf4j
public class TaskController {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @GetMapping("/")
    public List<Task> getAllTasks() {
        return taskPostgresRepository.findAll();
    }
}
