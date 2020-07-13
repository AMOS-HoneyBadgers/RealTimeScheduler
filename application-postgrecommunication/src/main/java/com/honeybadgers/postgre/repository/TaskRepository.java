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
     * Deletes the task with the given id
     * @param id id of task wanted to be deleted
     * @return Optional of deleted task or empty Optional if task with id not found
     */
    @Query(value = "DELETE FROM task WHERE id=?1 RETURNING *", nativeQuery = true)
    Optional<Task> deleteByIdCustomQuery(String id);

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
     * Query selects all tasks, which are in status 'Scheduled' and whose working_days are enabled at the given index
     * as well as the current time is within one of the time frames defined in active_times
     * @param postgresWorkingDayIndex index for current day WARNING: ARRAYS IN POSTGRES START AT 1!!!!
     * @return list of tasks, that are allowed to be dispatched (concerning their active_times and working_days)
     */
    @Query(value =
            "WITH RECURSIVE group_tree AS ( " +
            "    SELECT id, " +
            "    ARRAY[]\\:\\:CHARACTER VARYING[] || id AS ancestors, " +
            "    ARRAY[[]]\\:\\:INTEGER[][] || ARRAY[working_days] as days, " +
            "    ARRAY[]\\:\\:JSONB[] || active_times as times " +
            "    FROM public.group " +
            "    WHERE parent_id IS NULL " +
            "    " +
            "    UNION ALL " +
            "    " +
            "    SELECT g.id, " +
            "    g.id || t.ancestors, " +
            "    ARRAY[g.working_days] || t.days, " +
            "    g.active_times || t.times " +
            "    FROM public.group as g, group_tree as t " +
            "    WHERE g.parent_id = t.id " +
            "), wrapped AS ( " +
            "    SELECT t.id, " +
            "    unnest((array_remove((t.active_times || gt.times), '[]'))[1\\:1]) as unnested_active_times, " +
            "    reduce_dim((case when (t.working_days IS NULL AND gt.days = '{}') then ARRAY[ARRAY[1,1,1,1,1,1,1]]\\:\\:integer[][] else (ARRAY[t.working_days] || gt.days)[1\\:1] end)) as first_working_days " +
            "    FROM task t JOIN group_tree gt ON t.group_id=gt.id " +
            "), first_not_null_from_tree AS ( " +
            "    SELECT t.id, " +
            "    json_elements.value as first_active_times, " +
            "    w.first_working_days " +
            "    FROM task t JOIN wrapped w ON t.id=w.id LEFT JOIN jsonb_array_elements(w.unnested_active_times) json_elements ON true " +
            ") " +
            "SELECT DISTINCT t.* " +
            "FROM task t " +
            "LEFT JOIN first_not_null_from_tree c ON t.id=c.id " +
            "WHERE t.status='Scheduled' " +
            "AND c.first_working_days[?1]=1 " + // not neccessary to check on null due to default case in wrapped
            "AND (case when c.first_active_times IS NULL then true else TO_TIMESTAMP(c.first_active_times->>'to', 'HH24:MI:SS')\\:\\:TIME >= CURRENT_TIME end) " +
            "AND (case when c.first_active_times IS NULL then true else TO_TIMESTAMP(c.first_active_times->>'from', 'HH24:MI:SS')\\:\\:TIME <= CURRENT_TIME end)",
            nativeQuery = true)
    List<Task> getTasksToBeDispatched(int postgresWorkingDayIndex);
}
