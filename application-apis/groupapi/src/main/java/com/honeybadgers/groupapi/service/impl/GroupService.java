package com.honeybadgers.groupapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.service.IGroupConvertUtils;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class GroupService implements IGroupService {

    static final Logger logger = LogManager.getLogger(GroupService.class);

    @Autowired
    GroupService _self;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ICommunication sender;

    @Autowired
    @Qualifier("groupConvertUtils")
    IGroupConvertUtils convertUtils;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Value("${com.honeybadgers.transaction.max-retry-sleep:500}")
    int maxTransactionRetrySleep;

    @Value("${com.honeybadgers.transaction.max-retry-count:3}")
    int maxTransactionRetryCount;

    @Override
    public Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount){
            try{
                return _self.createGroupInternal(restModel);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Group " + restModel.getId() + " transaction exception while creating group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Group createGroupInternal(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException {

        Group checkGroup = groupRepository.findById(restModel.getId()).orElse(null);
        if (checkGroup != null) {
            throw new JpaException("Primary or unique constraint failed!");
        }
        if (restModel.getParentId() != null) {
            List<Task> taskChildren = taskRepository.findAllByGroupId(restModel.getParentId());
            if (!taskChildren.isEmpty())
                throw new CreationException(
                        "Parent group has tasks as children: " +
                                taskChildren.stream().map(Task::getId).collect(Collectors.joining(", ")) +
                                " -> aborting!"
                );
        }

        Group newGroup = convertUtils.groupRestToJpa(restModel);

        try {
            groupRepository.save(newGroup);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolation while trying to add new Group: \n" + e.getMessage());
            // exception has no message (should not happen but just in case)
            throw new JpaException("DataIntegrityViolation on save new group!");
        }
        return newGroup;
    }

    @Override
    public Group updateGroup(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException, NoSuchElementException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount){
            try{
                return _self.updateGroupInternal(groupId, restModel);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Group " + restModel.getId() + " transaction exception while updating group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Group updateGroupInternal(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException, NoSuchElementException {
        Group targetGroup = groupRepository.findById(groupId).orElse(null);

        if (targetGroup == null) {
            throw new NoSuchElementException("Group does not exist");
        }

        // prevent that parentGroup has tasks (in case the parent group gets changed)
        if (restModel.getParentId() != null && (targetGroup.getParentGroup() != null && restModel.getParentId().compareToIgnoreCase(targetGroup.getParentGroup().getId()) != 0)) {
            List<Task> taskChildren = taskRepository.findAllByGroupId(restModel.getParentId());
            if (!taskChildren.isEmpty())
                throw new JpaException(
                        "Parent group has tasks as children: " +
                                taskChildren.stream().map(Task::getId).collect(Collectors.joining(", ")) +
                                " -> aborting!"
                );
        }

        // prevent changing of id
        restModel.setId(groupId);

        // just use same conversion as for create (does not matter due to "replacing" object with same id)
        targetGroup = convertUtils.groupRestToJpa(restModel);

        try {
            groupRepository.save(targetGroup);
            sender.sendTaskToTasksQueue(scheduler_trigger);
        } catch (DataIntegrityViolationException e) {
            // exception has no message (should not happen but just in case)
            logger.error("DataIntegrityViolation on group update!");
            logger.error(Arrays.deepToString(e.getStackTrace()));
            throw new JpaException("DataIntegrityViolation on updating group!");
        }
        return targetGroup;
    }

    @Override
    public void sendGroupToTaskEventQueue(String groupId) {
        sender.sendGroupToTasksQueue(groupId);
    }

    @Override
    public List<Group> getAllGroups() throws InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                return groupRepository.findAll();
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while getting all groups. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public Group getGroupById(String groupId) throws NoSuchElementException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                Group group = groupRepository.findById(groupId).orElse(null);
                if(group == null)
                    throw new NoSuchElementException("Group with groupId " + groupId + " not found!");

                return group;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Group " + groupId + " transaction exception while getting group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public Group deleteGroup(String groupId) throws NoSuchElementException, JpaException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                Group group = groupRepository.deleteByIdCustomQuery(groupId).orElse(null);
                if (group == null)
                    throw new NoSuchElementException("Group with groupId " + groupId + " not found!");

                return group;
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage() != null) {
                    if(e.getMessage().contains("constraint [group_fk]")) {
                        logger.error("Group " + groupId + " delete failed due to foreign key: " + e.getMessage());
                        throw new JpaException("Group deletion failed due to being referenced by task!");
                    }
                }
                throw e;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Group " + groupId + " transaction exception while getting group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }
}
