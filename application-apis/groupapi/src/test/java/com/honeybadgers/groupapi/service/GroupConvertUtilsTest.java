package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.GroupModelActiveTimes;
import com.honeybadgers.groupapi.service.impl.GroupConvertUtils;
import com.honeybadgers.models.model.ActiveTimes;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.postgre.repository.GroupRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupConvertUtils.class)
public class GroupConvertUtilsTest {

    @Autowired
    GroupConvertUtils convertUtils;

    @MockBean
    GroupRepository groupRepository;

    // TODO tests for groupModel conversion
    @Before
    public void setUp() {
        Group parentGroup = new Group();
        parentGroup.setId("parentGroup");
        parentGroup.setPriority(200);

        when(groupRepository.findById("parentGroup")).thenReturn(Optional.of(parentGroup));
    }

    @Test
    public void testActiveTimesRestToJpa() {
        List<ActiveTimes> activeTimes;
        GroupModelActiveTimes modelActiveTimes = new GroupModelActiveTimes();
        modelActiveTimes.setFrom(Time.valueOf(LocalTime.now()));
        modelActiveTimes.setTo(Time.valueOf(LocalTime.now()));
        List<GroupModelActiveTimes> groupModelActiveTimes = Collections.singletonList(modelActiveTimes);

        activeTimes = convertUtils.activeTimesRestToJpa(groupModelActiveTimes);

        assertNotNull(activeTimes);
        assertEquals(1, activeTimes.size());
        assertEquals(groupModelActiveTimes.get(0).getFrom(), activeTimes.get(0).getFrom());
        assertEquals(groupModelActiveTimes.get(0).getTo(), activeTimes.get(0).getTo());
    }

    @Test
    public void testActiveTimesRestToJpa_nullInput() {
        List<ActiveTimes> activeTimes;
        List<GroupModelActiveTimes> groupModelActiveTimes = null;

        activeTimes = convertUtils.activeTimesRestToJpa(groupModelActiveTimes);

        assertNotNull(activeTimes);
        assertEquals(0, activeTimes.size());
    }

    @Test
    public void testActiveTimesRestToJpa_emptyInput() {
        List<ActiveTimes> activeTimes;
        List<GroupModelActiveTimes> groupModelActiveTimes = new ArrayList<>();

        activeTimes = convertUtils.activeTimesRestToJpa(groupModelActiveTimes);

        assertNotNull(activeTimes);
        assertEquals(0, activeTimes.size());
    }

    @Test
    public void testActiveTimesJpaToRest() {
        ActiveTimes modelActiveTimes = new ActiveTimes();
        modelActiveTimes.setFrom(Time.valueOf(LocalTime.now()));
        modelActiveTimes.setTo(Time.valueOf(LocalTime.now()));
        List<ActiveTimes> activeTimes = Collections.singletonList(modelActiveTimes);
        List<GroupModelActiveTimes> groupModelActiveTimes;

        groupModelActiveTimes = convertUtils.activeTimesJpaToRest(activeTimes);

        assertNotNull(groupModelActiveTimes);
        assertEquals(1, groupModelActiveTimes.size());
        assertEquals(activeTimes.get(0).getFrom(), groupModelActiveTimes.get(0).getFrom());
        assertEquals(activeTimes.get(0).getTo(), groupModelActiveTimes.get(0).getTo());
    }

    @Test
    public void testActiveTimesJpaToRest_nullInput() {
        List<ActiveTimes> activeTimes = null;
        List<GroupModelActiveTimes> groupModelActiveTimes;

        groupModelActiveTimes = convertUtils.activeTimesJpaToRest(activeTimes);

        assertNotNull(groupModelActiveTimes);
        assertEquals(0, groupModelActiveTimes.size());
    }

    @Test
    public void testActiveTimesJpaToRest_emptyInput() {
        List<ActiveTimes> activeTimes = new ArrayList<>();
        List<GroupModelActiveTimes> groupModelActiveTimes;

        groupModelActiveTimes = convertUtils.activeTimesJpaToRest(activeTimes);

        assertNotNull(groupModelActiveTimes);
        assertEquals(0, groupModelActiveTimes.size());
    }

    @Test
    public void testGroupRestToJpa() throws UnknownEnumException {
        Group group;
        GroupModel model = new GroupModel();
        model.setId("test");

        group = convertUtils.groupRestToJpa(model);

        assertNotNull(group);
        assertEquals(model.getId(), group.getId());
    }

    @Test
    public void testGroupRestToJpa_nullInput() throws UnknownEnumException {
        Group group;
        GroupModel model = null;

        group = convertUtils.groupRestToJpa(model);

        assertNull(group);
    }

    @Test
    public void testGroupRestToJpa_notFound() throws UnknownEnumException {
        Group group;
        GroupModel model = new GroupModel();
        model.setId("test");
        model.setParentId("parent");

        when(groupRepository.findById(model.getParentId())).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> convertUtils.groupRestToJpa(model));
        assertNotNull(e);
    }

    @Test
    public void testGroupJpaToRest() throws UnknownEnumException {
        Group group = new Group();
        group.setId("test");
        GroupModel model;

        model = convertUtils.groupJpaToRest(group);

        assertNotNull(model);
        assertEquals(group.getId(), model.getId());
    }

    @Test
    public void testGroupJpaToRest_nullInput() throws UnknownEnumException {
        Group group = null;
        GroupModel model;

        model = convertUtils.groupJpaToRest(group);

        assertNull(model);
    }
}
