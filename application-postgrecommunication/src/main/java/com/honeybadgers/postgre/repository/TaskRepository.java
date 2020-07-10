package com.honeybadgers.postgre.repository;


import com.honeybadgers.models.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    /**
     * Returns a List of all tasks with the same group.
     * @param groupId id of specified group.
     * @return List of tasks.
     */
    @Query(value = "SELECT * FROM public.task WHERE group_id=?1", nativeQuery = true)
    List<Task> findAllByGroupId(String groupId);

    Optional<Task> findById(String id);

    /**
     * Returns all Tasks which have the given priority and status='Scheduled' ordered by deadline DESC WITH NULLS ON TOP
     * @param priority priority the tasks have to have
     * @return List of all scheduled tasks ordered by deadline DESC NULL FIRST and then by type_flag ASC
     */
    @Query(value = "SELECT * FROM task WHERE priority=?1 AND (status='Scheduled' OR id=?2) ORDER BY deadline DESC NULLS FIRST, type_flag ASC", nativeQuery = true)
    List<Task> findAllScheduledTasksWithSamePrio(int priority, String ownId);

    /**
     * Returns all tasks that belong to the same group and have their status set to "Dispatched"
     * @param groupId if of specified group.
     * @return List of tasks with status "Dispatched".
     */
    @Query(value = "SELECT * FROM task WHERE status='Dispatched' AND group_id=?1", nativeQuery = true)
    List<Task> findAllDispatchedTasks(String groupId);

    /**
     * Gets all tasks with status "Scheduled" and sorts them by "total_priority" descending.
     * @return List of tasks with status "Scheduled".
     */
    @Query(value = "SELECT * FROM task WHERE status='Scheduled' ORDER BY total_priority DESC", nativeQuery = true)
    List<Task> findAllScheduledTasksSorted();

    /**
     * Returns all tasks with status "Waiting".
     * @return List of Tasks with status "Waiting".
     */
    @Query(value = "SELECT * FROM task WHERE status='Waiting'", nativeQuery = true)
    List<Task> findAllWaitingTasks();

    /**
     * Deletes all tasks whose status is 'Finished' and the timestamp of the last element in the history is at least n milliseconds old
     * @param nInMilliseconds milliseconds to be added to the timestamp of the last entry in the history for deletion check
     * @return all deleted tasks
     */
    @Query(value = "DELETE FROM task WHERE status='Finished' AND ((history->(jsonb_array_length(history)-1)->>'timestamp')::bigint + ?1) <= (EXTRACT(EPOCH FROM NOW() AT TIME ZONE 'UTC') * 1000)::bigint RETURNING *", nativeQuery = true)
    List<Task> deleteAllTasksFinishedSinceNMilliseconds(long nInMilliseconds);
}
