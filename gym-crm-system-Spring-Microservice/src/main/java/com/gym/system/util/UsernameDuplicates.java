package com.gym.system.util;

import org.springframework.stereotype.Component;

import com.gym.system.repository.UserDAO;

@Component
public class UsernameDuplicates {
    private final UserDAO userDAO;

    public UsernameDuplicates(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String generateUniqueUsername(String baseUsername) {

        if (!userDAO.existsByUsernameIgnoreCase(baseUsername)) {
            return baseUsername;
        }

        int counter = 1;
        while (true) {
            String candidate = baseUsername + counter;

            if (!userDAO.existsByUsernameIgnoreCase(candidate)) {
                return candidate;
            }

            counter++;
        }
    }
}
