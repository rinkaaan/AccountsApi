package com.rikagu.accounts.controllers;

import com.rikagu.accounts.dtos.CreateUserRequest;
import com.rikagu.accounts.entities.User;
import com.rikagu.accounts.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User create(@Valid @RequestBody CreateUserRequest user) {
        final User newUser = User.builder()
                .username(user.getUsername())
                .build();
        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists", e);
        }
    }
}

