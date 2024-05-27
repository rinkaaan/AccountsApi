package com.rikagu.accounts;

import com.rikagu.accounts.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPasswordHashing {
    @Test
    public void testPasswordHashing() {
        System.out.println("Test password hashing");

        String password = "password";
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.println("Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);

        assertTrue(BCrypt.checkpw("password", hashedPassword));

        final String incorrectPassword = "incorrectPassword";
        assertFalse(BCrypt.checkpw(incorrectPassword, hashedPassword));
    }

    @Test
    public void testGenerateRandom6numbers() throws Exception {
        User user = new User();

        // Use reflection to access the private method
        Method method = User.class.getDeclaredMethod("generateRandom6numbers");
        method.setAccessible(true);

        String randomNumber = (String) method.invoke(user);

        // Check if the generated number is of length 6 and is within the expected range
        assertTrue(randomNumber.matches("\\d{6}"), "Generated number should be a 6-digit number");
        int number = Integer.parseInt(randomNumber);
        assertTrue(number >= 100000 && number <= 999999, "Generated number should be between 100000 and 999999");
    }
}