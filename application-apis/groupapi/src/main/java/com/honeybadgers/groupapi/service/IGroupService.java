package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.UnknownEnumException;
import org.springframework.stereotype.Service;

@Service
public interface IGroupService {

    Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException;

    Group updateGroup(String group_id, GroupModel restModel) throws JpaException, UnknownEnumException;

    void sendGroupToTaskEventQueue(String groupId);
}
