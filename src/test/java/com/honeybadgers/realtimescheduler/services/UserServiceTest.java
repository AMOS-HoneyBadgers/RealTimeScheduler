package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.User;
import com.honeybadgers.realtimescheduler.repository.redis.UserRedisRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ActiveProfiles("redis")
public class UserServiceTest {

    @TestConfiguration          // create configuration used soley for this test class
    static class PostgresExampleServiceTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean       // mock repository
    private UserRedisRepository userRedisRepository;

    private String uuid = UUID.randomUUID().toString();

    @Before
    public void setUp() {
        User user = new User(uuid, "testUser", "role", 666);

        // define mock for @MockBean
        Mockito.when(userRedisRepository.findById(uuid))
                .thenReturn(Optional.of(user));
    }

    @Test
    public void assertUserExists() {
        User found = userRedisRepository.findById(uuid).orElse(null);

        assertNotNull(found);
        assertThat(found.getId())
                .isEqualTo(uuid);
    }
}
