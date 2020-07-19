package com.honeybadgers.groupapi.service;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.hibernate.TransactionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.honeybadgers.models.model.jpa.Group;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.groupapi.service.impl.GroupService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GroupService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GroupServiceTest {

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    ICommunication sender;

    @MockBean
    @Qualifier("groupConvertUtils")
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
    public void testCreateGroup() throws JpaException, UnknownEnumException, CreationException, TransactionRetriesExceeded, InterruptedException {

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
    public void testCreateGroup_transactionException() throws UnknownEnumException {
        when(groupRepository.findById(anyString())).thenThrow(new TransactionException(""));

        GroupModel restGroup = new GroupModel();
        restGroup.setId("TestGroup");
        restGroup.setParentId("parentGroup");
        restGroup.setPriority(100);

        Exception e = assertThrows(TransactionRetriesExceeded.class, () -> groupService.createGroup(restGroup));
        assertEquals("Failed transaction 1 times!", e.getMessage());
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
    public void testGroupUpdate() throws JpaException, UnknownEnumException, TransactionRetriesExceeded, InterruptedException {
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

        verify(sender, atMostOnce()).sendTaskToDispatcher(any());
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
    public void testGroupUpdate_transactionException() throws JpaException, UnknownEnumException {

        when(groupRepository.findById(anyString())).thenThrow(new TransactionException(""));

        String group_id = "testGroupNotFound";
        GroupModel restGroup = new GroupModel();
        restGroup.setId("testGroupNotFound");
        restGroup.setPriority(100);

        Exception e = assertThrows(TransactionRetriesExceeded.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("Failed transaction 1 times!", e.getMessage());
    }

    @Test
    public void testGroupUpdate_dataIntegrityException() throws JpaException, UnknownEnumException {
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

        when(groupRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        Exception e = assertThrows(JpaException.class, () -> groupService.updateGroup(group_id, restGroup));
        assertEquals("DataIntegrityViolation on updating group!", e.getMessage());
    }

    @Test
    public void testGetAllGroups() throws TransactionRetriesExceeded, InterruptedException {
        when(groupRepository.findAll()).thenReturn(new ArrayList<>());

        List<Group> groups = groupService.getAllGroups();

        assertNotNull(groups);
        assertEquals(0, groups.size());
    }

    @Test
    public void testGetAllGroups_transactionException() throws TransactionRetriesExceeded, InterruptedException {
        when(groupRepository.findAll()).thenThrow(new TransactionException(""));

        assertThrows(TransactionRetriesExceeded.class, () -> groupService.getAllGroups());
    }

    @Test
    public void testGetGroupById() throws TransactionRetriesExceeded, InterruptedException {

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
    public void testGetGroupById_transactionException() {

        when(groupRepository.findById("gg")).thenThrow(new TransactionException(""));

        Exception e = assertThrows(TransactionRetriesExceeded.class, () -> groupService.getGroupById("gg"));
        assertNotNull(e);
        assertEquals("Failed transaction 1 times!", e.getMessage());
    }

    @Test
    public void testDeleteGroup() throws InterruptedException, TransactionRetriesExceeded, JpaException {
        Group gr = new Group();
        gr.setId("testGroup");
        gr.setPriority(50);

        when(groupRepository.deleteByIdCustomQuery("testGroup")).thenReturn(Optional.of(gr));

        Group group = groupService.deleteGroup("testGroup");

        assertNotNull(group);
        assertEquals(gr.getId(), group.getId());
    }

    @Test
    public void testDeleteGroup_NotFound() {

        when(groupRepository.deleteByIdCustomQuery("testGroup")).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> groupService.deleteGroup("testGroup"));
        assertNotNull(e);
        assertEquals("Group with groupId testGroup not found!", e.getMessage());
    }

    @Test
    public void testDeleteGroup_transactionException() {

        when(groupRepository.deleteByIdCustomQuery("testGroup")).thenThrow(new TransactionException(""));

        Exception e = assertThrows(TransactionRetriesExceeded.class, () -> groupService.deleteGroup("testGroup"));
        assertNotNull(e);
        assertEquals("Failed transaction 1 times!", e.getMessage());
    }

    @Test
    public void testDeleteGroup_foreignKeyException() {

        when(groupRepository.deleteByIdCustomQuery("testGroup")).thenThrow(new DataIntegrityViolationException("constraint [group_fk]"));

        Exception e = assertThrows(JpaException.class, () -> groupService.deleteGroup("testGroup"));
        assertNotNull(e);
        assertEquals("Group deletion failed due to being referenced by task!", e.getMessage());
    }

    @Test
    public void testDeleteGroup_dataIntegrityException() {

        when(groupRepository.deleteByIdCustomQuery("testGroup")).thenThrow(new DataIntegrityViolationException(""));

        Exception e = assertThrows(DataIntegrityViolationException.class, () -> groupService.deleteGroup("testGroup"));
        assertNotNull(e);
        assertEquals("", e.getMessage());
    }

}
