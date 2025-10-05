package com.example.taskmanager.service;

import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initializes mocks and injects them into userService
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUser_ShouldReturnTrue_WhenCredentialsAreValid() {
        // Arrange
        String username = "demo";
        String password = "demo123";

        when(userRepository.validateUser(username, password)).thenReturn(true);

        // Act
        boolean result = userService.validateUser(username, password);

        // Assert
        assertTrue(result, "Expected true when credentials are valid");
        verify(userRepository, times(1)).validateUser(username, password);
    }

    @Test
    void validateUser_ShouldReturnFalse_WhenCredentialsAreInvalid() {
        // Arrange
        String username = "demo";
        String password = "wrong";

        when(userRepository.validateUser(username, password)).thenReturn(false);

        // Act
        boolean result = userService.validateUser(username, password);

        // Assert
        assertFalse(result, "Expected false when credentials are invalid");
        verify(userRepository, times(1)).validateUser(username, password);
    }

    @Test
    void validateUser_ShouldHandleNullInputsGracefully() {
        // Arrange
        when(userRepository.validateUser(null, null)).thenReturn(false);

        // Act
        boolean result = userService.validateUser(null, null);

        // Assert
        assertFalse(result, "Expected false for null credentials");
        verify(userRepository, times(1)).validateUser(null, null);
    }
}
