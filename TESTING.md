# Unit Testing Guide

This project includes comprehensive unit tests using **JUnit 5**, **Mockito**, and **AssertJ**.

## Test Coverage

### UserService Tests (12 tests)
Tests all business logic methods in the service layer:

- ✅ `createUser` - Success case
- ✅ `createUser` - Email already exists exception
- ✅ `getUserById` - Success case
- ✅ `getUserById` - User not found exception
- ✅ `getAllUsers` - Success with multiple users
- ✅ `getAllUsers` - Empty list
- ✅ `updateUser` - Success with email not changed
- ✅ `updateUser` - Success with email changed
- ✅ `updateUser` - User not found exception
- ✅ `updateUser` - Email already exists exception
- ✅ `deleteUser` - Success case
- ✅ `deleteUser` - User not found exception

### UserController Tests (15 tests)
Tests all REST endpoints:

- ✅ `createUser` - HTTP 201 success
- ✅ `createUser` - REST endpoint success
- ✅ `createUser` - HTTP 409 conflict (email exists)
- ✅ `getUserById` - HTTP 200 success
- ✅ `getUserById` - REST endpoint success
- ✅ `getUserById` - HTTP 404 not found
- ✅ `getAllUsers` - HTTP 200 success
- ✅ `getAllUsers` - REST endpoint success
- ✅ `updateUser` - HTTP 200 success
- ✅ `updateUser` - REST endpoint success
- ✅ `updateUser` - HTTP 404 not found
- ✅ `updateUser` - HTTP 409 conflict (email exists)
- ✅ `deleteUser` - HTTP 204 success
- ✅ `deleteUser` - REST endpoint success
- ✅ `deleteUser` - HTTP 404 not found

## Running Tests

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=UserControllerTest
```

### Run Specific Test Method
```bash
./mvnw test -Dtest=UserServiceTest#createUser_Success
```

### Run Tests with Coverage (if JaCoCo is configured)
```bash
./mvnw test jacoco:report
```

## Test Structure

### Service Tests
- **Mock**: `UserRepository` is mocked
- **Isolation**: Tests are isolated from database
- **Verification**: Verifies business logic and exception handling

### Controller Tests
- **Mock**: `UserService` is mocked
- **MockMvc**: Uses Spring's MockMvc for REST endpoint testing
- **Verification**: Verifies HTTP status codes, response bodies, and JSON structure

## Test Dependencies

All testing dependencies are included in `spring-boot-starter-test`:

- **JUnit 5** - Test framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **Spring Test** - Spring testing utilities
- **MockMvc** - Web layer testing

## Best Practices Used

1. **Arrange-Act-Assert (AAA) Pattern**
   - Arrange: Set up test data and mocks
   - Act: Execute the method under test
   - Assert: Verify the results

2. **Descriptive Test Names**
   - Format: `methodName_scenario_expectedResult`
   - Example: `createUser_EmailExists_ThrowsException`

3. **@DisplayName Annotation**
   - Human-readable test descriptions
   - Example: `@DisplayName("Should create user successfully when email does not exist")`

4. **Isolation**
   - Each test is independent
   - No shared state between tests
   - Mocks are reset for each test

5. **Verification**
   - Verify mock interactions
   - Assert expected results
   - Check exception types and messages

## Example Test

```java
@Test
@DisplayName("Should create user successfully when email does not exist")
void createUser_Success() {
    // Arrange
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // Act
    UserResponseDTO result = userService.createUser(testUserDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("John Doe");

    verify(userRepository).existsByEmail("john.doe@example.com");
    verify(userRepository).save(any(User.class));
}
```

## Test Files

- `UserServiceTest.java` - Service layer unit tests
- `UserControllerTest.java` - Controller layer unit tests
- `SampleProblemSpringWebApplicationTests.java` - Application context test

## Test Results

All 27 tests pass successfully:
- ✅ 12 UserService tests
- ✅ 15 UserController tests

## Adding New Tests

When adding new functionality:

1. **Add test methods** to the appropriate test class
2. **Follow naming conventions**: `methodName_scenario_expectedResult`
3. **Use @DisplayName** for descriptive test names
4. **Mock dependencies** using `@Mock` annotation
5. **Verify interactions** using `verify()` from Mockito
6. **Assert results** using AssertJ's fluent API

## Continuous Integration

These tests can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run tests
  run: ./mvnw test
```

Tests run quickly (under 2 seconds) and provide fast feedback during development.

