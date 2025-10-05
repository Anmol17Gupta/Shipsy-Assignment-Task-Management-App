package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask_ShouldSaveTask() {
        Task task = new Task();
        doNothing().when(task).calculateTotalTime();

        when(taskRepository.save(task)).thenReturn(task);

        Task savedTask = taskService.createTask(task);

        verify(task).calculateTotalTime();
        verify(taskRepository).save(task);
        assertEquals(task, savedTask);
    }

    @Test
    void testListTasks_WithPriority_ShouldReturnFilteredPage() {
        String priority = "HIGH";
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        List<Task> tasks = Collections.singletonList(new Task());
        Page<Task> pageResult = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findByPriority(priority, pageable)).thenReturn(pageResult);

        Page<Task> result = taskService.listTasks(page, priority, size);

        verify(taskRepository).findByPriority(priority, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testListTasks_WithoutPriority_ShouldReturnAll() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        List<Task> tasks = Collections.singletonList(new Task());
        Page<Task> pageResult = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findAll(pageable)).thenReturn(pageResult);

        Page<Task> result = taskService.listTasks(page, null, size);

        verify(taskRepository).findAll(pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testUpdateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setPriority("LOW");
        existingTask.setCompleted(false);
        existingTask.setEstimatedTime(5.0);
        existingTask.setActualTime(3.0);

        Task newTaskData = new Task();
        newTaskData.setTitle("New Title");
        newTaskData.setPriority("HIGH");
        newTaskData.setCompleted(true);
        newTaskData.setEstimatedTime(6.0);
        newTaskData.setActualTime(4.0);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> updatedTask = taskService.updateTask(taskId, newTaskData);

        assertTrue(updatedTask.isPresent());
        assertEquals("New Title", updatedTask.get().getTitle());
        assertEquals("HIGH", updatedTask.get().getPriority());
        assertEquals(true, updatedTask.get().getCompleted());
        verify(existingTask).calculateTotalTime();
        verify(taskRepository).save(existingTask);
    }

    @Test
    void testUpdateTask_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        Long taskId = 1L;
        Task newTaskData = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<Task> updatedTask = taskService.updateTask(taskId, newTaskData);

        assertFalse(updatedTask.isPresent());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testDeleteTask_WhenTaskExists_ShouldDeleteAndReturnTrue() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        boolean result = taskService.deleteTask(taskId);

        assertTrue(result);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void testDeleteTask_WhenTaskDoesNotExist_ShouldReturnFalse() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(false);

        boolean result = taskService.deleteTask(taskId);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(taskId);
    }
}
