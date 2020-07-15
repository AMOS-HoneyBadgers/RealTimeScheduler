package com.honeybadgers.groupapi.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.service.IGroupConvertUtils;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.groupapi.utils.TestUtils;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.postgre.repository.GroupRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(GroupIdApiController.class)
public class GroupIdApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    IGroupService groupService;

    @MockBean
    IGroupConvertUtils convertUtils;

    @MockBean
    GroupRepository groupRepository;


    @Test
    public void testGroupUpdate() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");

        mvc.perform(post( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isOk());

        verify(groupService, only())
                .updateGroup(any(String.class), any(GroupModel.class));
    }

    @Test
    public void testGroupUpdate_returns404() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");

        NoSuchElementException ex = new NoSuchElementException();
        when(groupService.updateGroup(any(String.class), any(GroupModel.class))).thenThrow(ex);

        mvc.perform(post( "/api/group/GroupNotFound/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isNotFound());

        verify(groupService, only())
                .updateGroup(any(String.class), any(GroupModel.class));
    }

    @Test
    public void testGroupUpdate_JpaExceptionWasThrown() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");

        JpaException ex = new JpaException("Primary or unique constraint failed!");
        when(groupService.updateGroup(any(String.class), any(GroupModel.class))).thenThrow(ex);

        mvc.perform(post( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isBadRequest());

        verify(groupService, only())
                .updateGroup(any(String.class), any(GroupModel.class));
    }


    @Test
    public void testGroupUpdate_invalidModel() throws Exception {

        mvc.perform(post( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                "\"id\":\"" + "testGroup" + "\"," +
                                "\"group_id\":\"TestGroup\"," +
                                "\"mode\":\"Solo\"" +
                                "}"
                ))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGroupIdIdGet() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");
        Group group = new Group();
        group.setId(testModel.getId());

        when(groupService.getGroupById("testGroup")).thenReturn(group);
        when(convertUtils.groupJpaToRest(any(Group.class))).thenReturn(testModel);

        MvcResult mvcResult = mvc.perform(get( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(groupService, only()).getGroupById("testGroup");
        verify(convertUtils, only()).groupJpaToRest(any(Group.class));

        String responseActualBody = mvcResult.getResponse().getContentAsString();
        // body assert
        assertThat(objectMapper.writeValueAsString(testModel)).isEqualToIgnoringWhitespace(responseActualBody);
    }

    @Test
    public void testGroupIdIdGet_notFound() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");
        Group group = new Group();
        group.setId(testModel.getId());

        when(groupService.getGroupById("testGroup")).thenThrow(new NoSuchElementException("Group Not found! - PLACEHOLDER"));
        when(convertUtils.groupJpaToRest(any(Group.class))).thenReturn(testModel);

        MvcResult mvcResult = mvc.perform(get( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(convertUtils, never()).groupJpaToRest(any(Group.class));
    }

    @Test
    public void testGroupIdIdDelete() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");
        Group group = new Group();
        group.setId(testModel.getId());

        when(groupService.deleteGroup("testGroup")).thenReturn(group);
        when(convertUtils.groupJpaToRest(any(Group.class))).thenReturn(testModel);

        MvcResult mvcResult = mvc.perform(delete( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(groupService, only()).deleteGroup("testGroup");
        verify(convertUtils, only()).groupJpaToRest(any(Group.class));

        String responseActualBody = mvcResult.getResponse().getContentAsString();
        // body assert
        assertThat(objectMapper.writeValueAsString(testModel)).isEqualToIgnoringWhitespace(responseActualBody);
    }

    @Test
    public void testGroupIdIdDelete_notFound() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");
        Group group = new Group();
        group.setId(testModel.getId());

        when(groupService.deleteGroup("testGroup")).thenThrow(new NoSuchElementException("Group Not found! - PLACEHOLDER"));
        when(convertUtils.groupJpaToRest(any(Group.class))).thenReturn(testModel);

        MvcResult mvcResult = mvc.perform(delete( "/api/group/testGroup/id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(convertUtils, never()).groupJpaToRest(any(Group.class));
    }

}