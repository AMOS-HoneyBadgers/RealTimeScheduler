package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.User;
import com.honeybadgers.realtimescheduler.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {


    // DISCLAIMER: would be best to use separate Model class for rest communication (not same as for database)
    // here the same is used because the tables/classes are so small

    @Autowired
    UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String userId) {
        User user = userService.getUserById(userId);
        if(user != null)
            return ResponseEntity.ok(user);
        else
            return ResponseEntity.notFound().build();
        // functional single line of same code for ifPresent condition
        // return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@RequestBody @Valid User newUser) {
        userService.createUser(newUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<?> deleteUserById(@PathVariable String userid) {
        User deleted = userService.deleteUser(userid);
        return ResponseEntity.ok(deleted);
    }
}
