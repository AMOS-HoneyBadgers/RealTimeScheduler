package com.honeybadgers.groupapi.service;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.model.UnknownEnumException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.service.impl.GroupService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupService.class)
public class GroupServiceTest {

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    ICommunication sender;

    @MockBean
    IGroupConvertUtils convertUtils;

    @Autowired
    IGroupService groupService;


    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");
        group.setPriority(50);

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
        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenReturn(new Group());

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

        Group alreadyInGroup = new Group();
        alreadyInGroup.setId("TestGroupAlreadyExists");

        when(groupRepository.findById(any(String.class))).thenReturn(Optional.of(alreadyInGroup));

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(JpaException.class, () -> groupService.createGroup(restGroup));
        assertEquals("Primary or unique constraint failed!", e.getMessage());
    }

    @Test
    public void testCreateGroup_JpaException() throws UnknownEnumException {
        DataIntegrityViolationException vio = new DataIntegrityViolationException("primary key violation");

        when(groupRepository.save(any(Group.class))).thenThrow(vio);
        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenReturn(new Group());

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(JpaException.class, () -> groupService.createGroup(restGroup));
        assertEquals("DataIntegrityViolation on save new group!", e.getMessage());
    }

    @Test
    public void testCreateGroup_parentChildrenViolation() throws UnknownEnumException {

        Task child = new Task();
        child.setId("TestTask");
        Task child2 = new Task();
        child2.setId("TestTask2");
        when(taskRepository.findAllByGroupId("parentGroup")).thenReturn(Arrays.asList(child, child2));
        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenReturn(new Group());

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

        Group parentGroup = new Group();
        parentGroup.setId("parentGroup");
        Group mockGroup = new Group();
        mockGroup.setPriority(restGroup.getPriority());
        mockGroup.setParentGroup(parentGroup);
        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenReturn(mockGroup);

        Group group = groupService.updateGroup(group_id, restGroup);

        verify(sender, never()).sendTaskToDispatcher(any());
        assertNotNull(group);
        assertEquals( 100, group.getPriority() );
        assertEquals("parentGroup", group.getParentGroup().getId());
    }

    @Test
    public void testGroupUpdate_GroupNotFound() throws JpaException, UnknownEnumException {

        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenReturn(new Group());

        String group_id = "testGroupNotFound";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroupNotFound");
        restGroup.setPriority(100);

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("Group does not exist", e.getMessage());
    }

    @Test
    public void testGroupUpdate_ParentGroupNotFound() throws JpaException, UnknownEnumException {

        when(convertUtils.groupRestToJpa(any(GroupModel.class))).thenThrow(new NoSuchElementException("Parent Group does not exist"));

        String group_id = "testGroup";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroup");
        restGroup.setParentId("parentGroupNotFound");
        restGroup.setPriority(100);

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("Parent Group does not exist", e.getMessage());
    }

    @Test
    public void testGetAllGroups() {
        when(groupRepository.findAll()).thenReturn(new ArrayList<>());

        List<Group> groups = groupService.getAllGroups();

        assertNotNull(groups);
        assertEquals(0, groups.size());
    }

    @Test
    public void testGetGroupById() {

        Group group = groupService.getGroupById("testGroup");

        assertNotNull(group);
        assertEquals("testGroup", group.getId());
    }

    @Test
    public void testGetGroupById_NotFound() {

        when(groupRepository.findById("gg")).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.getGroupById("gg"));
        assertNotNull(e);
        assertEquals("Group with groupId gg not found!", e.getMessage());
    }

    @Test
    public void testDeleteGroup() {

        doNothing().when(groupRepository).deleteById("testGroup");

        Group group = groupService.deleteGroup("testGroup");

        assertNotNull(group);
        assertEquals("testGroup", group.getId());
    }

    @Test
    public void testDeleteGroup_NotFound() {

        doNothing().when(groupRepository).deleteById("testGroup");

        when(groupRepository.findById("testGroup")).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.deleteGroup("testGroup"));
        assertNotNull(e);
        assertEquals("Group with groupId testGroup not found!", e.getMessage());
    }

}
