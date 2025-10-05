package com.example.taskmanager.controller;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession authenticatedSession;
    private MockHttpSession unauthenticatedSession;
    private Task task;

    @BeforeEach
    void setUp() {
        authenticatedSession = new MockHttpSession();
        authenticatedSession.setAttribute("user", "testUser");

        unauthenticatedSession = new MockHttpSession();

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setPriority("High");
        task.setEstimatedTime(2.0);
        task.setActualTime(1.5);
    }

    // --- Create Task Tests ---

    @Test
    void createTask_whenAuthenticated_shouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .session(authenticatedSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void createTask_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/tasks")
                        .session(unauthenticatedSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isUnauthorized());
    }

    // --- List Tasks Tests ---

    @Test
    void listTasks_whenAuthenticated_shouldReturnTasks() throws Exception {
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskService.listTasks(0, null, 5)).thenReturn(taskPage);

        mockMvc.perform(get("/tasks")
                        .session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void listTasks_whenAuthenticatedWithPriority_shouldReturnFilteredTasks() throws Exception {
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskService.listTasks(0, "High", 5)).thenReturn(taskPage);

        mockMvc.perform(get("/tasks")
                        .param("priority", "High")
                        .session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("High"));
    }

    @Test
    void listTasks_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/tasks")
                        .session(unauthenticatedSession))
                .andExpect(status().isUnauthorized());
    }

    // --- Update Task Tests ---

    @Test
    void updateTask_whenAuthenticatedAndTaskExists_shouldReturnUpdatedTask() throws Exception {
        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(Optional.of(task));

        mockMvc.perform(put("/tasks/1")
                        .session(authenticatedSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTask_whenAuthenticatedAndTaskNotFound_shouldReturnNotFound() throws Exception {
        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/tasks/1")
                        .session(authenticatedSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/tasks/1")
                        .session(unauthenticatedSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isUnauthorized());
    }

    // --- Delete Task Tests ---

    @Test
    void deleteTask_whenAuthenticated_shouldReturnDeletedStatus() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/tasks/1")
                        .session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void deleteTask_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/tasks/1")
                        .session(unauthenticatedSession))
                .andExpect(status().isUnauthorized());
    }
}