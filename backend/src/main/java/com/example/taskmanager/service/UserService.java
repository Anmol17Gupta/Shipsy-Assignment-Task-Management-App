package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        task.calculateTotalTime();
        return taskRepository.save(task);
    }

    public Page<Task> listTasks(int page, String priority, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (priority == null || priority.isEmpty()) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.findByPriority(priority, pageable);
    }

    public Optional<Task> updateTask(Long id, Task newTaskData) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(newTaskData.getTitle());
            task.setPriority(newTaskData.getPriority());
            task.setCompleted(newTaskData.getCompleted());
            task.setEstimatedTime(newTaskData.getEstimatedTime());
            task.setActualTime(newTaskData.getActualTime());
            task.calculateTotalTime();
            return taskRepository.save(task);
        });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
