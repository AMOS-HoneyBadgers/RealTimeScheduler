package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile({"redis", "postgre"})
@Slf4j
public class UserService {

    // Repository, can be either UserPostgreRepository or UserRedisRepository, depending on active profile
    @Autowired
    private CrudRepository<User, String> repository;

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

    /*public List<User> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public User getUserById(String userId) {
        return repository.findById(userId).orElse(null);
    }

    public void createUser(User newUser) {
        repository.save(newUser);
    }

    public User deleteUser(String userId) {
        User user = getUserById(userId);
        return deleteUser(user);
    }

    public User deleteUser(User user) {
        if(user == null)
            return null;
        repository.delete(user);
        return user;
    }*/
}
