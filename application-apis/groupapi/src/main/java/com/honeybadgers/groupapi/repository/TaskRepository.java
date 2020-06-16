package com.honeybadgers.groupapi.repository;

import com.honeybadgers.models.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    @Query(value = "SELECT * FROM public.task WHERE group_id=?1", nativeQuery = true)
    List<Task> findAllByGroupId(String groupId);
}
