package com.rikagu.accounts.controller;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final Configuration configuration;

    public HelloController(Configuration configuration) {
        this.configuration = configuration;
    }

    @GetMapping("/hello")
    public String hello() {
        Mail.using(configuration)
                .to("test@email.com")
                .subject("This is the subject")
                .text("Hello world!")
                .build()
                .send();

        return "Hello, World!";
    }
}

