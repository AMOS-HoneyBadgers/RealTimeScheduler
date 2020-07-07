package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.GroupModelActiveTimes;
import com.honeybadgers.models.model.ActiveTimes;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.UnknownEnumException;
import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public interface IGroupConvertUtils extends IConvertUtils {

    /**
     * Convert ActiveTimes Objects from Rest Model to JPA Model.
     * @param modelActiveTimes List of GroupModelActiveTimes Objects.
     * @return List of ActiveTimes.
     */
    List<ActiveTimes> activeTimesRestToJpa(List<GroupModelActiveTimes> modelActiveTimes);

    /**
     * Convert ActiveTimes Objects from JPA Model to Rest Model.
     * @param activeTimes List of ActiveTimes.
     * @return List of GroupModelActiveTimes.
     */
    List<GroupModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes);

    /**
     * Convert a Group from Rest Model to JPA Model.
     * @param groupModel GroupModel received via Rest.
     * @return Group Object which can be stored in the Database.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws NoSuchElementException Parent Group does not exist.
     */
    Group groupRestToJpa(GroupModel groupModel) throws UnknownEnumException, NoSuchElementException;

    /**
     * Convert a Group from JPA Model to Rest Model.
     * @param group JPA Group Model.
     * @return Group Object which can be sent via Rest.
     */
    GroupModel groupJpaToRest(Group group);
}
