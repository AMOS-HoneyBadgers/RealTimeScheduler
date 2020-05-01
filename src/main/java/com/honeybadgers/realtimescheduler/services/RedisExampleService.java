package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.redis.User;
import com.honeybadgers.realtimescheduler.repository.UserRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile({"redis"})
@Slf4j
public class RedisExampleService {

    @Autowired
    UserRedisRepository userRedisRepository;

    public void testRedis() {
        User newUser = new User();
        newUser.setAge(10);
        newUser.setName("TEST");
        newUser.setRole("ROLLIN'");
        userRedisRepository.save(newUser);

        List<User> allUsers = StreamSupport.stream(userRedisRepository.findAll().spliterator(), false).collect(Collectors.toList());
        log.info("SIZE: " + allUsers.size());
        log.info("Id: " + allUsers.get(0).getId() + " Name: " + allUsers.get(0).getName() + " Role: " + allUsers.get(0).getRole() + " Age: " + allUsers.get(0).getAge());

    }
}
