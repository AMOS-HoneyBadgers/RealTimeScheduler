package com.honeybadgers.realtimescheduler.services;


import com.honeybadgers.models.Group;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = GroupService.class)
class GroupServiceTest {

    @Mock//@MockBean
    GroupPostgresRepository groupPostgresRepository;
    @InjectMocks//@Autowired
    GroupService groupService;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        for (int i = 1; i < 4; i++) {
            Group group = new Group();
            group.setId(String.valueOf(i));
            groups.add(group);
        }
        Mockito.when(groupPostgresRepository.findAll()).thenReturn(groups);
        List<Group> returnedGroups = groupService.getAllGroups();
        Assert.assertEquals(groups, returnedGroups);
    }

    @Test
    public void getGroupById() {
        GroupService spy = Mockito.spy(groupService);
        spy.getGroupById("test");
        Mockito.verify(spy).getGroupById("test");
    }

    @Test
    public void uploadGroup() {
        Group group = new Group();
        group.setId("test");
        GroupService spy = Mockito.spy(groupService);
        spy.uploadGroup(group);
        Mockito.verify(spy).uploadGroup(group);
    }

    @Test
    public void deleteGroup() {
        GroupService spy = Mockito.spy(groupService);
        spy.deleteGroup("test");
        Mockito.verify(spy).deleteGroup("test");
    }
}