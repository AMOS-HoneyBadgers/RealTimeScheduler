package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public interface IGroupService {

    /**
     * Create a new Group and save it in a Database.
     * @param restModel Rest Group Model.
     * @return JPA Group Object created.
     * @throws JpaException Group ID already exists or Database action error.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws CreationException Parent Group has already Tasks assigned to it. Group can either have Tasks or Groups.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException;

    /**
     * Updates an existing Group in the Database.
     * @param groupId Group ID which Group is to be changed.
     * @param restModel Rest Group Model with new changes.
     * @return updated JPA Group Object.
     * @throws JpaException Parent Group has already Tasks assigned to it. Group can either have Tasks or Groups.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws NoSuchElementException Group with grouid does not exist.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group updateGroup(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException;

    /**
     * Send a Group to the Scheduling Queue.
     * @param groupId
     */
    void sendGroupToTaskEventQueue(String groupId);

    /**
     * Returns all Groups from Database.
     * @return List of all Groups.
     */
    List<Group> getAllGroups();

    /**
     * Returns single Group from Database..
     * @param groupId id of specified Group.
     * @return Group with specified id.
     * @throws NoSuchElementException Group with specified id does not exist.
     */
    Group getGroupById(String groupId) throws NoSuchElementException;

    /**
     * Delete a Group from Database.
     * @param groupId id of specified Group.
     * @return deleted Group.
     * @throws NoSuchElementException Group with specified id does not exist.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Group deleteGroup(String groupId) throws NoSuchElementException;
}
