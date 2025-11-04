package org.example.sampleproblemspringweb.service;

import org.example.sampleproblemspringweb.dto.UserDTO;
import org.example.sampleproblemspringweb.dto.UserResponseDTO;
import org.example.sampleproblemspringweb.exception.EmailAlreadyExistsException;
import org.example.sampleproblemspringweb.exception.UserNotFoundException;
import org.example.sampleproblemspringweb.model.User;
import org.example.sampleproblemspringweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john.doe@example.com", "Software Developer");
        testUser.setId(1L);

        testUserDTO = new UserDTO("John Doe", "john.doe@example.com", "Software Developer");
    }

    @Test
    @DisplayName("Should create user successfully when email does not exist")
    void createUser_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.createUser(testUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getDescription()).isEqualTo("Software Developer");

        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void createUser_EmailExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(testUserDTO))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .hasMessageContaining("john.doe@example.com");

        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void getUserById_NotFound_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("999");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Developer");
        user1.setId(1L);
        User user2 = new User("Jane Smith", "jane@example.com", "Manager");
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should update user successfully when email is not changed")
    void updateUser_Success_EmailNotChanged() {
        // Given
        Long userId = 1L;
        UserDTO updateDTO = new UserDTO("John Updated", "john.doe@example.com", "Senior Developer");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // When
        UserResponseDTO result = userService.updateUser(userId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getDescription()).isEqualTo("Senior Developer");

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should update user successfully when email is changed and new email does not exist")
    void updateUser_Success_EmailChanged() {
        // Given
        Long userId = 1L;
        UserDTO updateDTO = new UserDTO("John Updated", "john.updated@example.com", "Senior Developer");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // When
        UserResponseDTO result = userService.updateUser(userId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void updateUser_NotFound_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, testUserDTO))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("999");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when updating to existing email")
    void updateUser_EmailExists_ThrowsException() {
        // Given
        Long userId = 1L;
        UserDTO updateDTO = new UserDTO("John Updated", "existing@example.com", "Senior Developer");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, updateDTO))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .hasMessageContaining("existing@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void deleteUser_NotFound_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("999");

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}

