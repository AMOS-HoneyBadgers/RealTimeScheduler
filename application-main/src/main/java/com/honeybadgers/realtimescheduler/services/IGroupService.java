package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.Group;

import java.util.List;

public interface IGroupService {

    List<Group> getAllGroups();

    Group getGroupById(String groupId);

    void uploadGroup(Group grp);

    void deleteGroup(String id);
}
