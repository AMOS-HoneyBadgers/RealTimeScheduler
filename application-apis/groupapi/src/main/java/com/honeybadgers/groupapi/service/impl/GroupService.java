package com.honeybadgers.groupapi.service.impl;

import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.GroupModelActiveTimes;
import com.honeybadgers.groupapi.repository.GroupRepository;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.stream.Collectors;


@Service
public class GroupService implements IGroupService {

    static final Logger logger = LogManager.getLogger(GroupService.class);

    @Autowired
    GroupRepository groupRepository;

    @Override
    public Group createGroup(GroupModel restModel) throws JpaException, UnknownEnumException {
        Group newGroup = new Group();

        newGroup.setId(restModel.getId());
        newGroup.setParentGroup(groupRepository.findById(restModel.getParentId()).orElse(null));

        // convert List<TaskModelActiveTimes> to List<ActiveTimes>
        newGroup.setActiveTimeFrames(restModel.getActiveTimes().stream().map(GroupModelActiveTimes::getAsJpaModel).collect(Collectors.toList()));
        // convert List<Boolean> to int[]
        if(restModel.getWorkingDays() != null) {
            newGroup.setWorkingDays(restModel.getWorkingDays().stream().mapToInt(value -> {
                if (value == null)
                    return 1;
                // convert boolean to int
                return (value ? 1 : 0);
            }).toArray());
        } else {
            newGroup.setWorkingDays(new int[]{1,1,1,1,1,1,1});
        }

        // map OffsetDateTime to Timestamp
        if(restModel.getDeadline() != null)
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
            if(e.getMessage() != null) {
                logger.error("DataIntegrityViolation while trying to add new Group: \n" + e.getMessage());
                if(e.getMessage().contains("primary")) {
                    throw new JpaException("Primary or unique constraint failed!");
                } else {
                    throw new JpaException(e.getMessage());
                }
            } else {
                // exception has no message (should not happen but just in case)
                logger.error("DataIntegrityViolation on save new group!");
                logger.error(e.getStackTrace());
                throw new JpaException("DataIntegrityViolation on save new group!");
            }
        }

        return newGroup;
    }
}
