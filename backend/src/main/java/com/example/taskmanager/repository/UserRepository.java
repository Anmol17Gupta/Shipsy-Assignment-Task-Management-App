package com.example.taskmanager.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private Map<String, String> users = new ConcurrentHashMap<>();

    public UserRepository() {
        users.put("demo", "demo123"); 
    }

    public boolean validateUser(String username, String password) {
    String storedPass = users.get(username);
    return storedPass != null && storedPass.equals(password);
}

}
