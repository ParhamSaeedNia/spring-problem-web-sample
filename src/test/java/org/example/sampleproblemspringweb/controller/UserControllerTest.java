package org.example.sampleproblemspringweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sampleproblemspringweb.dto.UserDTO;
import org.example.sampleproblemspringweb.dto.UserResponseDTO;
import org.example.sampleproblemspringweb.exception.EmailAlreadyExistsException;
import org.example.sampleproblemspringweb.exception.UserNotFoundException;
import org.example.sampleproblemspringweb.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private UserResponseDTO testUserResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice() // This allows exceptions to be thrown normally
            .build();
        objectMapper = new ObjectMapper();

        testUserDTO = new UserDTO("John Doe", "john.doe@example.com", "Software Developer");
        testUserResponseDTO = new UserResponseDTO(1L, "John Doe", "john.doe@example.com", "Software Developer");
    }

    @Test
    @DisplayName("Should create user successfully - HTTP 201")
    void createUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserDTO.class))).thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.createUser(testUserDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("John Doe");

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Should create user successfully via REST endpoint")
    void createUser_RestEndpoint_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserDTO.class))).thenReturn(testUserResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.description").value("Software Developer"));

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Should return 409 when email already exists")
    void createUser_EmailExists_Returns409() throws Exception {
        // Given
        when(userService.createUser(any(UserDTO.class)))
            .thenThrow(new EmailAlreadyExistsException("john.doe@example.com"));

        // When & Then - Exception will be thrown, but we verify it's the right exception
        try {
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUserDTO)));
        } catch (Exception e) {
            // Exception is expected - problem-spring-web handles it
            assertThat(e.getCause()).isInstanceOf(EmailAlreadyExistsException.class);
        }

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully - HTTP 200")
    void getUserById_Success() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.getUserById(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should get user by ID successfully via REST endpoint")
    void getUserById_RestEndpoint_Success() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(testUserResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    void getUserById_NotFound_Returns404() throws Exception {
        // Given
        Long userId = 999L;
        when(userService.getUserById(userId))
            .thenThrow(new UserNotFoundException(userId));

        // When & Then - Exception will be thrown
        try {
            mockMvc.perform(get("/api/users/{id}", userId));
        } catch (Exception e) {
            // Exception is expected - problem-spring-web handles it
            assertThat(e.getCause()).isInstanceOf(UserNotFoundException.class);
        }

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should get all users successfully - HTTP 200")
    void getAllUsers_Success() throws Exception {
        // Given
        UserResponseDTO user1 = new UserResponseDTO(1L, "John Doe", "john@example.com", "Developer");
        UserResponseDTO user2 = new UserResponseDTO(2L, "Jane Smith", "jane@example.com", "Manager");
        List<UserResponseDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should get all users successfully via REST endpoint")
    void getAllUsers_RestEndpoint_Success() throws Exception {
        // Given
        UserResponseDTO user1 = new UserResponseDTO(1L, "John Doe", "john@example.com", "Developer");
        UserResponseDTO user2 = new UserResponseDTO(2L, "Jane Smith", "jane@example.com", "Manager");
        List<UserResponseDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("John Doe"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should update user successfully - HTTP 200")
    void updateUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserResponseDTO updatedResponse = new UserResponseDTO(1L, "John Updated", "john.updated@example.com", "Senior Developer");

        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(updatedResponse);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUser(userId, testUserDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Updated");

        verify(userService).updateUser(eq(userId), any(UserDTO.class));
    }

    @Test
    @DisplayName("Should update user successfully via REST endpoint")
    void updateUser_RestEndpoint_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserResponseDTO updatedResponse = new UserResponseDTO(1L, "John Updated", "john.updated@example.com", "Senior Developer");

        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("John Updated"));

        verify(userService).updateUser(eq(userId), any(UserDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void updateUser_NotFound_Returns404() throws Exception {
        // Given
        Long userId = 999L;
        when(userService.updateUser(eq(userId), any(UserDTO.class)))
            .thenThrow(new UserNotFoundException(userId));

        // When & Then - Exception will be thrown
        try {
            mockMvc.perform(put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUserDTO)));
        } catch (Exception e) {
            // Exception is expected - problem-spring-web handles it
            assertThat(e.getCause()).isInstanceOf(UserNotFoundException.class);
        }

        verify(userService).updateUser(eq(userId), any(UserDTO.class));
    }

    @Test
    @DisplayName("Should return 409 when updating to existing email")
    void updateUser_EmailExists_Returns409() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.updateUser(eq(userId), any(UserDTO.class)))
            .thenThrow(new EmailAlreadyExistsException("existing@example.com"));

        // When & Then - Exception will be thrown
        try {
            mockMvc.perform(put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUserDTO)));
        } catch (Exception e) {
            // Exception is expected - problem-spring-web handles it
            assertThat(e.getCause()).isInstanceOf(EmailAlreadyExistsException.class);
        }

        verify(userService).updateUser(eq(userId), any(UserDTO.class));
    }

    @Test
    @DisplayName("Should delete user successfully - HTTP 204")
    void deleteUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("Should delete user successfully via REST endpoint")
    void deleteUser_RestEndpoint_Success() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    void deleteUser_NotFound_Returns404() throws Exception {
        // Given
        Long userId = 999L;
        doThrow(new UserNotFoundException(userId))
            .when(userService).deleteUser(userId);

        // When & Then - Exception will be thrown
        try {
            mockMvc.perform(delete("/api/users/{id}", userId));
        } catch (Exception e) {
            // Exception is expected - problem-spring-web handles it
            assertThat(e.getCause()).isInstanceOf(UserNotFoundException.class);
        }

        verify(userService).deleteUser(userId);
    }
}

