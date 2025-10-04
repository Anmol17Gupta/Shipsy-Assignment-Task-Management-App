package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TaskController {
    @Autowired
    private TaskService taskService;

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        Task created = taskService.createTask(task);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<?> listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String priority,
            HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        Page<Task> results = taskService.listTasks(page, priority, 5);
        return ResponseEntity.ok(results.getContent());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        Optional<Task> updated = taskService.updateTask(id, task);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        boolean deleted = taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}
