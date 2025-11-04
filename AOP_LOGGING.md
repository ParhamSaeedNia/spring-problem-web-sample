# AOP-Based Logging

This project uses **Aspect-Oriented Programming (AOP)** for automatic method logging. All methods in controllers and services are automatically logged without requiring manual logging statements.

## Features

✅ **Automatic Logging**: All controller and service methods are automatically logged  
✅ **Method Entry**: Logs method name and parameters  
✅ **Method Exit**: Logs return values and execution time  
✅ **Exception Handling**: Logs exceptions with stack traces  
✅ **Customizable**: Use `@Loggable` annotation for fine-grained control  
✅ **Performance Tracking**: Automatic execution time measurement  

## How It Works

### 1. LoggingAspect

The `LoggingAspect` class uses Spring AOP to intercept method calls:

- **Pointcuts**: Automatically intercepts all methods in:
  - `@RestController` classes
  - `@Service` classes
  - Methods annotated with `@Loggable`

- **Around Advice**: Wraps method execution to log:
  - Method entry with parameters
  - Method exit with return value
  - Execution time in milliseconds
  - Exceptions with full stack traces

### 2. @Loggable Annotation

Optional annotation for fine-grained control:

```java
@Loggable(
    value = Loggable.LogLevel.INFO,  // Log level
    logParams = true,                 // Log parameters
    logResult = true,                 // Log return value
    logExecutionTime = true           // Log execution time
)
public UserResponseDTO createUser(UserDTO userDTO) {
    // Method implementation
}
```

## Log Output Examples

### Successful Method Execution

```
INFO  → Entering method: UserController.createUser with parameters: [UserDTO(name=John Doe, email=john@example.com, description=Developer)]
INFO  ← Exiting method: UserController.createUser | Execution time: 45ms | Return: {"id":1,"name":"John Doe","email":"john@example.com","description":"Developer"}
```

### Method with Exception

```
INFO  → Entering method: UserService.getUserById with parameters: [999]
ERROR ✗ Exception in method: UserService.getUserById | Execution time: 2ms | Exception: UserNotFoundException - User with id 999 not found
```

### Method with Collection Return

```
INFO  → Entering method: UserService.getAllUsers with parameters: []
INFO  ← Exiting method: UserService.getAllUsers | Execution time: 12ms | Return: ArrayList[size=3]
```

## Configuration

### Enable AOP

AOP is enabled in the main application class:

```java
@SpringBootApplication
@EnableAspectJAutoProxy
public class SampleProblemSpringWebApplication {
    // ...
}
```

### Pointcuts

The aspect automatically intercepts:

1. **Controller Methods**: All methods in `@RestController` classes
2. **Service Methods**: All methods in `@Service` classes
3. **Annotated Methods**: Any method with `@Loggable` annotation

## Customization

### Disable Logging for Specific Methods

If you want to exclude a method from AOP logging, you can:

1. **Remove it from the pointcut** (modify `LoggingAspect`)
2. **Use a different annotation** and create a separate pointcut
3. **Use manual logging** for that specific method

### Adjust Log Levels

Edit the `LoggingAspect.java` to change default log levels:

```java
Loggable.LogLevel logLevel = loggable != null ? loggable.value() : Loggable.LogLevel.DEBUG;
```

### Customize Log Format

Modify the `formatArguments()` and `formatReturnValue()` methods in `LoggingAspect` to customize how parameters and return values are logged.

## Benefits

1. **Clean Code**: No need for manual logging statements in every method
2. **Consistency**: All methods are logged in the same format
3. **Maintainability**: Change logging behavior in one place
4. **Performance**: Automatic execution time tracking
5. **Debugging**: Easy to trace method calls and parameters

## Manual Logging vs AOP Logging

You can still use manual logging statements alongside AOP logging:

- **AOP Logging**: Automatic method-level logging (entry, exit, time, exceptions)
- **Manual Logging**: Business-specific logging (e.g., "User created successfully")

Both will appear in the logs, giving you comprehensive logging coverage.

## Example Usage

### Controller Method (Automatically Logged)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserDTO userDTO) {
        // No manual logging needed - AOP handles it automatically!
        UserResponseDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
```

### Service Method with Custom Logging

```java
@Service
public class UserService {
    
    @Loggable(value = Loggable.LogLevel.DEBUG, logParams = false)
    public UserResponseDTO getUserById(Long id) {
        // AOP will log method entry/exit, but not parameters
        // You can still add manual logging for business logic
        return userRepository.findById(id).orElseThrow(...);
    }
}
```

## Files

- `LoggingAspect.java` - Main AOP aspect class
- `@Loggable.java` - Optional annotation for fine-grained control
- `SampleProblemSpringWebApplication.java` - AOP enabled with `@EnableAspectJAutoProxy`

## Log Files

All AOP logs are saved to:
- `logs/application.log` - All logs
- `logs/error.log` - Error logs only

Same as manual logging - everything goes to the same log files!

