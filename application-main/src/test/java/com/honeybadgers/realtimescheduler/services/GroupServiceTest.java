package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import com.honeybadgers.realtimescheduler.services.impl.GroupService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupService.class)
public class GroupServiceTest {

    @MockBean
    GroupPostgresRepository groupPostgresRepository;
    @Autowired
    GroupService groupService;

    @Test
    public void getAllGroups() {
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