package com.honeybadgers.groupapi.controllers;


import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.repository.GroupRepository;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.groupapi.utils.TestUtils;
import com.honeybadgers.models.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(GroupIdApiController.class)
public class GroupIdApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    IGroupService groupService;

    @MockBean
    GroupRepository groupRepository;


    @Test
    public void testGroupUpdate() throws Exception {
        GroupModel testModel = new GroupModel();
        testModel.setId("testGroup");
        testModel.setPaused(true);

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
        testModel.setPaused(true);

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
        testModel.setPaused(true);

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

}
