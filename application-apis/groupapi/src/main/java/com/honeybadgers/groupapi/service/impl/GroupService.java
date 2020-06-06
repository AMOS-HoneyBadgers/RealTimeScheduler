package com.honeybadgers.groupapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.repository.GroupRepository;
import com.honeybadgers.groupapi.repository.TaskRepository;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class GroupService implements IGroupService {

    static final Logger logger = LogManager.getLogger(GroupService.class);

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ICommunication sender;

    @Override
    public Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException, CreationException {
        Group newGroup = new Group();

        Group checkGroup = groupRepository.findById(restModel.getId()).orElse(null);
        if (checkGroup != null) {
            throw new JpaException("Primary or unique constraint failed!");
        }
        newGroup.setId(restModel.getId());
        Group parent = groupRepository.findById(restModel.getParentId()).orElse(null);
        if (parent != null) {
            List<Task> taskChildren = taskRepository.findAllByGroupId(parent.getId());
            if (!taskChildren.isEmpty())
                // TODO perhaps move group of task or sth similar
                throw new CreationException(
                        "Parent group has tasks as children: " +
                                taskChildren.stream().map(Task::getId).collect(Collectors.joining(", ")) +
                                " -> aborting!"
                );
        }
        newGroup.setParentGroup(parent);

        // convert List<TaskModelActiveTimes> to List<ActiveTimes>
        if (restModel.getActiveTimes() != null) {
            newGroup.setActiveTimeFrames(restModel.getActiveTimes().stream().map(groupModelActiveTimes -> groupModelActiveTimes.getAsJpaModel()).collect(Collectors.toList()));
        } else {
            ActiveTimes[] activeTimes = new ActiveTimes[]{
                    new ActiveTimes(new Time(9, 0, 0), new Time(12, 0, 0))
            };
            newGroup.setActiveTimeFrames(Arrays.asList(activeTimes));
        }

        // convert List<Boolean> to int[]
        if (restModel.getWorkingDays() != null) {
            newGroup.setWorkingDays(restModel.getWorkingDays().stream().mapToInt(value -> {
                if (value == null)
                    return 1;
                // convert boolean to int
                return (value ? 1 : 0);
            }).toArray());
        } else {
            newGroup.setWorkingDays(new int[]{1, 1, 1, 1, 1, 1, 1});
        }

        // map OffsetDateTime to Timestamp
        if (restModel.getDeadline() != null)
            newGroup.setDeadline(Timestamp.valueOf(restModel.getDeadline().toLocalDateTime()));

        newGroup.setModeEnum(ModeEnum.getFromString(restModel.getMode().getValue()));
        newGroup.setTypeFlagEnum(TypeFlagEnum.getFromString(restModel.getTypeFlag().getValue()));

        newGroup.setPriority(restModel.getPriority());

        newGroup.setPaused(restModel.getPaused());
        newGroup.setParallelismDegree(restModel.getParallelismDegree());
        newGroup.setLastIndexNumber(restModel.getLastIndexNumber());

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
    public Group updateGroup(String group_id, GroupModel restModel) throws JpaException, UnknownEnumException, NoSuchElementException {
        Group targetGroup = groupRepository.findById(group_id).orElse(null);
        Group parentgroup = null;

        if (targetGroup == null) {
            throw new NoSuchElementException("Group does not exist");
        }

        if (restModel.getParentId() == null) {
            targetGroup.setParentGroup(null);
        } else {
            parentgroup = groupRepository.findById(restModel.getParentId()).orElse(null);
            if (parentgroup == null) {
                throw new NoSuchElementException("Parent Group does not exist");
            } else {
                targetGroup.setParentGroup(parentgroup);
            }
        }

        // convert List<TaskModelActiveTimes> to List<ActiveTimes>
        if (restModel.getActiveTimes() != null) {
            targetGroup.setActiveTimeFrames(restModel.getActiveTimes().stream().map(groupModelActiveTimes -> groupModelActiveTimes.getAsJpaModel()).collect(Collectors.toList()));
        } else {
            targetGroup.setActiveTimeFrames(null);
        }

        if (restModel.getWorkingDays() != null) {
            targetGroup.setWorkingDays(restModel.getWorkingDays().stream().mapToInt(value -> {
                if (value == null)
                    return 1;
                // convert boolean to int
                return (value ? 1 : 0);
            }).toArray());
        } else {
            targetGroup.setWorkingDays(new int[]{1, 1, 1, 1, 1, 1, 1});
        }

        // map OffsetDateTime to Timestamp
        if (restModel.getDeadline() != null) {
            targetGroup.setDeadline(Timestamp.valueOf(restModel.getDeadline().toLocalDateTime()));
        } else {
            targetGroup.setDeadline(null);
        }

        targetGroup.setModeEnum(ModeEnum.getFromString(restModel.getMode().getValue()));
        targetGroup.setTypeFlagEnum(TypeFlagEnum.getFromString(restModel.getTypeFlag().getValue()));

        targetGroup.setPriority(restModel.getPriority());

        targetGroup.setPaused(restModel.getPaused());
        targetGroup.setParallelismDegree(restModel.getParallelismDegree());
        targetGroup.setLastIndexNumber(restModel.getLastIndexNumber());

        try {
            groupRepository.save(targetGroup);
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


}
