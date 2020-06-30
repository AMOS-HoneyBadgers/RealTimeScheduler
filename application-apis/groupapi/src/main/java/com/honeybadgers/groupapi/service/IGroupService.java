package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.UnknownEnumException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public interface IGroupService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group updateGroup(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException;

    void sendGroupToTaskEventQueue(String groupId);

    List<Group> getAllGroups();

    Group getGroupById(String groupId) throws NoSuchElementException;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group deleteGroup(String groupId) throws NoSuchElementException;
}
