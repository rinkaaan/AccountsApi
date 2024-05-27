package com.rikagu.accounts.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rikagu.accounts.dtos.CreateUserRequest;
import com.rikagu.accounts.dtos.LoginUserRequest;
import com.rikagu.accounts.dtos.ResendVerificationEmailRequest;
import com.rikagu.accounts.dtos.VerifyUserRequest;
import com.rikagu.accounts.dtos.VerifyUserResponse;
import com.rikagu.accounts.entities.User;
import com.rikagu.accounts.repositories.UserRepository;
import com.rikagu.accounts.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Environment environment;

    public UserController(UserRepository userRepository, EmailService emailService, Environment environment) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.environment = environment;
    }

    @PostMapping("/new-user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateUserRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            if (!existingUser.isVerified()) {
                userRepository.delete(existingUser);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with the same email already exists");
            }
        }

        existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with the same username already exists");
        }

        final User newUser = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .build();
        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving the user");
        }

        sendVerificationEmail(newUser);
    }

    @PostMapping("/new-user/verify")
    public VerifyUserResponse verify(@Valid @RequestBody VerifyUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!user.getVerificationCode().equals(request.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification code");
        }

        user.setVerified(true);
        userRepository.save(user);

        return VerifyUserResponse.builder()
                .jwtToken(getJwtToken(user))
                .build();
    }

    @PostMapping("/new-user/resend-verification")
    public void resendVerification(@Valid @RequestBody ResendVerificationEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        sendVerificationEmail(user);
    }

    @PostMapping("/login")
    public VerifyUserResponse login(@Valid @RequestBody LoginUserRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        User user;
        if (usernameOrEmail.contains("@")) {
            user = userRepository.findByEmail(usernameOrEmail);
        } else {
            user = userRepository.findByUsername(usernameOrEmail);
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not verified");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return VerifyUserResponse.builder()
                .jwtToken(getJwtToken(user))
                .build();
    }

    private String getJwtToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(Objects.requireNonNull(environment.getProperty("jwt.secret")));

        return JWT.create()
                .withIssuer("accounts.rikagu.com")
                .withSubject(user.getId().toString())
                .withAudience("rikagu.com")
                .withClaim("email", user.getEmail())
                .withClaim("username", user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(30)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    private void sendVerificationEmail(User user) {
        if (user.getVerificationCodeLastSentAt() == null || Instant.now().minus(Duration.ofMinutes(5)).isAfter(user.getVerificationCodeLastSentAt().toInstant())) {
            user.setVerificationCodeLastSentAt(new Date());
            userRepository.save(user);
            try {
                emailService.sendEmail(user.getEmail(), "Verify your email", "Your verification code is: " + user.getVerificationCode());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while sending the verification email");
            }
        } else {
            long remainingTime = 5 - Duration.between(user.getVerificationCodeLastSentAt().toInstant(), Instant.now()).toMinutes();
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Verification code already sent. Please wait " + remainingTime + " minutes before requesting another one.");
        }
    }
}

