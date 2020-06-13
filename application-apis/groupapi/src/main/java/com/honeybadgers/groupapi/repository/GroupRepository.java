package com.honeybadgers.groupapi.repository;


import com.honeybadgers.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    /*@Query(value = "SELECT *, ARRAY(SELECT id FROM public.\"group\" WHERE parent_id=?1) as childs FROM public.\"group\" WHERE id=?1", nativeQuery = true)
    Optional<GROUP EXTENSION> findByIdWithChildrenList(String groupId);*/

    @Query(value = "SELECT * FROM public.\"group\" LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<Group> getAllGroupsByPage(int size, int offset);
}
