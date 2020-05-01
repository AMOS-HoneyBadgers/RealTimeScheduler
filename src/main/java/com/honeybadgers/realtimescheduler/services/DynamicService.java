package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class DynamicService {

    final
    CrudRepository<User, String> repository;

    @Autowired
    public DynamicService(CrudRepository<User, String> repository) {
        this.repository = repository;
    }

    public void test() {

        List<User> users = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
        log.info("TEST: " + users.size());
        if(users.size() > 0)
            log.info("FIRST ID: " + users.get(0).getId());
        else {
            User newUser = new User();
            newUser.setName("test");
            newUser.setRole("test");
            newUser.setAge(10);
            repository.save(newUser);
        }
    }
}
