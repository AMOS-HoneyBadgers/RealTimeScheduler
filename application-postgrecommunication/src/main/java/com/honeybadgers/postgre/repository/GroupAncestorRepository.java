package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.jpa.GroupAncestorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupAncestorRepository extends JpaRepository<GroupAncestorModel, String> {

    /**
     * Returns a custom model, which contains the id of the group you gave as input to this query
     * and a string array containing the ids of all ancestors of the group with the input groupId (RECURSIVE!)
     * Note: do not ask me how it works here is the wiki: https://wiki.postgresql.org/wiki/Getting_list_of_all_children_from_adjacency_tree
     * Note2: The \\ before the : are required here, BUT not if entered into, e.g., pgadmin, to prevent syntax error (due to : indicating an parameter)
     * @param groupId groupId you want to have all ancestors of
     * @return GroupAncestorModel containing id of wanted group and String[] of the ids of all ancestors
     */
    @Query(value = "WITH RECURSIVE tree AS (SELECT id, ARRAY[]\\:\\:CHARACTER VARYING[] AS ancestors FROM public.group WHERE parent_id IS NULL UNION ALL SELECT g.id, t.ancestors || g.parent_id FROM public.group as g, tree as t WHERE g.parent_id = t.id) SELECT id, ancestors FROM tree WHERE id=?1", nativeQuery = true)
    Optional<GroupAncestorModel> getAllAncestorIdsFromGroup(String groupId);
}
