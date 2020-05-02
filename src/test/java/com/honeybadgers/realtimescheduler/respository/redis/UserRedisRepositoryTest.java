package com.honeybadgers.realtimescheduler.respository.redis;

import com.honeybadgers.realtimescheduler.config.RedisTestConfig;
import com.honeybadgers.realtimescheduler.domain.User;
import com.honeybadgers.realtimescheduler.repository.redis.UserRedisRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@ActiveProfiles("redis")
@SpringBootTest(classes = RedisTestConfig.class)          // start with given config
public class UserRedisRepositoryTest {


    @Autowired
    private UserRedisRepository userRedisRepository;

    @Test
    public void testFindById() {
        // given
        String uuid = UUID.randomUUID().toString();
        User user = new User(uuid, "testUser", "role", 666);
        userRedisRepository.save(user);

        // when
        User found = userRedisRepository.findById(uuid).orElse(null);

        // then
        assertNotNull(found);
        assertThat(found.getId())
                .isEqualTo(uuid);
    }
}
