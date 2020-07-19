package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.jpa.Group;

import java.util.Optional;

public interface IGroupService {

    /**
     * Warning: not transaction save (no try catch etc)
     * Returns a single group from the groupRepository
     *
     * @param groupId id of the group
     * @return groupModel
     */
    Optional<Group> getGroupById(String groupId);
}
