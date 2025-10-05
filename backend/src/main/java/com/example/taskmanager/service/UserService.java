package com.example.taskmanager.service;

import com.example.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean validateUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return userRepository.validateUser(username, password);
    }
}
