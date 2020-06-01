package com.honeybadgers.groupapi.service;

import com.honeybadgers.models.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.honeybadgers.models.Group;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.service.impl.GroupService;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.honeybadgers.groupapi.repository.GroupRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
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
    IGroupService groupService;


    @Test
    public void testCreateGroup() throws JpaException, UnknownEnumException {

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setPriority(100);

        Group group = groupService.createGroup(restGroup);

        assertNotNull(group);
    }

    @Test
    public void testCreateGroupPrimaryViolation(){
        DataIntegrityViolationException vio = new DataIntegrityViolationException("primary key violation");

        when(groupRepository.save(any(Group.class))).thenThrow(vio);

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(JpaException.class, () -> groupService.createGroup(restGroup));
        assertEquals(e.getMessage(), "Primary or unique constraint failed!");
    }
}
