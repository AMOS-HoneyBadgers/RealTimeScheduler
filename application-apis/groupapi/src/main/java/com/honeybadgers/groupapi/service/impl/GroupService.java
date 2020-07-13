package com.honeybadgers.groupapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.service.IGroupConvertUtils;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class GroupService implements IGroupService {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ICommunication sender;
    @Autowired
    IGroupConvertUtils convertUtils;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    static final Logger logger = LogManager.getLogger(GroupService.class);

    @Override
    public Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException {

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
    public Group updateGroup(String groupId, GroupModel restModel) throws JpaException, UnknownEnumException, NoSuchElementException {
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
            if (e.getMessage() != null) {
                logger.error("DataIntegrityViolation while trying to add new Group: \n" + e.getMessage());
                if (e.getMessage().contains("primary")) {
                    throw new JpaException("Primary or unique constraint failed!");
                } else {
                    throw new JpaException(e.getMessage());
                }
            } else {
                // exception has no message (should not happen but just in case)
                logger.error("DataIntegrityViolation on group update!");
                logger.error(e.getStackTrace());
                throw new JpaException("DataIntegrityViolation on save new group!");
            }
        }
        return targetGroup;
    }

    @Override
    public void sendGroupToTaskEventQueue(String groupId) {
        sender.sendGroupToTasksQueue(groupId);
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group getGroupById(String groupId) throws NoSuchElementException {
        Group group = groupRepository.findById(groupId).orElse(null);
        if(group == null)
            throw new NoSuchElementException("Group with groupId " + groupId + " not found!");

        return group;
    }

    @Override
    @Transactional
    public Group deleteGroup(String groupId) throws NoSuchElementException {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null)
            throw new NoSuchElementException("Group with groupId " + groupId + " not found!");

        groupRepository.deleteById(groupId);
        return group;
    }
}
