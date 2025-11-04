package org.example.sampleproblemspringweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.sampleproblemspringweb.dto.UserDTO;
import org.example.sampleproblemspringweb.dto.UserResponseDTO;
import org.example.sampleproblemspringweb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        logger.info("UserController initialized");
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO) {
        logger.info("Received request to create user with email: {}", userDTO.getEmail());
        try {
            UserResponseDTO createdUser = userService.createUser(userDTO);
            logger.info("User created successfully with ID: {} and email: {}", 
                createdUser.getId(), createdUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user with email: {}", userDTO.getEmail(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        logger.info("Received request to get user by ID: {}", id);
        try {
            UserResponseDTO user = userService.getUserById(id);
            logger.info("User found with ID: {} and email: {}", user.getId(), user.getEmail());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.warn("User not found with ID: {}", id);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Received request to get all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        logger.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        logger.info("Received request to update user with ID: {} and email: {}", id, userDTO.getEmail());
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
            logger.info("User updated successfully with ID: {} and email: {}", 
                updatedUser.getId(), updatedUser.getEmail());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User successfully deleted"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        logger.info("Received request to delete user with ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("User deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("Error deleting user with ID: {}", id);
            throw e;
        }
    }
}

