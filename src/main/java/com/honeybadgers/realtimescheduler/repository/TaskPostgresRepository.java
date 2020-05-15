package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.model.Task;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPostgresRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(Long id);
}
