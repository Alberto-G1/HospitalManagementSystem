package org.example.controller;

import org.example.models.User;
import org.example.service.UserService;
import org.example.enums.Role;

import java.util.regex.Pattern;

public class UserController {
    private final UserService userService = new UserService();

    public boolean register(String username, String password, String email, Role role) {
        if (userService.findByUsername(username) != null) {
            System.out.println("❌ Username already exists.");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("❌ Invalid email format.");
            return false;
        }

        if (password.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters.");
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(password)); // 🔒 Hash password
        user.setEmail(email);
        user.setRole(role);

        userService.registerUser(user);
        System.out.println("✅ User registered.");
        return true;
    }

    public boolean login(String username, String password) {
        User user = userService.findByUsername(username);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            System.out.println("✅ Login successful.");
            return true;
        }
        System.out.println("❌ Invalid credentials.");
        return false;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }

    private String hashPassword(String password) {
        // Simple example: In real systems, use BCrypt or SHA-256 + salt
        return Integer.toHexString(password.hashCode());
    }
}
