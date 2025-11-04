# Logging Configuration

This project uses **Log4j2** with **SLF4J** API for logging.

## Log Configuration

The logging configuration is defined in `src/main/resources/log4j2.xml`.

### Log Files

Logs are saved to the following files in the `logs/` directory:

1. **`logs/application.log`** - All application logs (INFO, DEBUG, WARN, ERROR)
2. **`logs/error.log`** - Only ERROR level logs
3. **Console** - Logs are also output to the console

### Log File Rotation

- Logs are rotated daily
- Files are compressed when they exceed 10MB
- Maximum of 10 archived files are kept

### Log Levels

- **Application packages** (`org.example.sampleproblemspringweb`): INFO
- **Spring Framework**: INFO
- **Hibernate SQL**: DEBUG (SQL queries)
- **Root Logger**: INFO

## Logging in Code

All classes use SLF4J Logger:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(ClassName.class);
```

### Logging Examples

- **INFO**: Important application events (user creation, updates, etc.)
- **DEBUG**: Detailed debugging information (method entry, data retrieval)
- **WARN**: Warning conditions (user not found, validation issues)
- **ERROR**: Error conditions with stack traces

## Viewing Logs

### During Development

1. **Console**: Logs appear in the terminal where you run the application
2. **Log Files**: Check `logs/application.log` and `logs/error.log`

### Log File Locations

```
logs/
├── application.log          # All application logs
├── error.log               # Error logs only
├── application-YYYY-MM-DD-1.log.gz  # Archived logs
└── error-YYYY-MM-DD-1.log.gz        # Archived error logs
```

## Log Format

Each log entry includes:
- **Timestamp**: `yyyy-MM-dd HH:mm:ss.SSS`
- **Thread**: Thread name
- **Level**: Log level (INFO, DEBUG, WARN, ERROR)
- **Logger**: Class name
- **Message**: Log message

Example:
```
2025-11-04 10:30:45.123 [main] INFO  o.e.s.SampleProblemSpringWebApplication - Starting Sample Problem Spring Web Application...
```

## Logging Coverage

Logging has been added to:

- ✅ **SampleProblemSpringWebApplication** - Application startup/shutdown
- ✅ **UserController** - All REST endpoint requests and responses
- ✅ **UserService** - Business logic operations
- ✅ **DataInitializer** - Database initialization

## Changing Log Levels

To change log levels, edit `src/main/resources/log4j2.xml`:

```xml
<Logger name="org.example.sampleproblemspringweb" level="DEBUG"/>
```

Available levels (from lowest to highest):
- **TRACE**: Most detailed
- **DEBUG**: Debugging information
- **INFO**: Informational messages (default)
- **WARN**: Warning messages
- **ERROR**: Error messages
- **FATAL**: Fatal errors

## Tips

1. **Check logs during development**: Monitor `logs/application.log` while testing
2. **Error investigation**: Check `logs/error.log` for error details
3. **Performance**: DEBUG level can be verbose - use INFO for production
4. **Log rotation**: Old logs are automatically archived and compressed

