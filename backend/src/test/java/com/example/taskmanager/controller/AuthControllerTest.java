package com.example.taskmanager.controller;

import com.example.taskmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_Success() throws Exception {
        when(userService.validateUser("user1", "pass1")).thenReturn(true);

        String requestBody = "{\"username\":\"user1\", \"password\":\"pass1\"}";

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", null))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("success"));

        verify(userService).validateUser("user1", "pass1");
        verify(session, never()).invalidate();  // session.invalidate is not called in login
    }

    @Test
    void login_Failure() throws Exception {
        when(userService.validateUser("user1", "wrongpass")).thenReturn(false);

        String requestBody = "{\"username\":\"user1\", \"password\":\"wrongpass\"}";

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        verify(userService).validateUser("user1", "wrongpass");
    }

    @Test
    void logout_ShouldInvalidateSession() throws Exception {
        doNothing().when(session).invalidate();

        mockMvc.perform(post("/logout").sessionAttr("user", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logout").value("success"));

        verify(session).invalidate();
    }
}
