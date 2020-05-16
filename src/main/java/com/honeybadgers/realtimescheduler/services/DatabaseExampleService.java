package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
//@Profile("!redis")
@Slf4j
public class DatabaseExampleService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @Autowired
    TaskRedisRepository taskRedisRepository;

    public void printMaxPrio() {
        List<Task> allByMax = taskPostgresRepository.getAllByMaxPrio(3);
        log.info(String.valueOf(allByMax.stream().map(task -> task.getTaskPriority()).collect(Collectors.toList())));
    }

    public void printMinPrio() {
        List<Task> allByMin = taskPostgresRepository.getAllByMinPrio(3);
        log.info(String.valueOf(allByMin.stream().map(task -> task.getTaskPriority()).collect(Collectors.toList())));
    }

    public void testRedis() {
        Task newTask = new Task();
        newTask.setName("testRedis");
        newTask.setTaskPriority(-1);
        taskRedisRepository.save(newTask);

        List<Task> allTasks = StreamSupport.stream(taskRedisRepository.findAll().spliterator(), false).collect(Collectors.toList());
        log.info("SIZE: " + allTasks.size());
        log.info("Id: " + allTasks.get(0).getId() + " Name: " + allTasks.get(0).getName() + " Prio: " + allTasks.get(0).getTaskPriority() + " TS: " + allTasks.get(0).getSubmittimestamp());
    }
}
