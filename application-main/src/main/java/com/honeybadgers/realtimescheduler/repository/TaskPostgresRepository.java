package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPostgresRepository extends JpaRepository<Task, String> {
    Optional<Task> findById(String id);

    List<Task> findAllByPriorityEqualsAndIdNot(int priority, String id);

    /**
     * Returns all Tasks which have the given priority and status='Scheduled' ordered by deadline DESC WITH NULLS ON TOP
     * @param priority priority the tasks have to have
     * @return List of all scheduled tasks ordered by deadline DESC NULL FIRST and then by type_flag ASC
     */
    @Query(value = "SELECT * FROM task WHERE priority=?1 AND (status='Scheduled' OR id=?2) ORDER BY deadline DESC NULLS FIRST, type_flag ASC", nativeQuery = true)
    List<Task> findAllScheduledTasksWithSamePrio(int priority, String ownId);

    @Query(value = "SELECT * FROM task WHERE status='Dispatched' AND group_id=?1", nativeQuery = true)
    List<Task> findAllDispatchedTasks(String groupId);
}
