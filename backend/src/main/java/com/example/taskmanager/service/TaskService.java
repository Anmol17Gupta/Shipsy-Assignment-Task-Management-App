package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    // Save a new task with calculated total time
    public Task createTask(Task task) {
        task.calculateTotalTime();
        return taskRepository.save(task);
    }

    // Return a plain list for JSON serialization compatibility with frontend
    public List<Task> getTasks(int page, String priority) {
        int size = 10; // Default page size, can be customized
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Task> taskPage;

        if (priority == null || priority.isEmpty()) {
            taskPage = taskRepository.findAll(pageable);
        } else {
            taskPage = taskRepository.findByPriority(priority, pageable);
        }
        return taskPage.getContent();
    }

    // Update task using a map of updates (for flexible PATCH/PUT)
    public Task updateTask(Long id, Map<String, Object> updates) {
        return taskRepository.findById(id).map(task -> {
            if (updates.containsKey("title")) {
                task.setTitle((String) updates.get("title"));
            }
            if (updates.containsKey("priority")) {
                task.setPriority((String) updates.get("priority"));
            }
            if (updates.containsKey("completed")) {
                task.setCompleted((Boolean) updates.get("completed"));
            }
            if (updates.containsKey("estimatedTime")) {
                task.setEstimatedTime(
                    ((Number) updates.get("estimatedTime")).doubleValue()
                );
            }
            if (updates.containsKey("actualTime")) {
                task.setActualTime(
                    ((Number) updates.get("actualTime")).doubleValue()
                );
            }
            task.calculateTotalTime();
            return taskRepository.save(task);
        }).orElse(null);
    }

    // Delete task by ID
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
