package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User registerUser(@RequestBody User user) {
        return this.userService.createUser(user);
    }


    @PatchMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return this.userService.updateUser(id, user);
    }
}
