package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Group;

import java.util.List;

public interface IGroupService {

    /**
     * Warning: not transaction save (no try catch etc)
     * Returns a single group from the groupRepository
     * @param groupId id of the group
     * @return groupModel
     */
    Group getGroupById(String groupId);
}
