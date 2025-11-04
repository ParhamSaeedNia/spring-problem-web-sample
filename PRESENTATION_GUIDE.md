# Presentation Guide

## Quick Start Demo Flow

### 1. Start the Application
```bash
./mvnw spring-boot:run
```

Wait for: `Started SampleProblemSpringWebApplication`

### 2. Open Swagger UI
Navigate to: **http://localhost:8080/swagger-ui.html**

**Key Points:**
- All endpoints are documented
- Try them directly from Swagger UI
- See request/response schemas

### 3. Demonstrate CRUD Operations

#### Create a User (POST /api/users)
- Use Swagger UI or curl
- Show success response (201 Created)
- **Try invalid data** → Show validation errors (400)
- **Try duplicate email** → Show conflict error (409)

#### Get All Users (GET /api/users)
- Shows 3 pre-populated users from DataInitializer
- Clean JSON response

#### Get User by ID (GET /api/users/{id})
- Use ID: 1, 2, or 3 (from initial data)
- **Try ID: 999** → Show 404 error with problem-spring-web format

#### Update User (PUT /api/users/{id})
- Update an existing user
- **Try updating with existing email** → Show 409 conflict

#### Delete User (DELETE /api/users/{id})
- Delete a user
- **Try deleting non-existent user** → Show 404 error

### 4. Show Error Handling (Problem Spring Web)

#### Validation Error Example
Request:
```json
{
  "name": "A",
  "email": "invalid-email",
  "description": "Hi"
}
```

Response (400):
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "violations": [
    {
      "field": "name",
      "message": "Name must be between 2 and 50 characters"
    },
    {
      "field": "email",
      "message": "Email must be valid"
    }
  ]
}
```

#### Custom Exception Example (404)
Request: `GET /api/users/999`

Response:
```json
{
  "type": "https://example.org/problems/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "User with id 999 not found"
}
```

#### Conflict Exception Example (409)
Request: Create user with existing email

Response:
```json
{
  "type": "https://example.org/problems/email-already-exists",
  "title": "Email Already Exists",
  "status": 409,
  "detail": "User with email john.doe@example.com already exists"
}
```

### 5. H2 Database Console
Navigate to: **http://localhost:8080/h2-console**

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

**SQL Query:**
```sql
SELECT * FROM USERS;
```

**Key Points:**
- In-memory database
- All data visible here
- Perfect for demos/presentations

## Code Highlights to Show

### 1. Custom Exception (UserNotFoundException.java)
```java
public class UserNotFoundException extends AbstractThrowableProblem {
    // RFC 7807 compliant exception
}
```

### 2. Problem-spring-web Auto-configuration
- No special configuration needed!
- Automatically handles exceptions
- Formats all errors consistently

### 3. Controller Error Handling
- Clean controller code
- No try-catch blocks needed
- Problem-spring-web handles everything

### 4. Validation Integration
- Bean validation annotations
- Automatic problem-spring-web formatting
- Detailed violation messages

## Talking Points

1. **Why Problem Spring Web?**
   - RFC 7807 standard compliance
   - Consistent error format across all endpoints
   - No need for custom exception handlers
   - Automatic integration with validation

2. **Benefits for API Consumers**
   - Predictable error structure
   - Clear error types with URIs
   - Detailed violation information
   - Easy to handle programmatically

3. **Developer Experience**
   - Minimal boilerplate
   - Custom exceptions extend AbstractThrowableProblem
   - Automatic error mapping
   - Works seamlessly with Spring validation

4. **Integration Points**
   - Swagger UI for API documentation
   - H2 for easy database inspection
   - Spring Data JPA for persistence
   - Bean Validation for input validation

## Common Questions

**Q: How do I add a new custom exception?**
A: Extend `AbstractThrowableProblem` and set the problem type, title, status, and detail.

**Q: Can I customize the error format?**
A: Yes, problem-spring-web provides configuration options and extension points.

**Q: Does it work with validation errors?**
A: Yes! Validation errors are automatically formatted as problem details.

**Q: What about 404 errors from Spring?**
A: Problem-spring-web handles all Spring exceptions automatically.

## Endpoints Summary

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | /api/users | Create user | 201, 400, 409 |
| GET | /api/users | List all users | 200 |
| GET | /api/users/{id} | Get user by ID | 200, 404 |
| PUT | /api/users/{id} | Update user | 200, 400, 404, 409 |
| DELETE | /api/users/{id} | Delete user | 204, 404 |

