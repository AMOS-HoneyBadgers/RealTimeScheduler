package com.honeybadgers.realtimescheduler.services;


import com.honeybadgers.models.Group;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class GroupServiceTest {

    @Mock
    GroupPostgresRepository groupPostgresRepository;

    private GroupService groupService;

    @Before
    public void setUp() throws Exception {
        groupService = new GroupService(groupPostgresRepository);
    }

    @Test
    public void getAllGroups() {
        GroupService spy = spy(groupService);
        spy.getAllGroups();
        verify(groupPostgresRepository).findAll();
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