package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.model.jpa.Group;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import org.springframework.stereotype.Service;

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
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Updates an existing Group in the Database.
     * @param groupId Group ID which Group is to be changed.
     * @param restModel Rest Group Model with new changes.
     * @return updated JPA Group Object.
     * @throws JpaException Parent Group has already Tasks assigned to it. Group can either have Tasks or Groups.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws NoSuchElementException Group with grouid does not exist.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Group updateGroup(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Send a Group to the Scheduling Queue.
     * @param groupId
     */
    void sendGroupToTaskEventQueue(String groupId);

    /**
     * Returns all Groups from Database.
     * @return List of all Groups.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    List<Group> getAllGroups() throws InterruptedException, TransactionRetriesExceeded;

    /**
     * Returns single Group from Database..
     * @param groupId id of specified Group.
     * @return Group with specified id.
     * @throws NoSuchElementException Group with specified id does not exist.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Group getGroupById(String groupId) throws NoSuchElementException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Delete a Group from Database.
     * @param groupId id of specified Group.
     * @return deleted Group.
     * @throws NoSuchElementException Group with specified id does not exist.
     * @throws JpaException Group could not be deleted due to foreign key
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Group deleteGroup(String groupId) throws NoSuchElementException, JpaException, InterruptedException, TransactionRetriesExceeded;
}
