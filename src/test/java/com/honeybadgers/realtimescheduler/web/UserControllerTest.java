package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.User;
import com.honeybadgers.realtimescheduler.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)       // mock only the given controller (for mock all use @WebMvcTest & @AutoConfigureMockMvc)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @Test
    public void testGetAllUsers() throws Exception {

        List<User> allUsers = new ArrayList<>();

        for(int i = 1; i < 10; i++) {
            allUsers.add(new User(UUID.randomUUID().toString(), "testUser" + i, "role", i));
        }

        // specify mock result
        given(service.getAll()).willReturn(allUsers);

        // perform test
        mvc.perform(get("/api/user/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)))
                .andExpect(jsonPath("$[0].name", is(allUsers.get(0).getName())));
    }
}
