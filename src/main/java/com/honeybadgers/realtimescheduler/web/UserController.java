package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.redis.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {


    // DISCLAIMER: would be best to use separate Model class for rest communication (not same as for database)
    // here the same is used because the tables/classes are so small


    final
    CrudRepository<User, String> userRepository;

    @Autowired
    public UserController(CrudRepository<User, String> userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent())
            return ResponseEntity.ok(user.get());
        else
            return ResponseEntity.notFound().build();
        // functional single line of same code for ifPresent condition
        // return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@RequestBody @Valid User newUser) {
        userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<?> deleteUserById(@PathVariable String userid) {
        Optional<User> user = userRepository.findById(userid);
        if(user.isEmpty())
            return ResponseEntity.notFound().build();
        userRepository.delete(user.get());
        return ResponseEntity.ok(user);
    }
}
