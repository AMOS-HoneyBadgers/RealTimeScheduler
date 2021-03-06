package com.honeybadgers.groupapi.service.impl;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.GroupModelActiveTimes;
import com.honeybadgers.groupapi.service.IGroupConvertUtils;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.model.jpa.ActiveTimes;
import com.honeybadgers.models.model.jpa.Group;
import com.honeybadgers.models.model.jpa.ModeEnum;
import com.honeybadgers.models.model.jpa.TypeFlagEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class GroupConvertUtils implements IGroupConvertUtils {

    @Autowired
    GroupRepository groupRepository;

    @Override
    public List<ActiveTimes> activeTimesRestToJpa(List<GroupModelActiveTimes> modelActiveTimes) {
        if(modelActiveTimes == null)
            return new ArrayList<>();
        return modelActiveTimes.stream().map(GroupModelActiveTimes::getAsJpaModel).collect(Collectors.toList());
    }

    @Override
    public List<GroupModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes) {
        if(activeTimes == null)
            return new ArrayList<>();
        return activeTimes.stream().map(GroupModelActiveTimes::new).collect(Collectors.toList());
    }

    @Override
    public Group groupRestToJpa(GroupModel groupModel) throws UnknownEnumException, NoSuchElementException {
        if(groupModel == null)
            return null;

        Group newGroup = new Group();
        newGroup.setId(groupModel.getId());
        Group parent = null;
        if(groupModel.getParentId() != null)
            parent = groupRepository.findById(groupModel.getParentId()).orElse(null);
        // check if parent was found if specified
        if(groupModel.getParentId() != null && parent == null)
            throw new NoSuchElementException("Parent Group does not exist");
        newGroup.setParentGroup(parent);

        // convert List<TaskModelActiveTimes> to List<ActiveTimes>
        newGroup.setActiveTimeFrames(activeTimesRestToJpa(groupModel.getActiveTimes()));

        // convert List<Boolean> to int[]
        newGroup.setWorkingDays(boolListToIntArray(groupModel.getWorkingDays()));

        // map OffsetDateTime to Timestamp
        newGroup.setDeadline(timestampRestToJpa(groupModel.getDeadline()));

        newGroup.setModeEnum(ModeEnum.getFromString(groupModel.getMode().getValue()));
        newGroup.setTypeFlagEnum(TypeFlagEnum.getFromString(groupModel.getTypeFlag().getValue()));

        if(groupModel.getPriority() != null)
            newGroup.setPriority(groupModel.getPriority());

        if(groupModel.getParallelismDegree() != null)
            newGroup.setParallelismDegree(groupModel.getParallelismDegree());
        else
            newGroup.setParallelismDegree(1);
        newGroup.setLastIndexNumber(groupModel.getLastIndexNumber());

        return newGroup;
    }

    @Override
    public GroupModel groupJpaToRest(Group group) {
        if(group == null)
            return null;
        GroupModel model = new GroupModel();
        model.setId(group.getId());
        if(group.getParentGroup() != null)
            model.setParentId(group.getParentGroup().getId());
        model.setActiveTimes(activeTimesJpaToRest(group.getActiveTimeFrames()));
        model.setDeadline(timestampJpaToRest(group.getDeadline()));
        model.setWorkingDays(intArrayToBoolList(group.getWorkingDays()));
        model.setLastIndexNumber(group.getLastIndexNumber());
        model.setMode(GroupModel.ModeEnum.fromValue(group.getModeEnum().toString().toLowerCase()));
        model.setTypeFlag(GroupModel.TypeFlagEnum.fromValue(group.getTypeFlagEnum().toString().toLowerCase()));
        model.setPriority(group.getPriority());
        model.setParallelismDegree(group.getParallelismDegree());
        return model;
    }
}
