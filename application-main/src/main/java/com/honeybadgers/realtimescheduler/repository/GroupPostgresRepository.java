package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.Group;
import com.honeybadgers.realtimescheduler.model.GroupAncestorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPostgresRepository extends JpaRepository<Group, String> {

    /**
     * Returns a custom model, which contains the id of the group you gave as input to this query
     * and a string array containing the ids of all ancestors of the group with the input groupId (RECURSIVE!)
     * Note: do not ask me how it works here is the wiki: https://wiki.postgresql.org/wiki/Getting_list_of_all_children_from_adjacency_tree
     * @param groupId groupId you want to have all ancestors of
     * @return GroupAncestorModel containing id of wanted group and String[] of the ids of all ancestors
     */
    @Query(value = "WITH RECURSIVE tree AS (SELECT id, ARRAY[]::CHARACTER VARYING[] AS ancestorsFROM public.group WHERE parent_id IS NULLUNION ALLSELECT g.id, t.ancestors || g.parent_idFROM public.group as g, tree as tWHERE g.parent_id = t.id) SELECT * FROM tree WHERE id=?1", nativeQuery = true)
    GroupAncestorModel getAllAncestorIdsFromGroup(String groupId);
}
