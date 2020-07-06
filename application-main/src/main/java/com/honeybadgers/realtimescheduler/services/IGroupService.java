package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Group;

import java.util.List;

public interface IGroupService {

    /**
     * Gets all groups from the groupRepository
     * @return List with all groups
     */
    List<Group> getAllGroups();

    /**
     * Returns a single group from the groupRepository
     * @param groupId id of the group
     * @return groupModel
     */
    Group getGroupById(String groupId);

    /**
     * Updates the group Object in the groupRepository
     * @param grp groupModel
     */
    void uploadGroup(Group grp);

    /**
     * Deletes group from the groupRepository
     * @param id id of the group
     */
    void deleteGroup(String id);
}
