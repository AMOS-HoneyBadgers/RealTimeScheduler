package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.GroupModelActiveTimes;
import com.honeybadgers.models.ActiveTimes;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public interface IGroupConvertUtils extends IConvertUtils {

    List<ActiveTimes> activeTimesRestToJpa(List<GroupModelActiveTimes> modelActiveTimes);

    List<GroupModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes);

    Group groupRestToJpa(GroupModel groupModel) throws UnknownEnumException, NoSuchElementException;

    GroupModel groupJpaToRest(Group group);
}
