package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.postgre.repository.GroupRepository;
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
    GroupRepository groupPostgresRepository;
    @Autowired
    GroupService groupService;

    @Test
    public void getGroupById() {
        GroupService spy = Mockito.spy(groupService);
        spy.getGroupById("test");
        Mockito.verify(spy).getGroupById("test");
    }
}