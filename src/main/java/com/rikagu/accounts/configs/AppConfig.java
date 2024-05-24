package com.rikagu.accounts.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

    private final Environment  environment;

    public AppConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public net.sargue.mailgun.Configuration configuration() {
        return new net.sargue.mailgun.Configuration()
                .domain("rinkagu.com")
                .apiKey(environment.getProperty("mailgun.api-key"))
                .from("Rinkagu", "noreply@rinkagu.com");
    }
}
