package com.honeybadgers.taskapi.repository;

import com.honeybadgers.models.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    @Query(value = "SELECT * FROM public.\"group\" WHERE parent_id=?1", nativeQuery = true)
    List<Group> findAllByParentGroupId(String parentId);
}
