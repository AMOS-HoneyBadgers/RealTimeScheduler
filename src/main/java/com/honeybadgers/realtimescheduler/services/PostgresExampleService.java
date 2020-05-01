package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile({"!redis"})
@Slf4j
public class PostgresExampleService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    public void printMaxPrio() {
        List<Task> allByMax = taskPostgresRepository.getAllByMaxPrio(3);
        log.info(String.valueOf(allByMax.stream().map(task -> task.getTaskPriority()).collect(Collectors.toList())));
    }

    public void printMinPrio() {
        List<Task> allByMin = taskPostgresRepository.getAllByMinPrio(3);
        log.info(String.valueOf(allByMin.stream().map(task -> task.getTaskPriority()).collect(Collectors.toList())));
    }
}
