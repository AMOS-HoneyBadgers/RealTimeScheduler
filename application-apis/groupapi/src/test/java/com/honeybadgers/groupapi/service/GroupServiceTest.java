package com.honeybadgers.groupapi.service;

import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.groupapi.repository.TaskRepository;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupService.class)
public class GroupServiceTest {

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    ISendGroupsToTaksQueue sender;

    @Autowired
    IGroupService groupService;


    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");
        group.setPriority(50);
        group.setPaused(false);

        Group parentGroup = new Group();
        parentGroup.setId("parentGroup");
        parentGroup.setPriority(200);
        group.setParentGroup(parentGroup);

        GroupModel restModel = new GroupModel();
        restModel.setParentId("parentGroup");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupRepository.findById(restModel.getParentId())).thenReturn(Optional.of(parentGroup));

    }

    @Test
    public void testCreateGroup() throws JpaException, UnknownEnumException, CreationException {

        when(taskRepository.findAllByGroupId("parentGroup")).thenReturn(new ArrayList<>());

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setParentId("parentGroup");
        restGroup.setPriority(100);

        Group group = groupService.createGroup(restGroup);

        assertNotNull(group);
    }

    @Test
    public void testCreateGroup_primaryViolation(){
        DataIntegrityViolationException vio = new DataIntegrityViolationException("primary key violation");

        when(groupRepository.save(any(Group.class))).thenThrow(vio);

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(JpaException.class, () -> groupService.createGroup(restGroup));
        assertEquals("Primary or unique constraint failed!", e.getMessage());
    }

    @Test
    public void testCreateGroup_parentChildrenViolation(){

        Task child = new Task();
        child.setId("TestTask");
        Task child2 = new Task();
        child2.setId("TestTask2");
        when(taskRepository.findAllByGroupId("parentGroup")).thenReturn(Arrays.asList(child, child2));

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setParentId("parentGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(CreationException.class, () -> groupService.createGroup(restGroup));
        assertEquals("Parent group has tasks as children: TestTask, TestTask2 -> aborting!", e.getMessage());
    }

    @Test
    public void testGroupUpdate() throws JpaException, UnknownEnumException {

        String group_id = "testGroup";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroup");
        restGroup.setPriority(100);
        restGroup.setParentId("parentGroup");

        Group group = groupService.updateGroup(group_id, restGroup);

        assertNotNull(group);
        assertEquals( 100, group.getPriority() );
        assertEquals("parentGroup", group.getParentGroup().getId());
    }

    @Test
    public void testGroupUpdate_GroupNotFound() throws JpaException, UnknownEnumException {

        String group_id = "testGroupNotFound";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroupNotFound");
        restGroup.setPriority(100);

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("Group does not exist", e.getMessage());
    }

    @Test
    public void testGroupUpdate_ParentGroupNotFound() throws JpaException, UnknownEnumException {

        String group_id = "testGroup";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroup");
        restGroup.setParentId("parentGroupNotFound");
        restGroup.setPriority(100);

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("Parent Group does not exist", e.getMessage());
    }

}
