# Sample Problem Spring Web Project

A sample Spring Boot REST API demonstrating **problem-spring-web** for standardized error handling, integrated with **Swagger UI** and **H2** in-memory database.

## Features

- ✅ **Problem Spring Web**: RFC 7807 compliant error responses
- ✅ **Swagger UI**: Interactive API documentation
- ✅ **H2 Database**: In-memory database with console access
- ✅ **REST API**: Full CRUD operations for User management
- ✅ **Validation**: Bean validation with proper error handling
- ✅ **Custom Exceptions**: Demonstrates problem-spring-web exception handling

## Technology Stack

- **Spring Boot 3.5.7**
- **Java 17**
- **Problem Spring Web 0.29.1** - For standardized error responses (RFC 7807)
- **SpringDoc OpenAPI 2.3.0** - Swagger UI integration
- **H2 Database** - In-memory database
- **Spring Data JPA** - Data persistence
- **Spring Validation** - Input validation

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

```bash
# Build and run
./mvnw spring-boot:run

# Or using Maven wrapper on Windows
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Base URL
```
http://localhost:8080/api/users
```

### Available Endpoints

- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Access Points

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### API Documentation (JSON)
```
http://localhost:8080/api-docs
```

### H2 Console
```
http://localhost:8080/h2-console
```

**H2 Console Credentials:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Sample Requests

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "description": "Software Developer"
  }'
```

### Get All Users
```bash
curl http://localhost:8080/api/users
```

### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

### Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com",
    "description": "Senior Software Developer"
  }'
```

### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## Error Handling Examples

### Validation Error (400)
When submitting invalid data, you'll receive a problem-spring-web formatted response:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/users",
  "violations": [
    {
      "field": "email",
      "message": "Email must be valid"
    }
  ]
}
```

### User Not Found (404)
```json
{
  "type": "https://example.org/problems/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "User with id 999 not found"
}
```

### Email Already Exists (409)
```json
{
  "type": "https://example.org/problems/email-already-exists",
  "title": "Email Already Exists",
  "status": 409,
  "detail": "User with email john.doe@example.com already exists"
}
```

## Project Structure

```
src/main/java/org/example/sampleproblemspringweb/
├── SampleProblemSpringWebApplication.java  # Main application class
├── config/
│   ├── OpenApiConfig.java                  # Swagger configuration
│   └── DataInitializer.java                # Sample data initialization
├── controller/
│   └── UserController.java                 # REST endpoints
├── dto/
│   ├── UserDTO.java                        # Request DTO
│   └── UserResponseDTO.java                # Response DTO
├── exception/
│   ├── UserNotFoundException.java          # Custom exception
│   └── EmailAlreadyExistsException.java    # Custom exception
├── model/
│   └── User.java                           # JPA entity
├── repository/
│   └── UserRepository.java                 # Data access layer
└── service/
    └── UserService.java                    # Business logic
```

## Presentation Tips

1. **Start with Swagger UI** - Show the interactive API documentation
2. **Demonstrate CRUD Operations** - Create, read, update, delete users
3. **Show Error Handling** - Try invalid requests, duplicate emails, non-existent IDs
4. **Compare Error Formats** - Show how problem-spring-web provides consistent error responses
5. **H2 Console** - Demonstrate database inspection

## Key Features to Highlight

- **Standardized Error Responses**: All errors follow RFC 7807 Problem Details format
- **Automatic Error Mapping**: Problem-spring-web automatically handles exceptions
- **Custom Problem Types**: Demonstrates custom exception types with specific URIs
- **Validation Integration**: Bean validation errors are automatically formatted
- **Swagger Integration**: Full API documentation with examples

## Configuration

Problem Spring Web is configured in `application.properties`:

```properties
problem.with-stack-trace=true
problem.with-causes=true
problem.with-message=true
```

## License

This is a sample project for demonstration purposes.

