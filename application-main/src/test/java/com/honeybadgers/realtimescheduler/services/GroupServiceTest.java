package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.Group;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceTest {

    @Mock
    GroupPostgresRepository groupPostgresRepository;
    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllGroups() {
        groupService = new GroupService(groupPostgresRepository);
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
    void getGroupById() {
        GroupService spy = Mockito.spy(groupService);
        spy.getGroupById("test");
        Mockito.verify(spy).getGroupById("test");
    }

    @Test
    void uploadGroup() {
        Group group = new Group();
        group.setId("test");
        GroupService spy = Mockito.spy(groupService);
        spy.uploadGroup(group);
        Mockito.verify(spy).uploadGroup(group);
    }

    @Test
    void deleteGroup() {
        GroupService spy = Mockito.spy(groupService);
        spy.deleteGroup("test");
        Mockito.verify(spy).deleteGroup("test");
    }
}