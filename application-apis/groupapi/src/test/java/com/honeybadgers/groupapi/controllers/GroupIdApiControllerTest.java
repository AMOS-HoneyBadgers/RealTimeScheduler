package com.honeybadgers.groupapi.controllers;


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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(DefaultApiController.class)
public class GroupIdApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    IGroupService groupService;

    @MockBean
    GroupRepository groupRepository;

    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");
        group.setPaused(false);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    }

    @Test
    public void testGroupUpdate() throws Exception {


    }

}
