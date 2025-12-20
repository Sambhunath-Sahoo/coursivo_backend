package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
