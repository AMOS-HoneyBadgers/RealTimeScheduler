package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"postgre"})
public interface TaskPostgresRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByTaskPriority(Integer taskPriority);

    Optional<Task> findById(Long id);

    @Query(value = "SELECT * FROM task WHERE priority <= :maxPrio", nativeQuery = true)
    List<Task> getAllByMaxPrio(@Param("maxPrio") int maxPrio);

    @Query(value = "SELECT * FROM task WHERE priority >= ?1", nativeQuery = true)
    List<Task> getAllByMinPrio(int minPrio);
}
