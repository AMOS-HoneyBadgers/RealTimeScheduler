package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPostgresRepository extends JpaRepository<Task, String> {
    Optional<Task> findById(String id);
}
