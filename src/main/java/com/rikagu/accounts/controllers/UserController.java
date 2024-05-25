package com.rikagu.accounts.controllers;

import com.rikagu.accounts.dtos.CreateUserRequest;
import com.rikagu.accounts.entities.User;
import com.rikagu.accounts.repositories.UserRepository;
import jakarta.validation.Valid;
import net.sargue.mailgun.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final Configuration configuration;

    public UserController(UserRepository userRepository, Configuration configuration) {
        this.userRepository = userRepository;
        this.configuration = configuration;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateUserRequest user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            if (!existingUser.isVerified()) {
                userRepository.delete(existingUser);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with the same email already exists");
            }
        }

        existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with the same username already exists");
        }

        final User newUser = User.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .build();
        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving the user");
        }

//        // Send verification email
//        Mail.using(configuration)
//                .to(newUser.getEmail())
//                .subject("Verify your email")
//                .text("Your verification code is: " + newUser.getVerificationCode())
//                .build()
//                .send();
    }
}

