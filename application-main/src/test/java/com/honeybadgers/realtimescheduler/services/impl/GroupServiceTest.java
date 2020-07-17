package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.postgre.repository.GroupRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupService.class)
public class GroupServiceTest {

    @MockBean
    GroupRepository groupRepository;

    @Autowired
    GroupService groupService;

    @Test
    public void getGroupById() {
        Group gr = new Group();
        gr.setId("TEST");

        when(groupRepository.findById(any())).thenReturn(Optional.of(gr));

        GroupService spy = Mockito.spy(groupService);

        Optional<Group> ret = spy.getGroupById("TEST");

        Mockito.verify(groupRepository).findById("TEST");
        assertNotNull(ret);
        assertTrue(ret.isPresent());
        assertEquals(gr, ret.get());
    }
}